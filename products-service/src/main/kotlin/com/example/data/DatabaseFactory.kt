package com.example.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(dbPath: String) {
        Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(ProductsTable)
            seedProducts()
        }
    }

    private fun seedProducts() {
        if (ProductsTable.selectAll().count() > 0) return

        data class Sneaker(val name: String, val brand: String, val colorway: String, val description: String, val price: Double, val stock: Int)

        listOf(
            Sneaker("Air Jordan 1 Retro High OG", "Jordan", "Chicago", "O clássico que definiu uma era. Couro premium, corte alto.", 2800.0, 5),
            Sneaker("Nike SB Dunk Low", "Nike", "Staple NYC Pigeon", "Colaboração icônica com Jeff Staple. Uma das mais raras da história.", 4500.0, 2),
            Sneaker("Yeezy Boost 350 V2", "Adidas", "Zebra", "Primeknit respirável com Boost no solado. Conforto e estilo.", 3200.0, 8),
            Sneaker("New Balance 550", "New Balance", "White Green", "Silhueta retrô que conquistou as ruas. Perfeito para qualquer outfit.", 1400.0, 12),
            Sneaker("Air Force 1 Low '07", "Nike", "Triple White", "O tênis mais vendido de todos os tempos. Versátil e intemporal.", 900.0, 20),
            Sneaker("Nike Dunk High", "Nike", "Varsity Maize", "Colorway universitário clássico. Alto, bold e cheio de história.", 1600.0, 7),
            Sneaker("New Balance 9060", "New Balance", "Sea Salt", "Chunky dad shoe com amortecimento ABZORB. O queridinho do momento.", 1250.0, 15)
        ).forEach { s ->
            ProductsTable.insert {
                it[name]        = s.name
                it[brand]       = s.brand
                it[colorway]    = s.colorway
                it[description] = s.description
                it[price]       = s.price
                it[stock]       = s.stock
            }
        }
    }
}
