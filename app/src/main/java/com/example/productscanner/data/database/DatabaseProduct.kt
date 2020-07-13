package com.example.productscanner.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.productscanner.data.domain.DomainProduct

@Entity(tableName = "products")
data class DatabaseProduct constructor(
    val id: Int,
    val name: String,
    val description: String,
    val picture: String,
    val sku: String,
    val upc: String,
    val quantity: Int,
    val price: Float
)

fun List<DatabaseProduct>.asDomainModel(): List<DomainProduct>{
    return map{
        DomainProduct(
            id = it.id,
            name = it.name,
            description = it.description,
            picture = it.picture,
            sku = it.sku,
            upc = it.upc,
            quantity = it.quantity,
            price = it.price
        )
    }
}
