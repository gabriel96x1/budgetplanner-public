package com.bytesdrawer.authmodule.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class CredentialsData(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val pass: String
)

fun CredentialsData.toRequestBody(): RequestBody {
    return Gson().toJson(this).toRequestBody()
}
