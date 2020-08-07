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
import java.lang.Exception

const val TEMP_ID = 0

class ProductLocalSource internal constructor(
    private val productDao: ProductDao,
    private val ioDispatcher: CoroutineDispatcher
) : IProductLocalSource{
    override suspend fun insertProducts(networkProducts: List<NetworkProduct>) = withContext(ioDispatcher){
        productDao.insertAll(networkProducts.asDatabaseModel())
    }

    override suspend fun insertTemp(product: DomainProduct) = withContext(ioDispatcher){
        product.id = TEMP_ID
        productDao.insert(product.asDatabaseProduct())
    }

    override fun getProducts(): LiveData<Result<List<DomainProduct>>> {
        return productDao.getProducts().map {
            Result.Success(it.asDomainModel())
        }
    }

    override suspend fun getTempProduct(): Result<DomainProduct> = withContext(ioDispatcher){
        val tempProduct = productDao.getProduct(TEMP_ID)
        return@withContext if(tempProduct != null){
            Result.Success(tempProduct.asDomainModel())
        }else{
            Result.Error(Exception("No temp product"))
        }
    }

    override suspend fun updateProduct(product: DomainProduct) = withContext(ioDispatcher){
        productDao.updateProduct(product.asDatabaseProduct())
    }

    override suspend fun getProductBySKU(sku: String): Result<DomainProduct> = withContext(ioDispatcher){
        val product = productDao.getProductBySKU(sku)
        return@withContext if(product != null){
            Result.Success(product.asDomainModel())
        }else{
            Result.Error(Exception("Product not found"))
        }
    }

    override suspend fun getProductByUPC(upc: String): Result<DomainProduct> = withContext(ioDispatcher){
        val product = productDao.getProductByUPC(upc)
        return@withContext if(product != null){
            Result.Success(product.asDomainModel())
        }else{
            Result.Error(Exception("Product not found"))
        }
    }
}