package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRoutesTest {

    private fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application { configureApp("test_secret", 86_400_000L, FakeUserRepository()) }
        block()
    }

    @Test
    fun `health returns 200`() = testApp {
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `register returns 201 for valid input`() = testApp {
        val response = client.post("/users/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"João","email":"joao@test.com","password":"senha123"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `register returns 400 for short password`() = testApp {
        val response = client.post("/users/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"João","email":"joao@test.com","password":"123"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `register returns 409 for duplicate email`() = testApp {
        repeat(2) {
            client.post("/users/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"name":"João","email":"joao@test.com","password":"senha123"}""")
            }
        }
        val response = client.post("/users/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"João","email":"joao@test.com","password":"senha123"}""")
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    @Test
    fun `login returns 200 with token for valid credentials`() = testApp {
        client.post("/users/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"João","email":"joao@test.com","password":"senha123"}""")
        }
        val response = client.post("/users/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"joao@test.com","password":"senha123"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("token"))
    }

    @Test
    fun `login returns 401 for wrong password`() = testApp {
        client.post("/users/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"João","email":"joao@test.com","password":"senha123"}""")
        }
        val response = client.post("/users/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"joao@test.com","password":"errada"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `get user returns 403 when accessing another users data`() = testApp {
        val response = client.get("/users/99") {
            header("X-User-Id", "1")
            header("X-User-Role", "user")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
