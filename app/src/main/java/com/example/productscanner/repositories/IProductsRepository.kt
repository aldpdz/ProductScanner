package com.example.productscanner.repositories

import com.example.productscanner.data.network.Product

interface IProductsRepository {
    suspend fun getProducts(): Response<List<Product>?>

    fun addProducts(vararg products: Product) // just for testing
}