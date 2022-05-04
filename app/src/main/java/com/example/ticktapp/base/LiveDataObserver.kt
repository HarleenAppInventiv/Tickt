package com.example.ticktapp.base

import androidx.lifecycle.Observer
import com.app.core.util.ApiError

interface LiveDataObserver<T> : Observer<ApiResponseData> {
    fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?)
    fun onException(exception: ApiError, apiCode: Int)
    fun onSessionExpired(exception: ApiError, apiCode: Int)
    fun noInternetConnection(apiCode: Int,msg : String?)
    fun authorizeFailed(apiCode: Int) {}

    override fun onChanged(apiResponse: ApiResponseData) {
        when(apiResponse){
            is ApiResponseData.API_SUCCEED ->{onResponseSuccess(apiResponse.statusCode,apiResponse.apiCode,apiResponse.msg)}
            is ApiResponseData.API_EXCEPTION ->{onException(apiResponse.exception,apiResponse.apiCode)}
            is ApiResponseData.API_EXPIRED_SESSION->{onSessionExpired(apiResponse.exception,apiResponse.apiCode)}
            is ApiResponseData.NO_INTERNET ->{noInternetConnection(apiResponse.apiCode,apiResponse.msg)}
            is ApiResponseData.AUTORIZE_FAILED -> {
                authorizeFailed(apiResponse.apiCode)
            }
        }
    }
}