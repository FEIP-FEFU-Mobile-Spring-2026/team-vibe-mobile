package com.example.feip_fefu_lab_clothing_store.presentation.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(viewModel: CatalogViewModel) {
    val state by viewModel.uiState.collectAsState()
    val selectedProductId by viewModel.selectedProductId.collectAsState()
    val selectedProduct = (state as? CatalogUiState.Success)?.products?.find { it.id == selectedProductId }

    if (selectedProduct != null) {
        ProductDetailScreen(
            product = selectedProduct,
            onBackClick = { viewModel.selectProduct(null) }
        )
    } else {
        Scaffold(
            topBar = { CenterAlignedTopAppBar(title = { Text("Каталог") }) }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (val currentState = state) {
                    is CatalogUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is CatalogUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Ошибка: ${currentState.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadCatalog() }) {
                            Text("Повторить")
                        }
                    }
                }
                    is CatalogUiState.Success -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            ScrollableTabRow(
                                selectedTabIndex = currentState.categories.indexOfFirst { it.id == currentState.selectedCategoryId }.coerceAtLeast(0),
                                indicator = {},
                                divider = {},
                                edgePadding = 16.dp
                            ) {
                                currentState.categories.forEach { category ->
                                    val isSelected = category.id == currentState.selectedCategoryId
                                    Tab(
                                        selected = isSelected,
                                        onClick = { viewModel.selectCategory(category.id) },
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) Color(0xFF6D4C41) else Color(0xFFF5F5F5)),
                                        text = { Text(category.name, color = if (isSelected) Color.White else Color.Black) }
                                    )
                                }
                            }
                            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(currentState.products) { product ->
                                    ProductRow(product = product, onClick = { viewModel.selectProduct(product.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductRow(product: ProductDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = product.shortDescription, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(color = Color(0xFFF4EFEF), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = formatPrice(product.priceInKopecks),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D4C41)
                    )
                }
            }
        }
    }
}

fun formatPrice(kopecks: Long): String {
    val rubles = kopecks / 100.0
    return String.format(Locale.forLanguageTag("ru-RU"), "%,.0f", rubles).replace(",", " ") + " ₽"
}
