package com.bytesdrawer.authmodule.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class ConfirmationData(
    @SerializedName("email")
    val email: String,

    @SerializedName("verificationCode")
    val verificationCode: String
)

fun ConfirmationData.toRequestBody(): RequestBody {
    return Gson().toJson(this).toRequestBody()
}