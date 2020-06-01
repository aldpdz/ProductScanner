package com.example.productscanner.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProductsService {
    private val BASE_URL = "https://raw.githubusercontent.com"

    fun getProductsService() : ProductsApi{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductsApi::class.java)
    }
}