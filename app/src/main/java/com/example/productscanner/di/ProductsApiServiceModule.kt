package com.example.productscanner.di
import android.content.Context
import androidx.room.Room
import com.example.productscanner.data.database.IProductLocalSource
import com.example.productscanner.data.database.ProductDao
import com.example.productscanner.data.database.ProductLocalSource
import com.example.productscanner.data.database.ProductsDatabase
import com.example.productscanner.data.network.IProductRemoteSource
import com.example.productscanner.data.network.ProductRemoteSource
import com.example.productscanner.data.network.ProductsApiService
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.repositories.ProductsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    fun provideProductLocalSource(productsDao: ProductDao,
                                  ioDispatcher: CoroutineDispatcher) : IProductLocalSource{
        return ProductLocalSource(productsDao, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideProductRemoteSource(productsApiService: ProductsApiService) : IProductRemoteSource{
        return ProductRemoteSource(productsApiService)
    }

    @Singleton
    @Provides
    fun provideProductsApiService(): ProductsApiService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ProductsApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): ProductsDatabase{
        return Room.databaseBuilder(
            appContext.applicationContext,
            ProductsDatabase::class.java,
            "products_scanner"
        ).build()
    }

    @Provides
    fun provideProductDao(database: ProductsDatabase): ProductDao{
        return database.productDao
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

@Module
@InstallIn(ApplicationComponent::class)
object ProductsRepositoryModule{

    @Singleton
    @Provides
    fun provideProductRepository(productLocalSource: IProductLocalSource,
                                 productRemoteSource: IProductRemoteSource): IProductsRepository{
        return ProductsRepository(productLocalSource, productRemoteSource)
    }
}