package com.example.data

import com.example.model.Product
import com.example.model.CreateProductRequest
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ProductRepository {

    fun findAll(): List<Product> = transaction {
        ProductsTable.selectAll().map { it.toProduct() }
    }

    fun findById(id: Int): Product? = transaction {
        ProductsTable.selectAll()
            .where { ProductsTable.id eq id }
            .map { it.toProduct() }
            .firstOrNull()
    }

    fun create(req: CreateProductRequest): Product = transaction {
        val stmt = ProductsTable.insert {
            it[name]        = req.name
            it[brand]       = req.brand
            it[colorway]    = req.colorway
            it[description] = req.description
            it[price]       = req.price
            it[stock]       = req.stock
        }
        val newId = stmt[ProductsTable.id]
        ProductsTable.selectAll()
            .where { ProductsTable.id eq newId }
            .map { it.toProduct() }
            .first()
    }
}

fun ResultRow.toProduct() = Product(
    id          = this[ProductsTable.id],
    name        = this[ProductsTable.name],
    brand       = this[ProductsTable.brand],
    colorway    = this[ProductsTable.colorway],
    description = this[ProductsTable.description],
    price       = this[ProductsTable.price],
    stock       = this[ProductsTable.stock]
)
