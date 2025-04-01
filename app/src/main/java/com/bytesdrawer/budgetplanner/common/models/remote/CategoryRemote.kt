package com.bytesdrawer.budgetplanner.common.models.remote

import com.bytesdrawer.budgetplanner.common.models.base.CategoryBase
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CategoryRemote(
    @SerializedName("category_id")
    val category_id: String,

    @SerializedName("parentCategory_id")
    val parentCategoryId: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("isIncome")
    val isIncome: String,

    @SerializedName("expenseLimit")
    val expenseLimit: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("order")
    val order: String,

    @SerializedName("timestamp")
    val timeStamp: String
)

fun CategoryRemote.toLocalObject(): Category {
    return Category(
        category_id.toLong(),
        if (parentCategoryId.isNullOrEmpty()) null else parentCategoryId.toLong(),
        name,
        icon,
        isIncome.toBoolean(),
        BigDecimal(expenseLimit),
        color.toLong(),
        order.toInt(),
        isSent = true,
        timeStamp,
        toDelete = false
    )
}
