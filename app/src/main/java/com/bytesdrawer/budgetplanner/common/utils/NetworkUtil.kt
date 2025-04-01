package com.bytesdrawer.budgetplanner.common.utils

import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.models.local.toRemoteObject
import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object NetworkUtil {
    fun accountToRequestBody(account: Account): RequestBody {
        return Gson().toJson(account.toRemoteObject()).toRequestBody()
    }

    fun accountTransferToRequestBody(accountTransfer: AccountTransfer): RequestBody {
        return Gson().toJson(accountTransfer.toRemoteObject()).toRequestBody()
    }

    fun categoryToRequestBody(category: Category): RequestBody {
        return Gson().toJson(category.toRemoteObject()).toRequestBody()
    }

    fun moneyMovementToRequestBody(moneyMovement: MoneyMovement): RequestBody {
        return Gson().toJson(moneyMovement.toRemoteObject()).toRequestBody()
    }

    fun notificationModelToRequestBody(notificationModel: NotificationModel): RequestBody {
        return Gson().toJson(notificationModel.toRemoteObject()).toRequestBody()
    }}