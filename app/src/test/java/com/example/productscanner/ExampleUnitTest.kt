package com.example.productscanner

import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun findSKUCode(){
        val sufix = "SKU-"
        val text = "klajflakjfds\nalksjflkasjfasdkf\nfklajflkasjdf\nfjklajfas SKU-code_01 khjk\nlafjsalkfj"
        val lines = text.split("\n")
        var code: String? =null
        for (line in lines){
            val words = line.split(" ")
            for (word in words){
                if (word.contains(sufix))
                    code = word
            }
        }

        assertEquals("SKU-code_01", code)
    }
}
