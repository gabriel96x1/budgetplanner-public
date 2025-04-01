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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditAccountScreen(
    selectedDivisa: MutableState<Divisa>,
    selectedAccount: MutableState<Account?>,
    viewModel: MainViewModel,
    navigateUp: () -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val openMySnackbar = remember { mutableStateOf(false)  }
    val snackBarMessage = remember { mutableStateOf("") }
    val accounts = viewModel.accountsList.observeAsState().value
    val digitPattern = remember { Regex("^\\d+\\.?\\d*$") }
    val buttonEnabled = remember { mutableStateOf(false) }
    val accountName = remember { mutableStateOf(selectedAccount.value?.name.toString()) }
    val accountNameError = remember {
        mutableStateOf(false)
    }
    val accountBalance = remember { mutableStateOf(selectedAccount.value?.balance.toString()) }
    val accountBalanceError = remember {
        mutableStateOf(false)
    }
    val dialogState = remember {
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
            text = stringResource(R.string.edit_account_string),
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
            modifier = Modifier.fillMaxWidth(),
            enabled = buttonEnabled.value,
            onClick = {
                val editedAccount = Account(
                    account_id = selectedAccount.value!!.account_id,
                    balance = accountBalance.value.toBigDecimal(),
                    name = accountName.value,
                    isSent = false,
                    timeStamp = "",
                    toDelete = false
                )
                selectedAccount.value = editedAccount
                viewModel.createOrUpdateAccount(
                    editedAccount
                )
                navigateUp()
            }
        ) {
            Text(text = stringResource(R.string.save_changes_edit_account))
        }

        Spacer(modifier = Modifier.padding(vertical = 6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            enabled = true,
            onClick = {
                dialogState.value = !dialogState.value
            }
        ) {
            Text(text = stringResource(R.string.delete_account_button), color = Color.White)
            Icon(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = null,
                tint = Color.White
            )
        }

        SnackbarWithoutScaffold(snackBarMessage.value, openMySnackbar.value) { openMySnackbar.value = it }
    }
    if (dialogState.value) {
        val context = LocalContext.current

        Dialog(onDismissRequest = {
            dialogState.value = !dialogState.value
        }) {

            Card {
                Column(
                    modifier = Modifier.padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.delete_account_title_dialog),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(vertical = 6.dp))
                    Text(
                        text = stringResource(R.string.delete_account_message_dialog),
                        textAlign = TextAlign.Justify
                    )
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = {

                                if (accounts!!.size > 1) {
                                    viewModel.deleteAccount(selectedAccount.value!!)
                                    selectedAccount.value = if (accounts.first() != selectedAccount.value)
                                        accounts.first()
                                    else
                                        accounts.last()
                                    navigateUp()
                                } else {
                                    dialogState.value = !dialogState.value
                                    openMySnackbar.value = true
                                    snackBarMessage.value =
                                        context.getString(R.string.not_deletable_last_account)
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.delete_account_aceptance), color = Color.White)
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                        TextButton(onClick = {
                            dialogState.value = !dialogState.value
                        }) {
                            Text(text = stringResource(R.string.cancel_button))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SnackbarWithoutScaffold(
    message: String,
    showSb: Boolean,
    openSnackbar: (Boolean) -> Unit
) {

    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()

    SnackbarHost(
        modifier = Modifier,
        hostState = snackState
    ){
        Snackbar(
            snackbarData = it
        )
    }

    if (showSb){
        LaunchedEffect(Unit) {
            snackScope.launch { snackState.showSnackbar(message) }
            openSnackbar(false)
        }

    }


}