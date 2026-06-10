package com.example.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object DatabaseFactory {

    fun init(dbPath: String) {
        Database.connect(
            url    = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        )
        transaction {
            SchemaUtils.create(UsersTable)
        }
        seedAdmin()
    }

    private fun seedAdmin() {
        val adminEmail    = System.getenv("SEED_ADMIN_EMAIL")    ?: "admin@ecommerce.com"
        val adminPassword = System.getenv("SEED_ADMIN_PASSWORD") ?: "admin123"

        transaction {
            val hasAdmin = UsersTable
                .selectAll()
                .where { UsersTable.role eq "admin" }
                .count() > 0

            if (!hasAdmin) {
                UsersTable.insert {
                    it[name]         = "Admin"
                    it[email]        = adminEmail
                    it[passwordHash] = BCrypt.hashpw(adminPassword, BCrypt.gensalt())
                    it[role]         = "admin"
                }
            }
        }
    }
}
