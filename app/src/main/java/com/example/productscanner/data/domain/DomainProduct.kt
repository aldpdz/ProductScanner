package com.example.productscanner.data.domain

import android.os.Parcelable
import com.example.productscanner.data.database.DatabaseProduct
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomainProduct(
    val id: Int,
    val name: String,
    val description: String,
    val picture: String,
    val sku: String,
    val upc: String,
    val quantity: Int,
    val price: Float,
    var isSaved: Boolean = false
) : Parcelable

fun DomainProduct.asDatabaseProduct(): DatabaseProduct{
    return let {
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