package com.bytesdrawer.budgetplanner.common.models.base

import java.math.BigDecimal

interface AccountTransferBase {
    val account_transfer_id: Long
    val from_account_id: Long
    val to_account_id: Long
    val category_id: Long
    val amount: BigDecimal
    val category: String
    val icon: String
    val date: String
    val isSent: Boolean
    val timeStamp: String
}
