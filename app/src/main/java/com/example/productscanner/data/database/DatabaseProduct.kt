package com.example.productscanner.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct

@Entity(tableName = "products")
data class DatabaseProduct constructor(
    @PrimaryKey
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

fun DatabaseProduct.asDomainModel(): DomainProduct{
    return let {
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

fun Result<List<DatabaseProduct>>.asDomainModel(): Result<List<DomainProduct>>{
    val content = let { result ->
        (result as Result.Success).data.asDomainModel()
    }
    return Result.Success(content)
}
