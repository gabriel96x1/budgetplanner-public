package com.bytesdrawer.budgetplanner.common.data

import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.models.local.AccountTransfer
import com.bytesdrawer.budgetplanner.common.models.remote.AccountTransferRemote
import com.bytesdrawer.budgetplanner.common.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class AccountTransferRepository(
    private val dao: BudgetPlannerDao,
    private val networkService: NetworkService
) {
    fun getAccountTransfers(): Flow<List<AccountTransfer>> {
        return dao.getAllAccountTransfers()
    }

    fun createOrUpdateAccountTransfer(accountTransfer: AccountTransfer) {
        dao.insertAccountTransfer(accountTransfer)
    }

    fun deleteAccountTransfer(accountTransfer: AccountTransfer) {
        dao.deleteAccountTransfer(accountTransfer)
    }

    fun deleteAllAccountTransfers() {
        dao.deleteAllAccountTransfers()
    }

    suspend fun getAllAccountTransferFromNetwork(): Response<List<AccountTransferRemote>> {
        return networkService.getAllAccountTransfer()
    }

    suspend fun getAccountTransferFromNetwork(id: Long): Response<AccountTransferRemote> {
        return networkService.getAccountTransfer(id.toString())
    }

    suspend fun createAccountTransferOnNetwork(accountTransfer: AccountTransfer): Response<Unit> {
        return networkService.createAccountTransfer(NetworkUtil.accountTransferToRequestBody(accountTransfer))
    }

    suspend fun updateAccountTransferOnNetwork(accountTransfer: AccountTransfer): Response<Unit> {
        return networkService.updateAccountTransfer(NetworkUtil.accountTransferToRequestBody(accountTransfer))
    }

    suspend fun deleteAccountTransferOnNetwork(id: Long): Response<Unit> {
        return networkService.deleteAccountTransfer(id.toString())
    }
}