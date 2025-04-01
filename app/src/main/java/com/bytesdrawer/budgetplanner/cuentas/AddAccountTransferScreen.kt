package com.bytesdrawer.budgetplanner.cuentas

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.bigDecimalParsed
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddAccountTransferScreen(
    accounts: List<Account>?,
    selectedDivisa: MutableState<Divisa>,
    viewModel: MainViewModel,
    categories: List<Category>?,
    navigateUp: () -> Unit
) {

    val transferCategory = categories?.first { it.name == "Transferencias_Especial_Plus20" }
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val digitPattern = remember { Regex("^\\d+\\.?\\d*$") }

    val transactionAmount = remember {
        mutableStateOf(
            ""
        )
    }

    val context = LocalContext.current

    val fromAccount: MutableState<Account?> = remember {
        mutableStateOf(null)
    }

    val toAccount: MutableState<Account?> = remember {
        mutableStateOf(null)
    }

    val expanded1 = remember {
        mutableStateOf(false)
    }

    val expanded2 = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 70.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
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
                label = { Text(stringResource(R.string.amount_to_transfer)) },
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
        Spacer(modifier = Modifier.padding(vertical = 36.dp))
        ExposedDropdownMenuBox(
            expanded = expanded1.value,
            onExpandedChange = {
                expanded1.value = !expanded1.value
            },
            content = {
                TextField(
                    readOnly = true,
                    value = if (fromAccount.value == null) "" else fromAccount.value!!.name,
                    onValueChange = { },
                    label = { Text(text = stringResource(R.string.from_account)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded1.value
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                AccountSelectorDropdownMenu(
                    menuExpanded = expanded1,
                    fromAccount,
                    accounts,
                    selectedDivisa
                )
            }
        )
        Spacer(modifier = Modifier.padding(vertical = 18.dp))
        Icon(
            painter = painterResource(id = R.drawable.repeat),
            contentDescription = null,
            modifier = Modifier.size(35.dp)
        )
        Spacer(modifier = Modifier.padding(vertical = 18.dp))
        ExposedDropdownMenuBox(
            expanded = expanded2.value,
            onExpandedChange = {
                expanded2.value = !expanded2.value
            },
            content = {
                TextField(
                    readOnly = true,
                    value = if (toAccount.value == null) "" else toAccount.value!!.name,
                    onValueChange = { },
                    label = { Text(text = stringResource(R.string.to_account)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded2.value
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                AccountSelectorDropdownMenu(
                    menuExpanded = expanded2,
                    toAccount,
                    accounts,
                    selectedDivisa
                )
            }
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Button(
            enabled = fromAccount.value != null
                    && toAccount.value != null
                    && transactionAmount.value.isNotEmpty()
                    && toAccount.value != fromAccount.value,
            onClick = {

                val updateForAccount1 = Account(
                    account_id = fromAccount.value!!.account_id,
                    balance = fromAccount.value!!.balance.subtract(transactionAmount.value.toBigDecimal()),
                    name = fromAccount.value!!.name,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                )

                val updateForAccount2 = Account(
                    account_id = toAccount.value!!.account_id,
                    balance = toAccount.value!!.balance.add(transactionAmount.value.toBigDecimal()),
                    name = toAccount.value!!.name,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                )
                viewModel.createOrUpdateAccount(updateForAccount1)
                viewModel.createOrUpdateAccount(updateForAccount2)

                viewModel.createOrUpdateAccountTransfer(
                    AccountTransfer(
                        from_account_id = fromAccount.value!!.account_id,
                        to_account_id = toAccount.value!!.account_id,
                        category_id = transferCategory!!.category_id,
                        amount = transactionAmount.value.toBigDecimal(),
                        category = transferCategory.name,
                        icon = context.resources.getResourceEntryName(R.drawable.repeat),
                        date = LocalDateTime.now().toString(),
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    ), context
                )

                viewModel.getTransactions()
                viewModel.getAccountTransfers()
                navigateUp()
            }
        ) {
            Text(text = stringResource(R.string.transfer_button))
        }
    }
}

@Composable
private fun AccountSelectorDropdownMenu(
    menuExpanded: MutableState<Boolean>,
    account: MutableState<Account?>,
    accounts: List<Account>?,
    selectedDivisa: MutableState<Divisa>
) {
    val df = remember {
        DecimalFormat("#,###.##")
    }
    df.roundingMode = RoundingMode.DOWN
    DropdownMenu(
        expanded = menuExpanded.value,
        onDismissRequest = { menuExpanded.value = !menuExpanded.value },
    ) {
        accounts?.forEach {
            DropdownMenuItem(
                text = {
                    Text("${it.name} $${bigDecimalParsed(it.balance, df)} ${selectedDivisa.value.name}")
                },
                onClick = {
                    account.value = it
                    menuExpanded.value = !menuExpanded.value
                },
            )
        }
    }
}