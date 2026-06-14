package com.example.client

import com.example.model.ProductSummary
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlinx.serialization.json.Json

class ProductsClientImpl(
    private val baseUrl: String,
    keystorePath: String?,
    keystorePassword: String
) : ProductsClient {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
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

    override suspend fun getProduct(productId: Int): ProductSummary? = try {
        httpClient.get("$baseUrl/products/$productId").body()
    } catch (e: Exception) {
        null
    }
}
