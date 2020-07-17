package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct

interface IProductsRepository {

    suspend fun getProductsFromRemote()

    suspend fun saveProducts(products: List<NetworkProduct>)

    fun getProductsFromLocal() : LiveData<Result<List<DomainProduct>>>

    suspend fun updateProduct(product: DomainProduct)

    fun addProducts(vararg products: DomainProduct) // just for testing
}