package com.example.client

import com.example.model.ProductSummary

interface ProductsClient {
    suspend fun getProduct(productId: Int): ProductSummary?
}
