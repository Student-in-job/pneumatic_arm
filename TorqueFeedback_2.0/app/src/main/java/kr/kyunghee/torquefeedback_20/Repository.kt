package kr.kyunghee.torquefeedback_20

import android.util.Log

class Repository(private val apiService: APIService) {
    suspend fun sendMessage(message: Message): Response? {
        val response = apiService.sendMessage(message)
        return if (response.isSuccessful){
            response.body()
        } else{
            Response().apply { result = "No" }
        }
    }

    suspend fun sendForce(message: Force): Response? {
        val response = apiService.sendForce(message)
        Log.d("DSSSSSSSSSSSS", message.message.toString())
        return if (response.isSuccessful){
            response.body()
        } else{
            Response().apply { result = "No" }
        }
    }

    suspend fun sendTorque(message: Torque): Response? {
        val response = apiService.sendTorque(message)
        Log.d("DSSSSSSSSSSSS", message.message.toString())
        return if (response.isSuccessful){
            response.body()
        } else{
            Response().apply { result = "No" }
        }
    }

    suspend fun sendCancel(): Response? {
        val response = apiService.sendCancel()
        return if (response.isSuccessful){
            response.body()
        } else{
            Response().apply { result = "No" }
        }
    }
}