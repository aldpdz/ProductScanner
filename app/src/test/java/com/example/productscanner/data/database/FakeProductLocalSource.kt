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
    override suspend fun insertProducts(networkProducts: List<NetworkProduct>) {
        val listProducts = networkProducts.asDatabaseModel()
        for (databaseProduct in listProducts){
            products[databaseProduct.id] = databaseProduct
        }
    }

    override fun getProducts(): LiveData<Result<List<DomainProduct>>> {
        val liveData = MutableLiveData<Result<List<DomainProduct>>>()
        val result = Result.Success(products.values.toList().asDomainModel())
        liveData.value = result
        return liveData
    }

    override suspend fun updateProduct(product: DomainProduct) {
        products[product.id] = product.asDatabaseProduct()
    }
}