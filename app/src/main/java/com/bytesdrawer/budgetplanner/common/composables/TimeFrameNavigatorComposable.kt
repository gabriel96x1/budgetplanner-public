package com.bytesdrawer.budgetplanner.common.composables

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.Events
import com.bytesdrawer.budgetplanner.home.PeriodOfTime
import com.bytesdrawer.budgetplanner.home.textFromDateSelection
import java.time.LocalDateTime

@Composable
fun TimeFrameNavigatorComposable(
    timeLapseSelected: MutableState<PeriodOfTime?>,
    dateTime: MutableState<LocalDateTime>,
    stringDateTime: MutableState<String>,
    context: Context,
    analyticsEvents: Events? = null,
    onLeftClicked: () -> Unit,
    onRightClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (timeLapseSelected.value == PeriodOfTime.PERSO) Arrangement.Center else Arrangement.SpaceBetween
    ) {
        if (timeLapseSelected.value != PeriodOfTime.PERSO) {
            Icon(
                modifier = Modifier.clickable {
                    analyticsEvents?.trackEvent(Events.TIMEFRAME_NAVIGATION_LEFT)
                    onLeftClicked()
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
                    analyticsEvents?.trackEvent(Events.TIMEFRAME_NAVIGATION_RIGHT)
                    onRightClicked()
                },
                painter = painterResource(id = R.drawable.right),
                contentDescription = null
            )
        }
    }
}