package com.bytesdrawer.budgetplanner.cuentas

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.bigDecimalParsed
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getNameOfTheMonth
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun AccountTransferItem(
    viewModel: MainViewModel,
    selectedDivisa: MutableState<Divisa>,
    accounts: List<Account>?,
    accountTransfer: AccountTransfer
) {
    val df = remember {
        DecimalFormat("#,###.##")
    }
    df.roundingMode = RoundingMode.DOWN
    val date = remember {
        mutableStateOf(dateStringToRegularFormat(accountTransfer.date))
    }

    val dialogState = remember {
        mutableStateOf(false)
    }

    val accountFrom = accounts?.first { it.account_id == accountTransfer.from_account_id }
    val accountTo = accounts?.first { it.account_id == accountTransfer.to_account_id }

    val context = LocalContext.current

    Card(
        modifier = Modifier.padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${date.value!!.dayOfMonth}-${getNameOfTheMonth(date.value!!.monthValue, context)}-${date.value!!.year}")
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
            ) {
                Text(
                    softWrap = true,
                    maxLines = 1,
                    text = stringResource(
                        R.string.from_account_transfer,
                        accountFrom?.name ?: ""
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.weight(.1f),
                        painter = painterResource(id = R.drawable.repeat),
                        contentDescription = null
                    )
                    Text(
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(.8f),
                        text = "${bigDecimalParsed(accountTransfer.amount, df)} ${selectedDivisa.value.name}"
                    )
                    IconButton(
                        modifier = Modifier.weight(.1f),
                        onClick = { dialogState.value = !dialogState.value }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.delete), contentDescription = null)
                    }
                }
                Text(
                    softWrap = true,
                    maxLines = 1,
                    text = stringResource(
                        R.string.to_account_transfer,
                        accountTo?.name ?: ""
                    )
                )
            }
        }
    }
    if (dialogState.value) {
        DeleteTransferDialog(viewModel, dialogState, accountTransfer, )
    }
}

@Composable
fun DeleteTransferDialog(
    viewModel: MainViewModel,
    dialogState: MutableState<Boolean>,
    accountTransfer: AccountTransfer
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
                    text = stringResource(R.string.delete_transference_title_dialog),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                    text = stringResource(R.string.delete_transference_message_dialog),
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
                            viewModel.deleteAccountTransfer(accountTransfer)
                            dialogState.value = !dialogState.value
                            viewModel.getAccountTransfers()
                        }) {
                        Text(text = stringResource(id = R.string.delete_button))
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