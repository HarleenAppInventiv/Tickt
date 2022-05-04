package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class NewApplicantRequestRepo : BaseRepo() {

    suspend fun getNewApplicationRequestListing(page: Int) =
        apiRequest(ApiCodes.NEW_APPLICANT) {
            unauthenticatedApiClient.newApplicantList(page)
        }
    suspend fun needApproval(page: Int) =
        apiRequest(ApiCodes.NEED_APPROVAL) {
            unauthenticatedApiClient.needApproval(page)
        }
    suspend fun newApplicantLists(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.NEW_APPLICANT_LIST) {
            unauthenticatedApiClient.newApplicantLists(params)
        }
}
