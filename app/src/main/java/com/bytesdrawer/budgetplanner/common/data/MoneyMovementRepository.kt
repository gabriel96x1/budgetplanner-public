package com.bytesdrawer.budgetplanner.common.data

import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.models.local.MoneyMovement
import com.bytesdrawer.budgetplanner.common.models.remote.MoneyMovementRemote
import com.bytesdrawer.budgetplanner.common.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class MoneyMovementRepository(
    private val dao: BudgetPlannerDao,
    private val networkService: NetworkService
) {

    fun getTransactions(): Flow<List<MoneyMovement>> {
        return dao.getAllMoneyMovements()
    }

    fun createOrUpdateTransaction(moneyMovement: MoneyMovement) {
        dao.insertTransaction(moneyMovement)
    }

    fun deleteTransaction(moneyMovement: MoneyMovement) {
        dao.deleteTransaction(moneyMovement)
    }

    fun deleteAllTransactions() {
        dao.deleteAllMoneyMovements()
    }

    suspend fun getAllMoneyMovementFromNetwork(): Response<List<MoneyMovementRemote>> {
        return networkService.getAllMoneyMovement()
    }

    suspend fun getMoneyMovementFromNetwork(id: Long): Response<MoneyMovementRemote> {
        return networkService.getMoneyMovement(id.toString())
    }

    suspend fun createMoneyMovementOnNetwork(moneyMovement: MoneyMovement): Response<Unit> {
        return networkService.createMoneyMovement(NetworkUtil.moneyMovementToRequestBody(moneyMovement))
    }

    suspend fun updateMoneyMovementOnNetwork(moneyMovement: MoneyMovement): Response<Unit> {
        return networkService.updateMoneyMovement(NetworkUtil.moneyMovementToRequestBody(moneyMovement))
    }

    suspend fun deleteMoneyMovementOnNetwork(id: Long): Response<Unit> {
        return networkService.deleteMoneyMovement(id.toString())
    }

}