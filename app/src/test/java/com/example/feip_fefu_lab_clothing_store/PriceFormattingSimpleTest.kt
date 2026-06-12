package com.example.feip_fefu_lab_clothing_store

import com.example.feip_fefu_lab_clothing_store.presentation.catalog.formatPrice
import org.junit.Assert.assertTrue
import org.junit.Test

class PriceFormattingSimpleTest {

    @Test
    fun formatPrice_containsRubleSymbol() {
        assertTrue(formatPrice(12345).contains("₽"))
    }

    @Test
    fun formatPrice_nonNegativePrice() {
        val result = formatPrice(99900)
        val digits = result.replace(Regex("[^0-9]"), "").toLongOrNull()
        assertTrue(digits != null && digits > 0)
    }
}