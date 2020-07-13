package com.example.productscanner.di
import com.example.productscanner.data.network.ProductsApiService
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.repositories.ProductsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://raw.githubusercontent.com"


// Tell in which containers the bindings are available
@InstallIn(ApplicationComponent::class)
@Module
object ProductsApiServiceModule{
    @Singleton
    @Provides
    fun provideProductsApiService(): ProductsApiService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ProductsApiService::class.java)
    }
}

@InstallIn(ApplicationComponent::class)
@Module
abstract class ProductsRepositoryModule{
    @Singleton
    @Binds
    abstract fun bindProductsRepository(productsRepository: ProductsRepository): IProductsRepository
}