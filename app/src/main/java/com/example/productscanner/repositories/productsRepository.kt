package com.example.productscanner.repositories

import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApi

class ProductsRepository {
    suspend fun getProducts(): Response<List<Product>?>{
        val response = ProductsApi.retrofitService.getProducts()
        return if(response.isSuccessful){
            Response(response.body(), null)
        }else{
            Response(null, "The products couldn't be loaded")
        }
    }
}

class Response<out T>(val body: T, val errorMessage: String?)