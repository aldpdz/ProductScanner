package com.example.productscanner.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.productscanner.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ProductLocalSource internal constructor(
    private val productDao: ProductDao,
    private val ioDispatcher: CoroutineDispatcher
) : IProductLocalSource{
    override suspend fun insertProducts(products: List<DatabaseProduct>) = withContext(ioDispatcher){
        productDao.insertAll(products)
    }

    override fun getProducts(): LiveData<Result<List<DatabaseProduct>>> {
        return productDao.getProducts().map {
            Result.Success(it)
        }
    }
}