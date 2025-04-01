package com.bytesdrawer.budgetplanner.common.models.base

import java.math.BigDecimal

interface NotificationModelBase {
    val notification_id: Long
    val account_id: Long
    val category_id: Long
    val subcategory_id: Long?
    val name: String
    val customNotificationText: String
    val comment: String
    val category: String
    val isIncome: Boolean
    val frequency: Int
    val remainingTimes: Int
    val isFiniteRepeating: Boolean
    val nextDateToShow: String
    val amount: BigDecimal
    val isSent: Boolean
    val timeStamp: String
}
