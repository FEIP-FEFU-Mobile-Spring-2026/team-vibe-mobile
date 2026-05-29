package com.example.feip_fefu_lab_clothing_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feip_fefu_lab_clothing_store.data.repository.ProductRepository
import com.example.feip_fefu_lab_clothing_store.presentation.catalog.CatalogScreen
import com.example.feip_fefu_lab_clothing_store.presentation.catalog.CatalogViewModel
import com.example.feip_fefu_lab_clothing_store.presentation.navigation.MainScreenContainer
import com.example.feip_fefu_lab_clothing_store.ui.theme.FEIPFEFULABCLOTHINGSTORETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = ProductRepository(applicationContext)

        setContent {
            FEIPFEFULABCLOTHINGSTORETheme {
                val catalogViewModel: CatalogViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(
                            modelClass: Class<T>,
                            extras: CreationExtras
                        ): T {
                            val savedStateHandle = extras.createSavedStateHandle()
                            return CatalogViewModel(repository, savedStateHandle) as T
                        }
                    }
                )
                
                MainScreenContainer { paddingValues ->
                    Box(modifier = androidx.compose.ui.Modifier.padding(paddingValues)) {
                        CatalogScreen(viewModel = catalogViewModel)
                    }
                }
            }
        }
    }
}
