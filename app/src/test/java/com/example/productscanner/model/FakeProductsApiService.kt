package com.example.productscanner.model

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class FakeProductsApiService(var products: MutableList<Product>? = mutableListOf()) : ProductsApiService {
    // TODO consider using a mock for retrofit
    override suspend fun getProducts(): Response<List<Product>> {
        products?.let { return Response.success(it) }
        val responseBody: ResponseBody = ResponseBody.create(MediaType.get("response"), "content")
        return Response.error(200, responseBody)
    }
}