package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class TradieJobRepo : BaseRepo() {


    suspend fun getActiveJobListing(page:Int) =
        apiRequest(ApiCodes.ACTIVE_JOBS) {
            unauthenticatedApiClient.activeJobList(page)
        }
    suspend fun getAppliedJobListing(page:Int) =
        apiRequest(ApiCodes.APPLIED_JOBS) {
            unauthenticatedApiClient.appliedJobList(page)
        }
    suspend fun getPastJobListing(page:Int) =
        apiRequest(ApiCodes.PAST_JOBS) {
            unauthenticatedApiClient.pastJobList(page)
        }
    suspend fun activeBuilderJobList(page:Int) =
        apiRequest(ApiCodes.ACTIVE_TRADIE_JOBS) {
            unauthenticatedApiClient.activeBuilderJobList(page)
        }
    suspend fun openBuilderJobList(page:Int) =
        apiRequest(ApiCodes.ACTIVE_TRADIE_JOBS) {
            unauthenticatedApiClient.openBuilderJobList(page)
        }

    suspend fun pastBuilderJobList(page:Int) =
        apiRequest(ApiCodes.ACTIVE_TRADIE_JOBS) {
            unauthenticatedApiClient.pastBuilderJobList(page)
        }
}