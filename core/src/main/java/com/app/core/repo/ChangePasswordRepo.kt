package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.google.gson.JsonObject

class ChangePasswordRepo : BaseRepo() {


    suspend fun changePassword(mObject:JsonObject) =
        apiRequest(ApiCodes.CHANGE_PASSWORD) {
            unauthenticatedApiClient.changePassword(mObject)
        }


}