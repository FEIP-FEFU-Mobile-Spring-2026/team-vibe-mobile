package com.example.feip_fefu_lab_clothing_store.presentation.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feip_fefu_lab_clothing_store.data.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    val cartItems = cartRepository.getCartUiItems()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var comment by mutableStateOf("")

    var showClearDialog by mutableStateOf(false)
    var showSuccessSheet by mutableStateOf(false)

    private val nameRegex = "^[a-zA-Zа-яА-ЯёЁ\\s'-]+$".toRegex()
    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    private val isNameValid: Boolean
        get() = name.isNotBlank() && name.matches(nameRegex)

    val isEmailValid: Boolean
        get() = email.matches(emailRegex)

    val isCheckoutValid: Boolean
        get() = isNameValid && isEmailValid && cartItems.value.isNotEmpty()

    fun incrementItem(productId: String, sizeId: String) = viewModelScope.launch {
        cartRepository.incrementItem(productId, sizeId)
    }

    fun decrementItem(productId: String, sizeId: String) = viewModelScope.launch {
        cartRepository.decrementItem(productId, sizeId)
    }

    fun removeItem(productId: String, sizeId: String) = viewModelScope.launch {
        cartRepository.removeItem(productId, sizeId)
    }

    fun clearCart() = viewModelScope.launch {
        cartRepository.clearCart()
        showClearDialog = false
    }

    fun checkout() = viewModelScope.launch {
        if (isCheckoutValid) {
            cartRepository.clearCart()
            name = ""
            email = ""
            comment = ""
            showSuccessSheet = true
        }
    }
}