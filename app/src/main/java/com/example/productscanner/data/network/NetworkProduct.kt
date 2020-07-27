package com.example.productscanner.data.network

import com.example.productscanner.data.database.DatabaseProduct
import com.google.gson.annotations.SerializedName

data class NetworkProduct(
    @SerializedName("ID")
    val id: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("Picture")
    val picture: String,
    @SerializedName("SKU")
    val sku: String,
    @SerializedName("UPC")
    val upc: String,
    @SerializedName("Quantity")
    var quantity: Int,
    @SerializedName("Price")
    var price: Float
)

fun List<NetworkProduct>.asDatabaseModel(): List<DatabaseProduct>{
    return map{
        DatabaseProduct(
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
