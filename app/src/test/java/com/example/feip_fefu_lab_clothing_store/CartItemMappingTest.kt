package com.example.feip_fefu_lab_clothing_store

import com.example.feip_fefu_lab_clothing_store.data.db.CartItemEntity
import com.example.feip_fefu_lab_clothing_store.data.model.CartItemUiModel
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import com.example.feip_fefu_lab_clothing_store.data.model.SizeDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CartItemMappingTest {

    @Test
    fun cartItemEntityToUiModel_mapsCorrectly() {
        val entity = CartItemEntity(productId = "p1", sizeId = "s1", quantity = 2)
        val product = ProductDto(
            id = "p1",
            name = "Куртка",
            shortDescription = "",
            longDescription = "",
            priceInKopecks = 50000,
            imageUrl = "http://example.com/jacket.jpg",
            tags = emptyList(),
            categoryId = "cat1",
            sizes = listOf(SizeDto("s1", "M"), SizeDto("s2", "L")),
            material = "Кожа",
            weight = "1kg",
            season = "Осень",
            countryOfOrigin = "Россия"
        )
        val size = product.sizes.find { it.id == "s1" }!!

        val uiModel = CartItemUiModel(
            productId = entity.productId,
            sizeId = entity.sizeId,
            name = product.name,
            sizeName = size.name,
            priceInKopecks = product.priceInKopecks,
            imageUrl = product.imageUrl,
            quantity = entity.quantity
        )

        assertEquals("p1", uiModel.productId)
        assertEquals("s1", uiModel.sizeId)
        assertEquals("Куртка", uiModel.name)
        assertEquals("M", uiModel.sizeName)
        assertEquals(50000, uiModel.priceInKopecks)
        assertEquals(2, uiModel.quantity)
    }
}