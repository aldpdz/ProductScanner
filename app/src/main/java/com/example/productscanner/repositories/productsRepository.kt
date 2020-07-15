package com.example.productscanner.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.productscanner.data.database.ProductDao
import com.example.productscanner.data.database.Result
import com.example.productscanner.data.database.asDomainModel
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.ProductsApiService
import com.example.productscanner.data.network.asDatabaseModel

class ProductsRepository (
    private val productsApiService: ProductsApiService,
    private val productDao: ProductDao
) : IProductsRepository {

//    @Inject lateinit var productsApiService: ProductsApiService
//    @Inject lateinit var productDao: ProductDao

    override val products: LiveData<List<DomainProduct>> =
        // Convert one LiveData object into another LiveData
        Transformations.map(productDao.getProducts()){
            it.asDomainModel()
        }

    /***
     * Getting the data from the remote source and update local cache
     */
    override suspend fun getProductsFromRemote(): Result<String>{
        val response = productsApiService.getProducts()
        return if(response.isSuccessful){
            Log.i("Repository", "Data loaded")
            response.body()?.let{
                productDao.insertAll(it.asDatabaseModel())
            }
            Result.Success("success")
        }else{
            Log.i("Repository", "Failed to load data")
            Result.Error(Exception())
        }
    }

    override fun getProductsFromLocal() = products

    // Just for testing
    override fun addProducts(vararg products: DomainProduct){}
}

class Response<out T>(val body: T, val errorMessage: String?)