package com.example.feip_fefu_lab_clothing_store.data.network

import com.example.feip_fefu_lab_clothing_store.data.model.ProductResponse
import retrofit2.http.GET

interface CatalogApi {
    @GET("catalog")
    suspend fun getCatalog(): ProductResponse
}
