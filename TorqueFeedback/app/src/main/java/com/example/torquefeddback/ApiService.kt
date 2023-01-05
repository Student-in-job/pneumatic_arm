package com.example.torquefeddback

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @POST("message/")
    suspend fun sendMessage(
        @Body data: Message
    ):Response<com.example.torquefeddback.Response>

    @POST("newtons/")
    suspend fun sendForce(
        @Body data: Force
    ):Response<com.example.torquefeddback.Response>

    @GET("cancel/")
    suspend fun sendCancel():Response<com.example.torquefeddback.Response>
}
