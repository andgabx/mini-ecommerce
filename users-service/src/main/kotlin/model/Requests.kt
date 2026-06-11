package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val userId: Int,
    val email: String,
    val role: String
)

data class LoginData(
    val userId: Int,
    val email: String,
    val role: String
)
