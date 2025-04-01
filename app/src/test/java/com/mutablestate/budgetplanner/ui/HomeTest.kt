package com.mutablestate.budgetplanner.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.bytesdrawer.authmodule.AuthViewModel
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.ExcelGenerator
import com.bytesdrawer.budgetplanner.common.utils.PersistenceSetupUtil
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import com.bytesdrawer.budgetplanner.home.HomeScreen
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import com.bytesdrawer.budgetplanner.ui.theme.BudgetPlannerTheme
import com.mutablestate.budgetplanner.TestingHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDateTime


@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class HomeTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val icon = context.resources.getResourceEntryName(R.drawable.right)
    /*private val activityController: ActivityController<MainActivity> = Robolectric.buildActivity(
        MainActivity::class.java
    ).create().start().resume().visible()
    private val mainActivity: MainActivity = activityController.get()*/

    private val moneyMovement = TestingHelper.mockMoneyMovementCategory(icon)
    private val category = TestingHelper.mockCategory(icon)
    private val excelGenerator = ExcelGenerator
    private val preferenceUtil = SharedPreferencesUtil(PersistenceSetupUtil.getSharedPreferences(context))
    private lateinit var authViewModel: AuthViewModel

    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setupVM() {
        authViewModel = TestingHelper.getAuthViewModel()
    }

    @Test
    fun testHomepage() {
        var onClickRecurrent = 0
        var onClickProfile = 0
        var onClickCategories = 0
        var onClickAnalytics = 0
        rule.setContent {
            val showCasePosition = remember { mutableStateMapOf<String, LayoutCoordinates?>() }
            val incomeExpenseState = remember { mutableStateOf(false) }
            val navController = rememberNavController()
            val stringDateTime = remember {
                mutableStateOf(LocalDateTime.now().toString())
            }
            val timeLapseSelected: MutableState<PeriodOfTime?> = remember {
                mutableStateOf(PeriodOfTime.MONTH)
            }
            val selectedAccount: MutableState<Account?> = remember {
                mutableStateOf(null)
            }
            val selectedDivisa: MutableState<Divisa> = remember {
                mutableStateOf(Divisa.USD)
            }
            BudgetPlannerTheme {
                HomeScreen(
                    showCasePosition = showCasePosition,
                    stringDateTime = stringDateTime,
                    timeLapseSelected = timeLapseSelected,
                    excelGenerator = excelGenerator,
                    categories = listOf(category),
                    incomeExpenseState = incomeExpenseState,
                    selectedAccount = selectedAccount,
                    transactions = listOf(moneyMovement),
                    navController = navController,
                    navigateToAnalytics = { onClickAnalytics += 1 },
                    navigateToRecurrents = { onClickRecurrent += 1 },
                    navigateToSettings = { onClickProfile += 1 },
                    navigateToCategories = { onClickCategories += 1 },
                    analyticsEvents = Events(context),
                    googleAccountManager = null,
                    preferencesUtil = preferenceUtil,
                    selectedDivisa = selectedDivisa
                )
            }
        }


        Assert.assertEquals(0, onClickRecurrent)
        Assert.assertEquals(0, onClickAnalytics)
        Assert.assertEquals(0, onClickCategories)
        Assert.assertEquals(0, onClickProfile)

        rule.onNodeWithTag("Recurrent Button").performClick()
        Assert.assertEquals(1, onClickRecurrent)

        rule.onNodeWithTag("Profile Button").performClick()
        Assert.assertEquals(1, onClickProfile)

        rule.onNodeWithTag("Categories Button").performClick()
        Assert.assertEquals(1, onClickCategories)

        rule.onNodeWithTag("Analytics Button").performClick()
        Assert.assertEquals(1, onClickAnalytics)
    }
}