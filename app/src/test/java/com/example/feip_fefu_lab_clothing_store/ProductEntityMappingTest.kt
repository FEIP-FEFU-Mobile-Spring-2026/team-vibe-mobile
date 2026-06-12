package com.example.feip_fefu_lab_clothing_store

import com.example.feip_fefu_lab_clothing_store.data.db.ProductEntity
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import com.example.feip_fefu_lab_clothing_store.data.model.SizeDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductEntityMappingTest {

    @Test
    fun toDto_convertsEntityCorrectly() {
        val entity = ProductEntity(
            id = "p1",
            name = "Футболка",
            shortDescription = "Кратко",
            longDescription = "Длинно",
            priceInKopecks = 19900,
            imageUrl = "http://example.com/img.jpg",
            tags = """["New", "Sale"]""",
            categoryId = "cat1",
            sizes = """[{"id":"s1","name":"S"},{"id":"s2","name":"M"}]""",
            material = "Хлопок",
            weight = "200г",
            season = "Лето",
            countryOfOrigin = "Россия"
        )

        val dto = entity.toDto()

        assertEquals("p1", dto.id)
        assertEquals("Футболка", dto.name)
        assertEquals(19900, dto.priceInKopecks)
        assertEquals(listOf("New", "Sale"), dto.tags)
        assertEquals(listOf(SizeDto("s1", "S"), SizeDto("s2", "M")), dto.sizes)
        assertEquals("Хлопок", dto.material)
    }

    @Test
    fun fromDto_convertsDtoCorrectly() {
        val dto = ProductDto(
            id = "p2",
            name = "Штаны",
            shortDescription = "Описание",
            longDescription = "Длинное описание",
            priceInKopecks = 39900,
            imageUrl = "http://example.com/pants.jpg",
            tags = listOf("Classic"),
            categoryId = "cat2",
            sizes = listOf(SizeDto("s3", "L")),
            material = "Джинса",
            weight = "400г",
            season = "Демисезон",
            countryOfOrigin = "Китай"
        )

        val entity = ProductEntity.fromDto(dto)

        assertEquals("p2", entity.id)
        assertEquals("Штаны", entity.name)
        assertEquals(39900, entity.priceInKopecks)
        assertEquals("""["Classic"]""", entity.tags)
        assertEquals("""[{"id":"s3","name":"L"}]""", entity.sizes)
        assertEquals("Джинса", entity.material)
    }
}