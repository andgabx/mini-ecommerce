package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 5002
    embeddedServer(Netty, port = port, module = Application::configureApp).start(wait = true)
}
