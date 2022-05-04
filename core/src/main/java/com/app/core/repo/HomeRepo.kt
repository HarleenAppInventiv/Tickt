package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class HomeRepo : BaseRepo() {
    suspend fun getJobTypeListing() =
        apiRequest(ApiCodes.JOB_TYPE_LIST) {
            unauthenticatedApiClient.jobTypeListing()
        }

    suspend fun getHomeListing(lat:String,lng:String) =
        apiRequest(ApiCodes.HOME) {
            unauthenticatedApiClient.getHome(lat,lng)
        }
}