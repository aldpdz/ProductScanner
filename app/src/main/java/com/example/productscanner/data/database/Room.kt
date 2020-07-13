package com.example.productscanner.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DatabaseProduct::class], version = 1)
abstract class ProductsDatabase: RoomDatabase(){
    abstract val productDao: ProductDao
}
