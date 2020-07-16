package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import com.example.productscanner.data.Result
import com.example.productscanner.data.database.DatabaseProduct
import com.example.productscanner.data.domain.DomainProduct

interface IProductsRepository {

    suspend fun getProductsFromRemote()

    suspend fun saveProducts(databaseProducts: List<DatabaseProduct>)

    fun getProductsFromLocal() : LiveData<Result<List<DomainProduct>>>

    fun addProducts(vararg products: DomainProduct) // just for testing
}