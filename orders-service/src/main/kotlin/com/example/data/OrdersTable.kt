package com.example.data

import org.jetbrains.exposed.sql.Table

object OrdersTable : Table("orders") {
    val id         = integer("id").autoIncrement()
    val userId     = integer("user_id")
    val productId  = integer("product_id")
    val quantity   = integer("quantity")
    val totalPrice = double("total_price")
    val status     = varchar("status", 50).default("pending")
    val createdAt  = varchar("created_at", 50)
    override val primaryKey = PrimaryKey(id)
}
