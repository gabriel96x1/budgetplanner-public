package com.bytesdrawer.budgetplanner.common.models.remote

import com.bytesdrawer.budgetplanner.common.models.base.NotificationModelBase
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class NotificationModelRemote(
    @SerializedName("notification_id")
    val notification_id: String,

    @SerializedName("account_id")
    val account_id: String,

    @SerializedName("category_id")
    val category_id: String,

    @SerializedName("subcategory_id")
    val subcategory_id: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("customNotificationText")
    val customNotificationText: String,

    @SerializedName("comment")
    val comment: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("isIncome")
    val isIncome: String,

    @SerializedName("frequency")
    val frequency: String,

    @SerializedName("remainingTimes")
    val remainingTimes: String,

    @SerializedName("isFiniteRepeating")
    val isFiniteRepeating: String,

    @SerializedName("nextDateToShow")
    val nextDateToShow: String,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("timestamp")
    val timeStamp: String
)

fun NotificationModelRemote.toLocalObject(): NotificationModel {
    return NotificationModel(
        notification_id.toLong(),
        account_id.toLong(),
        category_id.toLong(),
        subcategory_id?.toLong(),
        name,
        customNotificationText,
        comment,
        category,
        isIncome.toBoolean(),
        frequency.toInt(),
        remainingTimes.toInt(),
        isFiniteRepeating.toBoolean(),
        nextDateToShow,
        BigDecimal(amount),
        isSent = true,
        timeStamp,
        toDelete = false
    )
}
