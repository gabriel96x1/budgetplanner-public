package com.bytesdrawer.budgetplanner.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import java.time.LocalDateTime

@Composable
fun TimeFrameSelectionComposable(
    dateTime: MutableState<LocalDateTime>,
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateSelectionDialogState: MutableState<Boolean>,
    updateFilteredTransactions: () -> Unit,
    supressPerso: Boolean = false,
    analyticsEvents: Events? = null
) {
    val menuExpanded = remember { mutableStateOf(false) }
    val currentSelectedTimelapseString = when (timeLapseSelected.value) {
        PeriodOfTime.DAY -> stringResource(R.string.day)
        PeriodOfTime.WEEK -> stringResource(R.string.week)
        PeriodOfTime.HALF_MONTH -> stringResource(id = R.string.half_month)
        PeriodOfTime.MONTH -> stringResource(R.string.month)
        PeriodOfTime.YEAR -> stringResource(R.string.year)
        PeriodOfTime.PERSO -> stringResource(R.string.custom)
        null -> stringResource(R.string.month)
    }
    Box {
        Row(
            modifier = Modifier.clickable { menuExpanded.value = !menuExpanded.value },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = currentSelectedTimelapseString, fontSize = 14.sp)
            Icon(
                painter = painterResource(
                    id = if (!menuExpanded.value)
                        R.drawable.arrow_drop_up
                    else
                        R.drawable.arrow_drop_down),
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = menuExpanded.value,
            onDismissRequest = { menuExpanded.value = !menuExpanded.value },
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.day))
                },
                onClick = {
                    timeLapseSelected.value = PeriodOfTime.DAY
                    dateTime.value = LocalDateTime.now()
                    analyticsEvents?.trackEvent(Events.TIMEFRAME_SELECTED_DAY)
                    updateFilteredTransactions()
                    menuExpanded.value = !menuExpanded.value
                },
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.week))
                },
                onClick = {
                    timeLapseSelected.value = PeriodOfTime.WEEK
                    dateTime.value = LocalDateTime.now()
                    analyticsEvents?.trackEvent(Events.TIMEFRAME_SELECTED_WEEK)
                    updateFilteredTransactions()
                    menuExpanded.value = !menuExpanded.value
                },
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.half_month))
                },
                onClick = {
                    timeLapseSelected.value = PeriodOfTime.HALF_MONTH
                    dateTime.value = LocalDateTime.now()
                    analyticsEvents?.trackEvent(Events.TIMEFRAME_SELECTED_WEEK)
                    updateFilteredTransactions()
                    menuExpanded.value = !menuExpanded.value
                },
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.month))
                },
                onClick = {
                    timeLapseSelected.value = PeriodOfTime.MONTH
                    dateTime.value = LocalDateTime.now()
                    analyticsEvents?.trackEvent(Events.TIMEFRAME_SELECTED_MONTH)
                    updateFilteredTransactions()
                    menuExpanded.value = !menuExpanded.value
                },
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.year))
                },
                onClick = {
                    timeLapseSelected.value = PeriodOfTime.YEAR
                    dateTime.value = LocalDateTime.now()
                    analyticsEvents?.trackEvent(Events.TIMEFRAME_SELECTED_YEAR)
                    updateFilteredTransactions()
                    menuExpanded.value = !menuExpanded.value
                },
            )

            if (!supressPerso) {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.custom))
                    },
                    onClick = {
                        dateSelectionDialogState.value = !dateSelectionDialogState.value
                        analyticsEvents?.trackEvent(Events.TIMEFRAME_SELECTED_PERSO)
                        menuExpanded.value = !menuExpanded.value
                    },
                )
            }
        }
    }
}