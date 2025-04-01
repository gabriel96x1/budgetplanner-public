package com.bytesdrawer.budgetplanner.common.composables

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.TransactionDateCreation
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    dateSelectionDialogState: MutableState<Boolean>,
    dateToSaveTransaction: MutableState<String>,
    selectedTransactionDate: MutableState<TransactionDateCreation>
) {
    val todayDate = remember {
        LocalDateTime.now().year
    }
    Dialog(
        onDismissRequest = {
            selectedTransactionDate.value = TransactionDateCreation.TODAY
            dateSelectionDialogState.value = !dateSelectionDialogState.value
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = Instant.now().toEpochMilli(),
                yearRange = (todayDate - 10)..(todayDate + 10)
            )
            DatePicker(state = datePickerState, modifier = Modifier.padding(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    selectedTransactionDate.value = TransactionDateCreation.TODAY
                    dateSelectionDialogState.value = !dateSelectionDialogState.value
                }) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
                TextButton(onClick = {
                    dateSelectionDialogState.value = !dateSelectionDialogState.value
                    dateToSaveTransaction.value = Instant
                        .ofEpochMilli(datePickerState.selectedDateMillis!!)
                        .atZone(ZoneId.of("GMT"))
                        .toLocalDateTime()
                        .toString()
                }) {
                    Text(text = stringResource(id = R.string.save_button))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerDialog(
    dateSelectionDialogState: MutableState<Boolean>,
    dateToSaveNotification: MutableState<String>
) {
    val todayDate = remember {
        LocalDateTime.now().year
    }
    val selectedDate = remember {
        mutableStateOf(false)
    }
    val tempDateAndTimeSelection = remember {
        mutableStateOf(dateToSaveNotification.value)
    }
    Dialog(
        onDismissRequest = {
            dateSelectionDialogState.value = !dateSelectionDialogState.value
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        if (selectedDate.value) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                val timePickerState = rememberTimePickerState()
                IconButton(
                    onClick = { selectedDate.value = false },
                    modifier = Modifier.padding(6.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        dateSelectionDialogState.value = !dateSelectionDialogState.value
                    }) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                    TextButton(onClick = {
                        val dateSelection = dateStringToRegularFormat(tempDateAndTimeSelection.value)
                        val completeDateTime = LocalDateTime.of(
                            dateSelection!!.year,
                            dateSelection.monthValue,
                            dateSelection.dayOfMonth,
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        dateToSaveNotification.value = completeDateTime.toString()
                        dateSelectionDialogState.value = !dateSelectionDialogState.value
                    }) {
                        Text(text = stringResource(id = R.string.save_button))
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = Instant.now().toEpochMilli(),
                    yearRange = (todayDate - 10)..(todayDate + 10)
                )
                DatePicker(state = datePickerState, modifier = Modifier.padding(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        dateSelectionDialogState.value = !dateSelectionDialogState.value
                    }) {
                        Text(text = stringResource(id = R.string.cancel_button))
                    }
                    TextButton(onClick = {
                        tempDateAndTimeSelection.value = Instant
                            .ofEpochMilli(datePickerState.selectedDateMillis!!)
                            .atZone(ZoneId.of("GMT"))
                            .toLocalDateTime()
                            .toString()
                        selectedDate.value = true
                    }) {
                        Text(text = stringResource(R.string.next_button))
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DateEditPickerDialog(
    dateSelectionDialogState: MutableState<Boolean>,
    dateToSaveTransaction: MutableState<String>,
) {
    val todayDate = remember {
        LocalDateTime.now().year
    }
    Dialog(
        onDismissRequest = {
            dateSelectionDialogState.value = !dateSelectionDialogState.value
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateStringToRegularFormat(dateToSaveTransaction.value)
                    !!.atZone(ZoneId.of("GMT"))
                    .toInstant()
                    .toEpochMilli(),
                yearRange = (todayDate - 10)..(todayDate + 10)
            )
            DatePicker(state = datePickerState, modifier = Modifier.padding(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    dateSelectionDialogState.value = !dateSelectionDialogState.value
                }) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
                TextButton(onClick = {
                    dateSelectionDialogState.value = !dateSelectionDialogState.value
                    dateToSaveTransaction.value = Instant
                        .ofEpochMilli(datePickerState.selectedDateMillis!!)
                        .atZone(ZoneId.of("GMT"))
                        .toLocalDateTime()
                        .toString()
                }) {
                    Text(text = stringResource(id = R.string.save_button))
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DateRangePickerDialog(
    dateSelectionDialogState: MutableState<Boolean>,
    dateToUse: MutableState<String>,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    updateFilteredTransactions: () -> Unit
) {
    val todayDate = remember {
        LocalDateTime.now().year
    }
    Dialog(
        onDismissRequest = {
            dateSelectionDialogState.value = !dateSelectionDialogState.value
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            val dateRangePickerState = rememberDateRangePickerState(
                yearRange = (todayDate - 10)..(todayDate + 10)
            )
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .weight(0.9f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(0.1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    dateSelectionDialogState.value = !dateSelectionDialogState.value
                }) {
                    Text(text = stringResource(id = R.string.cancel_button))
                }
                TextButton(onClick = {
                    if (dateRangePickerState.selectedEndDateMillis != null && dateRangePickerState.selectedStartDateMillis != null) {
                        timeLapseSelected.value = PeriodOfTime.PERSO
                        dateToUse.value = "${Instant
                            .ofEpochMilli(dateRangePickerState.selectedStartDateMillis!!)
                            .atZone(ZoneId.of("GMT"))
                            .toLocalDateTime()}E${Instant
                            .ofEpochMilli(dateRangePickerState.selectedEndDateMillis!!)
                            .atZone(ZoneId.of("GMT"))
                            .toLocalDateTime()}"
                        dateSelectionDialogState.value = !dateSelectionDialogState.value
                        Log.d("PersoDateTime", dateToUse.value)
                        updateFilteredTransactions()
                    } else {
                        dateSelectionDialogState.value = !dateSelectionDialogState.value
                    }
                }) {
                    Text(text = stringResource(id = R.string.save_button))
                }
            }
        }
    }
}