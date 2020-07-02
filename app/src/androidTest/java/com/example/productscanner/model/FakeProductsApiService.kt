package com.example.productscanner.model

import com.example.productscanner.repositories.IProductsRepository

class FakeAndroidTestRepository: IProductsRepository {

    var productsServiceData: LinkedHashMap<String, Product> = LinkedHashMap()

    override suspend fun getProducts(): com.example.productscanner.repositories.Response<List<Product>?> {
        return com.example.productscanner.repositories.Response(
            productsServiceData.values.toList(),
            null
        )
    }

    fun addProducts(vararg products: Product){
        for (product in products){
            productsServiceData[product.id.toString()] = product
        }
    }

    fun getListProducts(): List<Product>{
        return productsServiceData.values.toList()
    }
}