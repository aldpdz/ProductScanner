package com.example.productscanner.data.network

import android.util.Log
import com.example.productscanner.data.Result
import java.lang.Exception

class ProductRemoteSource internal constructor(
    private val productsApiService: ProductsApiService
) : IProductRemoteSource{
    override suspend fun getProducts(): Result<List<NetworkProduct>>? {
        val response = productsApiService.getProducts()
        return if(response.isSuccessful){
            Log.i("ProductRemoteSource", "Load remote data")
            response.body()?.let {
                Result.Success(it)
            }
        }else{
            Log.i("ProductRemoteSource", "Fail to load remote data")
            Result.Error(Exception())
        }

    }
}