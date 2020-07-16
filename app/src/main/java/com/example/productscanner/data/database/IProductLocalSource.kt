package com.example.productscanner.data.database

import androidx.lifecycle.LiveData
import com.example.productscanner.data.Result

interface IProductLocalSource {
    suspend fun insertProducts(databaseProducts: List<DatabaseProduct>)
    fun getProducts(): LiveData<Result<List<DatabaseProduct>>>
}