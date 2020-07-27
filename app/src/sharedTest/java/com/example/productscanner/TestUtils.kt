package com.example.productscanner

import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct

/***
 * Convert DomainProduct object NetworkProduct
 */
fun networkToDomain(product: NetworkProduct): DomainProduct{
    return DomainProduct(
        product.id, product.name, product.description,
        product.picture, product.sku, product.upc,
        product.quantity, product.price, false)
}