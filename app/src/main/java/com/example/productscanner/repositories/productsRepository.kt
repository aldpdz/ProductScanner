package com.example.productscanner.repositories

import android.util.Log
import androidx.lifecycle.Transformations
import com.example.productscanner.data.Result
import com.example.productscanner.data.database.*
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.IProductRemoteSource
import com.example.productscanner.data.network.asDatabaseModel

class ProductsRepository (
    private val productLocalSource: IProductLocalSource,
    private val productRemoteSource: IProductRemoteSource
) : IProductsRepository {

    /***
     * Getting the data from the remote source and update local cache
     */
    override suspend fun getProductsFromRemote(){
        val response = productRemoteSource.getProducts()
        if(response is Result.Success){
            Log.i("Repository", "Data loaded")
            saveProducts(response.data.asDatabaseModel())
        }else if (response is Result.Error){
            Log.i("Repository", "Failed to load data")
            throw response.exception
        }
    }

    /***
     * Save the products in the database
     */
    override suspend fun saveProducts(databaseProducts: List<DatabaseProduct>){
        productLocalSource.insertProducts(databaseProducts)
    }

    /***
     * Get the products from the local database
     */
    override fun getProductsFromLocal() = Transformations.map(productLocalSource.getProducts()) {
        it.asDomainModel()
    }

    // Just for testing
    override fun addProducts(vararg products: DomainProduct){}
}

class Response<out T>(val body: T, val errorMessage: String?)