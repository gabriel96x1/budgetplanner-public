package com.bytesdrawer.budgetplanner.common.data.local

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.intl.Locale
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal


@Database(
    entities = [Account::class, MoneyMovement::class, Category::class, NotificationModel::class, AccountTransfer::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class BudgetPlannerDb : RoomDatabase() {

    abstract fun budgetPlannerDao(): BudgetPlannerDao

    companion object {

        private var INSTANCE: BudgetPlannerDb? = null

        fun getInstance(context: Context): BudgetPlannerDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BudgetPlannerDb::class.java,
                        "budget_planner_db"
                    ).addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                CoroutineScope(Dispatchers.IO).launch {
                                    prePopulateCategories(context)
                                }
                            }
                        })
                        .addMigrations(MIGRATION_1_2)
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }

        private val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE isInfiniteRepeating RENAME TO isFiniteRepeating"
                )
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE money_movements ADD subCategoryId INTEGER DEFAULT NULL"
                )

                database.execSQL(
                    "ALTER TABLE categories ADD parentCategory_id INTEGER DEFAULT NULL"
                )

                database.execSQL(
                    "ALTER TABLE notification_model ADD subcategory_id INTEGER DEFAULT NULL"
                )
            }
        }

        fun prePopulateCategories(context: Context) {
            try {
                val budgetPlannerDao = getInstance(context).budgetPlannerDao()

                val language = Locale.current.language

                if (language == "es") {
                    budgetPlannerDao.insertCategory(Category(name = "Negocio", icon = context.resources.getResourceEntryName(R.drawable.business), isIncome = true, expenseLimit = BigDecimal.ZERO, color = 0xFF0BFF07 , order = 0,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false))
                    budgetPlannerDao.insertCategory(Category(name = "Interés", icon = context.resources.getResourceEntryName(R.drawable.interest), isIncome = true, expenseLimit = BigDecimal.ZERO, color = 0xFF728A05, order = 1,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "" ,
                        toDelete = false))
                    budgetPlannerDao.insertCategory(Category(name = "Salario", icon = context.resources.getResourceEntryName(R.drawable.money), isIncome = true, expenseLimit = BigDecimal.ZERO, color = 0xFF258A05, order = 2,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "" ,
                        toDelete = false))
                    budgetPlannerDao.insertCategory(Category(category_id = 10000, name = "Otros", icon = context.resources.getResourceEntryName(R.drawable.other), isIncome = true, expenseLimit = BigDecimal.ZERO, color = Color.Gray.toArgb().toLong(), order = 3,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Familia", icon = context.resources.getResourceEntryName(R.drawable.family), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF5C70D7, order = 0,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Familia", icon = context.resources.getResourceEntryName(R.drawable.family), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF5C70D7, order = 0,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Comida", icon = context.resources.getResourceEntryName(R.drawable.food), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFFE89261, order = 1,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Ocio", icon = context.resources.getResourceEntryName(R.drawable.free_time), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF1F24AE, order = 2,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Salud", icon = context.resources.getResourceEntryName(R.drawable.health), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFFFF0051, order = 3,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Hogar", icon = context.resources.getResourceEntryName(R.drawable.house), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF1CABA0, order = 4,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Estudios", icon = context.resources.getResourceEntryName(R.drawable.study), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF546679, order = 5,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Transporte", icon = context.resources.getResourceEntryName(R.drawable.transport), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF5D35D0, order = 6,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Vacaciones", icon = context.resources.getResourceEntryName(R.drawable.vacations), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF00B2FF, order = 7,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Rutina", icon = context.resources.getResourceEntryName(R.drawable.workout), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF000000, order = 8,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(category_id = 10001, name = "Otros", icon = context.resources.getResourceEntryName(R.drawable.other), isIncome = false, expenseLimit = BigDecimal.ZERO, color = Color.Gray.toArgb().toLong(), order = 9,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Transferencias_Especial_Plus20", icon = context.resources.getResourceEntryName(R.drawable.repeat), isIncome = false, expenseLimit = BigDecimal.ZERO, color = Color.Gray.toArgb().toLong(), order = 1000000000,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))

                } else {
                    budgetPlannerDao.insertCategory(Category(name = "Business", icon = context.resources.getResourceEntryName(R.drawable.business), isIncome = true, expenseLimit = BigDecimal.ZERO, color = 0xFF0BFF07, order = 0,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Salary", icon = context.resources.getResourceEntryName(R.drawable.money), isIncome = true, expenseLimit = BigDecimal.ZERO, color = 0xFF258A05, order = 1,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(category_id = 10000, name = "Others", icon = context.resources.getResourceEntryName(R.drawable.other), isIncome = true, expenseLimit = BigDecimal.ZERO, color = Color.Gray.toArgb().toLong(), order = 2,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Family", icon = context.resources.getResourceEntryName(R.drawable.family), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF5C70D7, order = 0,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Family", icon = context.resources.getResourceEntryName(R.drawable.family), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF5C70D7, order = 0,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Food", icon = context.resources.getResourceEntryName(R.drawable.food), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFFE89261, order = 1,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Free Time", icon = context.resources.getResourceEntryName(R.drawable.free_time), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF1F24AE, order = 2,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Health", icon = context.resources.getResourceEntryName(R.drawable.health), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFFFF0051, order = 3,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Home", icon = context.resources.getResourceEntryName(R.drawable.house), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF1CABA0, order = 4,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Learning", icon = context.resources.getResourceEntryName(R.drawable.study), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF546679, order = 5,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Transport", icon = context.resources.getResourceEntryName(R.drawable.transport), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF5D35D0, order = 6,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Vacations", icon = context.resources.getResourceEntryName(R.drawable.vacations), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF00B2FF, order = 7,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Workout", icon = context.resources.getResourceEntryName(R.drawable.workout), isIncome = false, expenseLimit = BigDecimal.ZERO, color = 0xFF000000, order = 8,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(category_id = 10001, name = "Others", icon = context.resources.getResourceEntryName(R.drawable.other), isIncome = false, expenseLimit = BigDecimal.ZERO, color = Color.Gray.toArgb().toLong(), order = 9,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))
                    budgetPlannerDao.insertCategory(Category(name = "Transferencias_Especial_Plus20", icon = context.resources.getResourceEntryName(R.drawable.repeat), isIncome = false, expenseLimit = BigDecimal.ZERO, color = Color.Gray.toArgb().toLong(), order = 1000000000,
                        parentCategoryId = null,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false ))

                }


            } catch (exception: Exception) {
                Log.e(
                    "BudgetPlanner App",
                    exception.localizedMessage ?: "failed to pre-populate users into database"
                )
            }
        }
    }
}