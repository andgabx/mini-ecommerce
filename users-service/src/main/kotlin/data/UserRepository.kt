package com.example.data

import com.example.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserRepository {

    fun create(name: String, email: String, password: String): User? {
        if (findByEmail(email) != null) return null
        val hash = BCrypt.hashpw(password, BCrypt.gensalt())
        return transaction {
            val stmt = UsersTable.insert {
                it[UsersTable.name]         = name
                it[UsersTable.email]        = email
                it[UsersTable.passwordHash] = hash
                it[UsersTable.role]         = "user"
            }
            val newId = stmt[UsersTable.id]
            UsersTable.selectAll()
                .where { UsersTable.id eq newId }
                .map { it.toUser() }
                .first()
        }
    }

    fun findByEmail(email: String): ResultRow? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .firstOrNull()
    }

    fun findById(id: Int): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .firstOrNull()
    }

    fun validateCredentials(email: String, password: String): ResultRow? {
        val row = findByEmail(email) ?: return null
        return if (BCrypt.checkpw(password, row[UsersTable.passwordHash])) row else null
    }
}

fun ResultRow.toUser() = User(
    id    = this[UsersTable.id],
    name  = this[UsersTable.name],
    email = this[UsersTable.email],
    role  = this[UsersTable.role]
)
