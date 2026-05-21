package com.example.feip_fefu_lab_clothing_store.presentation.catalog

import com.example.feip_fefu_lab_clothing_store.data.model.CategoryDto
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto

sealed interface CatalogUiState {
    object Loading : CatalogUiState
    data class Success(
        val categories: List<CategoryDto>,
        val products: List<ProductDto>,
        val selectedCategoryId: String
    ) : CatalogUiState
    data class Error(val message: String) : CatalogUiState
}
