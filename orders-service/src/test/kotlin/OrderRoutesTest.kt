package com.example

import com.example.model.ProductSummary
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderRoutesTest {

    private val fakeProducts = mapOf(
        1 to ProductSummary(1, "Air Jordan 1 Retro High OG", "Jordan", 2800.0, 5)
    )

    private fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application { configureApp(FakeOrderRepository(), FakeProductsClient(fakeProducts)) }
        block()
    }

    @Test
    fun `health returns 200`() = testApp {
        assertEquals(HttpStatusCode.OK, client.get("/health").status)
    }

    @Test
    fun `create order returns 201 for valid product`() = testApp {
        val response = client.post("/orders") {
            header("X-User-Id", "1")
            contentType(ContentType.Application.Json)
            setBody("""{"productId":1,"quantity":2}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("5600"))
    }

    @Test
    fun `create order returns 404 for unknown product`() = testApp {
        val response = client.post("/orders") {
            header("X-User-Id", "1")
            contentType(ContentType.Application.Json)
            setBody("""{"productId":999,"quantity":1}""")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `create order returns 400 for zero quantity`() = testApp {
        val response = client.post("/orders") {
            header("X-User-Id", "1")
            contentType(ContentType.Application.Json)
            setBody("""{"productId":1,"quantity":0}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `create order returns 401 without user id`() = testApp {
        val response = client.post("/orders") {
            contentType(ContentType.Application.Json)
            setBody("""{"productId":1,"quantity":1}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `list orders returns empty for user with no orders`() = testApp {
        val response = client.get("/orders/1") {
            header("X-User-Id", "1")
            header("X-User-Role", "user")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun `list orders returns 403 when accessing another users orders`() = testApp {
        val response = client.get("/orders/99") {
            header("X-User-Id", "1")
            header("X-User-Role", "user")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
