package com.example.productscanner.repositories

import android.util.Log
import com.example.productscanner.data.Result
import com.example.productscanner.data.database.*
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.IProductRemoteSource
import com.example.productscanner.data.network.NetworkProduct

class ProductsRepository (
    private val productLocalSource: IProductLocalSource,
    private val productRemoteSource: IProductRemoteSource
) : IProductsRepository {

    /***
     * Getting the data from the remote source and update local cache
     */
    override suspend fun getProductsFromRemote(){
        val response = productRemoteSource.getProducts()
        if(response is Result.Success){
            Log.i("Repository", "Data loaded")
            saveProducts(response.data)
        }else if (response is Result.Error){
            Log.i("Repository", "Failed to load data")
            throw response.exception
        }
    }

    /***
     * Save the products in the database
     */
    override suspend fun saveProducts(networkProducts: List<NetworkProduct>){
        productLocalSource.insertProducts(networkProducts)
    }

    /***
     * Get the products from the local database
     */
    override fun getProductsFromLocal() = productLocalSource.getProducts()

    override suspend fun insertTempProduct(product: DomainProduct) {
        productLocalSource.insertTemp(product)
    }

    override suspend fun updateProduct(product: DomainProduct) {
        productLocalSource.updateProduct(product)
    }

    override suspend fun findBySKU(sku: String): Result<DomainProduct> {
        return productLocalSource.getProductBySKU(sku)
    }

    override suspend fun findByUPC(upc: String): Result<DomainProduct> {
        return productLocalSource.getProductByUPC(upc)
    }

    override suspend fun revertProduct(id: Int) {
        // old product saved as a temp product
        val tempProduct = productLocalSource.getTempProduct()
        if(tempProduct is Result.Success){
            // revert to previous values
            val product = tempProduct.data
            product.id = id
            productLocalSource.updateProduct(product)
        }
    }

    // Just for testing
    override fun addProducts(vararg products: DomainProduct){}
}