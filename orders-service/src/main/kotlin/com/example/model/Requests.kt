package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val productId: Int,
    val quantity: Int
)

@Serializable
data class ProductSummary(
    val id: Int,
    val name: String,
    val brand: String,
    val price: Double,
    val stock: Int
)
