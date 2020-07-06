package com.example.productscanner.repositories

import androidx.annotation.VisibleForTesting
import com.example.productscanner.model.Product

interface IProductsRepository {
    suspend fun getProducts(): Response<List<Product>?>

    fun addProducts(vararg products: Product) // just for testing
}