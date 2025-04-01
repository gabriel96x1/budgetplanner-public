package com.bytesdrawer.budgetplanner

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.bytesdrawer.authmodule.data.AuthNetworkService
import com.bytesdrawer.authmodule.data.AuthRepository
import com.bytesdrawer.authmodule.utils.AuthSharedPreferencesUtil
import com.bytesdrawer.budgetplanner.common.data.AccountRepository
import com.bytesdrawer.budgetplanner.common.data.AccountTransferRepository
import com.bytesdrawer.budgetplanner.common.data.CategoryRepository
import com.bytesdrawer.budgetplanner.common.data.MoneyMovementRepository
import com.bytesdrawer.budgetplanner.common.data.NotificationModelRepository
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.utils.PersistenceSetupUtil
import com.bytesdrawer.budgetplanner.common.utils.PurchasesManager
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    const val AWS_ACCOUNT_ENDPOINT_DEV = "dev/account/"
    const val AWS_ACCOUNT_TRANSFER_ENDPOINT_DEV = "dev/accountTransfer/"
    const val AWS_CATEGORY_ENDPOINT_DEV = "dev/category/"
    const val AWS_MONEY_MOVEMENT_ENDPOINT_DEV = "dev/moneyMovement/"
    const val AWS_NOTIFICATION_MODEL_ENDPOINT_DEV = "dev/notificationModel/"

    @Provides
    fun provideBaseUrl() = BuildConfig.AWS_ENDPOINT

    @Provides
    fun context(@ApplicationContext appContext: Context): Context = appContext

    @Singleton
    @Provides
    fun isOffline(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo == null || !netInfo.isConnected
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(isOffline: Boolean) =
        OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                var request = chain.request()
                request = request.newBuilder().header("Cache-Control", "public, max-age=" + 50).build()
                chain.proceed(request)
            }
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideNetworkService(retrofit: Retrofit): NetworkService = retrofit.create(NetworkService::class.java)

    @Singleton
    @Provides
    fun provideAuthNetworkService(retrofit: Retrofit): AuthNetworkService = retrofit.create(AuthNetworkService::class.java)

    @Singleton
    @Provides
    fun sharedPreferencesUtil(context: Context): SharedPreferencesUtil =
        SharedPreferencesUtil(PersistenceSetupUtil.getSharedPreferences(context))

    @Singleton
    @Provides
    fun authSharedPreferencesUtil(context: Context): AuthSharedPreferencesUtil =
        SharedPreferencesUtil(PersistenceSetupUtil.getSharedPreferences(context))

    @Singleton
    @Provides
    fun accountRepository(context: Context, networkService: NetworkService): AccountRepository =
        PersistenceSetupUtil.getAccountRepository(context, networkService)

    @Singleton
    @Provides
    fun accountTransferRepository(context: Context, networkService: NetworkService): AccountTransferRepository =
        PersistenceSetupUtil.getAccountTransferRepository(context, networkService)

    @Singleton
    @Provides
    fun categoryRepository(context: Context, networkService: NetworkService): CategoryRepository =
        PersistenceSetupUtil.getCategoryRepository(context, networkService)

    @Singleton
    @Provides
    fun moneyMovementRepository(context: Context, networkService: NetworkService): MoneyMovementRepository =
        PersistenceSetupUtil.getMoneyMovementRepository(context, networkService)

    @Singleton
    @Provides
    fun notificationModelRepository(context: Context, networkService: NetworkService): NotificationModelRepository =
        PersistenceSetupUtil.getNotificationModelRepository(context, networkService)

    @Singleton
    @Provides
    fun authRepository(networkService: AuthNetworkService): AuthRepository =
        AuthRepository(networkService)
}