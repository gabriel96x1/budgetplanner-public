package com.bytesdrawer.budgetplanner

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil
import com.bytesdrawer.budgetplanner.common.utils.CreateNotificationUtil.NOTIFICATION_ID
import com.bytesdrawer.budgetplanner.common.utils.PersistenceSetupUtil
import com.bytesdrawer.budgetplanner.common.utils.dateStringToRegularFormat
import com.bytesdrawer.budgetplanner.common.utils.getIncrementDateFromFrequencyNumber
import com.bytesdrawer.budgetplanner.common.utils.toMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

const val RECURRENT_NOTIFICATION_ACTION = "RECURRENT_NOTIFICATION_ACTION"
const val USER_HOOK_NOTIFICATION_ACTION = "USER_HOOK_NOTIFICATION_ACTION"

class NotificationsReceiver : BroadcastReceiver() {

    private lateinit var dao: BudgetPlannerDao
    lateinit var context: Context

    override fun onReceive(receivedContext: Context?, intent: Intent?) {
        if (intent != null && receivedContext != null) {
            Log.d("NotificationsReceiver", "Intent Launched")
            when (intent.action) {
                USER_HOOK_NOTIFICATION_ACTION -> {
                    context = receivedContext.applicationContext
                    displayHookNotification()
                }
                RECURRENT_NOTIFICATION_ACTION -> {
                    context = receivedContext.applicationContext
                    dao = PersistenceSetupUtil.getDao(context)
                    val notificationId = intent.extras!!.getLong(NOTIFICATION_ID)
                    displayRecurrentNotification(notificationId)
                }

            }
        }
    }

    private fun displayRecurrentNotification(notificationId: Long) {
        Log.d("NotificationId", notificationId.toString())
        CoroutineScope(Dispatchers.Default).launch {
            val notification = PersistenceSetupUtil.getNotification(dao, this, notificationId)
            Log.d("Notification", notification.toString())
            CreateNotificationUtil.create(context, notification!!)
            if (notification.frequency != 0) {
                val alarmManager = context
                    .getSystemService(Context.ALARM_SERVICE) as? AlarmManager?

                val nextDateToShow = getIncrementDateFromFrequencyNumber(
                    notification.frequency,
                    dateStringToRegularFormat(notification.nextDateToShow)!!)

                var remainingTimes = notification.remainingTimes

                Log.d("RemainingTimes", remainingTimes.toString())

                if (remainingTimes > 0 || !notification.isFiniteRepeating) {
                    if (notification.isFiniteRepeating) remainingTimes -= 1
                    val nextNotification = NotificationModel(
                        notification_id = notification.notification_id,
                        account_id = notification.account_id,
                        category_id = notification.category_id,
                        subcategory_id = notification.subcategory_id,
                        name = notification.name,
                        customNotificationText = notification.customNotificationText,
                        comment = notification.comment,
                        category = notification.category,
                        isIncome = notification.isIncome,
                        frequency = notification.frequency,
                        remainingTimes = remainingTimes,
                        nextDateToShow = nextDateToShow.toString(),
                        amount = notification.amount,
                        isFiniteRepeating = notification.isFiniteRepeating,
                        isSent = false,
                        timeStamp = "",
                        toDelete = false
                    )
                    Log.d("PreviousDate", notification.nextDateToShow)
                    Log.d("NextDate", nextDateToShow.toString())
                    CreateNotificationUtil.createAlarmToNotify(
                        context, alarmManager, notification.notification_id, nextDateToShow!!.toMillis()
                    )
                    PersistenceSetupUtil.updateNotificationNextDate(dao, this, nextNotification)
                }
            }
        }
    }

    private fun displayHookNotification() {
        Log.d("Hook Notification", "Launched!")
        CreateNotificationUtil.createUserHookNotification(context)
        runBlocking {
            delay(1000)
            CreateNotificationUtil.createUserHookAlarm(context)
        }
    }
}