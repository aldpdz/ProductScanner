package com.example.productscanner.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {

    /***
     * Observes list of products
     * @return all products
     */
    @Query("SELECT * from products")
    fun getProducts(): LiveData<List<DatabaseProduct>>

    /***
     * Get the product by the sku code
     */
    @Query("SELECT * from products WHERE sku = :sku")
    suspend fun getProductBySKU(sku: String): DatabaseProduct?

    /***
     * Get the product by the upc code
     */
    @Query("SELECT * from products WHERE upc = :upc")
    suspend fun getProductByUPC(upc: String): DatabaseProduct?

    /***
     * Insert a list of products. If the product already exists, replace it
     * @param products the list of products to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Overwrite if already exists
    suspend fun insertAll(products: List<DatabaseProduct>)

    /***
     * Update a product
     * @param product product to be updated
     */
    @Update
    suspend fun updateProduct(product: DatabaseProduct)
}