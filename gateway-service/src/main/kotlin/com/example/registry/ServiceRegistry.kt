package com.example.registry

import java.util.concurrent.atomic.AtomicInteger

class ServiceStatus(val name: String, val url: String) {
    @Volatile var isUp: Boolean = true
    @Volatile var consecutiveFailures: Int = 0
}

object ServiceRegistry {

    lateinit var usersService: ServiceStatus
    lateinit var productsPrimary: ServiceStatus
    lateinit var productsReplica: ServiceStatus
    lateinit var ordersService: ServiceStatus

    private val readIndex = AtomicInteger(0)

    fun init(
        usersUrl: String,
        productsPrimaryUrl: String,
        productsReplicaUrl: String,
        ordersUrl: String
    ) {
        usersService    = ServiceStatus("users-service",     usersUrl)
        productsPrimary = ServiceStatus("products-primary", productsPrimaryUrl)
        productsReplica = ServiceStatus("products-replica", productsReplicaUrl)
        ordersService   = ServiceStatus("orders-service",   ordersUrl)
    }

    val allServices get() = listOf(usersService, productsPrimary, productsReplica, ordersService)

    fun nextProductReadService(): ServiceStatus {
        val candidates = listOf(productsPrimary, productsReplica).filter { it.isUp }
        if (candidates.isEmpty()) return productsPrimary
        return candidates[readIndex.getAndIncrement() % candidates.size]
    }
}
