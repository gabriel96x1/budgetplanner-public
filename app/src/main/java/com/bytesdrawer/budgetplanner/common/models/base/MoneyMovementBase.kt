package com.bytesdrawer.budgetplanner.common.models.base

import java.math.BigDecimal

interface MoneyMovementBase {
    val movement_id: Long
    val account_id: Long
    val category_id: Long
    val subCategory_id: Long?
    val amount: BigDecimal
    val category: String
    val subCategory: String
    val comment: String
    val icon: String
    val isIncome: Boolean
    val date: String
    val isSent: Boolean
    val timeStamp: String
}
