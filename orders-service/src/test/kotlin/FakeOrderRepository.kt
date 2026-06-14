package com.example

import com.example.data.OrderRepository
import com.example.model.Order

class FakeOrderRepository : OrderRepository {

    private val orders = mutableListOf<Order>()
    private var nextId = 1

    override fun create(userId: Int, productId: Int, quantity: Int, totalPrice: Double): Order {
        val order = Order(nextId++, userId, productId, quantity, totalPrice, "pending", "2026-01-01T00:00:00Z")
        orders.add(order)
        return order
    }

    override fun findAll(): List<Order> = orders.toList()

    override fun findByUserId(userId: Int): List<Order> = orders.filter { it.userId == userId }
}
