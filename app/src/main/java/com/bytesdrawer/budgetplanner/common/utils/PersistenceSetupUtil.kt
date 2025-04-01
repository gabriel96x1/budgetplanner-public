package com.bytesdrawer.budgetplanner.common.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.bytesdrawer.budgetplanner.common.data.AccountRepository
import com.bytesdrawer.budgetplanner.common.data.AccountTransferRepository
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDb
import com.bytesdrawer.budgetplanner.common.data.CategoryRepository
import com.bytesdrawer.budgetplanner.common.data.MoneyMovementRepository
import com.bytesdrawer.budgetplanner.common.data.NotificationModelRepository
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PersistenceSetupUtil {

    private const val SHARED_PREFERENCES = "shared_preferences"

    fun getAccountRepository(context: Context, networkService: NetworkService): AccountRepository {
        val db = BudgetPlannerDb.getInstance(context)
        val dao = db.budgetPlannerDao()
        return AccountRepository(dao, networkService)
    }

    fun getAccountTransferRepository(context: Context, networkService: NetworkService): AccountTransferRepository {
        val db = BudgetPlannerDb.getInstance(context)
        val dao = db.budgetPlannerDao()
        return AccountTransferRepository(dao, networkService)
    }

    fun getCategoryRepository(context: Context, networkService: NetworkService): CategoryRepository {
        val db = BudgetPlannerDb.getInstance(context)
        val dao = db.budgetPlannerDao()
        return CategoryRepository(dao, networkService)
    }

    fun getMoneyMovementRepository(context: Context, networkService: NetworkService): MoneyMovementRepository {
        val db = BudgetPlannerDb.getInstance(context)
        val dao = db.budgetPlannerDao()
        return MoneyMovementRepository(dao, networkService)
    }

    fun getNotificationModelRepository(context: Context, networkService: NetworkService): NotificationModelRepository {
        val db = BudgetPlannerDb.getInstance(context)
        val dao = db.budgetPlannerDao()
        return NotificationModelRepository(dao, networkService)
    }

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
    }

    fun getDao(context: Context): BudgetPlannerDao {
        val db = BudgetPlannerDb.getInstance(context)
        return db.budgetPlannerDao()
    }

    suspend fun getNotification(dao: BudgetPlannerDao, scope: CoroutineScope, notificationId: Long): NotificationModel? {
        var notification: NotificationModel? = null
        val job = scope.launch(Dispatchers.IO) {
            notification = dao.getSynchronousNotification(notificationId)
            Log.d("NotificationRetrieved", notification.toString())
        }
        job.join()
        return notification
    }

    fun updateNotificationNextDate(dao: BudgetPlannerDao, scope: CoroutineScope, notificationModel: NotificationModel) {
        scope.launch(Dispatchers.IO) {
            dao.insertNotification(notificationModel)
        }
    }
}