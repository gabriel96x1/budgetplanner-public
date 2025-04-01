package com.bytesdrawer.budgetplanner.common.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetPlannerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: Account)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: MoneyMovement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notificationModel: NotificationModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountTransfer(accountTransfer: AccountTransfer)

    @Delete
    fun deleteAccount(account: Account)

    @Delete
    fun deleteTransaction(transaction: MoneyMovement)

    @Delete
    fun deleteCategory(category: Category)

    @Delete
    fun deleteNotification(notificationModel: NotificationModel)

    @Delete
    fun deleteAccountTransfer(accountTransfer: AccountTransfer)

    @Query("DELETE FROM accounts")
    fun deleteAllAccounts()

    @Query("DELETE FROM money_movements")
    fun deleteAllMoneyMovements()

    @Query("DELETE FROM categories")
    fun deleteAllCategories()

    @Query("DELETE FROM notification_model")
    fun deleteAllNotifications()

    @Query("DELETE FROM account_transfer")
    fun deleteAllAccountTransfers()

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<Account>>

    @Query("SELECT * FROM money_movements")
    fun getAllMoneyMovements(): Flow<List<MoneyMovement>>

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM notification_model")
    fun getAllNotifications(): Flow<List<NotificationModel>>

    @Query("SELECT * FROM account_transfer")
    fun getAllAccountTransfers(): Flow<List<AccountTransfer>>

    @Query("SELECT * FROM notification_model WHERE notification_id=:notificationId")
    fun getSynchronousNotification(notificationId: Long): NotificationModel

}