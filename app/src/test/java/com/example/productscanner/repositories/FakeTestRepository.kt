package com.example.productscanner.repositories

import com.example.productscanner.data.network.NetworkProduct

class FakeTestRepository(): IProductsRepository {

    var productsServiceData: LinkedHashMap<String, NetworkProduct>? = LinkedHashMap()
    var error: String? = null

    override suspend fun getProductsFromRemote(): Response<List<NetworkProduct>?> {
        return if (error != null) {
            Response(null, error)
        }else{
            Response(productsServiceData?.values?.toList(), null)
        }
    }

    override fun addProducts(vararg networkProducts: NetworkProduct){
        for (product in networkProducts){
            productsServiceData?.set(product.id.toString(), product)
        }
    }

    fun getListProducts(): List<NetworkProduct>?{
        return productsServiceData?.values?.toList()
    }
}