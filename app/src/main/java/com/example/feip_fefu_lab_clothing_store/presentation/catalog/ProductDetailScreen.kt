package com.example.feip_fefu_lab_clothing_store.presentation.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductDto,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Товар") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Изображение
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            )

            // Контент
            Column(modifier = Modifier.padding(16.dp)) {
                if (product.tags.contains("New")) {
                    Surface(
                        color = Color(0xFFC49A88),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "NEW",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = product.longDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // Footer с размерами и ценой
            Column(modifier = Modifier.padding(16.dp)) {
                // Размеры
                val sizes = listOf("XXS", "XS", "S", "M", "L", "XL")
                var selectedSize by remember { mutableStateOf(sizes[0]) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    sizes.forEach { size ->
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { selectedSize = size },
                            shape = RoundedCornerShape(8.dp),
                            color = if (size == selectedSize) Color(0xFF6D4C41) else Color(0xFFF5F5F5)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(size, color = if (size == selectedSize) Color.White else Color.Black)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка
                Button(
                    onClick = { /* Add to cart */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C41))
                ) {
                    Text("В корзину · ${formatPrice(product.priceInKopecks)}")
                }
            }
        }
    }
}
