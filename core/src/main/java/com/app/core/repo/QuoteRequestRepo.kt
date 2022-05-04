package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class QuoteRequestRepo : BaseRepo() {

    suspend fun getQuoteList(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.GET_QUOTE) {
            unauthenticatedApiClient.getQuoteList(
                params.get("jobId").toString(),
                params.get("sortBy").toString().toInt()
            )
        }

    suspend fun getQuoteListWithTraide(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.GET_QUOTE) {
            unauthenticatedApiClient.getQuoteListWithTraide(
                params.get("jobId").toString(),
                params.get("tradieId").toString()
            )
        }

    suspend fun addItem(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.ADD_ITEM) {
            unauthenticatedApiClient.addItem(params)
        }

    suspend fun addQuote(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.ADD_QUOTE) {
            unauthenticatedApiClient.addQuote(params)
        }

    suspend fun editItem(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.UPDATE_ITEM) {
            unauthenticatedApiClient.editItem(params)
        }

    suspend fun deleteItem(params: HashMap<String, Any>) =
        apiRequest(ApiCodes.DELETE_ITEM) {
            unauthenticatedApiClient.deleteItem(params)
        }
}
