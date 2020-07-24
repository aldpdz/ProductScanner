package com.example.productscanner

import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct

/***
 * Convert a list of DomainProducts to a list of NetworkProducts
 */
fun domainToNetwork(products: List<DomainProduct>): List<NetworkProduct>{
    val networkList = mutableListOf<NetworkProduct>()
    for(product in products){
        val networkProduct = NetworkProduct(
            product.id, product.name, product.description,
            product.picture, product.sku, product.upc,
            product.quantity, product.price)
        networkList.add(networkProduct)
    }
    return networkList
}