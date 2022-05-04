package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.util.ApiCodes
import okhttp3.MultipartBody

class PortfolioRepo : BaseRepo() {

    suspend fun addPortfolio(params: HashMap<String, Any>): BaseResponse<Any> {
        return apiRequest(ApiCodes.ADD_PORTFOLIO) {
            unauthenticatedApiClient.addPortfolio(params)
        }
    }

    suspend fun editPortfolio(params: HashMap<String, Any>): BaseResponse<Any> {
        return apiRequest(ApiCodes.EDIT_PORTFOLIO) {
            unauthenticatedApiClient.editPortfolio(params)
        }
    }

    suspend fun deletePortfolio(portfolioId:String): BaseResponse<Any> {
        return apiRequest(ApiCodes.DELETE_PORTFOLIO) {
            unauthenticatedApiClient.deletePortfolio(portfolioId)
        }
    }
}