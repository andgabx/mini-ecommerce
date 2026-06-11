package com.example

import com.example.data.UserRepository
import com.example.model.LoginData
import com.example.model.User

class FakeUserRepository : UserRepository {

    private data class Record(val user: User, val password: String)

    private val records = mutableListOf<Record>()
    private var nextId = 1

    fun addUser(user: User, password: String) {
        records.add(Record(user, password))
        if (user.id >= nextId) nextId = user.id + 1
    }

    override fun create(name: String, email: String, password: String): User? {
        if (records.any { it.user.email == email }) return null
        val user = User(nextId++, name, email, "user")
        records.add(Record(user, password))
        return user
    }

    override fun findById(id: Int): User? =
        records.find { it.user.id == id }?.user

    override fun validateCredentials(email: String, password: String): LoginData? {
        val record = records.find { it.user.email == email && it.password == password } ?: return null
        return LoginData(record.user.id, record.user.email, record.user.role)
    }
}
