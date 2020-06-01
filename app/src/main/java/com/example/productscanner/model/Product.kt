package com.example.productscanner.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
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
    val upc: Long,
    @SerializedName("Quantity")
    val quantity: Int
) : Parcelable