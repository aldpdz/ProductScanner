package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import com.example.productscanner.data.database.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct

interface IProductsRepository {
    suspend fun getProductsFromRemote(): Result<String>

    fun getProductsFromLocal() : LiveData<List<DomainProduct>>

    fun addProducts(vararg networkProducts: NetworkProduct) // just for testing
}