package com.bytesdrawer.authmodule.data

import com.bytesdrawer.authmodule.models.ResponseToken
import com.bytesdrawer.authmodule.utils.AuthConstants
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthNetworkService {

    @POST(AuthConstants.AWS_SIGN_UP)
    suspend fun signUp(@Body body: RequestBody): Response<Unit>
    @POST(AuthConstants.AWS_SIGN_UP_CONFIRM)
    suspend fun signUpCodeConfirmation(@Body body: RequestBody): Response<Unit>
    @POST(AuthConstants.AWS_SIGN_IN)
    suspend fun signIn(@Body body: RequestBody): Response<ResponseToken>

}