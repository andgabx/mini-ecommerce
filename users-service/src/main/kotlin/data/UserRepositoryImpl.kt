package com.example.data

import com.example.model.LoginData
import com.example.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserRepositoryImpl : UserRepository {

    override fun create(name: String, email: String, password: String): User? {
        if (findByEmailRow(email) != null) return null
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

    override fun findById(id: Int): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .firstOrNull()
    }

    override fun validateCredentials(email: String, password: String): LoginData? {
        val row = findByEmailRow(email) ?: return null
        if (!BCrypt.checkpw(password, row[UsersTable.passwordHash])) return null
        return LoginData(
            userId = row[UsersTable.id],
            email  = row[UsersTable.email],
            role   = row[UsersTable.role]
        )
    }

    private fun findByEmailRow(email: String): ResultRow? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .firstOrNull()
    }
}

private fun ResultRow.toUser() = User(
    id    = this[UsersTable.id],
    name  = this[UsersTable.name],
    email = this[UsersTable.email],
    role  = this[UsersTable.role]
)
