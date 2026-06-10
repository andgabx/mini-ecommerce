package com.example

import com.example.data.DatabaseFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.FileInputStream
import java.security.KeyStore

fun main() {
    val port             = System.getenv("PORT")?.toInt() ?: 5001
    val jwtSecret        = System.getenv("JWT_SECRET") ?: "dev_secret_change_in_prod"
    val jwtExpirationMs  = System.getenv("JWT_EXPIRATION_MS")?.toLong() ?: 86_400_000L
    val dbPath           = System.getenv("DB_PATH") ?: "users.db"
    val keystorePath     = System.getenv("KEYSTORE_PATH")
    val keystorePassword = System.getenv("KEYSTORE_PASSWORD") ?: "keystorepass"
    val keyAlias         = System.getenv("KEY_ALIAS") ?: "ecommerce"

    DatabaseFactory.init(dbPath)

    val module: Application.() -> Unit = { configureApp(jwtSecret, jwtExpirationMs) }

    val server = if (keystorePath != null) {
        val keyStore = KeyStore.getInstance("JKS").apply {
            FileInputStream(keystorePath).use { load(it, keystorePassword.toCharArray()) }
        }
        embeddedServer(Netty, configure = {
            sslConnector(
                keyStore           = keyStore,
                keyAlias           = keyAlias,
                keyStorePassword   = { keystorePassword.toCharArray() },
                privateKeyPassword = { keystorePassword.toCharArray() }
            ) { this.port = port }
        }, module = module)
    } else {
        embeddedServer(Netty, port = port, module = module)
    }

    server.start(wait = true)
}
