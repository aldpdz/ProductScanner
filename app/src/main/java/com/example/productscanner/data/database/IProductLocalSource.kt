package com.example.productscanner.data.database

import androidx.lifecycle.LiveData
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct

interface IProductLocalSource {
    suspend fun insertProducts(networkProducts: List<NetworkProduct>)
    suspend fun insertTemp(product: DomainProduct)
    fun getProducts(): LiveData<Result<List<DomainProduct>>>
    suspend fun getTempProduct(): Result<DomainProduct>
    suspend fun updateProduct(product: DomainProduct)
    suspend fun getProductBySKU(sku: String): Result<DomainProduct>
    suspend fun getProductByUPC(upc: String): Result<DomainProduct>
}