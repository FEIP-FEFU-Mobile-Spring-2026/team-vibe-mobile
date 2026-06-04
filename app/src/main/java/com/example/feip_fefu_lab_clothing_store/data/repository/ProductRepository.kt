package com.example.feip_fefu_lab_clothing_store.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.example.feip_fefu_lab_clothing_store.data.db.AppDatabase
import com.example.feip_fefu_lab_clothing_store.data.db.CategoryEntity
import com.example.feip_fefu_lab_clothing_store.data.db.ProductEntity
import com.example.feip_fefu_lab_clothing_store.data.model.CategoryDto
import com.example.feip_fefu_lab_clothing_store.data.model.ProductResponse
import com.example.feip_fefu_lab_clothing_store.data.network.CatalogApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import androidx.room.withTransaction

class ProductRepository(private val context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "catalog-db"
    ).build()

    private val api: CatalogApi by lazy {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val json = Json { ignoreUnknownKeys = true }
        
        Retrofit.Builder()
            .baseUrl("https://fefu2026spring.deploy.feip.dev/")
            //.client(client)
            .validateEagerly(true)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CatalogApi::class.java)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getCatalogDataFlow(): Flow<Result<ProductResponse>> = flow {
        val dao = db.catalogDao()
        val cachedCategories = dao.getCategories().map { CategoryDto(id = it.id, name = it.name) }
        val cachedProducts = dao.getProducts().map { it.toDto() }

        if (cachedCategories.isNotEmpty() && cachedProducts.isNotEmpty()) {
            emit(Result.success(ProductResponse(cachedCategories, cachedProducts)))
        }

        if (isNetworkAvailable()) {
            try {
                val response = api.getCatalog()
                val categoryEntities = response.categories.map { CategoryEntity(id = it.id, name = it.name) }
                val productEntities = response.items.map { ProductEntity.fromDto(it) }
                dao.updateCatalog(categoryEntities, productEntities)
                emit(Result.success(response))
            } catch (e: Exception) {
                if (cachedCategories.isEmpty()) {
                    emit(Result.failure(e))
                }
            }
        } else {
            if (cachedCategories.isEmpty()) {
                emit(Result.failure(Exception("No network and no cached data")))
            }
        }
    }.flowOn(Dispatchers.IO)

}