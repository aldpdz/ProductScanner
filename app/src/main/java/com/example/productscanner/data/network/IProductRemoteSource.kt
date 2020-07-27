package com.example.productscanner.data.network

import com.example.productscanner.data.Result

interface IProductRemoteSource {
    suspend fun getProducts(): Result<List<NetworkProduct>>?
}