package com.example.feip_fefu_lab_clothing_store.presentation.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feip_fefu_lab_clothing_store.data.repository.ProductRepository
import com.example.feip_fefu_lab_clothing_store.data.model.CategoryDto
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val KEY_SELECTED_CATEGORY = "selected_category_id"
    
    private var allCategories: List<CategoryDto> = emptyList()
    private var allProducts: List<ProductDto> = emptyList()

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            try {
                val response = repository.getCatalogData()
                
                val newCategory = CategoryDto(id = "cat_new", name = "Новинки")
                allCategories = listOf(newCategory) + response.categories
                allProducts = response.items

                val initialCategory = savedStateHandle.get<String>(KEY_SELECTED_CATEGORY) ?: "cat_new"
                updateState(initialCategory)
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }
    }

    fun selectCategory(categoryId: String) {
        savedStateHandle[KEY_SELECTED_CATEGORY] = categoryId
        updateState(categoryId)
    }

    private fun updateState(categoryId: String) {
        val filteredProducts = if (categoryId == "cat_new") {
            allProducts.filter { it.tags.contains("New") }
        } else {
            allProducts.filter { it.categoryId == categoryId }
        }

        _uiState.value = CatalogUiState.Success(
            categories = allCategories,
            products = filteredProducts,
            selectedCategoryId = categoryId
        )
    }
}
