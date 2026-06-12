package com.example.feip_fefu_lab_clothing_store

import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import com.example.feip_fefu_lab_clothing_store.data.model.SizeDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductFilterLogicTest {

    private val products = listOf(
        product("p1", categoryId = "cat1", tags = emptyList()),
        product("p2", categoryId = "cat1", tags = listOf("New")),
        product("p3", categoryId = "cat2", tags = emptyList()),
        product("p4", categoryId = "cat3", tags = listOf("New", "Sale"))
    )

    @Test
    fun filterByCategory_returnsProductsWithMatchingCategory() {
        val filtered = products.filter { it.categoryId == "cat1" }
        assertEquals(2, filtered.size)
        assertEquals(setOf("p1", "p2"), filtered.map { it.id }.toSet())
    }

    @Test
    fun filterByNewTag_returnsProductsWithNewTag() {
        val filtered = products.filter { it.tags.contains("New") }
        assertEquals(2, filtered.size)
        assertEquals(setOf("p2", "p4"), filtered.map { it.id }.toSet())
    }

    private fun product(id: String, categoryId: String, tags: List<String>): ProductDto {
        return ProductDto(
            id = id,
            name = "Product $id",
            shortDescription = "",
            longDescription = "",
            priceInKopecks = 1000,
            imageUrl = "",
            tags = tags,
            categoryId = categoryId,
            sizes = emptyList(),
            material = "",
            weight = "",
            season = "",
            countryOfOrigin = ""
        )
    }
}