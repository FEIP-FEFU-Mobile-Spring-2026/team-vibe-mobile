package com.example.feip_fefu_lab_clothing_store

import com.example.feip_fefu_lab_clothing_store.data.model.CategoryDto
import com.example.feip_fefu_lab_clothing_store.data.model.SizeDto
import org.junit.Assert.assertEquals
import org.junit.Test

class DtoCreationTest {

    @Test
    fun categoryDto_createsCorrectly() {
        val category = CategoryDto("cat1", "Одежда")
        assertEquals("cat1", category.id)
        assertEquals("Одежда", category.name)
    }

    @Test
    fun sizeDto_createsCorrectly() {
        val size = SizeDto("s1", "M")
        assertEquals("s1", size.id)
        assertEquals("M", size.name)
    }
}