package com.bytesdrawer.budgetplanner.common.models.base

import java.math.BigDecimal

interface CategoryBase {
    val category_id: Long
    val parentCategoryId: Long?
    val name: String
    val icon: String
    val isIncome: Boolean
    val expenseLimit: BigDecimal
    val color: Long
    val order: Int
    val isSent: Boolean
    val timeStamp: String
}
