package com.example.data

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id           = integer("id").autoIncrement()
    val name         = varchar("name", 255)
    val email        = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role         = varchar("role", 50).default("user")
    override val primaryKey = PrimaryKey(id)
}
