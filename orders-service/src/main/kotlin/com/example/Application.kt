package com.example

import com.example.client.ProductsClient
import com.example.data.OrderRepository
import io.ktor.server.application.*

fun Application.configureApp(repo: OrderRepository, productsClient: ProductsClient) {
    configureStatusPages()
    configureSerialization()
    configureRouting(repo, productsClient)
}
