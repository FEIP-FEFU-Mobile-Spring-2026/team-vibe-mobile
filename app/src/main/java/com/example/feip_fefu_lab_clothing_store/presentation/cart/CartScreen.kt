package com.example.feip_fefu_lab_clothing_store.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.feip_fefu_lab_clothing_store.data.model.CartItemUiModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: CartViewModel, onNavigateToCatalog: () -> Unit) {
    val cartItems by viewModel.cartItems.collectAsState()

    // Подсчет итоговой суммы
    val totalPrice = cartItems.sumOf { it.priceInKopecks * it.quantity }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Корзина", fontWeight = FontWeight.Bold) },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.showClearDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Очистить корзину", tint = Color.Gray)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (cartItems.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ваша корзина пуста", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Список товаров
                    items(cartItems, key = { "${it.productId}_${it.sizeId}" }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrement = { viewModel.incrementItem(item.productId, item.sizeId) },
                            onDecrement = { viewModel.decrementItem(item.productId, item.sizeId) },
                            onRemove = { viewModel.removeItem(item.productId, item.sizeId) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // 2. Форма оформления заказа
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = viewModel.name,
                                onValueChange = { viewModel.name = it },
                                label = { Text("Имя*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedContainerColor = Color(0xFFF5F5F5)
                                )
                            )

                            OutlinedTextField(
                                value = viewModel.email,
                                onValueChange = { viewModel.email = it },
                                label = { Text("Почта*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedContainerColor = Color(0xFFF5F5F5)
                                ),
                                isError = viewModel.email.isNotEmpty() && !viewModel.email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex())
                            )

                            OutlinedTextField(
                                value = viewModel.comment,
                                onValueChange = { viewModel.comment = it },
                                label = { Text("Комментарий к заказу") },
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedContainerColor = Color(0xFFF5F5F5)
                                )
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    // 3. Итого и кнопка
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Итого", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                text = formatPrice(totalPrice),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.checkout() },
                            enabled = viewModel.isCheckoutValid,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA67B67),
                                disabledContainerColor = Color(0xFFA67B67).copy(alpha = 0.5f)
                            )
                        ) {
                            Text("Оформить", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // Диалог очистки корзины
    if (viewModel.showClearDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showClearDialog = false },
            title = { Text("Очистить корзину?") },
            text = { Text("Все добавленные товары будут удалены.") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearCart() }) {
                    Text("Очистить", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showClearDialog = false }) {
                    Text("Отмена", color = Color.Black)
                }
            }
        )
    }

    // Шторка успешного оформления заказа
    if (viewModel.showSuccessSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.showSuccessSheet = false
                onNavigateToCatalog()
            },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart, // Или используйте иконку успешной сумки
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Заказ успешно оформлен", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Подтверждение и чек отправили на вашу почту",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        viewModel.showSuccessSheet = false
                        onNavigateToCatalog()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA67B67))
                ) {
                    Text("Вернуться на главную")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemUiModel,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF5F5F5))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить",
                    modifier = Modifier.size(20.dp).clickable { onRemove() },
                    tint = Color.Gray
                )
            }
            Text("Размер: ${item.sizeName}", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatPrice(item.priceInKopecks * item.quantity),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA67B67)
                )

                // Контролы количества (+ / -)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "—",
                        modifier = Modifier.clickable { onDecrement() }.padding(horizontal = 8.dp),
                        color = if (item.quantity > 1) Color.Black else Color.LightGray
                    )
                    Text(text = item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                    Text(
                        text = "+",
                        modifier = Modifier.clickable { onIncrement() }.padding(horizontal = 8.dp),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private fun formatPrice(kopecks: Long): String {
    val rubles = kopecks / 100.0
    return String.format(Locale.forLanguageTag("ru-RU"), "%,.0f", rubles).replace(",", " ") + " ₽"
}