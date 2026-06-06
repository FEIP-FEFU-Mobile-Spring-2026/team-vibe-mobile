package com.example.feip_fefu_lab_clothing_store.presentation.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import coil3.compose.AsyncImage
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import java.util.Locale

@Composable
fun ProductDetailScreen(
    product: ProductDto,
    onClose: () -> Unit,
    savedStateHandle: SavedStateHandle,
    onAddToCartClick: (sizeId: String) -> Unit
) {
    val KEY_SELECTED_SIZE = "selected_size_${product.id}"
    var selectedSizeId by remember {
        mutableStateOf(savedStateHandle.get<String>(KEY_SELECTED_SIZE) ?: product.sizes.firstOrNull()?.id)
    }

    var showInfoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. Картинка и Теги ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )

                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.tags.forEach { tag ->
                        Surface(
                            color = Color(0xFFA67B67),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = tag.uppercase(),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { showInfoDialog = true },
                        shape = CircleShape,
                        color = Color(0xFFF7EFEA)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "i",
                                color = Color(0xFFA67B67),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.longDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

            if (product.sizes.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    product.sizes.forEach { sizeDto ->
                        val isSelected = sizeDto.id == selectedSizeId

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    selectedSizeId = sizeDto.id
                                    savedStateHandle[KEY_SELECTED_SIZE] = sizeDto.id
                                },
                            color = if (isSelected) Color(0xFF5A3B2C) else Color(0xFFF5F5F5)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = sizeDto.name,
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedSizeId?.let { sizeId ->
                        onAddToCartClick(sizeId)
                    }
                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA67B67)) // Цвет кнопки как на макете
            ) {
                Text(
                    text = "В корзину · ${formatPrice(product.priceInKopecks)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(text = "Характеристики") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Материал: ${product.material ?: "Не указано"}")
                    Text("Вес: ${product.weight ?: "Не указано"}")
                    Text("Сезон: ${product.season ?: "Не указано"}")
                    Text("Страна: ${product.countryOfOrigin ?: "Не указано"}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("OK", color = Color(0xFFA67B67))
                }
            }
        )
    }
}
