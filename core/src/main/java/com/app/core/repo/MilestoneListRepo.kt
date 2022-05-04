package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class MilestoneListRepo : BaseRepo() {


    suspend fun getJobMilestonesList(jobId:String) =
        apiRequest(ApiCodes.JOB_MILESTONE_LIST) {
            unauthenticatedApiClient.jobMilestonesList(jobId)
        }

    suspend fun jobBuilderMilestonesList(jobId:String) =
        apiRequest(ApiCodes.JOB_MILESTONE_LIST) {
            unauthenticatedApiClient.jobBuilderMilestonesList(jobId)
        }

    suspend fun getMilestoneDetails(jobId:String,milestoneId:String?) =
        apiRequest(ApiCodes.JOB_MILESTONE_DETAILS) {
            unauthenticatedApiClient.getMilestoneDetails(jobId,milestoneId)
        }
    suspend fun changeMilestoneRequest(data:HashMap<String,Any>) =
        apiRequest(ApiCodes.CHANGE_MILESTONE_REQUEST) {
            unauthenticatedApiClient.changeMilestoneRequest(data)
        }
    suspend fun editMilestoneRequest(data:HashMap<String,Any>) =
        apiRequest(ApiCodes.EDIT_MILESTONE_REQUEST) {
            unauthenticatedApiClient.editMilestoneRequest(data)
        }
    suspend fun declineMilestoneRequest(data:HashMap<String,Any>) =
        apiRequest(ApiCodes.DECLINE_MILESTONE) {
            unauthenticatedApiClient.declineMilestoneRequest(data)
        }
}