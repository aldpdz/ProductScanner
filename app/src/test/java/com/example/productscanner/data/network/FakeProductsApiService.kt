package com.example.productscanner.data.network

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class FakeProductsApiService(var networkProducts: MutableList<NetworkProduct> = mutableListOf()) :
    ProductsApiService {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }

    // TODO consider using a mock for retrofit
    override suspend fun getProducts(): Response<List<NetworkProduct>> {
        return if(shouldReturnError){
            Response.error(400, ResponseBody.create(MediaType.parse("application/json"), "{}"))
        }else{
            Response.success(networkProducts)
        }
    }
}