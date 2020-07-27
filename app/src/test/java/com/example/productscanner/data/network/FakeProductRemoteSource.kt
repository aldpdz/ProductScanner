package com.example.productscanner.data.network

import com.example.productscanner.data.Result
import java.lang.Exception

class FakeProductRemoteSource(val products: List<NetworkProduct>) :
    IProductRemoteSource{

    private var shouldReturnError = false

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }

    override suspend fun getProducts(): Result<List<NetworkProduct>>? {
        if(shouldReturnError){
            return Result.Error(Exception())
        }
        return Result.Success(products)
    }
}