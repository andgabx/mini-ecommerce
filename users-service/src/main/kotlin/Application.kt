package com.example

import io.ktor.server.application.Application

fun Application.rootModule() {
    configureStatusPages()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
