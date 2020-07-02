package com.example.productscanner

import android.app.Application
import com.example.productscanner.repositories.IProductsRepository

// TODO maybe not necessary with dp
class ScannerProductApplication : Application() {
    val productsRepository: IProductsRepository
        get() = ServiceLocator.provideProductRepository()
}