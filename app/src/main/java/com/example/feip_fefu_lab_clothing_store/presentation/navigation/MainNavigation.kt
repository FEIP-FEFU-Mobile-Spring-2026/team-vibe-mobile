package com.example.feip_fefu_lab_clothing_store.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feip_fefu_lab_clothing_store.presentation.cart.CartScreen

@Composable
fun MainScreenContainer(content: @Composable (PaddingValues) -> Unit) {
    var selectedRoute by remember { mutableStateOf("catalog") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Меню") },
                    label = { Text("Меню") },
                    selected = selectedRoute == "catalog",
                    onClick = { selectedRoute = "catalog" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Корзина") },
                    label = { Text("Корзина") },
                    selected = selectedRoute == "cart",
                    onClick = { selectedRoute = "cart" }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedRoute) {
                "catalog" -> content(PaddingValues(0.dp))
                "cart" -> CartScreen()
            }
        }
    }
}
