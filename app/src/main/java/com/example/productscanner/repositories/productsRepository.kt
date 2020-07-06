package com.example.productscanner.repositories

import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApiService
import javax.inject.Inject

class ProductsRepository @Inject constructor() : IProductsRepository {

    @Inject lateinit var productsApiService: ProductsApiService

    override suspend fun getProducts(): Response<List<Product>?>{
        val response = productsApiService.getProducts()
        return if(response.isSuccessful){
            Response(response.body(), null)
        }else{
            Response(null, "The products couldn't be loaded")
        }
    }

    // Just for testing
    override fun addProducts(vararg products: Product){}
}

class Response<out T>(val body: T, val errorMessage: String?)