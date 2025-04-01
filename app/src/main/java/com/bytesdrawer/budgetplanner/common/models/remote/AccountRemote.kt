package com.bytesdrawer.budgetplanner.common.models.remote

import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AccountRemote(
    @SerializedName("account_id")
    val account_id: String,

    @SerializedName("balance")
    val balance: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("timestamp")
    val timeStamp: String
)

fun AccountRemote.toLocalObject(): Account {
    return Account(
        account_id = account_id.toLong(),
        balance = BigDecimal(balance),
        name = name,
        isSent = true,
        timeStamp = timeStamp,
        toDelete = false
    )
}

