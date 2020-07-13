package com.example.productscanner.data.domain

import android.os.Parcelable
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