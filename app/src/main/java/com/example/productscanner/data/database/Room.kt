package com.example.productscanner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseProduct::class], version = 1)
abstract class ProductsDatabase: RoomDatabase(){
    abstract val productDao: ProductDao
}

private lateinit var INSTANCE: ProductsDatabase