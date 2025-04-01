package com.bytesdrawer.authmodule.data

import com.bytesdrawer.authmodule.models.ConfirmationData
import com.bytesdrawer.authmodule.models.CredentialsData
import com.bytesdrawer.authmodule.models.ResponseToken
import com.bytesdrawer.authmodule.models.toRequestBody
import retrofit2.Response

class AuthRepository(
    private val service: AuthNetworkService
) {
    suspend fun signUp(data: CredentialsData): Response<Unit> {
        return service.signUp(data.toRequestBody())
    }

    suspend fun signUpCodeConfirmation(data: ConfirmationData): Response<Unit> {
        return service.signUpCodeConfirmation(data.toRequestBody())
    }

    suspend fun signIn(data: CredentialsData): Response<ResponseToken> {
        return service.signIn(data.toRequestBody())
    }
}