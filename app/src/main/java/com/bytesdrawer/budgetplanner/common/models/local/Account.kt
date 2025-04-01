package com.bytesdrawer.budgetplanner.common.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bytesdrawer.budgetplanner.common.models.base.AccountBase
import com.bytesdrawer.budgetplanner.common.models.remote.AccountRemote
import java.math.BigDecimal

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_id")
    override val account_id: Long = 0,

    @ColumnInfo(name = "balance")
    override val balance: BigDecimal,

    @ColumnInfo(name = "name")
    override val name: String,

    @ColumnInfo(name = "isSent")
    override val isSent: Boolean,

    @ColumnInfo(name = "timestamp")
    override val timeStamp: String,

    @ColumnInfo(name = "toDelete")
    val toDelete: Boolean
) : AccountBase

fun Account.toRemoteObject(): AccountRemote {
    return AccountRemote(
        account_id = account_id.toString(),
        name = name,
        balance = balance.toString(),
        timeStamp = timeStamp
    )
}
