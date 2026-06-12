package com.example

import com.example.client.ProductsClient
import com.example.data.OrderRepository
import com.example.model.CreateOrderRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repo: OrderRepository, productsClient: ProductsClient) {
    routing {

        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/orders") {

            post {
                val userId = call.request.headers["X-User-Id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Usuário não identificado"))

                val req = call.receive<CreateOrderRequest>()
                if (req.quantity <= 0) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Quantidade deve ser maior que zero"))
                    return@post
                }

                val product = productsClient.getProduct(req.productId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Produto não encontrado"))

                if (product.stock < req.quantity) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Estoque insuficiente"))
                    return@post
                }

                val order = repo.create(
                    userId     = userId,
                    productId  = req.productId,
                    quantity   = req.quantity,
                    totalPrice = product.price * req.quantity
                )
                call.respond(HttpStatusCode.Created, order)
            }

            get("/{userId}") {
                val requestedUserId = call.parameters["userId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))

                val callerId   = call.request.headers["X-User-Id"]?.toIntOrNull()
                val callerRole = call.request.headers["X-User-Role"]

                if (callerId != requestedUserId && callerRole != "admin") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acesso negado"))
                    return@get
                }

                call.respond(repo.findByUserId(requestedUserId))
            }
        }
    }
}
