package com.example.productscanner.repositories

import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApiService

// TODO Use constructor dependency injection
// TODO Replace with a dp library
class ProductsRepository (private val productsApiService: ProductsApiService) :
    IProductsRepository {
    override suspend fun getProducts(): Response<List<Product>?>{
        val response = productsApiService.getProducts()
        return if(response.isSuccessful){
            Response(response.body(), null)
        }else{
            Response(null, "The products couldn't be loaded")
        }
    }
}

class Response<out T>(val body: T, val errorMessage: String?)