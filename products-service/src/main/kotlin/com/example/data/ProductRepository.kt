package com.example.data

import com.example.model.CreateProductRequest
import com.example.model.Product

interface ProductRepository {
    fun findAll(): List<Product>
    fun findById(id: Int): Product?
    fun create(req: CreateProductRequest): Product
}
