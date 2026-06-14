package com.example.data

import com.example.model.LoginData
import com.example.model.User

interface UserRepository {
    fun create(name: String, email: String, password: String): User?
    fun findById(id: Int): User?
    fun findAll(): List<User>
    fun validateCredentials(email: String, password: String): LoginData?
}
