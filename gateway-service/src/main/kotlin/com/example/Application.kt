package com.example

import io.ktor.server.application.*

fun Application.configureApp() {
    configureStatusPages()
    configureSerialization()
    configureRouting()
}
