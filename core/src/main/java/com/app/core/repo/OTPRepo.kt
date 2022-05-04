package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.requestsmodel.OTPRequest
import com.app.core.model.usermodel.UserDataModel
import com.app.core.util.ApiCodes
import com.google.gson.Gson

class OTPRepo : BaseRepo() {
    suspend fun hitRegisterOTPVerify(otp: OTPRequest): BaseResponse<Any> {
        return apiRequest(ApiCodes.VERIFY_OTP) {
            unauthenticatedApiClient.verifiyOtp(otp)
        }
    }
    suspend fun verifiyMobileOtp(otp: OTPRequest): BaseResponse<Any> {
        return apiRequest(ApiCodes.VERIFY_OTP) {
            unauthenticatedApiClient.verifiyMobileOtp(otp)
        }
    }
    suspend fun hitCheckPhoneNumber(mobileNumber: String?): BaseResponse<Any> {
        val response = apiRequest(ApiCodes.CHECK_PHONE_NUMBER) {
            unauthenticatedApiClient.checkMobileNumber(mobileNumber)
        }
        saveUserData(Gson().fromJson(Gson().toJson(response.result), UserDataModel::class.java))
        return response
    }

    suspend fun checkEmailId(email: String?): BaseResponse<Any> {
        val response = apiRequest(ApiCodes.CHECK_PHONE_NUMBER) {
            val data = HashMap<String, Any>()
            if (email != null) {
                data.put("email", email)
            }
            unauthenticatedApiClient.checkEmailId(data)
        }
        saveUserData(Gson().fromJson(Gson().toJson(response.result), UserDataModel::class.java))
        return response
    }
}