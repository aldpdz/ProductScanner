package com.example.productscanner.data.network

import javax.inject.Inject
import com.example.productscanner.repositories.IProductsRepository

class FakeAndroidTestRepository @Inject constructor(): IProductsRepository {

    var productsServiceData: LinkedHashMap<String, NetworkProduct> = LinkedHashMap()

    override suspend fun getProductsFromRemote(): com.example.productscanner.repositories.Response<List<NetworkProduct>?> {
        return com.example.productscanner.repositories.Response(
            productsServiceData.values.toList(),
            null
        )
    }

    override fun addProducts(vararg networkProducts: NetworkProduct){
        for (product in networkProducts){
            productsServiceData[product.id.toString()] = product
        }
    }

    fun getListProducts(): List<NetworkProduct>{
        return productsServiceData.values.toList()
    }
}