package com.bytesdrawer.budgetplanner.common.models.base

import java.math.BigDecimal

interface AccountBase {
    val account_id: Long
    val balance: BigDecimal
    val name: String
    val isSent: Boolean
    val timeStamp: String
}
