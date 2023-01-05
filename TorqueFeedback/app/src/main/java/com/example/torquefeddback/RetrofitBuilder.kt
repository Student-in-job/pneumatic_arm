package com.example.torquefeddback

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    private const val basicURL = "163.180.118.144"
    private val logger =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private val gson = GsonBuilder()
        .serializeNulls()
        .serializeSpecialFloatingPointValues()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder().client(okHttpClient)
            .baseUrl("http://$basicURL:8000/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: APIService = getRetrofit().create(APIService::class.java)

}