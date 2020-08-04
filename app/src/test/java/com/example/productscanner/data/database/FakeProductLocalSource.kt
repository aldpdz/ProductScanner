package com.example.productscanner.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.domain.asDatabaseProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.data.network.asDatabaseModel

class FakeProductLocalSource :
    IProductLocalSource{
    private var products = hashMapOf<Int, DatabaseProduct>()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }

    override suspend fun insertProducts(networkProducts: List<NetworkProduct>) {
        val listProducts = networkProducts.asDatabaseModel()
        for (databaseProduct in listProducts){
            products[databaseProduct.id] = databaseProduct
        }
    }

    override suspend fun insertTemp(product: DomainProduct) {
        products[-1] = product.asDatabaseProduct()
    }

    override fun getProducts(): LiveData<Result<List<DomainProduct>>> {
        val liveData = MutableLiveData<Result<List<DomainProduct>>>()
        val result = Result.Success(products.values.toList().asDomainModel())
        liveData.value = result
        return liveData
    }

    override suspend fun getTempProduct(): Result<DomainProduct> {
        val tempProduct = products[-1]
        return if(tempProduct != null){
            Result.Success(tempProduct.asDomainModel())
        }else{
            Result.Error(Exception("Product not found"))
        }
    }

    override suspend fun updateProduct(product: DomainProduct) {
        products[product.id] = product.asDatabaseProduct()
    }

    override suspend fun getProductBySKU(sku: String): Result<DomainProduct> {
        return if (shouldReturnError){
            Result.Error(Exception("Product not found"))
        }else{
            Result.Success(products.values.first{it.sku == sku}.asDomainModel())
        }
    }

    override suspend fun getProductByUPC(upc: String): Result<DomainProduct> {
        return if (shouldReturnError){
            Result.Error(Exception("Product not found"))
        }else{
            Result.Success(products.values.first{it.upc == upc}.asDomainModel())
        }
    }
}