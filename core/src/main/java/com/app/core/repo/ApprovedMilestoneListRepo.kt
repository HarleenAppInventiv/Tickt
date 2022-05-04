package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class ApprovedMilestoneListRepo : BaseRepo() {


    suspend fun getApprovedMilestonesList(page:Int) =
        apiRequest(ApiCodes.APPROVED_MILESTONE_REQUEST) {
            unauthenticatedApiClient.approvedMilestoneList(page)
        }



}