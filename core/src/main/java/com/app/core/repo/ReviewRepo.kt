package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class ReviewRepo : BaseRepo() {

    suspend fun addReviewReply(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.ADD_REVIEW_REPLY) {
            unauthenticatedApiClient.addReviewReply(data)
        }

    suspend fun updateReviewReply(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.UPDATE_REVIEW_REPLY) {
            unauthenticatedApiClient.updateReviewReply(data)
        }

    suspend fun deleteReviewReply(qId: String, aId: String) =
        apiRequest(ApiCodes.DELETE_REVIEW_REPLY) {
            unauthenticatedApiClient.deleteReviewReply(qId, aId)
        }

    suspend fun getBuilderReviewList(tradieId: String, page: Int) =
        apiRequest(ApiCodes.REVIEW_LIST) {
            unauthenticatedApiClient.getBuilderReviewList(tradieId, page)
        }

}