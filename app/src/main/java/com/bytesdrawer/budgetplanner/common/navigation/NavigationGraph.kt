package com.bytesdrawer.budgetplanner.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.ajustes.SettingsScreen
import com.bytesdrawer.budgetplanner.categorias.AddNewCategoryScreen
import com.bytesdrawer.budgetplanner.categorias.CategoryManagementScreen
import com.bytesdrawer.budgetplanner.categorias.EditCategoryScreen
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.utils.AppReviewLauncher
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.ExcelGenerator
import com.bytesdrawer.budgetplanner.common.utils.GooglePlayAccountManager
import com.bytesdrawer.budgetplanner.common.utils.PurchasesManager
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import com.bytesdrawer.budgetplanner.cuentas.AccountTransferSummaryScreen
import com.bytesdrawer.budgetplanner.cuentas.AddAccountScreen
import com.bytesdrawer.budgetplanner.cuentas.AddAccountTransferScreen
import com.bytesdrawer.budgetplanner.cuentas.EditAccountScreen
import com.bytesdrawer.budgetplanner.graficos.AnalyticsScreen
import com.bytesdrawer.budgetplanner.home.HomeScreen
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import com.bytesdrawer.budgetplanner.ingresoegreso.TransactionsByCategoryScreen
import com.bytesdrawer.budgetplanner.recurrents.CreateNotificationScreen
import com.bytesdrawer.budgetplanner.recurrents.EditNotificationScreen
import com.bytesdrawer.budgetplanner.recurrents.RecurrentsScreen
import com.bytesdrawer.budgetplanner.transaccion.CreateTransactionScreen
import com.bytesdrawer.budgetplanner.transaccion.EditTransactionScreen
import java.math.BigDecimal

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    screenState: MutableState<NavigationScreens>,
    preferencesUtil: SharedPreferencesUtil,
    selectedAccount: MutableState<Account?>,
    categories: List<Category>?,
    accounts: List<Account>?,
    incomeExpenseState: MutableState<Boolean>,
    transactions: List<MoneyMovement>?,
    mainActivity: MainActivity,
    excelGenerator: ExcelGenerator,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    stringDateTime: MutableState<String>,
    notifications: List<NotificationModel>?,
    recurrentsTabState: MutableState<Int>,
    selectedDivisa: MutableState<Divisa>,
    accountSelectionVisible: MutableState<Boolean>,
    accountTransfers: List<AccountTransfer>?,
    dynamicColors: MutableState<Boolean>,
    showAdAfterSomeTime: () -> Unit,
    qaPaidUser: MutableState<Boolean>,
    showCasePosition: SnapshotStateMap<String, LayoutCoordinates?>,
    analyticsEvents: Events,
    appReviewLauncher: AppReviewLauncher,
    accountsTotal: BigDecimal,
    googleAccountManager: GooglePlayAccountManager,
    purchasesManger: PurchasesManager
) {
    NavHost(navController = navController, startDestination = stringResource(NavigationScreens.HOME.screen)) {
        composable(mainActivity.getString(NavigationScreens.HOME.screen)) {
            screenState.value = NavigationScreens.HOME
            HomeScreen(
                selectedDivisa,
                preferencesUtil,
                googleAccountManager,
                showCasePosition,
                stringDateTime,
                timeLapseSelected,
                excelGenerator,
                categories,
                incomeExpenseState,
                selectedAccount,
                transactions,
                navController,
                analyticsEvents,
                {
                    navController.navigate(mainActivity.getString(NavigationScreens.ANALISIS.screen))
                    showAdAfterSomeTime()
                },
                { navController.navigate(mainActivity.getString(NavigationScreens.PAGOS_RECURRENTES.screen)) },
                { navController.navigate(mainActivity.getString(NavigationScreens.SETTINGS.screen)) }
            ) { navController.navigate(mainActivity.getString(NavigationScreens.CATEGORY.screen)) }
        }

        composable(mainActivity.getString(NavigationScreens.TRANSACCION.screen)) {
            screenState.value = NavigationScreens.TRANSACCION
            CreateTransactionScreen(
                notifications = notifications,
                viewModel = viewModel,
                categories = categories,
                selectedDivisa = selectedDivisa,
                accounts = accounts,
                selectedAccount = selectedAccount,
                incomeExpenseState = incomeExpenseState,
                navigateUp = { navController.navigateUp() },
                accountSelectionVisible = accountSelectionVisible,
                analyticsEvents = analyticsEvents,
                appReviewLauncher = appReviewLauncher,
                navController = navController,
                navigateToCreateNewCategoryScreen = { navController.navigate(mainActivity.getString(NavigationScreens.ADD_CATEGORY.screen)) }
            )
        }

        composable(mainActivity.getString(NavigationScreens.ANALISIS.screen)) {
            screenState.value = NavigationScreens.ANALISIS
            AnalyticsScreen(
                stringDateTime,
                timeLapseSelected,
                selectedAccount,
                selectedDivisa,
                transactions,
                accountSelectionVisible,
                accountsTotal
            )
        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.TRANSACCION.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.TRANSACCION
            CreateTransactionScreen(
                backstackEntry.arguments?.getString("value")?.toLong(),
                notifications,
                viewModel,
                categories,
                accounts,
                selectedAccount,
                incomeExpenseState,
                selectedDivisa,
                { navController.navigateUp() },
                accountSelectionVisible,
                analyticsEvents = analyticsEvents,
                appReviewLauncher = appReviewLauncher,
                navController = navController,
                { navController.navigate(mainActivity.getString(NavigationScreens.ADD_CATEGORY.screen)) }
            )
        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.EDIT_TRANSACTION.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.EDIT_TRANSACTION
            EditTransactionScreen(
                backstackEntry.arguments?.getString("value"),
                transactions,
                viewModel,
                categories,
                accounts,
                incomeExpenseState,
                selectedDivisa,
                {
                    navController.navigate(mainActivity.getString(NavigationScreens.HOME.screen)) {
                        popUpTo(mainActivity.getString(NavigationScreens.TRANSACCIONS_BY_CATEGORY.screen)) {
                            inclusive = true
                        }
                    }
                },
                accountSelectionVisible,
                selectedAccount,
                navController = navController,
                analyticsEvents = analyticsEvents,
                navigateToCreateNewCategoryScreen = { navController.navigate(mainActivity.getString(NavigationScreens.ADD_CATEGORY.screen)) }
            )
        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.TRANSACCIONS_BY_CATEGORY.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.TRANSACCIONS_BY_CATEGORY
            TransactionsByCategoryScreen(
                navController,
                accountSelectionVisible,
                selectedAccount,
                viewModel,
                transactions,
                categories,
                backstackEntry.arguments?.getString("value")
            )
        }

        composable(mainActivity.getString(NavigationScreens.EDIT_ACCOUNT.screen)) {
            screenState.value = NavigationScreens.EDIT_ACCOUNT
            EditAccountScreen(selectedDivisa, selectedAccount, viewModel) { navController.navigateUp() }
        }

        composable(mainActivity.getString(NavigationScreens.ADD_ACCOUNT.screen)) {
            screenState.value = NavigationScreens.ADD_ACCOUNT
            AddAccountScreen(selectedDivisa, viewModel) { navController.navigateUp() }
        }

        composable(mainActivity.getString(NavigationScreens.ACCOUNT_TRANSFERENCE_SUMMARY.screen)) {
            screenState.value = NavigationScreens.ACCOUNT_TRANSFERENCE_SUMMARY
            AccountTransferSummaryScreen(
                selectedDivisa,
                accounts,
                viewModel,
                timeLapseSelected,
                stringDateTime,
                accountTransfers
            ) { navController.navigate(mainActivity.getString(NavigationScreens.ADD_ACCOUNT_TRANSFERENCE.screen)) }
        }

        composable(mainActivity.getString(NavigationScreens.ADD_ACCOUNT_TRANSFERENCE.screen)) {
            screenState.value = NavigationScreens.ADD_ACCOUNT_TRANSFERENCE
            AddAccountTransferScreen(
                accounts,
                selectedDivisa,
                viewModel,
                categories
            ) { navController.navigateUp() }
        }

        composable(mainActivity.getString(NavigationScreens.CATEGORY.screen)) {
            screenState.value = NavigationScreens.CATEGORY
            CategoryManagementScreen(
                analyticsEvents,
                incomeExpenseState,
                navController,
                viewModel
            ) { navController.navigate(mainActivity.getString(NavigationScreens.ADD_CATEGORY.screen)) }
        }

        composable(mainActivity.getString(NavigationScreens.ADD_CATEGORY.screen)) {
            screenState.value = NavigationScreens.ADD_CATEGORY
            AddNewCategoryScreen(
                incomeExpenseState,
                categories,
                selectedDivisa,
                viewModel,
                analyticsEvents,
                { navController.navigateUp() }
            )
        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.ADD_SUBCATEGORY.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.ADD_SUBCATEGORY
            AddNewCategoryScreen(
                incomeExpenseState,
                categories,
                selectedDivisa,
                viewModel,
                analyticsEvents,
                { navController.navigateUp() },
                backstackEntry.arguments?.getString("value")?.toLong()
            )

        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.EDIT_CATEGORY.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.EDIT_CATEGORY
            EditCategoryScreen(
                backstackEntry.arguments?.getString("value"),
                categories,
                viewModel,
                selectedDivisa,
                analyticsEvents,
                navController
            ) { navController.navigateUp() }
        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.EDIT_SUBCATEGORY.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.EDIT_SUBCATEGORY
            EditCategoryScreen(
                backstackEntry.arguments?.getString("value"),
                categories,
                viewModel,
                selectedDivisa,
                analyticsEvents,
                navController
            ) { navController.navigateUp() }
        }

        composable(mainActivity.getString(NavigationScreens.SETTINGS.screen)) {
            screenState.value = NavigationScreens.SETTINGS
            SettingsScreen(
                selectedDivisa,
                preferencesUtil,
                viewModel,
                activity = mainActivity,
                dynamicColors,
                qaPaidUser,
                appReviewLauncher,
                googleAccountManager,
                purchasesManger
            ) { navController.navigate(mainActivity.getString(NavigationScreens.HOME.screen)) }
        }

        composable(mainActivity.getString(NavigationScreens.PAGOS_RECURRENTES.screen)) {
            screenState.value = NavigationScreens.PAGOS_RECURRENTES
            RecurrentsScreen(
                recurrentsTabState,
                notifications,
                navController,
                viewModel
            )
        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.NUEVO_RECURRENTE.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.NUEVO_RECURRENTE
            CreateNotificationScreen(
                backstackEntry.arguments?.getString("value")?.toInt(),
                viewModel,
                categories,
                selectedDivisa,
                selectedAccount,
                accountSelectionVisible,
                notifications,
                incomeExpenseState,
                navController,
                { navController.navigateUp() }
            ) { navController.navigate(mainActivity.getString(NavigationScreens.ADD_CATEGORY.screen)) }
        }

        composable(
            route = "${mainActivity.getString(NavigationScreens.EDIT_RECURRENTE.screen)}/{value}",
            arguments = listOf(
                navArgument("value") {
                    type = NavType.StringType
                    defaultValue = "Default"
                }
            )
        ) { backstackEntry ->
            screenState.value = NavigationScreens.EDIT_RECURRENTE
            EditNotificationScreen(
                backstackEntry.arguments?.getString("value")?.toLong(),
                accounts,
                viewModel,
                categories,
                selectedDivisa,
                selectedAccount,
                accountSelectionVisible,
                notifications,
                incomeExpenseState,
                navController,
                { navController.navigateUp() }
            ) { navController.navigate(mainActivity.getString(NavigationScreens.ADD_CATEGORY.screen)) }
        }
    }
}