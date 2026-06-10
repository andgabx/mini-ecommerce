package com.example

import com.example.data.UserRepository
import com.example.data.UsersTable
import com.example.model.LoginRequest
import com.example.model.LoginResponse
import com.example.model.RegisterRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(jwtSecret: String, jwtExpirationMs: Long) {
    val repo = UserRepository()

    routing {

        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/users") {

            post("/register") {
                val req = call.receive<RegisterRequest>()

                if (req.name.isBlank() || req.email.isBlank() || req.password.length < 6) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Dados inválidos: nome e email obrigatórios, senha mínima 6 caracteres"))
                    return@post
                }

                val user = repo.create(req.name, req.email, req.password)
                    ?: return@post call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email já cadastrado"))

                call.respond(HttpStatusCode.Created, user)
            }

            post("/login") {
                val req = call.receive<LoginRequest>()

                val row = repo.validateCredentials(req.email, req.password)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciais inválidas"))

                val token = generateJwt(
                    userId       = row[UsersTable.id],
                    email        = row[UsersTable.email],
                    role         = row[UsersTable.role],
                    secret       = jwtSecret,
                    expirationMs = jwtExpirationMs
                )

                call.respond(LoginResponse(
                    token  = token,
                    userId = row[UsersTable.id],
                    email  = row[UsersTable.email],
                    role   = row[UsersTable.role]
                ))
            }

            get("/{id}") {
                val requestedId = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))

                val callerId   = call.request.headers["X-User-Id"]?.toIntOrNull()
                val callerRole = call.request.headers["X-User-Role"]

                if (callerId != requestedId && callerRole != "admin") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acesso negado"))
                    return@get
                }

                val user = repo.findById(requestedId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuário não encontrado"))

                call.respond(user)
            }
        }
    }
}
