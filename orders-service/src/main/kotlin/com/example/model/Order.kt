package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int,
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val totalPrice: Double,
    val status: String,
    val createdAt: String
)
