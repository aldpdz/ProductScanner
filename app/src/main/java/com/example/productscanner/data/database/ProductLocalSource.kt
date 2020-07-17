package com.example.productscanner.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.domain.asDatabaseProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.data.network.asDatabaseModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ProductLocalSource internal constructor(
    private val productDao: ProductDao,
    private val ioDispatcher: CoroutineDispatcher
) : IProductLocalSource{
    override suspend fun insertProducts(networkProducts: List<NetworkProduct>) = withContext(ioDispatcher){
        productDao.insertAll(networkProducts.asDatabaseModel())
    }

    override fun getProducts(): LiveData<Result<List<DomainProduct>>> {
        return productDao.getProducts().map {
            Result.Success(it.asDomainModel())
        }
    }

    override suspend fun updateProduct(product: DomainProduct) = withContext(ioDispatcher){
        productDao.updateProduct(product.asDatabaseProduct())
    }
}