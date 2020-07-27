package com.example.productscanner.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.productscanner.databinding.ProductListBinding
import androidx.recyclerview.widget.ListAdapter
import com.example.productscanner.data.domain.DomainProduct

class ProductAdapter(val clickListener: OpenProductListener) :
    ListAdapter<DomainProduct, ProductAdapter.ViewHolder>(ProductDiffCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    class ViewHolder private constructor(val binding: ProductListBinding):
            RecyclerView.ViewHolder(binding.root){
        fun bind(domainProduct: DomainProduct, clickListener: OpenProductListener){
            binding.product = domainProduct
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ProductListBinding.inflate(
                    layoutInflater,
                    parent,
                    false)
                return ViewHolder(binding)
            }
        }
    }
}

class ProductDiffCallBack : DiffUtil.ItemCallback<DomainProduct>(){
    override fun areItemsTheSame(oldItem: DomainProduct, newItem: DomainProduct): Boolean {
        return oldItem.id == newItem.id && (oldItem.isSaved == newItem.isSaved)
    }

    override fun areContentsTheSame(oldItem: DomainProduct, newItem: DomainProduct): Boolean {
        return oldItem == newItem
    }
}

class OpenProductListener(val clickListener: (domainProduct: DomainProduct) -> Unit){
    fun onClick(domainProduct: DomainProduct){
        clickListener(domainProduct)
    }
}