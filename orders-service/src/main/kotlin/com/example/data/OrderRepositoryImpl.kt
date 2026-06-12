package com.example.data

import com.example.model.Order
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class OrderRepositoryImpl : OrderRepository {

    override fun create(userId: Int, productId: Int, quantity: Int, totalPrice: Double): Order = transaction {
        val stmt = OrdersTable.insert {
            it[OrdersTable.userId]     = userId
            it[OrdersTable.productId] = productId
            it[OrdersTable.quantity]  = quantity
            it[OrdersTable.totalPrice] = totalPrice
            it[OrdersTable.status]    = "pending"
            it[OrdersTable.createdAt] = Instant.now().toString()
        }
        val newId = stmt[OrdersTable.id]
        OrdersTable.selectAll()
            .where { OrdersTable.id eq newId }
            .map { it.toOrder() }
            .first()
    }

    override fun findByUserId(userId: Int): List<Order> = transaction {
        OrdersTable.selectAll()
            .where { OrdersTable.userId eq userId }
            .orderBy(OrdersTable.createdAt, SortOrder.DESC)
            .map { it.toOrder() }
    }
}

private fun org.jetbrains.exposed.sql.ResultRow.toOrder() = Order(
    id         = this[OrdersTable.id],
    userId     = this[OrdersTable.userId],
    productId  = this[OrdersTable.productId],
    quantity   = this[OrdersTable.quantity],
    totalPrice = this[OrdersTable.totalPrice],
    status     = this[OrdersTable.status],
    createdAt  = this[OrdersTable.createdAt]
)
