package com.bytesdrawer.budgetplanner.ingresoegreso

import android.content.Context import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getNameOfTheMonth

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TransactionsByCategoryScreen(
    navController: NavHostController,
    accountSelectionVisible: MutableState<Boolean>,
    selectedAccount: MutableState<Account?>,
    viewModel: MainViewModel,
    transactions: List<MoneyMovement>?,
    categories: List<Category>?,
    categoryNameClicked: String?,
) {
    val context = LocalContext.current
    val accountToUse = remember { mutableStateOf(selectedAccount.value) }
    LaunchedEffect(selectedAccount.value) {
        accountToUse.value = selectedAccount.value
    }
    val category = remember {
        mutableStateOf(categories!!.filter { it.category_id == categoryNameClicked?.toLong() })
    }
    val searchText = remember {
        mutableStateOf("")
    }

    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .padding(top = 65.dp, bottom = 50.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(category.value.first().color), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = context.resources.getIdentifier(category.value.first().icon, "drawable", context.packageName)),
                contentDescription = "",
                tint = Color.White
            )
        }
        Text(
            text = category.value.first().name,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(.3f)
            ) {
                Text(text = stringResource(id = R.string.account_analytics_screen))
                Row(
                    modifier = Modifier.clickable { accountSelectionVisible.value = !accountSelectionVisible.value }
                ) {
                    Text(text = if (accountToUse.value == null) "Total" else accountToUse.value!!.name)
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_drop_down),
                        contentDescription = ""
                    )
                }
            }
            TextField(
                modifier = Modifier.weight(.7f),
                singleLine = true,
                value = searchText.value,
                onValueChange = {
                    searchText.value = it
                },
                label = { Text(stringResource(R.string.filter_str)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = null
                    )
                }
            )
        }

        if (accountToUse.value == null) {
            transactions
                ?.filter {
                    it.category_id == category.value.first().category_id
                            && searchText(searchText.value, it, context)
                }?.sortedByDescending { it.date }
                ?.forEach {
                    TransactionItem(it, viewModel, navController, categories)
                }
        } else {
            transactions
                ?.filter {
                    it.category_id == category.value.first().category_id
                        && it.account_id == accountToUse.value?.account_id
                            && searchText(searchText.value, it, context)
                }
                ?.sortedByDescending { it.date }
                ?.forEach {
                    TransactionItem(it, viewModel, navController, categories)
                }
        }
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
    }
}

private fun searchText(searchTerm: String, item: MoneyMovement, context: Context): Boolean {
    return if (searchTerm.isEmpty()) {
        true
    } else {
        val date = dateStringToRegularFormat(item.date)
        /*Log.d(
            "Elements",
            "element:${item.date}, ${item.amount} result:${
                "${date?.dayOfMonth} de ${getNameOfTheMonth(date?.month?.value)} de ${date?.year}".lowercase().contains(searchTerm.lowercase())
                        || item.comment.lowercase().contains(searchTerm.lowercase())
                        || item.amount.toString().contains(searchTerm.lowercase())
            }"
            )*/
        return ("${date?.dayOfMonth}-${getNameOfTheMonth(date?.month?.value, context)}-${date?.year}".lowercase().contains(searchTerm.lowercase())
                || item.comment.lowercase().contains(searchTerm.lowercase())
                || item.amount.toString().contains(searchTerm.lowercase()))
    }
}