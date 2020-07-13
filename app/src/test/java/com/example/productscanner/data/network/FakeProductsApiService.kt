package com.example.productscanner.data.network

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class FakeProductsApiService(var networkProducts: MutableList<NetworkProduct>? = mutableListOf()) :
    ProductsApiService {
    // TODO consider using a mock for retrofit
    override suspend fun getProducts(): Response<List<NetworkProduct>> {
        networkProducts?.let { return Response.success(it) }
        val responseBody: ResponseBody = ResponseBody.create(MediaType.get("response"), "content")
        return Response.error(200, responseBody)
    }
}