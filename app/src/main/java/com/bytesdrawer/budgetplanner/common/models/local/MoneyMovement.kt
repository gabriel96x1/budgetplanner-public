package com.bytesdrawer.budgetplanner.common.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bytesdrawer.budgetplanner.common.models.base.MoneyMovementBase
import com.bytesdrawer.budgetplanner.common.models.remote.MoneyMovementRemote
import java.math.BigDecimal

@Entity(tableName = "money_movements")
data class MoneyMovement(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "movement_id")
    override val movement_id: Long = 0,

    @ColumnInfo(name = "account_id")
    override val account_id: Long,

    @ColumnInfo(name = "category_id", defaultValue = "0")
    override val category_id: Long,

    @ColumnInfo(name = "subCategoryId", defaultValue = "NULL")
    override val subCategory_id: Long?,

    @ColumnInfo(name = "amount")
    override val amount: BigDecimal,

    @ColumnInfo(name = "category")
    override val category: String,

    @ColumnInfo(name = "subCategory")
    override val subCategory: String,

    @ColumnInfo(name = "comment")
    override val comment: String,

    @ColumnInfo(name = "icon")
    override val icon: String,

    @ColumnInfo(name = "isIncome")
    override val isIncome: Boolean,

    @ColumnInfo(name = "date")
    override val date: String,

    @ColumnInfo(name = "isSent")
    override val isSent: Boolean,

    @ColumnInfo(name = "timestamp")
    override val timeStamp: String,

    @ColumnInfo(name = "toDelete")
    val toDelete: Boolean
): MoneyMovementBase

fun MoneyMovement.toRemoteObject(): MoneyMovementRemote {
    return MoneyMovementRemote(
        movement_id.toString(),
        account_id.toString(),
        category_id.toString(),
        subCategory_id.toString(),
        amount.toString(),
        category,
        comment,
        icon,
        isIncome.toString(),
        date,
        timeStamp
    )
}
