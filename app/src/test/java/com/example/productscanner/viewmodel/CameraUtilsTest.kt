package com.example.productscanner.viewmodel

import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.core.Is.`is`

import org.junit.Assert.*
import org.junit.Test

class CameraUtilsTest{

    @Test
    fun findSKUCode_noSKU_resultNull(){
        // Given a text with multiple lines
        val text = "This is a sample text\n with no code in it\n it should return null."

        // When there are not sku codes
        val result = findSKUCode(text)

        // Then the function returns null
        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun funSKUCode_semiSKUCode_resultsNull(){
        // Given a string
        val text = "This is a string with sku-code_ no code."

        // When there's not a complete code
        val result = findSKUCode(text)

        // Then the results is null
        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun findSKUCode_oneSKU_resultSKUCode(){
        // Given a string with one word
        val text = "sku-code_01."

        // When there are is one word with just the code
        val result = findSKUCode(text)

        // Then the function returns sku-code_01
        assertThat(result, `is`("sku-code_01"))
    }

    @Test
    fun findSKUCode_multipleLinesOneCode_resultsSKUCode(){
        // Given a string with multiple lines
        val text = "This is a sample text\n with no code in it\n it should return null. " +
                "\nHere sku-code_01 the code."

        // When there are is one code
        val result = findSKUCode(text)

        // Then the function returns sku-code-01
        assertThat(result, `is`("sku-code_01"))
    }

    @Test
    fun findSKUCode_multipleLinesMultipleCodes_resultsFirstSKUCode(){
        // Given a string with multiple lines
        val text = "This is a text with a code sku-code_01.\n" +
                "This is another text with code sku-code_02."

        // When there are two codes
        val result = findSKUCode(text)

        // Then the result is the first found
        assertThat(result, `is`("sku-code_01"))
    }
}