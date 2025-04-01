package com.bytesdrawer.budgetplanner.common.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bytesdrawer.budgetplanner.common.models.base.AccountTransferBase
import com.bytesdrawer.budgetplanner.common.models.remote.AccountTransferRemote
import java.math.BigDecimal

@Entity("account_transfer")
data class AccountTransfer(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_transfer_id")
    override val account_transfer_id: Long = 0,

    @ColumnInfo(name = "from_account_id")
    override val from_account_id: Long,

    @ColumnInfo(name = "to_account_id")
    override val to_account_id: Long,

    @ColumnInfo(name = "category_id")
    override val category_id: Long,

    @ColumnInfo(name = "amount")
    override val amount: BigDecimal,

    @ColumnInfo(name = "category")
    override val category: String,

    @ColumnInfo(name = "icon")
    override val icon: String,

    @ColumnInfo(name = "date")
    override val date: String,

    @ColumnInfo(name = "isSent")
    override val isSent: Boolean,

    @ColumnInfo(name = "timestamp")
    override val timeStamp: String,

    @ColumnInfo(name = "toDelete")
    val toDelete: Boolean
) : AccountTransferBase

fun AccountTransfer.toRemoteObject(): AccountTransferRemote {
    return AccountTransferRemote(
        account_transfer_id.toString(),
        from_account_id.toString(),
        to_account_id.toString(),
        category_id.toString(),
        amount.toString(),
        category,
        icon,
        date,
        timeStamp
    )
}
