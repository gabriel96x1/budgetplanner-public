package com.mutablestate.budgetplanner.functional

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDb
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.mutablestate.budgetplanner.TestingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class RoomDbTest {

    private lateinit var database: BudgetPlannerDb
    private lateinit var dao: BudgetPlannerDao
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val icon = context.resources.getResourceEntryName(R.drawable.right)

    private val moneyMovement = TestingHelper.mockMoneyMovementCategory(icon)
    private val account = TestingHelper.mockAccount()
    private val category = TestingHelper.mockCategory(icon)
    private val accountTransfer = TestingHelper.mockAccountTransfer(icon)
    private val notificationModel = TestingHelper.mockNotificationModelCategory()

    @Before
    fun setupDatabase() {
        val instrumentationContext = context
        database = Room.inMemoryDatabaseBuilder(
            instrumentationContext,
            BudgetPlannerDb::class.java
        ).allowMainThreadQueries().build()

        dao = database.budgetPlannerDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun moneyMovementTests() = runBlocking {
        dao.insertTransaction(moneyMovement)

        val job = async(Dispatchers.IO) {
            dao.getAllMoneyMovements().collect {
                Assert.assertEquals(it.first(), moneyMovement)
                this.cancel()
            }
        }
        job.join()

        dao.deleteTransaction(moneyMovement)

        val job2 = async(Dispatchers.IO) {
            dao.getAllMoneyMovements().collect {
                Assert.assertEquals(it, emptyList<MoneyMovement>())
                this.cancel()
            }
        }
        job2.join()
    }

    @Test
    fun accountTests() = runBlocking {

        dao.insertAccount(account)

        val job = async(Dispatchers.IO) {
            dao.getAllAccounts().collect {
                Assert.assertEquals(it.first(), account)
                this.cancel()
            }
        }
        job.join()

        dao.deleteAccount(account)

        val job2 = async(Dispatchers.IO) {
            dao.getAllAccounts().collect {
                Assert.assertEquals(it, emptyList<Account>())
                this.cancel()
            }
        }
        job2.join()
    }

    @Test
    fun categoryTests() = runBlocking {
        dao.insertCategory(category)

        val job = async(Dispatchers.IO) {
            dao.getAllCategories().collect {
                Assert.assertEquals(it.first(), category)
                this.cancel()
            }
        }
        job.join()

        dao.deleteCategory(category)

        val job2 = async(Dispatchers.IO) {
            dao.getAllCategories().collect {
                Assert.assertEquals(it, emptyList<Category>())
                this.cancel()
            }
        }
        job2.join()
    }

    @Test
    fun accountTransferTests() = runBlocking {
        dao.insertAccountTransfer(accountTransfer)

        val job = async(Dispatchers.IO) {
            dao.getAllAccountTransfers().collect {
                Assert.assertEquals(it.first(), accountTransfer)
                this.cancel()
            }
        }
        job.join()

        dao.deleteAccountTransfer(accountTransfer)

        val job2 = async(Dispatchers.IO) {
            dao.getAllAccountTransfers().collect {
                Assert.assertEquals(it, emptyList<AccountTransfer>())
                this.cancel()
            }
        }
        job2.join()
    }

    @Test
    fun notificationModelTests() = runBlocking {
        dao.insertNotification(notificationModel)

        val job = async(Dispatchers.IO) {
            dao.getAllNotifications().collect {
                Assert.assertEquals(it.first(), notificationModel)
                this.cancel()
            }
        }
        job.join()

        val job1 = async(Dispatchers.IO) {
            val syncNotification = dao.getSynchronousNotification(notificationModel.notification_id)
            Assert.assertEquals(syncNotification, notificationModel)
            this.cancel()
        }
        job1.join()

        dao.deleteNotification(notificationModel)

        val job2 = async(Dispatchers.IO) {
            dao.getAllNotifications().collect {
                Assert.assertEquals(it, emptyList<NotificationModel>())
                this.cancel()
            }
        }
        job2.join()
    }

}