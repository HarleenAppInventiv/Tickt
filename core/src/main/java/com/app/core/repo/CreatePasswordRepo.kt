package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.requestsmodel.CreatePasswordRequest
import com.app.core.util.ApiCodes

class CreatePasswordRepo : BaseRepo() {

    suspend fun hitCreatePassword(createPasswordRequest: CreatePasswordRequest): BaseResponse<Any> {
        return apiRequest(ApiCodes.CREATE_PASSWORD) {
            unauthenticatedApiClient.createPassword(createPasswordRequest)
        }
    }

    suspend fun hitCheckPhone(mobileNumber: String): BaseResponse<Any> {
        return apiRequest(ApiCodes.CHECK_PHONE_NUMBER) {
            unauthenticatedApiClient.checkMobileNumber(mobileNumber)
        }
    }
}