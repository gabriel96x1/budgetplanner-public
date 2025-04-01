package com.bytesdrawer.budgetplanner.common.models.remote

import com.bytesdrawer.budgetplanner.common.models.base.MoneyMovementBase
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class MoneyMovementRemote(
    @SerializedName("movement_id")
    val movement_id: String,

    @SerializedName("account_id")
    val account_id: String,

    @SerializedName("category_id")
    val category_id: String,

    @SerializedName("subCategory_id")
    val subCategory_id: String?,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("comment")
    val comment: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("isIncome")
    val isIncome: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("timestamp")
    val timeStamp: String
)

fun MoneyMovementRemote.toLocalObject(): MoneyMovement {
    return MoneyMovement(
        movement_id.toLong(),
        account_id.toLong(),
        category_id.toLong(),
        subCategory_id?.toLong(),
        BigDecimal(amount),
        category,
        "",
        comment,
        icon,
        isIncome.toBoolean(),
        date,
        isSent = true,
        timeStamp,
        toDelete = false
    )
}
