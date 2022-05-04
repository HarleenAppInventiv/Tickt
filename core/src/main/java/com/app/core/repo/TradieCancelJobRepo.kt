package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.google.gson.JsonObject

class TradieCancelJobRepo : BaseRepo() {


    suspend fun cancelJob(mObject:JsonObject) =
        apiRequest(ApiCodes.TRADIE_JOB_CANCEL) {
            unauthenticatedApiClient.tradieCancelJob(mObject)
        }

    suspend fun builderCancelJob(mObject:JsonObject) =
        apiRequest(ApiCodes.BUILDER_JOB_CANCEL) {
            unauthenticatedApiClient.builderCancelJob(mObject)
        }
}