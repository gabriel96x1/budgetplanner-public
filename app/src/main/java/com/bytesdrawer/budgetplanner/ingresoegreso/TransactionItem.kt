package com.bytesdrawer.budgetplanner.ingresoegreso

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.composables.CategoryIcon
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.navigation.NavigationScreens
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getNameOfTheMonth
import com.bytesdrawer.budgetplanner.ui.theme.GreenMoney

@Composable
fun TransactionItem(
    moneyMovement: MoneyMovement,
    viewModel: MainViewModel,
    navController: NavHostController,
    categories: List<Category>?
) {
    val context = LocalContext.current
    val dialogState = remember {
        mutableStateOf(false)
    }
    val menuExpanded = remember {
        mutableStateOf(false)
    }
    val categoryToShow = remember {
        mutableStateOf(
            if (moneyMovement.subCategory_id == null) {
                categories?.first { it.category_id == moneyMovement.category_id }
            } else {
                categories?.first { it.category_id == moneyMovement.subCategory_id }
            }
        )
    }
    Card(
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            categoryToShow.value?.let {
                CategoryIcon(context = context, category = it)
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(.90f)
            ) {
                Text(
                    text = "${
                    dateStringToRegularFormat(moneyMovement.date)?.dayOfMonth
                }-${
                    getNameOfTheMonth(dateStringToRegularFormat(moneyMovement.date)?.month?.value, context)
                }-${
                    dateStringToRegularFormat(moneyMovement.date)?.year
                }",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(vertical = 3.dp))
                Text(
                    text = moneyMovement.comment,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(vertical = 3.dp))
                Text(
                    text = if (moneyMovement.isIncome)
                        "+ ${moneyMovement.amount}"
                    else
                        "- ${moneyMovement.amount}",
                    color = if (moneyMovement.isIncome)
                        if (isSystemInDarkTheme()) Color.Green else GreenMoney
                    else
                        Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(
                modifier = Modifier.weight(.10f),
                onClick = {
                    menuExpanded.value = !menuExpanded.value
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.more_vert),
                    contentDescription = null
                )
                DropDownMenuOptions(
                    dialogState,
                    menuExpanded
                ) {
                    navController.navigate(
                        "${context.getString(NavigationScreens.EDIT_TRANSACTION.screen)}/${moneyMovement.movement_id}"
                    )
                }
            }

        }

        if (dialogState.value) {
            DeleteTransactionDialog(viewModel, dialogState, moneyMovement)
        }
    }
}

@Composable
private fun DropDownMenuOptions(
    deleteDialogState: MutableState<Boolean>,
    menuExpanded: MutableState<Boolean>,
    navigateToEdit: () -> Unit
) {
    DropdownMenu(
        expanded = menuExpanded.value,
        onDismissRequest = { menuExpanded.value = !menuExpanded.value },
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.edit_notification_item))
            },
            onClick = {
                navigateToEdit()
                menuExpanded.value = !menuExpanded.value
            },
        )
        DropdownMenuItem(
            text = {
                Text(stringResource(id = R.string.delete_button))
            },
            onClick = {
                deleteDialogState.value = true
                menuExpanded.value = !menuExpanded.value
            },
        )
    }
}

@Composable
private fun DeleteTransactionDialog(
    viewModel: MainViewModel,
    dialogState: MutableState<Boolean>,
    moneyMovement: MoneyMovement
) {
    Dialog(onDismissRequest = {
        dialogState.value = !dialogState.value
    }) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.delete_transaction_title_dialog),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                    text = stringResource(R.string.delete_transaction_message_dialog),
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Button(
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        onClick = {
                        viewModel.deleteTransaction(moneyMovement)
                        dialogState.value = !dialogState.value
                        viewModel.getTransactions()
                    }) {
                        Text(text = stringResource(id = R.string.delete_button), color = Color.White)
                    }
                    Button(onClick = {
                        dialogState.value = !dialogState.value
                    }) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                }
            }
        }
    }
}