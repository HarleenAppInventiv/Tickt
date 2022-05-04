package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.google.gson.JsonObject

class TradieBankDetailsRepo : BaseRepo() {


    suspend fun getBankDetails() =
        apiRequest(ApiCodes.GET_BANK_DETAILS) {
            unauthenticatedApiClient.getBankDetails()
        }

    suspend fun addBankDetails(mObject:JsonObject) =
        apiRequest(ApiCodes.ADD_BANK_DETAILS) {
            unauthenticatedApiClient.addBankDetails(mObject)
        }


    suspend fun markJobComplete(mObject: HashMap<String, Any>) =
        apiRequest(ApiCodes.MARK_JOB_COMPLETE) {
            unauthenticatedApiClient.markJobComplete(mObject)
        }

    suspend fun UpdateBankDetails(mObject:JsonObject) =
        apiRequest(ApiCodes.ADD_BANK_DETAILS) {
            unauthenticatedApiClient.updateBankDetails(mObject)
        }

    suspend fun removeBankDetails() =
        apiRequest(ApiCodes.REMOVE_BANK_DETAILS) {
            unauthenticatedApiClient.removeBankDetails()
        }

}