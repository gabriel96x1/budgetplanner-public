package com.bytesdrawer.budgetplanner.common.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.Events

@Composable
fun IncomeExpenseSwitchComposable(
    incomeExpenseState: MutableState<Boolean>,
    analyticsEvents: Events? = null
) {
    val language = Locale.current.language

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.expense),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (language == "es") {
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        }
        Switch(checked = incomeExpenseState.value, onCheckedChange = {
            if (it) {
                analyticsEvents?.trackEvent(Events.INCOME_SELECTED)
            } else {
                analyticsEvents?.trackEvent(Events.EXPENSE_SELECTED)
            }
            incomeExpenseState.value = it
        })
        Text(
            text = stringResource(R.string.income),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}