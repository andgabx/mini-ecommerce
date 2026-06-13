package com.example

import com.example.monitor.startHeartbeat
import com.example.registry.ServiceRegistry
import io.ktor.client.*
import io.ktor.server.application.*

fun Application.configureApp(jwtSecret: String, httpClient: HttpClient) {
    configureStatusPages()
    configureSerialization()
    startHeartbeat(httpClient)
    configureRouting(jwtSecret, httpClient)
}
