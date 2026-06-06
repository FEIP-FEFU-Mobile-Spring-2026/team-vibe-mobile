package com.example.feip_fefu_lab_clothing_store.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val categories: List<CategoryDto>,
    val items: List<ProductDto>
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String
)

@Serializable
data class SizeDto(
    val id: String,
    val name: String
)

@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Long,
    val imageUrl: String,
    val tags: List<String> = emptyList(),
    val categoryId: String,

    val sizes: List<SizeDto> = emptyList(),
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String
)

data class CartItemUiModel(
    val productId: String,
    val sizeId: String,
    val name: String,
    val sizeName: String,
    val priceInKopecks: Long,
    val imageUrl: String,
    val quantity: Int
)