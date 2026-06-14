package com.example

import com.example.registry.ServiceRegistry
import com.example.registry.ServiceStatus
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(jwtSecret: String, httpClient: HttpClient) {
    routing {

        get("/status") {
            call.respond(ServiceRegistry.allServices.map { s ->
                mapOf(
                    "name"     to s.name,
                    "url"      to s.url,
                    "status"   to if (s.isUp) "UP" else "DOWN",
                    "failures" to s.consecutiveFailures.toString()
                )
            })
        }

        get("/dashboard") {
            call.respondText(buildDashboardHtml(), ContentType.Text.Html)
        }

        get("/ui") {
            call.respondText(buildUiHtml(), ContentType.Text.Html)
        }

        post("/users/register") {
            call.guardedProxy(httpClient, ServiceRegistry.usersService, "/users/register")
        }
        post("/users/login") {
            call.guardedProxy(httpClient, ServiceRegistry.usersService, "/users/login")
        }
        get("/products") {
            val service = ServiceRegistry.nextProductReadService()
            call.guardedProxy(httpClient, service, "/products")
        }
        get("/products/{id}") {
            val id = call.parameters["id"]
            val service = ServiceRegistry.nextProductReadService()
            call.guardedProxy(httpClient, service, "/products/$id")
        }

        get("/users") {
            val claims = call.requireJwt(jwtSecret) ?: return@get
            if (claims.role != "admin") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acesso negado"))
                return@get
            }
            call.guardedProxy(httpClient, ServiceRegistry.usersService, "/users", claims)
        }
        get("/users/{id}") {
            val claims = call.requireJwt(jwtSecret) ?: return@get
            val id = call.parameters["id"]
            call.guardedProxy(httpClient, ServiceRegistry.usersService, "/users/$id", claims)
        }
        post("/products") {
            val claims = call.requireJwt(jwtSecret) ?: return@post
            if (claims.role != "admin") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Requer role admin"))
                return@post
            }
            call.guardedProxy(httpClient, ServiceRegistry.productsPrimary, "/products", claims)
        }
        post("/orders") {
            val claims = call.requireJwt(jwtSecret) ?: return@post
            call.guardedProxy(httpClient, ServiceRegistry.ordersService, "/orders", claims)
        }
        get("/orders") {
            val claims = call.requireJwt(jwtSecret) ?: return@get
            if (claims.role != "admin") {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Acesso negado"))
                return@get
            }
            call.guardedProxy(httpClient, ServiceRegistry.ordersService, "/orders", claims)
        }
        get("/orders/{userId}") {
            val claims = call.requireJwt(jwtSecret) ?: return@get
            val userId = call.parameters["userId"]
            call.guardedProxy(httpClient, ServiceRegistry.ordersService, "/orders/$userId", claims)
        }
    }
}

private suspend fun ApplicationCall.requireJwt(jwtSecret: String): JwtClaims? {
    val token = request.headers[HttpHeaders.Authorization]
        ?.removePrefix("Bearer ")?.trim()
    if (token.isNullOrBlank()) {
        respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token ausente"))
        return null
    }
    val claims = extractJwt(token, jwtSecret)
    if (claims == null) {
        respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token inválido ou expirado"))
    }
    return claims
}

private suspend fun ApplicationCall.guardedProxy(
    client: HttpClient,
    service: ServiceStatus,
    path: String,
    claims: JwtClaims? = null
) {
    if (!service.isUp) {
        respond(HttpStatusCode.ServiceUnavailable, mapOf("error" to "${service.name} indisponível"))
        return
    }

    val method = request.httpMethod
    val body = if (method == HttpMethod.Post || method == HttpMethod.Put) {
        receive<ByteArray>()
    } else null

    val response = client.request("${service.url}$path") {
        this.method = method
        body?.let {
            setBody(it)
            contentType(ContentType.Application.Json)
        }
        claims?.let {
            header("X-User-Id",    it.userId.toString())
            header("X-User-Email", it.email)
            header("X-User-Role",  it.role)
        }
    }

    respondBytes(response.bodyAsBytes(), status = response.status)
}
