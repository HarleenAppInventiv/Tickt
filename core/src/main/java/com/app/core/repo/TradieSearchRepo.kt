package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.example.ticktapp.model.registration.JobSearchRequestModel

class TradieSearchRepo : BaseRepo() {
    suspend fun searchTradie(request: Any) =
        apiRequest(ApiCodes.SEARCH) {
            unauthenticatedApiClient.searchTradie(request)
        }
}