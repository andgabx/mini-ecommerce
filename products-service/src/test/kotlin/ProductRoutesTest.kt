package com.example

import com.example.model.Product
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductRoutesTest {

    private fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        val repo = FakeProductRepository().apply {
            addProduct(Product(1, "Air Jordan 1 Retro High OG", "Jordan", "Chicago", null, 2800.0, 5))
            addProduct(Product(2, "Nike SB Dunk Low", "Nike", "Pigeon", null, 4500.0, 2))
        }
        application { configureApp(repo, replicationClient = null, serviceRole = "primary") }
        block()
    }

    @Test
    fun `health returns 200 with role`() = testApp {
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("primary"))
    }

    @Test
    fun `list products returns all seeded products`() = testApp {
        val response = client.get("/products")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Jordan"))
    }

    @Test
    fun `get product by id returns 200 for existing product`() = testApp {
        val response = client.get("/products/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Chicago"))
    }

    @Test
    fun `get product by id returns 404 for unknown id`() = testApp {
        val response = client.get("/products/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `create product returns 403 without admin role`() = testApp {
        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            header("X-User-Role", "user")
            setBody("""{"name":"Yeezy","brand":"Adidas","colorway":"Zebra","price":3200.0,"stock":8}""")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `create product returns 201 with admin role`() = testApp {
        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            header("X-User-Role", "admin")
            setBody("""{"name":"Yeezy","brand":"Adidas","colorway":"Zebra","price":3200.0,"stock":8}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Yeezy"))
    }
}
