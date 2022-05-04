package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class TradieQuestionsRepo : BaseRepo() {

    suspend fun tradieQuestionList(jobId: String, page: Int = 1) =
        apiRequest(ApiCodes.QUESTIONS_LIST) {
            unauthenticatedApiClient.tradieQuestionList(jobId, page)
        }

    suspend fun addQuestion(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.TRADIE_ADD_QUESTION) {
            unauthenticatedApiClient.tradieAddQuestion(data)
        }

    suspend fun updateQuestion(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.TRADIE_UPDATE_QUESTION) {
            unauthenticatedApiClient.tradieUpdateQuestion(data)
        }

    suspend fun deleteQuestion(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.TRADIE_DELETE_QUESTION) {
            unauthenticatedApiClient.tradieDeleteQuestion(data)
        }

    suspend fun deleteTradieAnswer(qId: String, aId: String) =
        apiRequest(ApiCodes.DELETE_ANSWER) {
            unauthenticatedApiClient.deleteTradieAnswer(qId, aId)
        }
}