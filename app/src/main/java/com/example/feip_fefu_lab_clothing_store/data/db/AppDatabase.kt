package com.example.feip_fefu_lab_clothing_store.data.db

import androidx.room.*
import com.example.feip_fefu_lab_clothing_store.data.model.CategoryDto
import com.example.feip_fefu_lab_clothing_store.data.model.ProductDto
import com.example.feip_fefu_lab_clothing_store.data.model.SizeDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Long,
    val imageUrl: String,
    val tags: String, // Stored as JSON
    val categoryId: String,
    val sizes: String, // Stored as JSON
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String
) {
    fun toDto() = ProductDto(
        id = id,
        name = name,
        shortDescription = shortDescription,
        longDescription = longDescription,
        priceInKopecks = priceInKopecks,
        imageUrl = imageUrl,
        tags = Json.decodeFromString<List<String>>(tags),
        categoryId = categoryId,
        sizes = Json.decodeFromString<List<SizeDto>>(sizes),
        material = material,
        weight = weight,
        season = season,
        countryOfOrigin = countryOfOrigin
    )
    
    companion object {
        fun fromDto(dto: ProductDto) = ProductEntity(
            id = dto.id,
            name = dto.name,
            shortDescription = dto.shortDescription,
            longDescription = dto.longDescription,
            priceInKopecks = dto.priceInKopecks,
            imageUrl = dto.imageUrl,
            tags = Json.encodeToString(dto.tags),
            categoryId = dto.categoryId,
            sizes = Json.encodeToString(dto.sizes),
            material = dto.material,
            weight = dto.weight,
            season = dto.season,
            countryOfOrigin = dto.countryOfOrigin
        )
    }
}

@Dao
interface CatalogDao {
    @Query("SELECT * FROM categories")
    suspend fun getCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM products")
    suspend fun getProducts(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
    
    @Query("DELETE FROM categories")
    suspend fun clearCategories()
    
    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Transaction
    suspend fun updateCatalog(categories: List<CategoryEntity>, products: List<ProductEntity>) {
        clearCategories()
        clearProducts()
        insertCategories(categories)
        insertProducts(products)
    }
}

@Database(entities = [CategoryEntity::class, ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catalogDao(): CatalogDao
}
