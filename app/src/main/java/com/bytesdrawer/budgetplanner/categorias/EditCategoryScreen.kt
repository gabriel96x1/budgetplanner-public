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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun EditCategoryScreen(
    categoryId: String?,
    categories: List<Category>?,
    viewModel: MainViewModel,
    selectedDivisa: MutableState<Divisa>,
    analyticsEvents: Events,
    navController: NavController,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val category = remember {
        categories?.first { it.category_id == categoryId?.toLong() }
    }
    val subCategories = viewModel.categoryList.observeAsState().value?.filter { it.parentCategoryId == categoryId?.toLong() }
    val colorPickerDialogState = remember {
        mutableStateOf(false)
    }
    val currentColor = remember {
        mutableStateOf(category!!.color)
    }
    val iconPickerDialogState = remember {
        mutableStateOf(false)
    }
    val currentIcon = remember {
        mutableStateOf(context.resources.getIdentifier(category!!.icon, "drawable", context.packageName))
    }
    val categoryName = remember { mutableStateOf(category!!.name) }
    val categoryNameError = remember {
        mutableStateOf(false)
    }
    categoryNameError.value = categoryName.value.isEmpty()

    val digitPattern = remember { Regex("^\\d+\\.?\\d*$") }
    val categorySpentLimitPerMonth = remember {
        mutableStateOf(
            if (category!!.expenseLimit == BigDecimal.ZERO) ""
            else category.expenseLimit.toString()
        )
    }

    val incomeExpenseState = remember {
        mutableStateOf(category!!.isIncome)
    }

    Column(
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 66.dp)
            .fillMaxWidth()
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
                Text(text = stringResource(R.string.edit_color_edit_category))
            }
            Button(onClick = { iconPickerDialogState.value = !iconPickerDialogState.value }) {
                Text(text = stringResource(R.string.edit_icon_edit_category))
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
                if (category?.parentCategoryId == null) {
                    Text(stringResource(R.string.category_name_add_category))
                } else {
                    Text(stringResource(R.string.subcategory_name_add_category))
                }
            }
        )
        if (categoryNameError.value) Text(
            text = if (category?.parentCategoryId == null) {
                stringResource(R.string.enter_category_name_add_category)
            } else {
                stringResource(R.string.enter_subcategory_name_add_category)
            },
            color = Color.Red,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        if (!incomeExpenseState.value && category?.parentCategoryId == null) {
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
                    label = { Text(stringResource(id = R.string.expense_limit_add_category)) },
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
        }
        else {
            categorySpentLimitPerMonth.value = ""
        }
        if (category?.parentCategoryId == null) {
            IncomeExpenseSwitchComposable(incomeExpenseState = incomeExpenseState)
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            SubCategoryGrid(context, navController, subCategories, category,viewModel)
        }
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        Button(
            onClick = {

                analyticsEvents.trackEvent(Events.CATEGORY_EDITED)
                if (categorySpentLimitPerMonth.value.isEmpty()) {
                    analyticsEvents.trackEvent(Events.ADD_EDIT_CATEGORY_NO_EXPENSE_LIMIT)
                } else {
                    analyticsEvents.trackEvent(Events.ADD_EDIT_CATEGORY_USE_EXPENSE_LIMIT)
                }

                // Modificación de categorias padre
                val newCategoryVersion = Category(
                    category!!.category_id,
                    category.parentCategoryId,
                    categoryName.value,
                    context.resources.getResourceEntryName(currentIcon.value),
                    incomeExpenseState.value,
                    if (categorySpentLimitPerMonth.value.isEmpty() || BigDecimal(categorySpentLimitPerMonth.value) == BigDecimal.ZERO)
                        BigDecimal.ZERO
                    else
                        BigDecimal(categorySpentLimitPerMonth.value),
                    currentColor.value,
                    category.order,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                )
                viewModel.updateTransactionsWithNewCategory(newCategoryVersion)
                viewModel.createOrUpdateCategory(newCategoryVersion)

                // Modificación de subcategorías:
                // A este bloque de codigo se le utiliza cuando se modifica una categoría padre,
                // para mantener la coherencia y correspondencia en las categorias hijas con la
                // padre.
                if (category.parentCategoryId == null) {
                    subCategories?.forEach { subCategory ->
                        val updatedSubcategoryVersion = Category(
                            subCategory.category_id,
                            subCategory.parentCategoryId,
                            subCategory.name,
                            subCategory.icon,
                            incomeExpenseState.value,
                            BigDecimal.ZERO,
                            subCategory.color,
                            subCategory.order,
                            isSent = subCategory.isSent,
                            timeStamp = subCategory.timeStamp,
                            toDelete = subCategory.toDelete
                        )
                        viewModel.updateTransactionsWithNewCategory(newCategoryVersion, updatedSubcategoryVersion)
                        viewModel.createOrUpdateCategory(updatedSubcategoryVersion)
                    }
                }
                viewModel.getTransactions()
                viewModel.getCategoryItems()
                navigateUp()
            },
            enabled = categoryName.value.isNotEmpty()
        ) {
            if (category?.parentCategoryId == null) {
                Text(text = stringResource(R.string.save_category))
            } else {
                Text(text = stringResource(R.string.save_subcategory))
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