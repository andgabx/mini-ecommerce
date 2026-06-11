package com.example

import com.example.data.UserRepository
import io.ktor.server.application.*

fun Application.configureApp(jwtSecret: String, jwtExpirationMs: Long, repo: UserRepository) {
    configureStatusPages()
    configureSerialization()
    configureRouting(jwtSecret, jwtExpirationMs, repo)
}
