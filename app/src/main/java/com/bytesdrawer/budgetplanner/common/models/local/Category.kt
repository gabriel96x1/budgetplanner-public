package com.bytesdrawer.budgetplanner.common.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bytesdrawer.budgetplanner.common.models.base.CategoryBase
import com.bytesdrawer.budgetplanner.common.models.remote.CategoryRemote
import java.math.BigDecimal

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    override val category_id: Long = 0,

    @ColumnInfo(name = "parentCategory_id")
    override val parentCategoryId: Long?,

    @ColumnInfo(name = "name")
    override val name: String,

    @ColumnInfo(name = "icon")
    override val icon: String,

    @ColumnInfo(name = "isIncome")
    override val isIncome: Boolean,

    @ColumnInfo(name = "expenseLimit")
    override val expenseLimit: BigDecimal,

    @ColumnInfo(name = "color")
    override val color: Long,

    @ColumnInfo(name = "order")
    override val order: Int,

    @ColumnInfo(name = "isSent")
    override val isSent: Boolean,

    @ColumnInfo(name = "timestamp")
    override val timeStamp: String,

    @ColumnInfo(name = "toDelete")
    val toDelete: Boolean
) : CategoryBase

fun Category.toRemoteObject(): CategoryRemote {
    return CategoryRemote(
        category_id.toString(),
        parentCategoryId.toString(),
        name,
        icon,
        isIncome.toString(),
        expenseLimit.toString(),
        color.toString(),
        order.toString(),
        timeStamp
    )
}