package com.bytesdrawer.budgetplanner.common.data

import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.models.local.Category
import com.bytesdrawer.budgetplanner.common.models.remote.CategoryRemote
import com.bytesdrawer.budgetplanner.common.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class CategoryRepository(
    private val dao: BudgetPlannerDao,
    private val networkService: NetworkService
) {

    fun getCategories(): Flow<List<Category>> {
        return dao.getAllCategories()
    }

    fun createOrUpdateCategory(category: Category) {
        dao.insertCategory(category)
    }

    fun deleteCategory(category: Category) {
        dao.deleteCategory(category)
    }

    fun deleteAllCategories() {
        dao.deleteAllCategories()
    }

    suspend fun getAllCategoryFromNetwork(): Response<List<CategoryRemote>> {
        return networkService.getAllCategory()
    }

    suspend fun getCategoryFromNetwork(id: Long): Response<CategoryRemote> {
        return networkService.getCategory(id.toString())
    }

    suspend fun createCategoryOnNetwork(category: Category): Response<Unit> {
        return networkService.createCategory(NetworkUtil.categoryToRequestBody(category))
    }

    suspend fun updateCategoryOnNetwork(category: Category): Response<Unit> {
        return networkService.updateCategory(NetworkUtil.categoryToRequestBody(category))
    }

    suspend fun deleteCategoryOnNetwork(id: Long): Response<Unit> {
        return networkService.deleteCategory(id.toString())
    }

}