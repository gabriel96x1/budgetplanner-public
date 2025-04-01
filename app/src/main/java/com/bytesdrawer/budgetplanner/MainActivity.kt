package com.bytesdrawer.budgetplanner

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bytesdrawer.authmodule.AuthViewModel
import com.google.android.gms.ads.MobileAds
import com.bytesdrawer.budgetplanner.ajustes.FingerprintPinIntroScreen
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.composables.LoadingDialog
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.navigation.NavigationGraph
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.common.utils.AppReviewLauncher
import com.bytesdrawer.budgetplanner.common.utils.BiometricAuthUtil
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil.CREATE_TRANSACTION_BY_USER_INTENT
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil.CREATE_TRANSACTION_INTENT
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil.NOTIFICATION_ID
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.ExcelGenerator
import com.bytesdrawer.budgetplanner.common.utils.GooglePlayAccountManager
import com.bytesdrawer.budgetplanner.common.utils.PermissionRequestUtil
import com.bytesdrawer.budgetplanner.common.utils.PurchasesManager
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import com.bytesdrawer.budgetplanner.common.utils.bigDecimalParsed
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getDecrementDateFromFrequencyNumber
import com.bytesdrawer.budgetplanner.common.utils.getDivisaFromString
import com.bytesdrawer.budgetplanner.common.utils.getNameNavigationScreen
import com.bytesdrawer.budgetplanner.common.utils.toMillis
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import com.bytesdrawer.budgetplanner.home.WRITE_REQUEST_CODE
import com.bytesdrawer.budgetplanner.onboarding.OnBoardingNavigation
import com.bytesdrawer.budgetplanner.onboarding.ShowCase
import com.bytesdrawer.budgetplanner.onboarding.ShowcaseSteps
import com.bytesdrawer.budgetplanner.ui.theme.BudgetPlannerTheme
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var preferencesUtil: SharedPreferencesUtil
    private lateinit var purchasesManger: PurchasesManager
    private lateinit var viewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private val excelGenerator: ExcelGenerator = ExcelGenerator
    private val analyticsEvents = Events(this)
    private val startTime = LocalDateTime.now()
    private lateinit var googleAccountManager: GooglePlayAccountManager

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val receivedIntentAction = intent.action
        MobileAds.initialize(this)
        googleAccountManager = GooglePlayAccountManager(this, preferencesUtil, authViewModel)
        purchasesManger = PurchasesManager(this)

        setContent {
            val dynamicColors = remember {
                mutableStateOf(preferencesUtil.isDynamicColors())
            }
            BudgetPlannerTheme(dynamicColor = dynamicColors.value) {

                val context = LocalContext.current.applicationContext

                val notificationManager = context
                    .getSystemService(NotificationManager::class.java)
                val navController = rememberNavController()
                val screenState = remember {
                    mutableStateOf(NavigationScreens.HOME)
                }

                val qaPaidUser = remember {
                    mutableStateOf(preferencesUtil.isQaPaidUser())
                }

                val adsDialogState = remember { mutableStateOf(true) }

                val showCasePosition = remember { mutableStateMapOf<String, LayoutCoordinates?>() }

                val showcaseShown = remember { mutableStateOf(true) }

                val showcaseStep = remember { mutableStateOf(ShowcaseSteps.ACCOUNTS) }

                val isOnBoardingComplete = remember {
                    mutableStateOf(preferencesUtil.isOnBoardingComplete())
                }

                val initTimes = preferencesUtil.getInitTimes()

                val appReviewLauncher = remember { AppReviewLauncher(this, preferencesUtil, initTimes, analyticsEvents) }

                val accountSelectionVisible = remember {
                    mutableStateOf(false)
                }
                val timeLapseSelected: MutableState<PeriodOfTime?> = remember {
                    mutableStateOf(null)
                }
                val stringDateTime = remember {
                    mutableStateOf(LocalDateTime.now().toString())
                }
                val recurrentsTabState = remember { mutableStateOf(0) }
                val selectedDivisa = remember {
                    mutableStateOf(getDivisaFromString(preferencesUtil.getGlobalDivisa().toString()))
                }
                LaunchedEffect(isOnBoardingComplete.value) {
                    selectedDivisa.value = getDivisaFromString(preferencesUtil.getGlobalDivisa().toString())
                }

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val showSnackbar = viewModel.showSnackBar.collectAsState().value
                val locationFile = viewModel.fileLocation.collectAsState().value
                val composeUserAuth = viewModel.userAuth.collectAsState().value

                val incomeExpenseState = remember { mutableStateOf(false) }

                val accounts = viewModel.accountsList.observeAsState().value
                val categories = viewModel.categoryList.observeAsState().value?.sortedBy { it.order }
                val transactions = viewModel.transactionsList.observeAsState().value
                val notifications = viewModel.notificationList.observeAsState().value
                val accountTransfers = viewModel.accountTransfersList.observeAsState().value
                val isSecurityEnabled = viewModel.isSecurityEnabled.collectAsState()
                val isLoading = viewModel.isLoading.collectAsState().value

                if (
                    BiometricAuthUtil.hasBiometricCapability(this) != BiometricManager.BIOMETRIC_SUCCESS
                    && isSecurityEnabled.value
                ) {
                    viewModel.disableSecurity()
                }

                if (
                    isOnBoardingComplete.value && !isSecurityEnabled.value ||
                    isOnBoardingComplete.value && isSecurityEnabled.value && composeUserAuth
                ) {
                    PermissionRequestUtil.requestNotificationPermissions(context = this)
                    viewModel.getAccounts()
                    viewModel.getTransactions()
                    viewModel.getCategoryItems()
                    viewModel.getAllNotifications()
                    viewModel.getAccountTransfers()
                    CreateNotificationUtil.createUserHookAlarm(context)
                    if (adsDialogState.value && initTimes > 3 && !qaPaidUser.value) {
                        adsView(this, adsDialogState)
                    }
                }

                if (!isOnBoardingComplete.value) {
                    googleAccountManager.tryGetUser()
                }

                val selectedAccount: MutableState<Account?> = remember {
                    mutableStateOf(null)
                }
                var accountsTotal: BigDecimal = BigDecimal.ZERO
                if (accounts != null) {
                    accountsTotal = accounts.map { it.balance }
                        .fold(BigDecimal.ZERO) { acc, e -> acc + e }
                }
                LaunchedEffect(true) {
                    timeLapseSelected.value = PeriodOfTime.MONTH
                    if (initTimes <= 30 && isOnBoardingComplete.value) {
                        preferencesUtil.setInitTimes(initTimes + 1)
                    }
                }
                Scaffold(
                    topBar = {
                        if (
                            isOnBoardingComplete.value && !isSecurityEnabled.value ||
                            isOnBoardingComplete.value && isSecurityEnabled.value && composeUserAuth
                        ) {
                            TopAppBarComposable(
                                showCasePosition,
                                accountsTotal,
                                screenState,
                                navController,
                                selectedDivisa,
                                accountSelectionVisible,
                                selectedAccount,
                                analyticsEvents
                            )
                        }
                    },
                    bottomBar = {
                        /*if (
                            isOnBoardingComplete.value && !isSecurityEnabled.value ||
                            isOnBoardingComplete.value && isSecurityEnabled.value && composeUserAuth
                        ) {
                            BottomAdsBar()
                        }*/
                    },
                    floatingActionButton = {
                        if (
                            isOnBoardingComplete.value && screenState.value == NavigationScreens.HOME &&
                            !isSecurityEnabled.value ||
                            isOnBoardingComplete.value && screenState.value == NavigationScreens.HOME &&
                            isSecurityEnabled.value && composeUserAuth
                        ) {
                            Fab(showCasePosition, navController, analyticsEvents) { showAdAfterSomeTime(adsDialogState) }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) {
                    if (
                        isOnBoardingComplete.value && !isSecurityEnabled.value ||
                        isOnBoardingComplete.value && isSecurityEnabled.value && composeUserAuth
                    ) {
                        NavigationGraph(
                            navController = navController,
                            viewModel = viewModel,
                            screenState = screenState,
                            preferencesUtil = preferencesUtil,
                            selectedAccount = selectedAccount,
                            categories = categories,
                            accounts = accounts,
                            incomeExpenseState = incomeExpenseState,
                            transactions = transactions,
                            this,
                            excelGenerator,
                            timeLapseSelected,
                            stringDateTime,
                            notifications,
                            recurrentsTabState,
                            selectedDivisa,
                            accountSelectionVisible,
                            accountTransfers,
                            dynamicColors,
                            { showAdAfterSomeTime(adsDialogState) },
                            qaPaidUser,
                            showCasePosition,
                            analyticsEvents,
                            appReviewLauncher,
                            accountsTotal,
                            googleAccountManager,
                            purchasesManger
                        )
                    } else if (isOnBoardingComplete.value && isSecurityEnabled.value && !composeUserAuth) {
                        FingerprintPinIntroScreen({ viewModel.authUser() }, this) { viewModel.deauthUser() }
                    } else {
                        OnBoardingNavigation(
                            isOnBoardingComplete,
                            navController = navController,
                            viewModel = viewModel,
                            screenState = screenState,
                            preferencesUtil = preferencesUtil
                        )
                    }

                    if (accountSelectionVisible.value) {
                        AccountSelectionDialog(
                            screenState,
                            accountsTotal,
                            accountSelectionVisible,
                            accounts,
                            selectedDivisa,
                            selectedAccount,
                            analyticsEvents,
                            { navController.navigate(context.getString(NavigationScreens.EDIT_ACCOUNT.screen)) },
                            { navController.navigate(context.getString(NavigationScreens.ADD_ACCOUNT.screen)) }
                        ) { navController.navigate(context.getString(NavigationScreens.ACCOUNT_TRANSFERENCE_SUMMARY.screen)) }
                    }
                }

                if (isLoading) {
                    LoadingDialog()
                }

                LaunchedEffect(showSnackbar) {
                    if (showSnackbar) {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                getString(R.string.excel_guardado_con_exito),
                                getString(R.string.open_string),
                                duration = SnackbarDuration.Short
                            )

                            when(result) {
                                SnackbarResult.Dismissed -> {
                                    viewModel.dismissSnackBar()
                                }
                                SnackbarResult.ActionPerformed -> {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    val mydir = Uri.parse("${locationFile.host}")
                                    intent.setDataAndType(mydir, DocumentsContract.Document.MIME_TYPE_DIR)

                                    startActivity(intent)
                                }
                            }
                            viewModel.dismissSnackBar()
                        }
                    }
                }

                if (transactions != null && accounts != null && categories != null && notifications != null) {
                    LaunchedEffect(true) {
                        when (receivedIntentAction) {
                            CREATE_TRANSACTION_INTENT -> {
                                val notification = notifications.first { it.notification_id == intent.getLongExtra(NOTIFICATION_ID, 0) }
                                val category = categories.first { it.category_id == notification.category_id }
                                val account = accounts.first { it.account_id == notification.account_id}
                                val updateForAccount = Account(
                                    account_id = account.account_id,
                                    balance = if (notification.isIncome)
                                        account.balance.add(notification.amount)
                                    else
                                        account.balance.subtract(notification.amount),
                                    name = account.name,
                                    isSent = false,
                                    timeStamp = "",
                                    toDelete = false
                                )
                                incomeExpenseState.value = notification.isIncome

                                viewModel.createOrUpdateAccount(updateForAccount)

                                viewModel.createOrUpdateTransaction(
                                    MoneyMovement(
                                        account_id = notification.account_id,
                                        category_id = notification.category_id,
                                        subCategory_id = notification.subcategory_id,
                                        amount = notification.amount,
                                        category = category.name,
                                        subCategory = "",
                                        comment = notification.comment,
                                        icon = category.icon,
                                        isIncome = notification.isIncome,
                                        date = getDecrementDateFromFrequencyNumber(
                                            notification.frequency,
                                            dateStringToRegularFormat(notification.nextDateToShow)!!
                                        ).toString(),
                                        isSent = false,
                                        timeStamp = "",
                                        toDelete = false
                                    )
                                )
                                notificationManager.cancel(notification.notification_id.toInt())
                            }
                            CREATE_TRANSACTION_BY_USER_INTENT -> {
                                navController.navigate(
                                    "${context.getString(NavigationScreens.TRANSACCION.screen)}/${intent.getLongExtra(NOTIFICATION_ID, 0)}"
                                )
                            }

                            CreateNotificationUtil.HOOK_NOTIFICATION_CLICKED -> {
                                analyticsEvents.trackEvent(CreateNotificationUtil.HOOK_NOTIFICATION_CLICKED)
                            }
                        }
                        notifications.forEach {
                            CreateNotificationUtil.cancelAlarm(
                                context,
                                context
                                    .getSystemService(Context.ALARM_SERVICE) as? AlarmManager?,
                                it.notification_id
                            )

                            val isActive = dateStringToRegularFormat(it.nextDateToShow)!!.toMillis() > LocalDateTime.now().toMillis()

                            if (isActive) {
                                CreateNotificationUtil.createAlarmToNotify(
                                    context,
                                    context
                                        .getSystemService(Context.ALARM_SERVICE) as? AlarmManager?,
                                    it.notification_id,
                                    dateStringToRegularFormat(it.nextDateToShow)!!.toMillis()
                                )
                            }
                        }
                    }
                }

                if (
                    isOnBoardingComplete.value && !isSecurityEnabled.value &&
                    showcaseShown.value && initTimes == 0 ||
                    isOnBoardingComplete.value && isSecurityEnabled.value && composeUserAuth &&
                    showcaseShown.value && initTimes == 0
                //showcaseShown.value
                ) {
                    ShowCase(showCasePosition, showcaseShown, showcaseStep)
                    preferencesUtil.setPromoTime(LocalDateTime.now().plusDays(3).toMillis())
                }
            }
        }
    }

    @Deprecated("By Google :)")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            WRITE_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        if (data != null && data.data != null) {
                            viewModel.generateExcel(this, excelGenerator, data.data!!)
                        }
                    }
                    RESULT_CANCELED -> {}
                }
            }
            BiometricAuthUtil.BiometricEnrollmentRequestCode -> {
                when (requestCode) {
                    RESULT_OK -> {
                        viewModel.enableSecurity()
                        Toast.makeText(this, getString(R.string.correctly_enroled), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        if (BiometricAuthUtil.hasBiometricCapability(this) == BiometricManager.BIOMETRIC_SUCCESS) {
                            viewModel.enableSecurity()
                            Toast.makeText(this, getString(R.string.correctly_enroled), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this,
                                getString(R.string.suspended_enrollment), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            GooglePlayAccountManager.RC_SIGN_IN -> {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                googleAccountManager.handleResult(task)
            }
        }

    }

    private fun showAdAfterSomeTime(adsDialogState: MutableState<Boolean>) {
        if (startTime.plusHours(1) < LocalDateTime.now()) {
            adsDialogState.value = true
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSelectionDialog(
    screenState: MutableState<NavigationScreens>,
    accountsTotal: BigDecimal,
    accountSelectionVisible: MutableState<Boolean>,
    accounts: List<Account>?,
    selectedDivisa: MutableState<Divisa>,
    selectedAccount: MutableState<Account?>,
    analyticsEvents: Events,
    navigateToEditAccount: () -> Unit,
    navigateToAddAccount: () -> Unit,
    navigateToTransferences: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val df = remember {
        DecimalFormat("#,###.##")
    }
    df.roundingMode = RoundingMode.DOWN
    val sheetState = SheetState(skipPartiallyExpanded = true, initialValue = SheetValue.Hidden)
    ModalBottomSheet(
        onDismissRequest = {
            analyticsEvents.trackEvent(Events.DISMISS_ACCOUNT_SELECTION_BOTTOM_SHEET)
            accountSelectionVisible.value = !accountSelectionVisible.value
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.select_account_account_dialog),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(top = 12.dp))
            if (
                screenState.value == NavigationScreens.HOME
                || screenState.value == NavigationScreens.ANALISIS
                || screenState.value == NavigationScreens.TRANSACCIONS_BY_CATEGORY
                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedAccount.value = null
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = selectedAccount.value == null,
                        onClick = {
                            selectedAccount.value = null
                        }
                    )
                    Text(
                        text = "Total $${bigDecimalParsed(accountsTotal, df)} ${selectedDivisa.value.name}",
                        fontSize = 18.sp,
                    )
                }
            }
            accounts?.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(0.9f)
                            .clickable {
                                selectedAccount.value = it
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = it == selectedAccount.value,
                            onClick = {
                                analyticsEvents.trackEvent(Events.ACCOUNT_SELECTED_CHANGE_BOTTOM_SHEET)
                                selectedAccount.value = it
                            }
                        )
                        Text(
                            text = "${it.name} $${bigDecimalParsed(it.balance, df)} ${selectedDivisa.value.name}",
                            fontSize = 18.sp,
                        )
                    }
                    if (it == selectedAccount.value && selectedAccount.value != null) {
                        IconButton(
                            modifier = Modifier.weight(0.1f),
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                    accountSelectionVisible.value = !accountSelectionVisible.value
                                }
                                analyticsEvents.trackEvent(Events.NAVIGATE_TO_EDIT_ACCOUNT)
                                navigateToEditAccount()
                            }) {
                            Icon(painter = painterResource(id = R.drawable.edit), contentDescription = null)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(top = 24.dp))
            if (screenState.value == NavigationScreens.HOME) {
                Button(onClick = {
                    scope.launch {
                        sheetState.hide()
                        accountSelectionVisible.value = !accountSelectionVisible.value
                        analyticsEvents.trackEvent(Events.NAVIGATE_TO_ADD_NEW_ACCOUNT_TRANSFER)
                        navigateToTransferences()
                    }
                }) {
                    Text(text = stringResource(R.string.transferences_account_dialog))
                    Icon(painter = painterResource(id = R.drawable.repeat), contentDescription = null)
                }
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            }

            Button(onClick = {
                scope.launch {
                    sheetState.hide()
                    accountSelectionVisible.value = !accountSelectionVisible.value
                }
                analyticsEvents.trackEvent(Events.NAVIGATE_TO_ADD_NEW_ACCOUNT)
                navigateToAddAccount()
            }) {
                Text(text = stringResource(R.string.add_account_account_dialog))
                Icon(painter = painterResource(id = R.drawable.add), contentDescription = null)
            }
            Spacer(modifier = Modifier.padding(top = 24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            accountSelectionVisible.value = !accountSelectionVisible.value
                        }
                        analyticsEvents.trackEvent(Events.DISMISS_ACCOUNT_SELECTION_BOTTOM_SHEET)
                    },
                ) {
                    Text(text = stringResource(R.string.accept_account_selection))
                }
            }
            Spacer(modifier = Modifier.padding(top = 24.dp))
        }
    }
}

