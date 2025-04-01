package com.bytesdrawer.budgetplanner.common.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.bytesdrawer.budgetplanner.MainActivity
import com.bytesdrawer.budgetplanner.NotificationsReceiver
import com.bytesdrawer.budgetplanner.R
import com.bytesdrawer.budgetplanner.RECURRENT_NOTIFICATION_ACTION
import com.bytesdrawer.budgetplanner.USER_HOOK_NOTIFICATION_ACTION
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.ui.theme.Purple40
import java.util.Calendar
import java.util.Random
import kotlin.streams.asSequence

object CreateNotificationUtil {

    private const val CHANNEL_NAME = "notification"
    const val CREATE_TRANSACTION_INTENT = "CREATE_TRANSACTION_INTENT"
    const val CREATE_TRANSACTION_BY_USER_INTENT = "CREATE_TRANSACTION_BY_USER_INTENT"
    const val HOOK_NOTIFICATION_CLICKED = "hook_notification_clicked"
    const val NOTIFICATION_ID = "NOTIFICATION_ID"

    fun createAlarmToNotify(
        context: Context,
        alarmManager: AlarmManager?,
        notificationId: Long,
        millisTime: Long
    ) {
        val intent = Intent(context, NotificationsReceiver::class.java).apply {
            action = RECURRENT_NOTIFICATION_ACTION
            putExtra(NOTIFICATION_ID, notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    millisTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                millisTime,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(
        context: Context,
        alarmManager: AlarmManager?,
        notificationId: Long
    ) {
        val intent = Intent(context, NotificationsReceiver::class.java).apply {
            action = RECURRENT_NOTIFICATION_ACTION
            putExtra(NOTIFICATION_ID, notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager?.cancel(pendingIntent)
    }

    fun create(context: Context, notificationModel: NotificationModel) {
        val setContentText = if (notificationModel.customNotificationText.isNotEmpty()) {
            notificationModel.customNotificationText
        } else if (notificationModel.comment.isNotEmpty()) {
            notificationModel.comment
        } else if (notificationModel.isIncome) {
            context.getString(R.string.reminder_for_income_notification, notificationModel.name)
        } else {
            context.getString(R.string.reminder_for_expense_notification, notificationModel.name)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channelId = getRandomString(notificationModel.notification_id.toInt())
        createChannel(channelId, notificationManager)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_logo_app1)
            .setColor(Purple40.toArgb())
            .setContentTitle(notificationModel.name)
            .setContentText(setContentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntentCreateTransactionByUser(context, notificationModel.notification_id))
            .addAction(R.drawable.notification_logo_app1,
                context.getString(R.string.add_transaction_notification), getPendingIntentCreateTransaction(context, notificationModel.notification_id)
            )

        notificationManager.notify(notificationModel.notification_id.toInt(), builder.build())
    }

    private fun createChannel(
        channelId: String,
        notificationManager: NotificationManager
    ) {
        val channel = NotificationChannel(
            channelId,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH)

        notificationManager.createNotificationChannel(channel)
    }

    private fun getPendingIntentCreateTransaction(context: Context, notificationId: Long): PendingIntent? {
        val intentCreateTransaction = Intent(context, MainActivity::class.java).apply {
            action = CREATE_TRANSACTION_INTENT
            putExtra(NOTIFICATION_ID, notificationId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intentCreateTransaction,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getPendingIntentCreateTransactionByUser(context: Context, notificationId: Long): PendingIntent? {
        val intentCreateTransaction = Intent(context, MainActivity::class.java).apply {
            action = CREATE_TRANSACTION_BY_USER_INTENT
            putExtra(NOTIFICATION_ID, notificationId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intentCreateTransaction,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getRandomString(notificationId: Int): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        return Random().ints(10, notificationId, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
    }
    
    private fun userHookPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = HOOK_NOTIFICATION_CLICKED
        }
        return PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun createUserHookNotification(
        context: Context
    ) {
        val notificationManager = context.applicationContext.getSystemService(NotificationManager::class.java)
        val random = (0..9).random()
        val channelId = getRandomString(random)
        createChannel(channelId, notificationManager)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_logo_app1)
            .setColor(Purple40.toArgb())
            .setContentTitle(context.getString(R.string.dont_forget_title_notification))
            .setContentText(context.getString(R.string.dont_forget_message_notification))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(userHookPendingIntent(context))

        notificationManager.notify(random, builder.build())
    }

    private fun getHookPendingIntent1(
        context: Context,
        intent: Intent
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getHookPendingIntent2(
        context: Context,
        intent: Intent
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            2,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    
    fun createUserHookAlarm(
        context: Context
    ) {
        val alarmManager = context.applicationContext
            .getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
        val intent = Intent(context, NotificationsReceiver::class.java).apply {
            action = USER_HOOK_NOTIFICATION_ACTION
        }

        alarmManager?.cancel(getHookPendingIntent1(context, intent))
        alarmManager?.cancel(getHookPendingIntent2(context, intent))

        val calendar1: Calendar = Calendar.getInstance()
        calendar1.timeInMillis = System.currentTimeMillis()
        calendar1.set(Calendar.HOUR_OF_DAY, 14)
        calendar1.set(Calendar.MINUTE, 0)
        calendar1.set(Calendar.SECOND, 0)

        if (Calendar.getInstance().after(calendar1)) {
            calendar1.add(Calendar.DATE, 1)
        }

        val calendar2: Calendar = Calendar.getInstance()
        calendar2.timeInMillis = System.currentTimeMillis()
        calendar2.set(Calendar.HOUR_OF_DAY, 20)
        calendar2.set(Calendar.MINUTE, 0)
        calendar2.set(Calendar.SECOND, 0)

        Log.d("Hook Notification", "Running")

        if (Calendar.getInstance().after(calendar2)) {
            calendar2.add(Calendar.DATE, 1)
            Log.d("Hook Notification", "1More Day")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar1.timeInMillis,
                    getHookPendingIntent1(context, intent)
                )
            }
        } else {
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar1.timeInMillis,
                getHookPendingIntent1(context, intent)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar2.timeInMillis,
                    getHookPendingIntent2(context, intent)
                )
            }
        } else {
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar2.timeInMillis,
                getHookPendingIntent2(context, intent)
            )
        }
    }
}