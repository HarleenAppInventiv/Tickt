package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class MyRevenueRepo : BaseRepo() {

    suspend fun getMyRevenue(page: Int) =
        apiRequest(ApiCodes.MY_REVENUE_REQUEST) {
            unauthenticatedApiClient.myRevenueList(page)
        }

    suspend fun myBuilderRevenueList(page: Int) =
        apiRequest(ApiCodes.MY_REVENUE_REQUEST) {
            unauthenticatedApiClient.myBuilderRevenueList(page)
        }
    suspend fun myBuilderRevenueDetails(jobId:String) =
        apiRequest(ApiCodes.MY_REVENUE_REQUEST_DETAILS) {
            unauthenticatedApiClient.myBuilderRevenueDetails(jobId)
        }
    suspend fun getSearchRevenue(text: String, page: Int) =
        apiRequest(ApiCodes.MY_REVENUE_REQUEST) {
            unauthenticatedApiClient.getSearchRevenue(text, page)
        }

    suspend fun getBuilderSearchRevenue(text: String, page: Int) =
        apiRequest(ApiCodes.MY_REVENUE_REQUEST) {
            unauthenticatedApiClient.getMyBuilderRevenue(text, page)
        }
}
