package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct

interface IProductsRepository {

    suspend fun getProductsFromRemote()

    suspend fun saveProducts(networkProducts: List<NetworkProduct>)

    fun getProductsFromLocal() : LiveData<Result<List<DomainProduct>>>

    suspend fun updateProduct(product: DomainProduct)

    suspend fun findBySKU(sku: String): Result<DomainProduct>

    suspend fun findByUPC(upc: String): Result<DomainProduct>

    fun addProducts(vararg products: DomainProduct) // just for testing
}