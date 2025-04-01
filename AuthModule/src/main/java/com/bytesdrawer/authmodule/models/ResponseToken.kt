package com.bytesdrawer.authmodule.models

import com.google.gson.annotations.SerializedName

data class ResponseToken(
    @SerializedName("idToken")
    val result: Token
)

data class Token(
    @SerializedName("idToken")
    val idToken: String
)
