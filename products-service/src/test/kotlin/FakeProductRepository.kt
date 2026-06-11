package com.example

import com.example.data.ProductRepository
import com.example.model.CreateProductRequest
import com.example.model.Product

class FakeProductRepository : ProductRepository {

    private val products = mutableListOf<Product>()
    private var nextId = 1

    fun addProduct(product: Product) {
        products.add(product)
        if (product.id >= nextId) nextId = product.id + 1
    }

    override fun findAll(): List<Product> = products.toList()

    override fun findById(id: Int): Product? = products.find { it.id == id }

    override fun create(req: CreateProductRequest): Product {
        val product = Product(nextId++, req.name, req.brand, req.colorway, req.description, req.price, req.stock)
        products.add(product)
        return product
    }
}
