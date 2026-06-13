package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

data class JwtClaims(val userId: Int, val email: String, val role: String)

fun extractJwt(token: String, secret: String): JwtClaims? = try {
    val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)
    JwtClaims(
        userId = decoded.getClaim("userId").asInt(),
        email  = decoded.getClaim("email").asString(),
        role   = decoded.getClaim("role").asString()
    )
} catch (e: Exception) {
    null
}
