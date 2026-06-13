package com.example

import com.example.registry.ServiceRegistry
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun main() {
    val port             = System.getenv("PORT")?.toInt() ?: 8080
    val jwtSecret        = System.getenv("JWT_SECRET") ?: "dev_secret_change_in_prod"
    val keystorePath     = System.getenv("KEYSTORE_PATH")
    val keystorePassword = System.getenv("KEYSTORE_PASSWORD") ?: "keystorepass"

    ServiceRegistry.init(
        usersUrl           = System.getenv("USERS_SERVICE_URL")        ?: "http://localhost:5001",
        productsPrimaryUrl = System.getenv("PRODUCTS_PRIMARY_URL")     ?: "http://localhost:5002",
        productsReplicaUrl = System.getenv("PRODUCTS_REPLICA_URL")     ?: "http://localhost:5012",
        ordersUrl          = System.getenv("ORDERS_SERVICE_URL")       ?: "http://localhost:5003"
    )

    val httpClient = buildHttpClient(keystorePath, keystorePassword)
    val module: Application.() -> Unit = { configureApp(jwtSecret, httpClient) }

    embeddedServer(Netty, port = port, module = module).start(wait = true)
}

private fun buildHttpClient(keystorePath: String?, keystorePassword: String): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) { json() }
        engine {
            requestTimeout = 10_000
            if (keystorePath != null) {
                https {
                    val ks = KeyStore.getInstance("JKS").apply {
                        FileInputStream(keystorePath).use { load(it, keystorePassword.toCharArray()) }
                    }
                    val tmf = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm()
                    ).apply { init(ks) }
                    trustManager = tmf.trustManagers[0] as X509TrustManager
                }
            }
        }
    }
}
