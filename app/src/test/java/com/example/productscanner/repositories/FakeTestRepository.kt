package com.example.productscanner.repositories

import com.example.productscanner.data.network.Product

class FakeTestRepository(): IProductsRepository {

    var productsServiceData: LinkedHashMap<String, Product>? = LinkedHashMap()
    var error: String? = null

    override suspend fun getProducts(): Response<List<Product>?> {
        return if (error != null) {
            Response(null, error)
        }else{
            Response(productsServiceData?.values?.toList(), null)
        }
    }

    override fun addProducts(vararg products: Product){
        for (product in products){
            productsServiceData?.set(product.id.toString(), product)
        }
    }

    fun getListProducts(): List<Product>?{
        return productsServiceData?.values?.toList()
    }
}