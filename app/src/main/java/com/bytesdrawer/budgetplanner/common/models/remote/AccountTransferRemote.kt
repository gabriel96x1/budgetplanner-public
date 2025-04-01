package com.bytesdrawer.budgetplanner.common.models.remote

import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AccountTransferRemote(
    @SerializedName("account_transfer_id")
    val account_transfer_id: String,

    @SerializedName("from_account_id")
    val from_account_id: String,

    @SerializedName("to_account_id")
    val to_account_id: String,

    @SerializedName("category_id")
    val category_id: String,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("timestamp")
    val timeStamp: String
)

fun AccountTransferRemote.toLocalObject(): AccountTransfer {
    return AccountTransfer(
        account_transfer_id.toLong(),
        from_account_id.toLong(),
        to_account_id.toLong(),
        category_id.toLong(),
        BigDecimal(amount),
        category,
        icon,
        date,
        isSent = true,
        timeStamp,
        toDelete = false
    )
}