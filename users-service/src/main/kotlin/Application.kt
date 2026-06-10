package com.example

import io.ktor.server.application.*

fun Application.configureApp(jwtSecret: String, jwtExpirationMs: Long) {
    configureStatusPages()
    configureSerialization()
    configureRouting(jwtSecret, jwtExpirationMs)
}
