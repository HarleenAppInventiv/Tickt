package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class QuestionsRepo : BaseRepo() {

    suspend fun addAnswer(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.ADD_ANSWER) {
            unauthenticatedApiClient.addAnswer(data)
        }

    suspend fun addTradieAnswer(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.ADD_ANSWER) {
            unauthenticatedApiClient.addTradieAnswer(data)
        }

    suspend fun builderQuestionList(jobId: String, page: Int = 1) =
        apiRequest(ApiCodes.QUESTIONS_LIST) {
            unauthenticatedApiClient.builderQuestionList(jobId, page)
        }

    suspend fun updateAnswer(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.UPDATE_ANSWER) {
            unauthenticatedApiClient.updateAnswer(data)
        }

    suspend fun updateTradieAnswer(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.UPDATE_ANSWER) {
            unauthenticatedApiClient.updateTradieAnswer(data)
        }

    suspend fun deleteAnswer(qId: String, aId: String) =
        apiRequest(ApiCodes.DELETE_ANSWER) {
            unauthenticatedApiClient.deleteAnswer(qId, aId)
        }
}