package com.example.productscanner.viewmodel

private val regexOption: RegexOption = RegexOption.IGNORE_CASE
private val skuCodeRegex: Regex = "SKU-code_\\d\\d".toRegex(regexOption)

/***
 * Find the first instance of a skuCode
 */
fun findSKUCode(texts: String):String?{
    return skuCodeRegex.find(texts)?.value
}