@Composable
private fun Fab(
    showCasePosition: SnapshotStateMap<String, LayoutCoordinates?>,
    navController: NavHostController,
    analyticsEvents: Events,
    showAdAfterSomeTime: () -> Unit
) {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            showAdAfterSomeTime()
            analyticsEvents.trackEvent(Events.NAVIGATE_TO_CREATE_NEW_MONEY_MOVEMENT)
            navController.navigate(context.getString(NavigationScreens.TRANSACCION.screen))
        },
        modifier = Modifier.onGloballyPositioned {
            showCasePosition[ShowcaseSteps.ADD_TRANSACTION_FAB.name] = it

        }
    ) {
        Icon(painter = painterResource(id = R.drawable.add), contentDescription = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComposable(
    showCasePosition: SnapshotStateMap<String, LayoutCoordinates?>,
    accountsTotal: BigDecimal,
    screenState: MutableState<NavigationScreens>,
    navController: NavHostController,
    selectedDivisa: MutableState<Divisa>,
    accountSelectionVisible: MutableState<Boolean>,
    selectedAccount: MutableState<Account?>,
    analyticsEvents: Events
) {
    val df = remember {
        DecimalFormat("#,###.##")
    }
    df.roundingMode = RoundingMode.DOWN
    val context = LocalContext.current

    TopAppBar(
        title = {
            if (screenState.value == NavigationScreens.HOME) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.clickable {
                            analyticsEvents.trackEvent(Events.OPEN_ACCOUNTS_BOTTOM_SHEET_HOME)
                            accountSelectionVisible.value = !accountSelectionVisible.value
                        },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedAccount.value != null) selectedAccount.value!!.name else "Total",
                            fontSize = 18.sp,
                            maxLines = 1
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_drop_down),
                            contentDescription = null
                        )
                    }
                    Text(
                        text = if (selectedAccount.value?.balance != null)
                                    "$ ${bigDecimalParsed(selectedAccount.value?.balance!!, df)} ${selectedDivisa.value.name}"
                                else if (selectedAccount.value == null)
                                    "$ ${bigDecimalParsed(accountsTotal, df)} ${selectedDivisa.value.name}"
                                else
                                    "Loading",
                        maxLines = 1,
                        modifier = Modifier
                            .onGloballyPositioned {
                                showCasePosition[ShowcaseSteps.ACCOUNTS.name] = it
                            }
                    )
                }
            } else {
                Text(
                    text = stringResource(id = screenState.value.screen)
                )
            }
        },
        navigationIcon = {
            if (screenState.value != NavigationScreens.HOME) {
                IconButton(onClick = {
                    screenState.value = getNameNavigationScreen(navController.previousBackStackEntry?.destination?.route, context)
                    navController.navigateUp()
                } ) {
                    Icon(
                        painterResource(id = R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
            }
        },
    )
}

fun adsView(
    context: MainActivity,
    adsDialogState: MutableState<Boolean>,
) {
    val request = AdManagerAdRequest.Builder().build()

    if (adsDialogState.value) {
        AppOpenAd.load(
            context,
            "ca-app-pub-8235021525952254/8373776129",
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    context.let { ad.show(it) }
                    Log.d("Loaded Ad", "Ad")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adsDialogState.value = false
                }
            }
        )
        adsDialogState.value = false
    }
}