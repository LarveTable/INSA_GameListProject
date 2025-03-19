package com.insa.mygamelist

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }
    val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl("https://api.igdb.com/v4/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

object IGDBClient{
    val service = RetrofitClient.retrofit.create(IGDBService::class.java)
}