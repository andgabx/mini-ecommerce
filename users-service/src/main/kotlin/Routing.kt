package com.example

import com.example.data.UserRepository
import com.example.model.LoginRequest
import com.example.model.LoginResponse
import com.example.model.RegisterRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(jwtSecret: String, jwtExpirationMs: Long, repo: UserRepository) {
    routing {

        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        route("/users") {

            get {
                if (call.request.headers["X-User-Role"] != "admin") {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acesso negado"))
                    return@get
                }
                call.respond(repo.findAll())
            }

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
                val loginData = repo.validateCredentials(req.email, req.password)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciais inválidas"))

                val token = generateJwt(
                    userId       = loginData.userId,
                    email        = loginData.email,
                    role         = loginData.role,
                    secret       = jwtSecret,
                    expirationMs = jwtExpirationMs
                )
                call.respond(LoginResponse(token, loginData.userId, loginData.email, loginData.role))
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
