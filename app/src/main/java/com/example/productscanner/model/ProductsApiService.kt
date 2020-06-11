package com.example.productscanner.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val BASE_URL = "https://raw.githubusercontent.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ProductsApiService{
    @GET("aldpdz/productScannerData/master/products.json")
    suspend fun getProducts(): Response<List<Product>>
}

object ProductsApi{
    val retrofitService: ProductsApiService by lazy {
        retrofit.create(ProductsApiService::class.java)
    }
}