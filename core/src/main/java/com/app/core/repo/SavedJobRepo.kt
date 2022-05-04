package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class SavedJobRepo : BaseRepo() {

    suspend fun getSavedJobListing(page:Int) =
        apiRequest(ApiCodes.SAVE_JOB_REQUEST) {
            unauthenticatedApiClient.savedJobList(page)
        }
}