package com.example.productscanner.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.productscanner.databinding.ProductListBinding
import com.example.productscanner.model.Product
import androidx.recyclerview.widget.ListAdapter

class ProductAdapter(val clickListener: OpenProductListener) :
    ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    class ViewHolder private constructor(val binding: ProductListBinding):
            RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product, clickListener: OpenProductListener){
            binding.product = product
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

class ProductDiffCallBack : DiffUtil.ItemCallback<Product>(){
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id && (oldItem.isSaved == newItem.isSaved)
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}

class OpenProductListener(val clickListener: (product: Product) -> Unit){
    fun onClick(product: Product){
        clickListener(product)
    }
}