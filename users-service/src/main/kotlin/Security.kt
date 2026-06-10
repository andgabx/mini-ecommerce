package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

fun generateJwt(
    userId: Int,
    email: String,
    role: String,
    secret: String,
    expirationMs: Long
): String = JWT.create()
    .withClaim("userId", userId)
    .withClaim("email", email)
    .withClaim("role", role)
    .withIssuedAt(Date())
    .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
    .sign(Algorithm.HMAC256(secret))
