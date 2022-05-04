package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.google.gson.JsonObject

class JobRepo : BaseRepo() {
    suspend fun getJobTypeListing(jobId: String?, tradeId: String?, specId: String?) =
        apiRequest(ApiCodes.JOB_DETAILS) {
            unauthenticatedApiClient.jobDetails(
                jobId,
                tradeId,
                if (!specId.isNullOrEmpty()) specId else null
            )
        }

    suspend fun jobDetailsFromBuilder(jobId: String?, tradeId: String?, specId: String?) =
        apiRequest(ApiCodes.JOB_DETAILS) {
            unauthenticatedApiClient.jobDetailsFromBuilder(jobId)
        }

    suspend fun jobDetailsFromBuilderRepublish(
        jobId: String?,
        tradeId: String?,
        specId: String?,
    ) =
        apiRequest(ApiCodes.JOB_REPULISH_DETAILS) {
            unauthenticatedApiClient.jobDetailsFromBuilderRepublish(jobId)
        }

    suspend fun jobDetailsForEditBuilderJob(
        jobId: String?,
        isRespJob: Boolean
    ) =
        apiRequest(ApiCodes.JOB_REPUBLISH_EDIT_JOB) {
            unauthenticatedApiClient.jobDetailsForBuilderEditJob(jobId, isRespJob)
        }

    suspend fun jobDetailsFromBuilderOpen(jobId: String?, tradeId: String?, specId: String?) =
        apiRequest(ApiCodes.OPEN_JOB_DETAILS) {
            unauthenticatedApiClient.jobDetailsFromBuilderOpen(jobId)
        }

    suspend fun jobDetailsRemove(jobId: String?) =
        apiRequest(ApiCodes.REMOVE_JOB_DETAILS) {
            unauthenticatedApiClient.jobDetailsRemove(jobId)
        }

    suspend fun closeOpenJob(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.CLOSE_OPEN_JOB) {
            unauthenticatedApiClient.closeOpenJob(data)
        }

    suspend fun closeQuoteJob(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.CLOSE_QUOTE_JOB) {
            unauthenticatedApiClient.closeQuoteJob(data)
        }

    suspend fun getTradieProfile(tradeId: String?, jobId: String?) =
        apiRequest(ApiCodes.TRADIE_PROFILE) {
            unauthenticatedApiClient.getTradieProfile(tradeId, jobId)
        }

    suspend fun getTradieProfile(tradeId: String?) =
        apiRequest(ApiCodes.TRADIE_PROFILE) {
            unauthenticatedApiClient.getTradieProfile(tradeId)
        }

    suspend fun saveJob(jobId: String?, tradeId: String?, specId: String?, isSaveJob: Boolean?) =
        apiRequest(ApiCodes.SAVE_JOBS) {
            unauthenticatedApiClient.saveJob(jobId, tradeId, specId, isSaveJob)
        }

    suspend fun saveTradie(tradeId: String?, isSaveJob: Boolean?) =
        apiRequest(ApiCodes.SAVE_TRADIE) {
            unauthenticatedApiClient.saveTradie(tradeId, isSaveJob)
        }

    suspend fun cancelInvite(ivId: String?, tradeId: String?, jobId: String?) =
        apiRequest(ApiCodes.CANCEL_INVITE) {
            unauthenticatedApiClient.cancelInvite(ivId, tradeId, jobId)
        }

    suspend fun replyCancellationRequest(body: JsonObject) =
        apiRequest(ApiCodes.CANCELLATION_REPLY) {
            unauthenticatedApiClient.replyCancellationRequest(body)
        }

    suspend fun replyChangeRequest(params: JsonObject) =
        apiRequest(ApiCodes.CHANGEREQUEST) {
            unauthenticatedApiClient.replyChangeRequest(params)
        }

    suspend fun replyCancellationRequestBuilder(body: JsonObject) =
        apiRequest(ApiCodes.CANCELLATION_REPLY) {
            unauthenticatedApiClient.replyCancellationRequestBuilder(body)
        }

    suspend fun reviewTradie(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.REVIEW_TRADIE) {
            unauthenticatedApiClient.reviewTradie(data)
        }

    suspend fun reviewUpdateTradie(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.REVIEW_UPDATE_TRADIE) {
            unauthenticatedApiClient.reviewUpdateTradie(data)
        }

    suspend fun reviewRemoveTradie(reviewId: String) =
        apiRequest(ApiCodes.REVIEW_REMOVE_TRADIE) {
            unauthenticatedApiClient.reviewRemoveTradie(reviewId)
        }

    suspend fun applyJob(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.APPLY) {
            unauthenticatedApiClient.applyJob(data)
        }

    suspend fun milestoneLists(jobId: String) =
        apiRequest(ApiCodes.MILESTONE_LIST) {
            unauthenticatedApiClient.milestoneLists(jobId)
        }

    suspend fun jobsList(page: Int) =
        apiRequest(ApiCodes.JOB_LIST) {
            unauthenticatedApiClient.jobsList(page)
        }

    suspend fun vouchJobsList(page: Int, tradieId: String?) =
        apiRequest(ApiCodes.JOB_LIST) {
            unauthenticatedApiClient.vouchJobsList(page, tradieId)
        }

    suspend fun inviteForJob(tradieId: String, jobID: String) =
        apiRequest(ApiCodes.INVITE_FOR_JOB) {
            unauthenticatedApiClient.inviteForJob(tradieId, jobID)
        }

    suspend fun acceptDeclineRequest(jobId: String?, tradeId: String?, status: Int?) =
        apiRequest(ApiCodes.ACCEPT_DECLINE_REQUEST) {
            unauthenticatedApiClient.acceptDeclineRequest(jobId, tradeId, status)
        }

    suspend fun getTradieJobDetails(jobId: String?) =
        apiRequest(ApiCodes.JOB_DETAILS) {
            unauthenticatedApiClient.tradieJobDetails(jobId)
        }

    suspend fun builderCancelJob(mObject: JsonObject) =
        apiRequest(ApiCodes.BUILDER_JOB_CANCEL) {
            unauthenticatedApiClient.builderCancelJob(mObject)
        }
}