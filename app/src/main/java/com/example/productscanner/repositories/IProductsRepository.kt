package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import com.example.productscanner.data.database.Result
import com.example.productscanner.data.domain.DomainProduct

interface IProductsRepository {
    val products: LiveData<List<DomainProduct>>

    suspend fun getProductsFromRemote(): Result<String>

    fun getProductsFromLocal() : LiveData<List<DomainProduct>>

    fun addProducts(vararg products: DomainProduct) // just for testing
}