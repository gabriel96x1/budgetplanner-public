package com.mutablestate.budgetplanner.functional

import android.os.Looper
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.test.platform.app.InstrumentationRegistry
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.utils.PersistenceSetupUtil
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import com.mutablestate.budgetplanner.TestingHelper
import com.mutablestate.budgetplanner.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class ViewModelTest {

    private lateinit var mainViewModel: MainViewModel

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val icon = context.resources.getResourceEntryName(R.drawable.right)
    private val sharedPreferencesUtil = SharedPreferencesUtil(PersistenceSetupUtil.getSharedPreferences(context))

    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setupVM() {
        mainViewModel = TestingHelper.getMainViewModel(sharedPreferencesUtil)
    }
    @Test
    fun testSecurityEnabled() {
        mainViewModel.enableSecurity()
        Assert.assertEquals(true, sharedPreferencesUtil.isSecurityEnabled())
        mainViewModel.disableSecurity()
        Assert.assertEquals(false, sharedPreferencesUtil.isSecurityEnabled())
    }

    @Test
    fun testAuthUser() {
        mainViewModel.authUser()
        Assert.assertEquals(true, mainViewModel.userAuth.value)
        mainViewModel.deauthUser()
        Assert.assertEquals(false, mainViewModel.userAuth.value)
    }

    @Test
    fun testCRUDsWithModels() {
        /*Assert.assertEquals(null, mainViewModel.accountsList.value)
        Assert.assertEquals(null, mainViewModel.accountTransfersList.value)
        Assert.assertEquals(null, mainViewModel.categoryList.value)
        Assert.assertEquals(null, mainViewModel.notificationList.value)
        Assert.assertEquals(null, mainViewModel.transactionsList.value)

        mainViewModel.createOrUpdateAccountTransfer(TestingHelper.mockAccountTransfer(icon), context)
        mainViewModel.createOrUpdateCategory(TestingHelper.mockCategory(icon))
        mainViewModel.createOrUpdateNotification(TestingHelper.mockNotificationModelCategory())
        mainViewModel.createOrUpdateTransaction(TestingHelper.mockMoneyMovementCategory(icon))

        var allAccounts: List<Account>? = emptyList()
        val latch = CountDownLatch(1)
        rule.setContent {
            val accountList = mainViewModel.accountsList.observeAsState().value

            mainViewModel.createOrUpdateAccount(TestingHelper.mockAccount())
            mainViewModel.getAccounts()
            latch.await(5, TimeUnit.SECONDS)
            Assert.assertEquals(listOf(TestingHelper.mockAccount()), accountList)
        }

                mainViewModel.getAccountTransfers()
                Assert.assertEquals(listOf(TestingHelper.mockAccountTransfer(icon)), mainViewModel.accountTransfersList.value)

                mainViewModel.getCategoryItems()
                Assert.assertEquals(listOf(TestingHelper.mockCategory(icon)), mainViewModel.categoryList.value)

                mainViewModel.getAllNotifications()
                Assert.assertEquals(listOf(TestingHelper.mockNotificationModelCategory()), mainViewModel.notificationList.value)

                mainViewModel.getTransactions()
                Assert.assertEquals(listOf(TestingHelper.mockMoneyMovementCategory(icon)), mainViewModel.transactionsList.value)
        */
    }


}