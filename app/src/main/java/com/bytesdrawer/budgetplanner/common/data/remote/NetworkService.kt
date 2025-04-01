package com.bytesdrawer.budgetplanner.common.data.remote

import com.bytesdrawer.budgetplanner.AppModule
import com.bytesdrawer.budgetplanner.common.models.remote.AccountRemote
import com.bytesdrawer.budgetplanner.common.models.remote.AccountTransferRemote
import com.bytesdrawer.budgetplanner.common.models.remote.CategoryRemote
import com.bytesdrawer.budgetplanner.common.models.remote.MoneyMovementRemote
import com.bytesdrawer.budgetplanner.common.models.remote.NotificationModelRemote
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NetworkService {

    //Account Endpoint
    @GET(AppModule.AWS_ACCOUNT_ENDPOINT_DEV)
    suspend fun getAllAccounts(): Response<List<AccountRemote>>

    @GET("${AppModule.AWS_ACCOUNT_ENDPOINT_DEV}{account_id}")
    suspend fun getAccount(@Path("account_id") accountId: String): Response<AccountRemote>

    @POST(AppModule.AWS_ACCOUNT_ENDPOINT_DEV)
    suspend fun createAccount(@Body body: RequestBody): Response<Unit>

    @PUT(AppModule.AWS_ACCOUNT_ENDPOINT_DEV)
    suspend fun updateAccount(@Body body: RequestBody): Response<Unit>

    @DELETE("${AppModule.AWS_ACCOUNT_ENDPOINT_DEV}{account_id}")
    suspend fun deleteAccount(@Path("account_id") accountId: String): Response<Unit>

    //AccountTransfer Endpoint
    @GET(AppModule.AWS_ACCOUNT_TRANSFER_ENDPOINT_DEV)
    suspend fun getAllAccountTransfer(): Response<List<AccountTransferRemote>>

    @GET("${AppModule.AWS_ACCOUNT_TRANSFER_ENDPOINT_DEV}{id}")
    suspend fun getAccountTransfer(@Path("id") id: String): Response<AccountTransferRemote>

    @POST(AppModule.AWS_ACCOUNT_TRANSFER_ENDPOINT_DEV)
    suspend fun createAccountTransfer(@Body body: RequestBody): Response<Unit>

    @PUT(AppModule.AWS_ACCOUNT_TRANSFER_ENDPOINT_DEV)
    suspend fun updateAccountTransfer(@Body body: RequestBody): Response<Unit>

    @DELETE("${AppModule.AWS_ACCOUNT_TRANSFER_ENDPOINT_DEV}{id}")
    suspend fun deleteAccountTransfer(@Path("id") id: String): Response<Unit>

    //Category Endpoint
    @GET(AppModule.AWS_CATEGORY_ENDPOINT_DEV)
    suspend fun getAllCategory(): Response<List<CategoryRemote>>

    @GET("${AppModule.AWS_CATEGORY_ENDPOINT_DEV}{id}")
    suspend fun getCategory(@Path("id") id: String): Response<CategoryRemote>

    @POST(AppModule.AWS_CATEGORY_ENDPOINT_DEV)
    suspend fun createCategory(@Body body: RequestBody): Response<Unit>

    @PUT(AppModule.AWS_CATEGORY_ENDPOINT_DEV)
    suspend fun updateCategory(@Body body: RequestBody): Response<Unit>

    @DELETE("${AppModule.AWS_CATEGORY_ENDPOINT_DEV}{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<Unit>

    //MoneyMovement Endpoint
    @GET(AppModule.AWS_MONEY_MOVEMENT_ENDPOINT_DEV)
    suspend fun getAllMoneyMovement(): Response<List<MoneyMovementRemote>>

    @GET("${AppModule.AWS_MONEY_MOVEMENT_ENDPOINT_DEV}{id}")
    suspend fun getMoneyMovement(@Path("id") id: String): Response<MoneyMovementRemote>

    @POST(AppModule.AWS_MONEY_MOVEMENT_ENDPOINT_DEV)
    suspend fun createMoneyMovement(@Body body: RequestBody): Response<Unit>

    @PUT(AppModule.AWS_MONEY_MOVEMENT_ENDPOINT_DEV)
    suspend fun updateMoneyMovement(@Body body: RequestBody): Response<Unit>

    @DELETE("${AppModule.AWS_MONEY_MOVEMENT_ENDPOINT_DEV}{id}")
    suspend fun deleteMoneyMovement(@Path("id") id: String): Response<Unit>

    //NotificationModel Endpoint
    @GET(AppModule.AWS_NOTIFICATION_MODEL_ENDPOINT_DEV)
    suspend fun getAllNotificationModel(): Response<List<NotificationModelRemote>>

    @GET("${AppModule.AWS_NOTIFICATION_MODEL_ENDPOINT_DEV}{id}")
    suspend fun getNotificationModel(@Path("id") id: String): Response<NotificationModelRemote>

    @POST(AppModule.AWS_NOTIFICATION_MODEL_ENDPOINT_DEV)
    suspend fun createNotificationModel(@Body body: RequestBody): Response<Unit>

    @PUT(AppModule.AWS_NOTIFICATION_MODEL_ENDPOINT_DEV)
    suspend fun updateNotificationModel(@Body body: RequestBody): Response<Unit>

    @DELETE("${AppModule.AWS_NOTIFICATION_MODEL_ENDPOINT_DEV}{id}")
    suspend fun deleteNotificationModel(@Path("id") id: String): Response<Unit>
}