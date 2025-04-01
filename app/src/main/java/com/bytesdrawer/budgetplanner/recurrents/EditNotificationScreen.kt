package com.bytesdrawer.budgetplanner.recurrents

import android.app.AlarmManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.categorias.CategoryItem
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.composables.DateAndTimePickerDialog
import com.bytesdrawer.budgetplanner.common.composables.IncomeExpenseSwitchComposable
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getNameFromFrequencyNumber
import com.bytesdrawer.budgetplanner.common.utils.toMillis
import java.math.BigDecimal

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditNotificationScreen(
    notificationId: Long?,
    accounts: List<Account>?,
    viewModel: MainViewModel,
    categories: List<Category>?,
    selectedDivisa: MutableState<Divisa>,
    selectedAccount: MutableState<Account?>,
    accountSelectionVisible: MutableState<Boolean>,
    notifications: List<NotificationModel>?,
    incomeExpenseParentState: MutableState<Boolean>,
    navController: NavController,
    navigateUp: () -> Boolean,
    navigateToCreateNewCategoryScreen: () -> Unit
) {
    val context = LocalContext.current.applicationContext

    val alarmMgr = remember {
        context
            .getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
    }

    val notification: MutableState<NotificationModel?> = remember {
        mutableStateOf(notifications?.first { it.notification_id == notificationId })
    }

    val isFiniteRepeating = remember {
        mutableStateOf(notification.value?.isFiniteRepeating!!)
    }

    val incomeExpenseState = remember { mutableStateOf(notification.value?.isIncome!!) }
    val expanded = remember { mutableStateOf(false) }
    val digitPattern = remember { Regex("^\\d+\\.?\\d*$") }
    val name = remember { mutableStateOf(notification.value?.name!!) }
    val customMessage = remember { mutableStateOf(notification.value?.customNotificationText!!) }
    val transactionAmount = remember { mutableStateOf(notification.value?.amount!!.toString()) }
    val repeatingReminder = remember { mutableStateOf(notification.value?.remainingTimes!!) }
    val optionalComment = remember { mutableStateOf(notification.value?.comment!!) }
    val frequency = remember { mutableStateOf(notification.value?.frequency!!) }
    val accountToUse = remember {
        mutableStateOf(
            accounts?.first { notification.value?.account_id == it.account_id }
        )
    }

    LaunchedEffect(selectedAccount.value) {
        if (selectedAccount.value != null) {
            accountToUse.value = selectedAccount.value
        }
    }

    val dateSelectionDialogState = remember {
        mutableStateOf(false)
    }

    val selectedCategory: MutableState<Category?> = remember {
        mutableStateOf(
            categories?.first { it.category_id == notification.value?.category_id }
        )
    }

    val subCategorySelected: MutableState<Category?> = remember {
        mutableStateOf(
            categories?.firstOrNull { it.category_id == notification.value?.subcategory_id }
        )
    }

    val dateToSaveTransaction = remember {
        mutableStateOf(notification.value?.nextDateToShow!!)
    }

    val buttonEnabled = remember { mutableStateOf(false) }
    val transactionAmountError = remember { mutableStateOf(false) }
    val nameError = remember { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    transactionAmountError.value = transactionAmount.value.isEmpty()
    nameError.value = name.value.isEmpty()
    buttonEnabled.value = !transactionAmountError.value &&
            !nameError.value &&
            selectedCategory.value != null

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 50.dp, top = 66.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        columns = GridCells.Fixed(5),
        state = rememberLazyGridState(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(span = { GridItemSpan(5) }) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                value = name.value,
                onValueChange = {
                    name.value = it
                },
                label = { Text(stringResource(R.string.movement_title_recurrentsa)) },
            )
        }

        item(span = { GridItemSpan(5) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier.weight(0.8f),
                    singleLine = true,
                    value = transactionAmount.value,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(digitPattern)) {
                            transactionAmount.value = it
                        }
                    },
                    label = { Text(stringResource(R.string.movement_amount_recurrents)) },
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

        item(span = { GridItemSpan(5) }) {
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(text = stringResource(id = R.string.account_analytics_screen))
                Row(
                    modifier = Modifier.clickable { accountSelectionVisible.value = !accountSelectionVisible.value }
                ) {
                    Text(text = accountToUse.value!!.name)
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_drop_down),
                        contentDescription = null
                    )
                }
            }
        }

        item(span = { GridItemSpan(5) }) {
            IncomeExpenseSwitchComposable(incomeExpenseState = incomeExpenseState)

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
                    }                },
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
                        incomeExpenseParentState.value = incomeExpenseState.value
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
                Text(text = stringResource(R.string.special_message_recurrents))
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true,
                    value = customMessage.value,
                    onValueChange = {
                        customMessage.value = it
                    },
                    label = { Text(stringResource(R.string.special_message_label_recurrents)) },
                )
            }
        }

        item(span = { GridItemSpan(5) }) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.movement_frecuency))
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = {
                        expanded.value = !expanded.value
                    },
                    content = {
                        TextField(
                            readOnly = true,
                            value = getNameFromFrequencyNumber(frequency = frequency.value, context),
                            onValueChange = { },
                            label = { Text(text = stringResource(R.string.frecuency_string)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded.value
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )
                        FrequencyDropdownMenu(
                            menuExpanded = expanded,
                            frequency
                        )
                    }
                )
            }
        }

        if (frequency.value != 0) {
            item(span = { GridItemSpan(5) }) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isFiniteRepeating.value) stringResource(R.string.repetition_times) else stringResource(
                        R.string.activate_repetitions
                    ))
                    Spacer(modifier = Modifier.padding(vertical = 6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isFiniteRepeating.value) Arrangement.SpaceBetween else Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isFiniteRepeating.value) {
                            IconButton(onClick = { if (repeatingReminder.value > 1) repeatingReminder.value -= 1 }) {
                                Icon(painter = painterResource(id = R.drawable.left), contentDescription = null)
                            }
                            Text("${repeatingReminder.value}")
                            IconButton(onClick = { repeatingReminder.value += 1 }) {
                                Icon(painter = painterResource(id = R.drawable.right), contentDescription = null)
                            }
                        }
                        Checkbox(checked = isFiniteRepeating.value, onCheckedChange = {
                            isFiniteRepeating.value = !isFiniteRepeating.value
                            if (!isFiniteRepeating.value) repeatingReminder.value = 1
                        })
                    }
                }
            }
        }

        item(span = { GridItemSpan(5) }) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
                Text(text = stringResource(R.string.select_date_time_recurrents))
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
                                    dateSelectionDialogState.value = !dateSelectionDialogState.value
                                },
                            contentAlignment = Alignment.Center
                        ){
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.dayOfMonth}/" +
                                            "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.monthValue}/" +
                                            "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.year}",
                                )
                                Text(
                                    text = if (dateStringToRegularFormat(dateToSaveTransaction.value)!!.minute < 10) {
                                        "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.hour}:" +
                                                "0${dateStringToRegularFormat(dateToSaveTransaction.value)!!.minute}"
                                    } else {
                                        "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.hour}:" +
                                                "${dateStringToRegularFormat(dateToSaveTransaction.value)!!.minute}"
                                    },
                                )
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
                        notification.value = NotificationModel(
                            notification_id = notification.value!!.notification_id,
                            account_id = accountToUse.value!!.account_id,
                            category_id = selectedCategory.value!!.category_id,
                            subcategory_id = subCategorySelected.value?.category_id,
                            name = name.value,
                            customNotificationText = customMessage.value,
                            comment = optionalComment.value,
                            category = selectedCategory.value!!.name,
                            isIncome = incomeExpenseState.value,
                            frequency = frequency.value,
                            remainingTimes = repeatingReminder.value,
                            nextDateToShow = dateToSaveTransaction.value,
                            amount = transactionAmount.value.toBigDecimal(),
                            isFiniteRepeating = isFiniteRepeating.value,
                            isSent = false,
                            timeStamp = "",
                            toDelete = false
                        )
                        viewModel.createOrUpdateNotification(notification.value!!)

                        CreateNotificationUtil.cancelAlarm(
                            context,
                            alarmMgr,
                            notification.value!!.notification_id
                        )

                        CreateNotificationUtil.createAlarmToNotify(
                            context,
                            alarmMgr,
                            notification.value!!.notification_id,
                            dateStringToRegularFormat(dateToSaveTransaction.value)!!.toMillis()
                        )

                        viewModel.getTransactions()
                        viewModel.getAllNotifications()
                        navigateUp()

                    }
                ) {
                    Text(text = stringResource(R.string.save_changes_recurrents))
                }
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
            }
        }

    }
    if (dateSelectionDialogState.value) {
        DateAndTimePickerDialog(dateSelectionDialogState, dateToSaveTransaction)
    }
}

@Composable
private fun FrequencyDropdownMenu(
    menuExpanded: MutableState<Boolean>,
    frequency: MutableState<Int>
) {
    DropdownMenu(
        expanded = menuExpanded.value,
        onDismissRequest = { menuExpanded.value = !menuExpanded.value },
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.one_time_string))
            },
            onClick = {
                frequency.value = 0
                menuExpanded.value = !menuExpanded.value
            },
        )
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.daily_string))
            },
            onClick = {
                frequency.value = 1
                menuExpanded.value = !menuExpanded.value
            },
        )
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.weekly_string))
            },
            onClick = {
                frequency.value = 2
                menuExpanded.value = !menuExpanded.value
            },
        )
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.monthly_string))
            },
            onClick = {
                frequency.value = 3
                menuExpanded.value = !menuExpanded.value
            },
        )
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.yearly_string))
            },
            onClick = {
                frequency.value = 4
                menuExpanded.value = !menuExpanded.value
            },
        )
    }
}