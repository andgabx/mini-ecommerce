package com.example.monitor

import com.example.registry.ServiceRegistry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.time.Instant

private val log = LoggerFactory.getLogger("Heartbeat")

fun Application.startHeartbeat(httpClient: HttpClient) {
    launch(Dispatchers.IO) {
        while (isActive) {
            delay(5_000)
            ServiceRegistry.allServices.forEach { service ->
                val wasUp = service.isUp
                val healthy = try {
                    val response = httpClient.get("${service.url}/health")
                    response.status.value in 200..299
                } catch (e: Exception) {
                    false
                }

                if (healthy) {
                    if (!wasUp || service.consecutiveFailures > 0) {
                        log.info("[${Instant.now()}] RECOVERED: ${service.name} voltou a responder")
                    }
                    service.isUp = true
                    service.consecutiveFailures = 0
                } else {
                    service.consecutiveFailures++
                    log.warn("[${Instant.now()}] FALHA #${service.consecutiveFailures}: ${service.name} não respondeu")
                    if (service.consecutiveFailures >= 2 && wasUp) {
                        service.isUp = false
                        log.error("[${Instant.now()}] DOWN: ${service.name} marcado como indisponível")
                    }
                }
            }
        }
    }
}
