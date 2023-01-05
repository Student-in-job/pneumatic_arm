package com.example.torquefeddback

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Message: Serializable {
    @SerializedName("message")
    @Expose
    var message: String? = null
}

class Force: Serializable {
    @SerializedName("message")
    @Expose
    var message: Float? = null
}