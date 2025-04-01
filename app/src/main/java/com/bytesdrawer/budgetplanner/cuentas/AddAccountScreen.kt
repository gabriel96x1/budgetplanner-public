package com.bytesdrawer.budgetplanner.cuentas

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddAccountScreen(
    selectedDivisa: MutableState<Divisa>,
    viewModel: MainViewModel,
    navigateUp: () -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val digitPattern = remember { Regex("^\\d+\\.?\\d*$") }
    val buttonEnabled = remember { mutableStateOf(false) }
    val accountName = remember { mutableStateOf("") }
    val accountNameError = remember {
        mutableStateOf(false)
    }
    val accountBalance = remember { mutableStateOf("") }
    val accountBalanceError = remember {
        mutableStateOf(false)
    }
    buttonEnabled.value = accountBalance.value.isNotEmpty() && accountName.value.isNotEmpty()
    accountBalanceError.value = accountBalance.value.isEmpty()
    accountNameError.value = accountName.value.isEmpty()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.greeting_new_account),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = accountNameError.value,
            value = accountName.value,
            onValueChange = { if (it.length <= 30) accountName.value = it },
            label = { Text(stringResource(R.string.new_account_name)) }
        )
        if (accountNameError.value) Text(
            text = stringResource(R.string.new_account_enter_name_error),
            color = Color.Red,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.padding(vertical = 6.dp))
        Row {
            TextField(
                singleLine = true,
                isError = accountBalanceError.value,
                modifier = Modifier.weight(.6f),
                value = accountBalance.value,
                onValueChange = {
                    if (it.isEmpty() || it.matches(digitPattern)) {
                        accountBalance.value = it
                    }
                },
                label = { Text(stringResource(R.string.new_account_balance)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            TextField(
                modifier = Modifier.weight(.4f),
                readOnly = true,
                value = selectedDivisa.value.name,
                onValueChange = { }
            )
        }
        if (accountBalanceError.value) Text(
            text = stringResource(R.string.new_account_enter_balance_error),
            color = Color.Red,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.padding(vertical = 6.dp))

        Button(
            enabled = buttonEnabled.value,
            onClick = {
                viewModel.createOrUpdateAccount(
                    Account(
                        balance = accountBalance.value.toBigDecimal(),
                        name = accountName.value,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    )
                )
                navigateUp()
            }
        ) {
            Text(text = stringResource(R.string.new_account_add_account_button))
        }
    }
}