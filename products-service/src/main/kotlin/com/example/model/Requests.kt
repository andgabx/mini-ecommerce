package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val name: String,
    val brand: String,
    val colorway: String,
    val description: String? = null,
    val price: Double,
    val stock: Int = 0
)
