package com.example.productscanner.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.util.Event
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.product_bottom_sheet.*

// Better approach to show this information would be by using a layout instead of a fragment
// there's no need to handel configuration changes.
class ProductBottomDialogFragment(private val isPaused: MutableLiveData<Event<Boolean>>,
                                  private val product: DomainProduct,
                                  private val productEvent: MutableLiveData<DomainProduct>)
    : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setData()
        fab_bs.setOnClickListener {
            productEvent.value = product
        }
    }

    override fun onPause() {
        super.onPause()
        isPaused.value = Event(true)
    }

    private fun setData(){
        tv_name_bs.text = product.name
        tv_price_bs.text = getString(R.string.money_symbol).plus(product.price.toString())
        tv_quantity_bs.text = product.quantity.toString()

        val imgUri = product.picture.toUri().buildUpon().scheme("https").build()
        Glide.with(image_view_bs.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_img)
                .error(R.drawable.ic_broken_image))
            .into(image_view_bs)
    }
}