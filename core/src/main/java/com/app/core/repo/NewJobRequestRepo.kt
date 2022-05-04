package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class NewJobRequestRepo : BaseRepo() {

    suspend fun getNewJobRequestListing(page:Int) =
        apiRequest(ApiCodes.NEW_JOB_REQUEST) {
            unauthenticatedApiClient.newJobList(page)
        }
}