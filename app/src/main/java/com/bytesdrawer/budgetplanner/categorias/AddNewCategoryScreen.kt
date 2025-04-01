package com.bytesdrawer.budgetplanner.categorias

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.composables.ColorPickerDialog
import com.bytesdrawer.budgetplanner.common.composables.IconPickerDialog
import com.bytesdrawer.budgetplanner.common.composables.IncomeExpenseSwitchComposable
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import java.math.BigDecimal

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddNewCategoryScreen(
    incomeExpenseStateParent: MutableState<Boolean>,
    categories: List<Category>?,
    selectedDivisa: MutableState<Divisa>,
    viewModel: MainViewModel,
    analyticsEvents: Events,
    navigateUp: () -> Unit,
    parentCategoryId: Long? = null,
    ) {
    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val colorPickerDialogState = remember {
        mutableStateOf(false)
    }
    val currentColor = remember {
        mutableStateOf(Color.DarkGray.toArgb().toLong())
    }
    val iconPickerDialogState = remember {
        mutableStateOf(false)
    }
    val currentIcon = remember {
        mutableStateOf(R.drawable.airplane)
    }
    val categoryName = remember { mutableStateOf("") }
    val categoryNameError = remember {
        mutableStateOf(false)
    }
    categoryNameError.value = categoryName.value.isEmpty()

    val digitPattern = remember { Regex("^\\d+\\.?\\d*$") }
    val categorySpentLimitPerMonth = remember { mutableStateOf("") }

    val incomeExpenseState = remember {
        mutableStateOf(incomeExpenseStateParent.value)
    }

    Column(
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 66.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(currentColor.value), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = currentIcon.value),
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { colorPickerDialogState.value = !colorPickerDialogState.value }) {
                Text(text = stringResource(R.string.choose_color_add_category))
            }
            Button(onClick = { iconPickerDialogState.value = !iconPickerDialogState.value }) {
                Text(text = stringResource(R.string.choose_icon_add_category))
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = categoryNameError.value,
            value = categoryName.value,
            onValueChange = { if (it.length <= 20) categoryName.value = it },
            label = {
                if (parentCategoryId == null) {
                    Text(stringResource(R.string.category_name_add_category))
                } else {
                    Text(stringResource(R.string.subcategory_name_add_category))
                }
            }
        )
        if (categoryNameError.value) Text(
            text = if (parentCategoryId == null) {
                stringResource(R.string.enter_category_name_add_category)
            } else {
                stringResource(R.string.enter_subcategory_name_add_category)
            },
            color = Color.Red,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        if (!incomeExpenseState.value && parentCategoryId == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.weight(0.8f),
                    singleLine = true,
                    isError = false,
                    value = categorySpentLimitPerMonth.value,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(digitPattern)) {
                            categorySpentLimitPerMonth.value = it
                        }
                    },
                    label = { Text(stringResource(R.string.expense_limit_add_category)) },
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
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
        } else {
            categorySpentLimitPerMonth.value = ""
        }
        if (parentCategoryId == null) {
            IncomeExpenseSwitchComposable(incomeExpenseState)
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
        }
        Button(
            onClick = {
                analyticsEvents.trackEvent(Events.CATEGORY_CREATED)
                if (categorySpentLimitPerMonth.value.isEmpty()) {
                    analyticsEvents.trackEvent(Events.ADD_EDIT_CATEGORY_NO_EXPENSE_LIMIT)
                } else {
                    analyticsEvents.trackEvent(Events.ADD_EDIT_CATEGORY_USE_EXPENSE_LIMIT)
                }
                viewModel.createOrUpdateCategory(
                    Category(
                        parentCategoryId = parentCategoryId,
                        name = categoryName.value,
                        icon = context.resources.getResourceEntryName(currentIcon.value),
                        isIncome = incomeExpenseState.value,
                        expenseLimit = if (categorySpentLimitPerMonth.value.isEmpty()) BigDecimal.ZERO
                            else
                                BigDecimal(categorySpentLimitPerMonth.value),
                        color = currentColor.value,
                        order = if (parentCategoryId == null) {
                            categories!!.filter {
                                it.isIncome == incomeExpenseState.value &&
                                        it.name != "Transferencias_Especial_Plus20" &&
                                        it.parentCategoryId == null
                            }.sortedByDescending { it.order }.first().order + 1
                        } else {
                            1000000000
                        },
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    )
                )
                viewModel.getTransactions()
                viewModel.getCategoryItems()
                navigateUp()
            },
            enabled = categoryName.value.isNotEmpty()
        ) {
            if (parentCategoryId == null) {
                Text(text = stringResource(R.string.create_category))
            } else {
                Text(text = stringResource(R.string.create_subcategory))
            }
        }
    }
    if (colorPickerDialogState.value) {
        ColorPickerDialog(colorPickerDialogState, currentColor, analyticsEvents)
    }
    if (iconPickerDialogState.value) {
        IconPickerDialog(iconPickerDialogState, currentIcon, analyticsEvents)
    }
}