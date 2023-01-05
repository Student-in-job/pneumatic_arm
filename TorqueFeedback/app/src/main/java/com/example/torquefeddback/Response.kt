package com.example.torquefeddback

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Response: Serializable {
    @SerializedName("result")
    @Expose
    var result: String? = null
}