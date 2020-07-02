package com.example.productscanner

import androidx.annotation.VisibleForTesting
import com.example.productscanner.model.ProductsApi
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.repositories.ProductsRepository
// TODO replace with dependency injection
// This is a singleton, it has the possibility of being accidentally shared between tests
// You can't run test in parallel
object ServiceLocator{
    @Volatile
    var productRepository: IProductsRepository? = null
        @VisibleForTesting set // the setter is public because of testing

    private val lock = Any()

    fun provideProductRepository(): IProductsRepository{
        synchronized(this){
            return productRepository ?: ProductsRepository(ProductsApi.retrofitService)
        }
    }

    /***
     * To avoid share the ServiceLocator between test, this method is used to reset the repository
     */
    @VisibleForTesting
    fun resetRepository(){
        synchronized(lock){
            productRepository = null
        }
    }
}