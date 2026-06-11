package com.example.data

import org.jetbrains.exposed.sql.Table

object ProductsTable : Table("products") {
    val id          = integer("id").autoIncrement()
    val name        = varchar("name", 255)
    val brand       = varchar("brand", 100)
    val colorway    = varchar("colorway", 100)
    val description = text("description").nullable()
    val price       = double("price")
    val stock       = integer("stock").default(0)
    override val primaryKey = PrimaryKey(id)
}
