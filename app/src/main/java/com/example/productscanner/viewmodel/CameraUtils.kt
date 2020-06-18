package com.example.productscanner.viewmodel

private val regexOption: RegexOption = RegexOption.IGNORE_CASE
private val skuCodeRegex: Regex = "SKU-code_\\d\\d".toRegex(regexOption)

fun findSKUCode(texts: String):String?{

    val code = skuCodeRegex.find(texts)

    if(code != null){
        return code.value
    }else{
        return null
    }
}