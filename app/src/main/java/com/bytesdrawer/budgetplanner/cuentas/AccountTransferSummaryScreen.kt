package com.bytesdrawer.budgetplanner.cuentas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.MainViewModel
import com.bytesdrawer.budgetplanner.common.composables.DateRangePickerDialog
import com.bytesdrawer.budgetplanner.common.composables.TimeFrameSelectionComposable
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.utils.Divisa
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.common.utils.compareSelectedDateWithTransactionDateForAccountTransfers
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.toMillis
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import com.bytesdrawer.budgetplanner.home.addTime
import com.bytesdrawer.budgetplanner.home.substractTime
import com.bytesdrawer.budgetplanner.home.textFromDateSelection
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTransferSummaryScreen(
    selectedDivisa: MutableState<Divisa>,
    accounts: List<Account>?,
    viewModel: MainViewModel,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    stringDateTime: MutableState<String>,
    transactions: List<AccountTransfer>?,
    navigateToCreateTransference: () -> Unit
) {

    val context = LocalContext.current
    val dateTime = remember {
        mutableStateOf(LocalDateTime.now())
    }

    val filteredTransactions = remember {
        mutableStateOf(
            transactions?.filter {
                it.category == "Transferencias_Especial_Plus20"
                        && compareSelectedDateWithTransactionDateForAccountTransfers(timeLapseSelected, dateTime, it, stringDateTime)
            }
        )
    }

    LaunchedEffect(transactions) {
        filteredTransactions.value = transactions?.filter {
            it.category == "Transferencias_Especial_Plus20"
                    && compareSelectedDateWithTransactionDateForAccountTransfers(timeLapseSelected, dateTime, it, stringDateTime)
        }
    }

    val dateSelectionDialogState = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimeFrameSelectionComposable(
                    dateTime,
                    timeLapseSelected,
                    dateSelectionDialogState,
                    {
                        filteredTransactions.value = transactions?.filter {
                            it.category == "Transferencias_Especial_Plus20"
                                    && compareSelectedDateWithTransactionDateForAccountTransfers(timeLapseSelected, dateTime, it, stringDateTime)
                        }
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (timeLapseSelected.value == PeriodOfTime.PERSO) Arrangement.Center else Arrangement.SpaceBetween
                ) {
                    if (timeLapseSelected.value != PeriodOfTime.PERSO) {
                        Icon(
                            modifier = Modifier.clickable {
                                substractTime(dateTime, timeLapseSelected.value)
                                filteredTransactions.value = transactions?.filter {
                                    it.category == "Transferencias_Especial_Plus20"
                                            && compareSelectedDateWithTransactionDateForAccountTransfers(timeLapseSelected, dateTime, it, stringDateTime)
                                }
                            },
                            painter = painterResource(id = R.drawable.left),
                            contentDescription = null
                        )
                    }
                    Text(
                        text = textFromDateSelection(dateTime.value, timeLapseSelected.value, stringDateTime.value, context),
                        fontSize = 14.sp
                    )
                    if (timeLapseSelected.value != PeriodOfTime.PERSO) {
                        Icon(
                            modifier = Modifier.clickable {
                                addTime(dateTime, timeLapseSelected.value)
                                filteredTransactions.value = transactions?.filter {
                                    it.category == "Transferencias_Especial_Plus20"
                                            && compareSelectedDateWithTransactionDateForAccountTransfers(timeLapseSelected, dateTime, it, stringDateTime)
                                }
                            },
                            painter = painterResource(id = R.drawable.right),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Button(onClick = { navigateToCreateTransference() }) {
            Icon(
                painter = painterResource(id = R.drawable.repeat),
                contentDescription = null
            )
        }
        Text(text = stringResource(R.string.create_new_transference_button))

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        if (!filteredTransactions.value.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                filteredTransactions.value!!.forEach {
                    AccountTransferItem(viewModel,selectedDivisa,accounts ,it)
                }
            }
        }
    }

    if (dateSelectionDialogState.value) {
        DateRangePickerDialog(
            dateSelectionDialogState,
            stringDateTime,
            timeLapseSelected
        ) {
            filteredTransactions.value = transactions?.filter {
                it.category == "Transferencias_Especial_Plus20"
                        && compareSelectedDateWithTransactionDateForAccountTransfers(timeLapseSelected, dateTime, it, stringDateTime)
            }?.sortedByDescending { it.date }
        }
    }
}