package com.mutablestate.budgetplanner

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import com.bytesdrawer.authmodule.AuthViewModel
import com.bytesdrawer.authmodule.data.AuthNetworkService
import com.bytesdrawer.authmodule.data.AuthRepository
import com.bytesdrawer.authmodule.utils.AuthSharedPreferencesUtil
import com.bytesdrawer.budgetplanner.BuildConfig
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.data.AccountRepository
import com.bytesdrawer.budgetplanner.common.data.AccountTransferRepository
import com.bytesdrawer.budgetplanner.common.data.CategoryRepository
import com.bytesdrawer.budgetplanner.common.data.MoneyMovementRepository
import com.bytesdrawer.budgetplanner.common.data.NotificationModelRepository
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDb
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.utils.PersistenceSetupUtil
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import org.mockito.Mockito
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Singleton

object TestingHelper {

    private lateinit var authSharedPreferencesUtil: AuthSharedPreferencesUtil
    private lateinit var authRepository: AuthRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var accountTransferRepository: AccountTransferRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var moneyMovementRepository: MoneyMovementRepository
    private lateinit var notificationModelRepository: NotificationModelRepository

    private fun setupRepositoryResponses() {
        Mockito.`when`(accountRepository.getAccounts()).thenReturn(
            flowOf(listOf(mockAccount()))
        )
        /*Mockito.`when`(accountTransferRepository.getAccountTransfers()).thenReturn(
            flowOf(listOf(mockAccountTransfer(icon)))
        )*/
    }

    fun getMainViewModel(sharedPreferencesUtil: SharedPreferencesUtil): MainViewModel {
        accountRepository = Mockito.mock(AccountRepository::class.java)
        accountTransferRepository = Mockito.mock(AccountTransferRepository::class.java)
        categoryRepository = Mockito.mock(CategoryRepository::class.java)
        moneyMovementRepository = Mockito.mock(MoneyMovementRepository::class.java)
        notificationModelRepository = Mockito.mock(NotificationModelRepository::class.java)

        setupRepositoryResponses()

        return MainViewModel(
            sharedPreferencesUtil,
            accountRepository,
            accountTransferRepository,
            categoryRepository,
            moneyMovementRepository,
            notificationModelRepository
        )
    }

    fun getAuthViewModel(): AuthViewModel {
        authSharedPreferencesUtil = Mockito.mock(AuthSharedPreferencesUtil::class.java)
        authRepository = Mockito.mock(AuthRepository::class.java)
        return AuthViewModel(
            authRepository = authRepository,
            preferencesUtil = authSharedPreferencesUtil
        )
    }

    fun mockCategory(icon: String) = Category(
        1,
        null,
        "name",
        icon,
        true,
        BigDecimal.ONE,
        0,
        0,
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )

    fun mockSubCategory(icon: String) = Category(
        2,
        1,
        "name",
        icon,
        true,
        BigDecimal.ONE,
        0,
        0,
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )

    fun mockAccount() = Account(
        1,
        BigDecimal.ONE,
        "account",
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )

    fun mockMoneyMovementCategory(icon: String) = MoneyMovement(
        1,
        1,
        category_id = 1,
        subCategory_id = null,
        BigDecimal.ONE,
        "category",
        "",
        "comment",
        icon,
        true,
        LocalDateTime.now().toString(),
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )

    fun mockMoneyMovementSubCategory(icon: String) = MoneyMovement(
        1,
        1,
        category_id = 1,
        subCategory_id = 2,
        BigDecimal.ONE,
        "category",
        "",
        "comment",
        icon,
        true,
        LocalDateTime.now().toString(),
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )

    fun mockAccountTransfer(icon: String) = AccountTransfer(
        1,
        2,
        1,
        category_id = 1,
        BigDecimal.ONE,
        "category",
        icon,
        LocalDateTime.now().toString(),
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )

    fun mockNotificationModelCategory() = NotificationModel(
        1,
        1,
        1,
        null,
        "name",
        "custom text",
        "comment",
        "category",
        isIncome = true,
        frequency = 0,
        remainingTimes = 2,
        true,
        LocalDateTime.now().toString(),
        BigDecimal.ONE,
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )

    fun mockNotificationModelSubCategory() = NotificationModel(
        1,
        1,
        1,
        null,
        "name",
        "custom text",
        "comment",
        "category",
        isIncome = true,
        frequency = 0,
        remainingTimes = 2,
        true,
        LocalDateTime.now().toString(),
        BigDecimal.ONE,
        isSent = false,
        toDelete = false,
        timeStamp = ""
    )
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)

    val observer = object : Observer<T> {
        override fun onChanged(o: T) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
            //throw Exception("changed")
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}