package com.bytesdrawer.budgetplanner.common.data

import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.models.local.Account
import com.bytesdrawer.budgetplanner.common.models.remote.AccountRemote
import com.bytesdrawer.budgetplanner.common.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class AccountRepository(
    private val dao: BudgetPlannerDao,
    private val networkService: NetworkService
) {

    fun getAccounts(): Flow<List<Account>> {
        return dao.getAllAccounts()
    }

    fun createOrUpdateAccount(account: Account) {
        dao.insertAccount(account)
    }

    fun deleteAccount(account: Account) {
        dao.deleteAccount(account)
    }

    fun deleteAllAccounts() {
        dao.deleteAllAccounts()
    }

    suspend fun getAccountsFromNetwork(): Response<List<AccountRemote>> {
        return networkService.getAllAccounts()
    }

    suspend fun getAccountFromNetwork(accountId: Long): Response<AccountRemote> {
        return networkService.getAccount(accountId.toString())
    }

    suspend fun createAccountOnNetwork(account: Account): Response<Unit> {
        return networkService.createAccount(NetworkUtil.accountToRequestBody(account))
    }

    suspend fun updateAccountOnNetwork(account: Account): Response<Unit> {
        return networkService.updateAccount(NetworkUtil.accountToRequestBody(account))
    }

    suspend fun deleteAccountOnNetwork(accountId: Long): Response<Unit> {
        return networkService.deleteAccount(accountId.toString())
    }



}