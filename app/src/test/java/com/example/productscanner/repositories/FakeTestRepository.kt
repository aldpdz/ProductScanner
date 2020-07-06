package com.example.productscanner.repositories

import com.example.productscanner.model.Product

class FakeTestRepository: IProductsRepository {

    var productsServiceData: LinkedHashMap<String, Product> = LinkedHashMap()

    override suspend fun getProducts(): Response<List<Product>?> {
        return Response(productsServiceData.values.toList(), null)
    }

    override fun addProducts(vararg products: Product){
        for (product in products){
            productsServiceData[product.id.toString()] = product
        }
    }

    fun getListProducts(): List<Product>{
        return productsServiceData.values.toList()
    }
}