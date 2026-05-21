package com.example.feip_fefu_lab_clothing_store.data.repository

import android.content.Context
import com.example.feip_fefu_lab_clothing_store.data.model.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ProductRepository(private val context: Context) {
    
    private val jsonParser = Json { 
        ignoreUnknownKeys = true 
    }

    suspend fun getCatalogData(): ProductResponse = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("products.json").bufferedReader().use { it.readText() }
        jsonParser.decodeFromString<ProductResponse>(jsonString)
    }
}
