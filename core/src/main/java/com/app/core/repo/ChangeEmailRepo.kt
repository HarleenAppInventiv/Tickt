package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.google.gson.JsonObject

class ChangeEmailRepo : BaseRepo() {


    suspend fun changeEmail(mObject:JsonObject) =
        apiRequest(ApiCodes.CHANGE_EMAIL) {
            unauthenticatedApiClient.changeEmail(mObject)
        }

    suspend fun verifyEmail(mObject:JsonObject) =
        apiRequest(ApiCodes.VERIFY_EMAIL) {
            unauthenticatedApiClient.verifyEmail(mObject)
        }
}