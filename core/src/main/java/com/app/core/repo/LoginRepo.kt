package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.loginmodel.LoginRequest
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.util.ApiCodes

class LoginRepo : BaseRepo() {
    suspend fun hitLogin(login: LoginRequest): BaseResponse<Any> {
        return apiRequest(ApiCodes.LOGIN) {
            unauthenticatedApiClient.login(login)
        }
    }

    suspend fun hitCheckSocialId(
        socialId: String,
        email: String,
        userType: String
    ): BaseResponse<Any> {
        return apiRequest(ApiCodes.SOCIAL_ID) {
            unauthenticatedApiClient.checkSocialID(socialId, email, userType)
        }
    }

    suspend fun hitSignUp(onBoardingData: OnBoardingData): BaseResponse<Any> {
        return apiRequest(ApiCodes.REGISTER_USER) {
            unauthenticatedApiClient.socialAuth(onBoardingData)
        }
    }
}