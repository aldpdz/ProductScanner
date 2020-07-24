package com.example.productscanner.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.productscanner.data.Result
import com.example.productscanner.data.database.DatabaseProduct
import com.example.productscanner.data.database.asDomainModel
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.domain.asDatabaseProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.data.network.asDatabaseModel
import javax.inject.Inject

class FakeTestRepository: IProductsRepository {
    private var networkProducts: MutableList<NetworkProduct> = ArrayList()
    private var databaseProducts: MutableList<DatabaseProduct> = ArrayList()
    private var shouldReturnError = false
    private var _liveDataDBProducts = MutableLiveData<Result<List<DomainProduct>>>()
    private val liveDataDBProduct : LiveData<Result<List<DomainProduct>>> get() = _liveDataDBProducts

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }

    override suspend fun getProductsFromRemote(){
        if(shouldReturnError){
            setLocalData()
            throw Exception()
        }
        databaseProducts = networkProducts.asDatabaseModel().toMutableList()
        setLocalData()
    }

    override suspend fun saveProducts(networkProducts: List<NetworkProduct>) {
        this.networkProducts = networkProducts.toMutableList()
    }

    override fun getProductsFromLocal(): LiveData<Result<List<DomainProduct>>> {
        return liveDataDBProduct
    }

    override suspend fun updateProduct(product: DomainProduct) {
        for((index, databaseProduct) in databaseProducts.withIndex()){
            if(databaseProduct.id == product.id){
                databaseProducts[index] = product.asDatabaseProduct()
                setLocalData()
            }
        }
    }

    private fun setLocalData(){
        val result = Result.Success(databaseProducts.asDomainModel())
        _liveDataDBProducts.value = result
    }

    override fun addProducts(vararg products: DomainProduct){}
}