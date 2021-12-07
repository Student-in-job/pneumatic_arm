package kr.kyunghee.torquefeedback_20

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @POST("message/")
    suspend fun sendMessage(
        @Body data: Message
    ):Response<kr.kyunghee.torquefeedback_20.Response>

    @POST("newtons/")
    suspend fun sendForce(
        @Body data: Force
    ):Response<kr.kyunghee.torquefeedback_20.Response>

    @POST("torque/")
    suspend fun sendTorque(
        @Body data: Torque
    ):Response<kr.kyunghee.torquefeedback_20.Response>

    @GET("cancel/")
    suspend fun sendCancel():Response<kr.kyunghee.torquefeedback_20.Response>
}
