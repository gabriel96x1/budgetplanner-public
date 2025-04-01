package com.bytesdrawer.budgetplanner.recurrents

import android.app.AlarmManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.toMillis
import java.time.LocalDateTime

@Composable
fun NotificationItem(
    notificationModel: NotificationModel,
    viewModel: MainViewModel,
    navigateToEdit: () -> Unit
) {
    val context = LocalContext.current
    val nextDateToShow = dateStringToRegularFormat(notificationModel.nextDateToShow)
    val isActive = nextDateToShow!!.toMillis() > LocalDateTime.now().toMillis()
    val menuExpanded = remember { mutableStateOf(false) }
    val deleteNotificationDialogState = remember { mutableStateOf(false) }
    Card(
        elevation = CardDefaults.cardElevation(9.dp),
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(.90f)
                    .padding(start = 12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.name_notification_item), fontWeight = FontWeight.Bold)
                    Text(text = notificationModel.name)
                }
                if (notificationModel.isFiniteRepeating) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(R.string.number_of_times_notification_item), fontWeight = FontWeight.Bold)
                        Text(text = notificationModel.remainingTimes.toString())
                    }
                }
                Text(text = if (isActive) stringResource(R.string.next_notification) else stringResource(
                    R.string.last_notification
                ), fontWeight = FontWeight.Bold)
                Text(text = "${nextDateToShow.dayOfMonth}/${nextDateToShow.monthValue}/" +
                        "${nextDateToShow.year} ${nextDateToShow.hour}:${if (nextDateToShow.minute < 10) "0${nextDateToShow.minute}" else nextDateToShow.minute}")
                if (isActive) {
                    Text(
                        text = stringResource(R.string.active_notification_item),
                        color = Color(0xFF008F39)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.finalized_notification_item),
                        color = Color.Red
                    )
                }
            }
            IconButton(
                onClick = { menuExpanded.value = !menuExpanded.value },
                modifier = Modifier.weight(.10f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.more_vert),
                    contentDescription = null,
                )
                DropDownMenuOptions(
                    context.applicationContext,
                    notificationModel,
                    viewModel,
                    deleteNotificationDialogState,
                    isActive,
                    menuExpanded
                ) { navigateToEdit() }
            }
        }
    }
    if (deleteNotificationDialogState.value) {
        DeleteNotificationDialog(
            viewModel,
            deleteNotificationDialogState,
            notificationModel,
            context,
            context.applicationContext
                .getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
        )
    }
}

@Composable
private fun DropDownMenuOptions(
    context: Context,
    notificationModel: NotificationModel,
    viewModel: MainViewModel,
    deleteNotificationDialogState: MutableState<Boolean>,
    isActive: Boolean,
    menuExpanded: MutableState<Boolean>,
    navigateToEdit: () -> Unit
) {
    DropdownMenu(
        expanded = menuExpanded.value,
        onDismissRequest = { menuExpanded.value = !menuExpanded.value },
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.edit_notification_item))
            },
            onClick = {
                navigateToEdit()
                menuExpanded.value = !menuExpanded.value
            },
        )
        if (isActive) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.deactivate_notification_item))
                },
                onClick = {
                    val dateToModify = dateStringToRegularFormat(notificationModel.nextDateToShow)!!
                    val currentTime = LocalDateTime.now()
                        .withHour(dateToModify.hour)
                        .withMinute(dateToModify.minute)
                        .minusDays(1)
                    CreateNotificationUtil.cancelAlarm(
                        context,
                        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager?,
                        notificationModel.notification_id
                    )

                    viewModel.createOrUpdateNotification(
                        NotificationModel(
                            notificationModel.notification_id,
                            notificationModel.account_id,
                            notificationModel.category_id,
                            notificationModel.subcategory_id,
                            notificationModel.name,
                            notificationModel.customNotificationText,
                            notificationModel.comment,
                            notificationModel.category,
                            notificationModel.isIncome,
                            notificationModel.frequency,
                            notificationModel.remainingTimes,
                            notificationModel.isFiniteRepeating,
                            currentTime.toString(),
                            notificationModel.amount,
                            isSent = false,
                            timeStamp = "",
                            toDelete = false
                        )
                    )

                    menuExpanded.value = !menuExpanded.value
                },
            )
        }
        DropdownMenuItem(
            text = {
                Text(stringResource(id = R.string.delete_button))
            },
            onClick = {
                deleteNotificationDialogState.value = true
                menuExpanded.value = !menuExpanded.value
            },
        )
    }
}

@Composable
private fun DeleteNotificationDialog(
    viewModel: MainViewModel,
    dialogState: MutableState<Boolean>,
    notification: NotificationModel,
    context: Context,
    alarmMgr: AlarmManager?
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
                    text = stringResource(R.string.delete_notification_title_dialog),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Text(
                    text = stringResource(R.string.delete_notification_message_dialog),
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
                            viewModel.deleteNotification(notification)

                            CreateNotificationUtil.cancelAlarm(
                                context,
                                alarmMgr,
                                notification.notification_id
                            )
                        }) {
                        Text(text = stringResource(id = R.string.delete_button), color = Color.White)
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