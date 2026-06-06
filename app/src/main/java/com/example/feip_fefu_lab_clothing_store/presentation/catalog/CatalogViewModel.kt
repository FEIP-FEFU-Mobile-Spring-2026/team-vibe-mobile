package com.example.feip_fefu_lab_clothing_store.presentation.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feip_fefu_lab_clothing_store.data.repository.CartRepository
import com.example.feip_fefu_lab_clothing_store.data.repository.ProductRepository
import com.example.feip_fefu_lab_clothing_store.data.model.CategoryDto
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _networkErrorEvent = MutableSharedFlow<Unit>()
    val networkErrorEvent: SharedFlow<Unit> = _networkErrorEvent.asSharedFlow()

    private val KEY_SELECTED_CATEGORY = "selected_category_id"
    private val KEY_SELECTED_PRODUCT_ID = "selected_product_id"

    private var allCategories: List<CategoryDto> = emptyList()
    private var allProducts: List<ProductDto> = emptyList()

    val selectedProductId: StateFlow<String?> = savedStateHandle.getStateFlow(KEY_SELECTED_PRODUCT_ID, null)

    init { loadCatalog() }

    fun isNetworkAvailable(): Boolean = repository.isNetworkAvailable()

    fun selectProduct(productId: String?) { savedStateHandle[KEY_SELECTED_PRODUCT_ID] = productId }

    fun loadCatalog() {
        viewModelScope.launch {
            if (_uiState.value !is CatalogUiState.Success) { _uiState.value = CatalogUiState.Loading }
            if (!repository.isNetworkAvailable()) { _networkErrorEvent.emit(Unit) }

            repository.getCatalogDataFlow().collect { result ->
                result.onSuccess { response ->
                    val newCategory = CategoryDto(id = "cat_new", name = "Новинки")
                    allCategories = listOf(newCategory) + response.categories
                    allProducts = response.items
                    val initialCategory = savedStateHandle.get<String>(KEY_SELECTED_CATEGORY) ?: "cat_new"
                    updateState(initialCategory)
                }.onFailure { e ->
                    if (_uiState.value !is CatalogUiState.Success) {
                        _uiState.value = CatalogUiState.Error("Ошибка загрузки. ${e.message}")
                    }
                }
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

    fun addProductToCart(productId: String, sizeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.addToCart(productId, sizeId)
        }
    }
}