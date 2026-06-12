package com.example

import com.example.client.ProductsClient
import com.example.model.ProductSummary

class FakeProductsClient(
    private val products: Map<Int, ProductSummary> = emptyMap()
) : ProductsClient {
    override suspend fun getProduct(productId: Int): ProductSummary? = products[productId]
}
