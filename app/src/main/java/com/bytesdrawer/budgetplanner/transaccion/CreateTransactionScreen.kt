package com.bytesdrawer.budgetplanner.transaccion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.categorias.CategoryItem
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.composables.CalculatorOperation
import com.bytesdrawer.budgetplanner.common.composables.DatePickerDialog
import com.bytesdrawer.budgetplanner.common.composables.IncomeExpenseSwitchComposable
import com.bytesdrawer.budgetplanner.common.composables.onCalculatorButtonClicked
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.common.utils.AppReviewLauncher
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.TransactionDateCreation
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getCalculatorButtons
import com.bytesdrawer.budgetplanner.common.utils.transformDateFromNotification
import java.math.BigDecimal
import java.time.LocalDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateTransactionScreen(
    notificationId: Long? = null,
    notifications: List<NotificationModel>?,
    viewModel: MainViewModel,
    categories: List<Category>?,
    accounts: List<Account>?,
    selectedAccount: MutableState<Account?>,
    incomeExpenseState: MutableState<Boolean>,
    selectedDivisa: MutableState<Divisa>,
    navigateUp: () -> Boolean,
    accountSelectionVisible: MutableState<Boolean>,
    analyticsEvents: Events,
    appReviewLauncher: AppReviewLauncher,
    navController: NavController,
    navigateToCreateNewCategoryScreen: () -> Unit
) {
    val notification = if (notificationId == null) {
        remember {
            null
        }
    } else {
        remember {
            notifications?.first { it.notification_id == notificationId }
        }
    }
    val accountFromNotification = remember {
        if (notification == null) {
            null
        } else {
            accounts?.first { it.account_id == notification.account_id }
        }
    }

    LaunchedEffect(true) {
        if (notificationId != null) {
            incomeExpenseState.value = notification!!.isIncome
        }
    }

    val digitPattern = remember { Regex("^\\d+\\.?\\d*$") }
    val transactionAmount = remember {
        mutableStateOf(
            if (notificationId == null) "" else notification?.amount.toString()
        )
    }
    val optionalComment = remember {
        mutableStateOf(
            if (notificationId == null) "" else notification?.comment.toString()
        )
    }
    val dateSelectionDialogState = remember {
        mutableStateOf(false)
    }
    val accountToUse: MutableState<Account?> = if (notificationId != null) {
        remember { mutableStateOf(accountFromNotification) }
    } else {
        remember { mutableStateOf(null) }
    }

    LaunchedEffect(selectedAccount.value) {
        if (selectedAccount.value != null) {
            accountToUse.value = selectedAccount.value
        }
    }

    val selectedTransactionDate = remember {
        mutableStateOf(
            TransactionDateCreation.TODAY
        )
    }
    val dateToSaveTransaction = remember {
        mutableStateOf(
            if (notificationId == null)
                LocalDateTime.now().toString()
            else {
                transformDateFromNotification(notification!!.nextDateToShow, notification.frequency)
            }
        )
    }
    val selectedCategory: MutableState<Category?> = remember {
        mutableStateOf(
            if (notificationId == null) null else categories!!.first { it.category_id == notification!!.category_id }
        )
    }
    val subCategorySelected: MutableState<Category?> = remember {
        mutableStateOf(
            if (notificationId == null) null else categories!!.firstOrNull { it.category_id == notification!!.subcategory_id }
        )
    }
    val buttonEnabled = remember { mutableStateOf(false) }
    val transactionAmountError = remember { mutableStateOf(false) }
    val calculatorState = remember { mutableStateOf(false) }
    val accumulatedCalculatorValue = remember { mutableStateOf("0") }
    val restoreCalculationString = remember { mutableStateOf(false) }
    val focusRequesterAmountTextField = remember { FocusRequester() }
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    transactionAmountError.value = transactionAmount.value.isEmpty()
    buttonEnabled.value = !transactionAmountError.value && selectedCategory.value != null && selectedAccount.value != null

    val context = LocalContext.current
    LaunchedEffect(selectedTransactionDate.value) {
        when (selectedTransactionDate.value) {
            TransactionDateCreation.TODAY -> {
                dateToSaveTransaction.value = LocalDateTime.now().toString()
            }
            TransactionDateCreation.YESTERDAY -> {
                dateToSaveTransaction.value = LocalDateTime.now().minusDays(1).toString()
            }
            TransactionDateCreation.PERSO -> {
                dateSelectionDialogState.value = !dateSelectionDialogState.value
            }
        }
    }

    LaunchedEffect(calculatorState.value) {
        if (!calculatorState.value) {
            localFocusManager.clearFocus()
            keyboardController?.hide()
            analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_CLOSE_CALC)
        } else {
            analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_OPEN_CALC)
        }
    }

    val clickedOperation = remember {
        mutableStateOf(CalculatorOperation.NOTHING)
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                bottom = 50.dp,
                top = if (calculatorState.value) 200.dp else 66.dp
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (!calculatorState.value) {
                        localFocusManager.clearFocus()
                        keyboardController?.hide()
                    }
                })
            },
        columns = if (calculatorState.value) GridCells.Fixed(4) else GridCells.Fixed(5),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        item(span = { if (calculatorState.value) GridItemSpan(4) else GridItemSpan(5) }) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .weight(0.8f)
                        .focusRequester(focusRequesterAmountTextField),
                    singleLine = true,
                    readOnly = calculatorState.value,
                    value = transactionAmount.value,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(digitPattern)) {
                            transactionAmount.value = it
                        }
                    },
                    label = { Text(stringResource(R.string.transaction_amount_create_transaction)) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.table_view),
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                focusRequesterAmountTextField.requestFocus()
                                keyboardController?.hide()
                                calculatorState.value = !calculatorState.value
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                Text(
                    modifier = Modifier.weight(0.2f),
                    text = selectedDivisa.value.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (!calculatorState.value) {
            item(span = { GridItemSpan(5) }) {
                Column(
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.account_analytics_screen)
                    )
                    Row(
                        modifier = Modifier.clickable {
                            analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_CHANGE_ACCOUNT)
                            accountSelectionVisible.value = !accountSelectionVisible.value
                        }
                    ) {
                        Text(
                            text = if (accountToUse.value == null) stringResource(R.string.no_selected_account) else accountToUse.value!!.name,
                            color = if (accountToUse.value == null) Color.Red else Color.Unspecified
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_drop_down),
                            contentDescription = null,
                            tint = if (accountToUse.value == null) Color.Red else if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                }
            }

            item(span = { GridItemSpan(5) }) {
                IncomeExpenseSwitchComposable(incomeExpenseState = incomeExpenseState, analyticsEvents)
            }

            item(span = { GridItemSpan(5) }) {
                Text(text = stringResource(R.string.select_category_recurrents))

            }

            items(categories!!.filter { it.isIncome == incomeExpenseState.value && it.parentCategoryId == null && it.name != "Transferencias_Especial_Plus20" }) {
                val subCategorySelectorDialogState = remember { mutableStateOf(false) }
                CategoryItem(
                    category = it,
                    selectedCategory = selectedCategory.value,
                    modifier = Modifier.clickable {
                        selectedCategory.value = it
                        if (it.category_id != 10000L && it.category_id != 10001L) {
                            subCategorySelectorDialogState.value = !subCategorySelectorDialogState.value
                        }
                    },
                    subCategorySelectorDialogState = subCategorySelectorDialogState,
                    subCategoryList = categories.filter { filteredCategory ->
                        filteredCategory.isIncome == incomeExpenseState.value &&
                                filteredCategory.parentCategoryId != null &&
                                filteredCategory.parentCategoryId == it.category_id &&
                                filteredCategory.name != "Transferencias_Especial_Plus20"
                    },
                    subCategorySelected = subCategorySelected,
                    navigateToCreateSubCategory = {
                        navController.navigate(
                            "${context.getString(NavigationScreens.ADD_SUBCATEGORY.screen)}/${selectedCategory.value?.category_id}"
                        )
                    }
                )
            }

            item {
                CategoryItem(
                    category = Category(
                        parentCategoryId = null,
                        name = stringResource(R.string.new_category),
                        icon = context.resources.getResourceEntryName(R.drawable.add),
                        isIncome = false,
                        expenseLimit = BigDecimal.ZERO,
                        color = 0xFF0D01FF,
                        order = 999999999,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ),
                    modifier = Modifier
                        .clickable {
                            navigateToCreateNewCategoryScreen()
                        }
                )
            }

            item(span = { GridItemSpan(5) }) {
                TextField(
                    singleLine = false,
                    value = optionalComment.value,
                    onValueChange = {
                        optionalComment.value = it
                    },
                    label = { Text(stringResource(R.string.add_coment_optional)) }
                )
            }

            item(span = { GridItemSpan(5) }) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.padding(vertical = 6.dp))
                    Text(text = stringResource(R.string.select_date_create_transaction))
                    Spacer(modifier = Modifier.padding(vertical = 6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Card {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        selectedTransactionDate.value =
                                            TransactionDateCreation.TODAY
                                    }
                                    .background(
                                        if (selectedTransactionDate.value == TransactionDateCreation.TODAY)
                                            Color.DarkGray
                                        else
                                            MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = stringResource(R.string.today_string),
                                    color = if (selectedTransactionDate.value == TransactionDateCreation.TODAY)
                                        Color.White
                                    else
                                        if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            }
                        }
                        Card {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        selectedTransactionDate.value =
                                            TransactionDateCreation.YESTERDAY
                                    }
                                    .background(
                                        if (selectedTransactionDate.value == TransactionDateCreation.YESTERDAY)
                                            Color.DarkGray
                                        else
                                            MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = stringResource(R.string.yesterday_string),
                                    color = if (selectedTransactionDate.value == TransactionDateCreation.YESTERDAY)
                                        Color.White
                                    else
                                        if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            }
                        }
                        Card {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        if (selectedTransactionDate.value == TransactionDateCreation.PERSO) {
                                            dateSelectionDialogState.value =
                                                !dateSelectionDialogState.value
                                        } else {
                                            selectedTransactionDate.value =
                                                TransactionDateCreation.PERSO
                                        }
                                    }
                                    .background(
                                        if (selectedTransactionDate.value == TransactionDateCreation.PERSO)
                                            Color.DarkGray
                                        else
                                            MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ){
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.other_date),
                                        color = if (selectedTransactionDate.value == TransactionDateCreation.PERSO)
                                            Color.White
                                        else
                                            if (isSystemInDarkTheme()) Color.White else Color.Black
                                    )
                                    if (selectedTransactionDate.value == TransactionDateCreation.PERSO) {
                                        Text(
                                            text = "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.dayOfMonth}/" +
                                                    "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.monthValue}",
                                            color = if (selectedTransactionDate.value == TransactionDateCreation.PERSO)
                                                Color.White
                                            else
                                                if (isSystemInDarkTheme()) Color.White else Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(5) }) {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buttonEnabled.value,
                        onClick = {
                            when (selectedTransactionDate.value) {
                                TransactionDateCreation.TODAY -> {
                                    analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_DATE_TODAY)
                                }
                                TransactionDateCreation.YESTERDAY -> {
                                    analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_DATE_YESTERDAY)
                                }
                                TransactionDateCreation.PERSO -> {
                                    analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_DATE_PERSO)
                                }
                            }

                            if (optionalComment.value.isEmpty()) {
                                analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_NO_COMMENT)
                            } else {
                                analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_W_COMMENT)
                            }

                            val updateForAccount = Account(
                                account_id = accountToUse.value!!.account_id,
                                balance = if (incomeExpenseState.value)
                                    accountToUse.value!!.balance.add(
                                        if (transactionAmount.value.toBigDecimal() < BigDecimal.ZERO)
                                        transactionAmount.value.substring(1, transactionAmount.value.length).toBigDecimal()
                                    else
                                        transactionAmount.value.toBigDecimal())
                                else
                                    accountToUse.value!!.balance.subtract(
                                        if (transactionAmount.value.toBigDecimal() < BigDecimal.ZERO)
                                        transactionAmount.value.substring(1, transactionAmount.value.length).toBigDecimal()
                                    else
                                        transactionAmount.value.toBigDecimal()),
                                name = accountToUse.value!!.name,
                                isSent = false,
                                timeStamp = "",
                                toDelete = false
                            )
                            viewModel.createOrUpdateAccount(updateForAccount)
                            viewModel.createOrUpdateTransaction(
                                MoneyMovement(
                                    account_id = accountToUse.value!!.account_id,
                                    category_id = selectedCategory.value!!.category_id,
                                    subCategory_id = if (subCategorySelected.value != null)
                                        subCategorySelected.value!!.category_id
                                    else
                                        null,
                                    amount = if (transactionAmount.value.toBigDecimal() < BigDecimal.ZERO)
                                        transactionAmount.value.substring(1, transactionAmount.value.length).toBigDecimal()
                                    else
                                        transactionAmount.value.toBigDecimal(),
                                    category = selectedCategory.value!!.name,
                                    subCategory = "",
                                    comment = optionalComment.value,
                                    icon = if (subCategorySelected.value != null)
                                        subCategorySelected.value!!.icon
                                    else
                                        selectedCategory.value!!.icon,
                                    isIncome = incomeExpenseState.value,
                                    date = dateToSaveTransaction.value,
                                    isSent = false,
                                    timeStamp = "",
                                    toDelete = false
                                )
                            )
                            selectedAccount.value = updateForAccount
                            viewModel.getTransactions()
                            navigateUp()
                            appReviewLauncher.launchIntegratedReview()
                        }
                    ) {
                        Text(text = stringResource(R.string.add_transaction_button))
                    }
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                }
            }
        }
        else {
            items(getCalculatorButtons()) {
                Button(
                    modifier = Modifier.padding(3.dp),
                    onClick = {
                        onCalculatorButtonClicked(
                            transactionAmount,
                            it,
                            clickedOperation,
                            accumulatedCalculatorValue,
                            restoreCalculationString
                        )
                    }
                ) {
                    Text(
                        text = it,
                        fontSize = 20.sp
                    )
                }
            }
            item(
                span = { GridItemSpan(4) }
            ) {
                Button(
                    modifier = Modifier.padding(3.dp),
                    onClick = {
                        calculatorState.value = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.accept_account_selection),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }

    if (dateSelectionDialogState.value) {
        analyticsEvents.trackEvent(Events.CREATE_TRANSACTION_CHANGE_DATE)
        DatePickerDialog(dateSelectionDialogState, dateToSaveTransaction, selectedTransactionDate)
    }
}
