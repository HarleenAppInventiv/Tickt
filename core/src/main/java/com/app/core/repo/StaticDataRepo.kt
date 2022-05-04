package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.util.ApiCodes

class StaticDataRepo : BaseRepo() {

    suspend fun hitPrivacyData(): BaseResponse<Any> {
        return apiRequest(ApiCodes.PRIVACY) {
            unauthenticatedApiClient.getPrivacyData()
        }
    }
    suspend fun hitTncData(): BaseResponse<Any> {
        return apiRequest(ApiCodes.TNC) {
            unauthenticatedApiClient.getTermsData()
        }
    }
}