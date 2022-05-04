package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class NotificationRequestRepo : BaseRepo() {

    suspend fun notifications(page: Int) =
        apiRequest(ApiCodes.NOTIFICATIONS) {
            unauthenticatedApiClient.notifications(page)
        }

    suspend fun markAllNotificationsRead(page: Int, markRead: Int) =
        apiRequest(ApiCodes.NOTIFICATIONS) {
            unauthenticatedApiClient.markAllNotificationsRead(page, markRead)
        }

    suspend fun readNotificaitons(mObjcet: HashMap<String, Any>) =
        apiRequest(ApiCodes.READ_NOTIFICATIONS) {
            unauthenticatedApiClient.readNotificaitons(mObjcet)
        }
}
