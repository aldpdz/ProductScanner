package com.example.productscanner.model

import retrofit2.Response
import retrofit2.http.GET

interface ProductsApi {
    @GET("aldpdz/productScannerData/master/products.json")
    suspend fun getProducts(): Response<List<Product>>
}