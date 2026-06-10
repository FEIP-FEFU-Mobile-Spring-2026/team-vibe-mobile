package com.example.feip_fefu_lab_clothing_store.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items", primaryKeys = ["productId", "sizeId"])
data class CartItemEntity(
    val productId: String,
    val sizeId: String,
    val quantity: Int
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND sizeId = :sizeId")
    suspend fun getCartItem(productId: String, sizeId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = quantity + :amount WHERE productId = :productId AND sizeId = :sizeId")
    suspend fun incrementQuantity(productId: String, sizeId: String, amount: Int = 1)

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE productId = :productId AND sizeId = :sizeId AND quantity > 1")
    suspend fun decrementQuantity(productId: String, sizeId: String)

    @Query("DELETE FROM cart_items WHERE productId = :productId AND sizeId = :sizeId")
    suspend fun deleteItem(productId: String, sizeId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}