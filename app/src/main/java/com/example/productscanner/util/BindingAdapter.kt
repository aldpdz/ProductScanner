package com.example.productscanner.util

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.productscanner.R
import com.example.productscanner.viewmodel.ProductApiStatus

@BindingAdapter("picture")
fun bindImage(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("productApiStatus")
fun bindStatus(statusImageView: ImageView, status: ProductApiStatus?){
    when(status){
        ProductApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        else -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter( "productApiStatusProgress")
fun bindStatusProgress(statusProgressBar: ProgressBar, status: ProductApiStatus?){
    when(status){
        ProductApiStatus.LOADING -> {
            statusProgressBar.visibility = View.VISIBLE
        }
        else -> {
            statusProgressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("productApiStatusRecycler")
fun bindStatusRecycler(statusRecyclerView: RecyclerView, status: ProductApiStatus?){
    when(status){
        ProductApiStatus.DONE -> {
            statusRecyclerView.visibility = View.VISIBLE
        }
        else -> {
            statusRecyclerView.visibility = View.GONE
        }
    }
}