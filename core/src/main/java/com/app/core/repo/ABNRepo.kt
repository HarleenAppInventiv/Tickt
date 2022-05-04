package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.util.ApiCodes

class ABNRepo : BaseRepo() {
    suspend fun hitSignUp(onBoardingData: OnBoardingData): BaseResponse<Any> {
        return apiRequest(ApiCodes.REGISTER_USER) {
            if (!onBoardingData.socialId.isNullOrEmpty())
                unauthenticatedApiClient.socialAuth(onBoardingData)
            else
            unauthenticatedApiClient.registerUser(onBoardingData)
        }
    }
}