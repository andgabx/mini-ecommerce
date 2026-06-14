package com.example.data

import com.example.model.Order

interface OrderRepository {
    fun create(userId: Int, productId: Int, quantity: Int, totalPrice: Double): Order
    fun findByUserId(userId: Int): List<Order>
    fun findAll(): List<Order>
}
