package com.example.apppost.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitInstance {
    private const val BASE_URL = "https://my-api-0362.onrender.com/"

    // Configuração do OkHttpClient lidar com redirecionamento do PUT e do DELETE
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Usa o OkHttpClient configurado
            .build()
            .create(ApiService::class.java)
    }
}
