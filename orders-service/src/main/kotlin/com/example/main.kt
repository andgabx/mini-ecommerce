package com.example

import com.example.data.DatabaseFactory
import com.example.client.ProductsClientImpl
import com.example.data.OrderRepositoryImpl
import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.FileInputStream
import java.security.KeyStore

fun main() {
    val port                = System.getenv("PORT")?.toInt() ?: 5003
    val dbPath              = System.getenv("DB_PATH") ?: "orders.db"
    val productsPrimaryUrl  = System.getenv("PRODUCTS_PRIMARY_URL") ?: "http://localhost:5002"
    val keystorePath        = System.getenv("KEYSTORE_PATH")
    val keystorePassword    = System.getenv("KEYSTORE_PASSWORD") ?: "keystorepass"
    val keyAlias            = System.getenv("KEY_ALIAS") ?: "ecommerce"

    DatabaseFactory.init(dbPath)
    val repo = OrderRepositoryImpl()
    val productsClient = ProductsClientImpl(productsPrimaryUrl, keystorePath, keystorePassword)

    val module: Application.() -> Unit = { configureApp(repo, productsClient) }

    val server = if (keystorePath != null) {
        val keyStore = KeyStore.getInstance("JKS").apply {
            FileInputStream(keystorePath).use { load(it, keystorePassword.toCharArray()) }
        }
        embeddedServer(Netty, configure = {
            sslConnector(keyStore, keyAlias, { keystorePassword.toCharArray() }, { keystorePassword.toCharArray() }) {
                this.port = port
            }
        }, module = module)
    } else {
        embeddedServer(Netty, port = port, module = module)
    }

    server.start(wait = true)
}
