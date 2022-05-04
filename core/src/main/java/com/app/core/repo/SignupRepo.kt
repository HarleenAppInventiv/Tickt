package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.model.requestsmodel.CreatePasswordRequest
import com.app.core.util.ApiCodes

class SignupRepo : BaseRepo() {

    suspend fun hitCheckSocialId(socialId: String, email: String, user_type: String): BaseResponse<Any>
    {
        return apiRequest(ApiCodes.SOCIAL_ID) {
            unauthenticatedApiClient.checkSocialID(socialId,email,user_type)
        }
    }

    suspend fun hitSignUp(onBoardingData: OnBoardingData): BaseResponse<Any>
    {
        return apiRequest(ApiCodes.REGISTER_USER) {
            unauthenticatedApiClient.socialAuth(onBoardingData)
        }
    }

    suspend fun hitVerfiyEmail(email: String): BaseResponse<Any>
    {
        return apiRequest(ApiCodes.VERIFY_EMAIL) {
            unauthenticatedApiClient.checkEmail(email)
        }
    }

}