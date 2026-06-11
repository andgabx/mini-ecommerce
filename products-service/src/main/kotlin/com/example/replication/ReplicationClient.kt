package com.example.replication

import com.example.model.CreateProductRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class ReplicationClient(
    private val replicaUrl: String,
    keystorePath: String?,
    keystorePassword: String
) {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
        if (keystorePath != null) {
            engine {
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

    suspend fun replicate(req: CreateProductRequest): Boolean = try {
        val response = httpClient.post("$replicaUrl/internal/replicate") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }
        response.status.isSuccess()
    } catch (e: Exception) {
        false
    }
}
