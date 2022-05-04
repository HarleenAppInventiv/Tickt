package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.requestsmodel.CreatePasswordRequest
import com.app.core.model.requestsmodel.DeviceToken
import com.app.core.model.usermodel.UserDataModel
import com.app.core.util.ApiCodes
import com.google.gson.Gson

class PhoneNumberRepo : BaseRepo() {
    suspend fun hitCheckPhoneNumber(mobileNumber: String?): BaseResponse<Any> {
        val response = apiRequest(ApiCodes.CHECK_PHONE_NUMBER) {
            unauthenticatedApiClient.checkMobileNumber(mobileNumber)
        }
        saveUserData(Gson().fromJson(Gson().toJson(response.result), UserDataModel::class.java))
        return response
    }


    suspend fun hitForgotPAssword(forgotPassword: CreatePasswordRequest): BaseResponse<Any> {
        val response = apiRequest(ApiCodes.FORGOT_PASSWORD) {
            unauthenticatedApiClient.hitForgotPasswordApi(forgotPassword)
        }
        saveUserData(Gson().fromJson(Gson().toJson(response.result), UserDataModel::class.java))
        return response
    }

    suspend fun addDeviceToken(deviceToken: DeviceToken): BaseResponse<Any> {
        val response = apiRequest(ApiCodes.DEVICE_TOKEN) {
            unauthenticatedApiClient.addDeviceToken(deviceToken)
        }
        return response
    }
}