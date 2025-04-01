package com.bytesdrawer.budgetplanner.common.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bytesdrawer.budgetplanner.common.models.base.NotificationModelBase
import com.bytesdrawer.budgetplanner.common.models.remote.NotificationModelRemote
import java.math.BigDecimal

@Entity(tableName = "notification_model")
data class NotificationModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id")
    override val notification_id: Long = 0,

    @ColumnInfo(name = "account_id")
    override val account_id: Long,

    @ColumnInfo(name = "category_id", defaultValue = "0")
    override val category_id: Long,

    @ColumnInfo(name = "subcategory_id", defaultValue = "NULL")
    override val subcategory_id: Long?,

    @ColumnInfo(name = "name")
    override val name: String,

    @ColumnInfo(name = "customNotificationText")
    override val customNotificationText: String,

    @ColumnInfo(name = "comment")
    override val comment: String,

    @ColumnInfo(name = "category")
    override val category: String,

    @ColumnInfo(name = "isIncome")
    override val isIncome: Boolean,

    @ColumnInfo(name = "frequency")
    override val frequency: Int,

    @ColumnInfo(name = "remainingTimes", defaultValue = "0")
    override val remainingTimes: Int,

    @ColumnInfo(name = "isFiniteRepeating", defaultValue = "false")
    override val isFiniteRepeating: Boolean,

    @ColumnInfo(name = "nextDateToShow")
    override val nextDateToShow: String,

    @ColumnInfo(name = "amount")
    override val amount: BigDecimal,

    @ColumnInfo(name = "isSent")
    override val isSent: Boolean,

    @ColumnInfo(name = "timestamp")
    override val timeStamp: String,

    @ColumnInfo(name = "toDelete")
    val toDelete: Boolean
): NotificationModelBase

fun NotificationModel.toRemoteObject(): NotificationModelRemote {
    return NotificationModelRemote(
        notification_id.toString(),
        account_id.toString(),
        category_id.toString(),
        subcategory_id.toString(),
        name,
        customNotificationText,
        comment,
        category,
        isIncome.toString(),
        frequency.toString(),
        remainingTimes.toString(),
        isFiniteRepeating.toString(),
        nextDateToShow,
        amount.toString(),
        timeStamp
    )
}