package com.bytesdrawer.budgetplanner.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.common.utils.PurchasesManager
import com.bytesdrawer.budgetplanner.common.utils.SharedPreferencesUtil
import com.bytesdrawer.budgetplanner.common.utils.toMillis
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun BuyCTADialog(
    dialogState: MutableState<Boolean>,
    preferencesUtil: SharedPreferencesUtil,
    purchasesManger: PurchasesManager
) {
    val finalTime = remember {
        preferencesUtil.getPromoTime()
    }

    val ticks = remember {
        mutableStateOf(true)
    }

    val difference = remember {
        mutableStateOf(
            Instant
                .ofEpochMilli(finalTime - LocalDateTime.now().toMillis())
                .atZone(ZoneId.of("GMT"))
                .toLocalDateTime()
        )
    }

    val daysDifference = remember {
        mutableStateOf(
            getDaysDifference(
                LocalDateTime.now(),
                Instant
                    .ofEpochMilli(finalTime)
                    .atZone(ZoneId.of("GMT"))
                    .toLocalDateTime()
            )
        )
    }

    LaunchedEffect(true) {
        if (finalTime - LocalDateTime.now().toMillis() <= 0L) {
            preferencesUtil.setPromoTime(LocalDateTime.now().plusDays(3).toMillis())
        }
        while (true) {
            delay(1000)  // Espera un segundo
            ticks.value = !ticks.value
        }
    }

    LaunchedEffect(ticks.value) {
        difference.value = Instant
            .ofEpochMilli(finalTime - LocalDateTime.now().toMillis())
            .atZone(ZoneId.of("GMT"))
            .toLocalDateTime()

        daysDifference.value = getDaysDifference(
            LocalDateTime.now(),
            Instant
                .ofEpochMilli(finalTime)
                .atZone(ZoneId.of("GMT"))
                .toLocalDateTime()
        )
    }
    
    Dialog(
        onDismissRequest = { dialogState.value = !dialogState.value },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier.clickable { dialogState.value = !dialogState.value }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), 
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = stringResource(R.string.promotion_ends),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "${daysDifference.value?.toDays() } : ${difference.value.hour} : ${difference.value.minute} : ${difference.value.second}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "DD HH MM SS",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.unlocked_powers),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(.9f)
                ) {
                    Row {
                        Icon(painter = painterResource(id = R.drawable.cloud_done), contentDescription = null)
                        Text(
                            text = stringResource(R.string.cloud_sync_ad),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                        )
                    }
                    Row {
                        Icon(painter = painterResource(id = R.drawable.no_ads), contentDescription = null)
                        Text(
                            text = stringResource(R.string.delete_ads_ad),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                        )
                    }
                    Row {
                        Icon(painter = painterResource(id = R.drawable.stars), contentDescription = null)
                        Text(
                            text = stringResource(R.string.unlock_predictions),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Column (
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Text(
                            text = "$599",
                            fontSize = 18.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Text(
                            text = stringResource(R.string.year_ad),
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                    Row {
                        Text(
                            text = "$199",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = stringResource(R.string.year_ad),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "66.7% OFF",
                        color = Color.Red,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { purchasesManger.getPurchaseOptions() }) {
                    Text(text = stringResource(R.string.buy_cta_button))
                }
            }
        }
    }
}

private fun getDaysDifference(startDateTime: LocalDateTime, endDateTime: LocalDateTime): Duration? {
    return Duration.between(startDateTime, endDateTime)
}