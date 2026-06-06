package com.example.feip_fefu_lab_clothing_store.data.repository

import android.content.Context
import androidx.room.Room
import com.example.feip_fefu_lab_clothing_store.data.db.CartDatabase
import com.example.feip_fefu_lab_clothing_store.data.db.CartItemEntity
import com.example.feip_fefu_lab_clothing_store.data.model.CartItemUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class CartRepository(
    context: Context,
    private val productRepository: ProductRepository
) {
    // 1. Создаем отдельную базу данных для корзины в том же стиле
    private val db = Room.databaseBuilder(
        context.applicationContext,
        CartDatabase::class.java, "cart-db" // Название файла БД отличается!
    ).build()

    private val cartDao = db.cartDao()

    // 2. Сборка корзины: скрещиваем локальные id с данными из сети/кэша
    fun getCartUiItems(): Flow<List<CartItemUiModel>> {
        return combine(
            cartDao.getCartItems(),
            productRepository.getCatalogDataFlow()
        ) { cartEntities, catalogResult ->

            // Пытаемся достать данные каталога из Result
            val catalog = catalogResult.getOrNull() ?: return@combine emptyList()
            val products = catalog.items

            // Маппим сущности БД в красивые UI-модели
            cartEntities.mapNotNull { entity ->
                val product = products.find { it.id == entity.productId } ?: return@mapNotNull null
                val size = product.sizes.find { it.id == entity.sizeId } ?: return@mapNotNull null

                CartItemUiModel(
                    productId = entity.productId,
                    sizeId = entity.sizeId,
                    name = product.name,
                    sizeName = size.name,
                    priceInKopecks = product.priceInKopecks,
                    imageUrl = product.imageUrl,
                    quantity = entity.quantity
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    // 3. Методы управления корзиной
    suspend fun addToCart(productId: String, sizeId: String) {
        val existingItem = cartDao.getCartItem(productId, sizeId)
        if (existingItem != null) {
            cartDao.incrementQuantity(productId, sizeId, 1)
        } else {
            cartDao.insertItem(CartItemEntity(productId, sizeId, 1))
        }
    }

    suspend fun incrementItem(productId: String, sizeId: String) {
        cartDao.incrementQuantity(productId, sizeId, 1)
    }

    suspend fun decrementItem(productId: String, sizeId: String) {
        cartDao.decrementQuantity(productId, sizeId)
    }

    suspend fun removeItem(productId: String, sizeId: String) {
        cartDao.deleteItem(productId, sizeId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}