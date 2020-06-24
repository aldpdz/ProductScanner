package com.example.productscanner.repositories

import com.example.productscanner.model.Product

interface IProductsRepository {
    suspend fun getProducts(): Response<List<Product>?>
}