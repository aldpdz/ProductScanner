package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct

interface IProductsRepository {

    suspend fun getProductsFromRemote()

    fun getProductsFromLocal() : LiveData<Result<List<DomainProduct>>>

    suspend fun insertTempProduct(product: DomainProduct)

    suspend fun saveProducts(networkProducts: List<NetworkProduct>)

    suspend fun updateProduct(product: DomainProduct)

    suspend fun findBySKU(sku: String): Result<DomainProduct>

    suspend fun findByUPC(upc: String): Result<DomainProduct>

    suspend fun revertProduct(id: Int)

    fun addProducts(vararg products: DomainProduct) // just for testing
}