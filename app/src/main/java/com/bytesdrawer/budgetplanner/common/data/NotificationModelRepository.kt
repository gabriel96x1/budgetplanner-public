package com.bytesdrawer.budgetplanner.common.data

import com.bytesdrawer.budgetplanner.common.data.local.BudgetPlannerDao
import com.bytesdrawer.budgetplanner.common.data.remote.NetworkService
import com.bytesdrawer.budgetplanner.common.models.local.NotificationModel
import com.bytesdrawer.budgetplanner.common.models.remote.NotificationModelRemote
import com.bytesdrawer.budgetplanner.common.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class NotificationModelRepository(
    private val dao: BudgetPlannerDao,
    private val networkService: NetworkService
) {

    fun getNotifications(): Flow<List<NotificationModel>> {
        return dao.getAllNotifications()
    }

    fun createOrUpdateNotification(notificationModel: NotificationModel) {
        dao.insertNotification(notificationModel)
    }

    fun deleteNotification(notificationModel: NotificationModel) {
        dao.deleteNotification(notificationModel)
    }

    fun deleteAllNotifications() {
        dao.deleteAllNotifications()
    }

    suspend fun getAllNotificationModelFromNetwork(): Response<List<NotificationModelRemote>> {
        return networkService.getAllNotificationModel()
    }

    suspend fun getNotificationModelFromNetwork(id: Long): Response<NotificationModelRemote> {
        return networkService.getNotificationModel(id.toString())
    }

    suspend fun createNotificationModelOnNetwork(notificationModel: NotificationModel): Response<Unit> {
        return networkService.createNotificationModel(NetworkUtil.notificationModelToRequestBody(notificationModel))
    }

    suspend fun updateNotificationModelOnNetwork(notificationModel: NotificationModel): Response<Unit> {
        return networkService.updateNotificationModel(NetworkUtil.notificationModelToRequestBody(notificationModel))
    }

    suspend fun deleteNotificationModelOnNetwork(id: Long): Response<Unit> {
        return networkService.deleteNotificationModel(id.toString())
    }
}