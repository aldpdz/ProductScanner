package com.example.productscanner.util

import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.repositories.FakeTestRepository

suspend fun FakeTestRepository.basicStart(networkProducts: List<NetworkProduct>){
    saveProducts(networkProducts)
    getProductsFromRemote()
}