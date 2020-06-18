package com.example.productscanner.viewmodel

private val regexOption: RegexOption = RegexOption.IGNORE_CASE
private val skuCodeRegex: Regex = "SKU-code_\\d\\d".toRegex(regexOption)

fun findSKUCode(texts: String):String?{

    return skuCodeRegex.find(texts)?.value
}