package com.example

import com.example.data.ProductRepository
import com.example.model.CreateProductRequest
import com.example.replication.ReplicationClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    repo: ProductRepository,
    replicationClient: ReplicationClient?,
    serviceRole: String
) {
    routing {

        get("/health") {
            call.respond(mapOf("status" to "ok", "role" to serviceRole))
        }

        route("/products") {

            get {
                call.respond(repo.findAll())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                val product = repo.findById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Produto não encontrado"))
                call.respond(product)
            }

            post {
                if (serviceRole == "replica") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Réplica não aceita escritas diretas"))
                    return@post
                }
                if (call.request.headers["X-User-Role"] != "admin") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Requer privilégio admin"))
                    return@post
                }
                val req = call.receive<CreateProductRequest>()
                if (req.name.isBlank() || req.price < 0) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Dados inválidos"))
                    return@post
                }
                val replicated = replicationClient?.replicate(req) ?: true
                if (!replicated) {
                    call.respond(HttpStatusCode.ServiceUnavailable, mapOf("error" to "Réplica indisponível — escrita cancelada"))
                    return@post
                }
                call.respond(HttpStatusCode.Created, repo.create(req))
            }
        }

        post("/internal/replicate") {
            if (serviceRole == "primary") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Primary não aceita replicação"))
                return@post
            }
            call.respond(repo.create(call.receive<CreateProductRequest>()))
        }
    }
}
