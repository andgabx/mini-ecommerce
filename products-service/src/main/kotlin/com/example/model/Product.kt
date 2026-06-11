package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val colorway: String,
    val description: String?,
    val price: Double,
    val stock: Int
)
