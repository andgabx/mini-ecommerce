package com.example

import com.example.data.DatabaseFactory
import com.example.data.ProductRepository
import com.example.replication.ReplicationClient
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.FileInputStream
import java.security.KeyStore

fun main() {
    val port             = System.getenv("PORT")?.toInt() ?: 5002
    val serviceRole      = System.getenv("SERVICE_ROLE") ?: "primary"
    val dbPath           = System.getenv("DB_PATH") ?: "products.db"
    val replicaUrl       = System.getenv("REPLICA_URL")
    val keystorePath     = System.getenv("KEYSTORE_PATH")
    val keystorePassword = System.getenv("KEYSTORE_PASSWORD") ?: "keystorepass"
    val keyAlias         = System.getenv("KEY_ALIAS") ?: "ecommerce"

    DatabaseFactory.init(dbPath)
    val repo = ProductRepository()
    val replicationClient = if (serviceRole == "primary" && replicaUrl != null) {
        ReplicationClient(replicaUrl, keystorePath, keystorePassword)
    } else null

    val module: Application.() -> Unit = { configureApp(repo, replicationClient, serviceRole) }

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
