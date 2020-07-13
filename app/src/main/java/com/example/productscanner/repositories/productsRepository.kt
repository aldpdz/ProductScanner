package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.productscanner.data.database.ProductDao
import com.example.productscanner.data.database.Result
import com.example.productscanner.data.database.asDomainModel
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.data.network.ProductsApiService
import com.example.productscanner.data.network.asDatabaseModel
import javax.inject.Inject

class ProductsRepository @Inject constructor() : IProductsRepository {

    @Inject lateinit var productsApiService: ProductsApiService
    @Inject lateinit var productDao: ProductDao

    /***
     * Getting the data from the remote source and update local cache
     */
    override suspend fun getProductsFromRemote(): Result<String>{
        val response = productsApiService.getProducts()
        return if(response.isSuccessful){
            response.body()?.let{
                productDao.insertAll((it.asDatabaseModel()))
            }
            Result.Success("success")
        }else{
            Result.Error(Exception())
        }
    }

    override fun getProductsFromLocal() : LiveData<List<DomainProduct>>{
        return Transformations.map(productDao.getProducts()){
            it.asDomainModel()
        }
    }

    // Just for testing
    override fun addProducts(vararg networkProducts: NetworkProduct){}
}

class Response<out T>(val body: T, val errorMessage: String?)