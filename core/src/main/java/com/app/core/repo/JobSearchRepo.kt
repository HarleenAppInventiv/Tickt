package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.example.ticktapp.model.registration.JobSearchRequestModel

class JobSearchRepo : BaseRepo() {
    suspend fun search(jobSearchRequestModel: Any) =
        apiRequest(ApiCodes.SEARCH) {
            unauthenticatedApiClient.search(jobSearchRequestModel)
        }
}