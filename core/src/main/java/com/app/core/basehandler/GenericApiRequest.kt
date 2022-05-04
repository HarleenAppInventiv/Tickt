package com.app.core.basehandler

import CoreUtils
import android.util.Log
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import org.json.JSONObject
import retrofit2.Response

abstract class GenericApiRequest<T> {
    suspend fun apiRequest(

        apiCode: Int = ApiCodes.NO_API,
        call: suspend () -> Response<BaseResponse<T>>

    ): BaseResponse<T> {
        var dataClass = BaseResponse<T>()
//        dataClass.apiCode = apiCode
        if (CoreUtils.isInternetAvailable()) {
            try {
                dataClass.isInternetOn = true
                val response: Response<BaseResponse<T>> = call.invoke()
                if (response.body() != null) {
                    if (response.isSuccessful && (response.body()!!.status_code == 200 ||
                                response.body()!!.status_code == 202 ||
                                response.body()!!.status_code == 201)
                    ) {
                        dataClass = response.body()!!
                        dataClass.apiError = null
                        dataClass.apiCode = apiCode
                        return dataClass
                    } else {
                        dataClass.apiError = getError(response)
                        dataClass.result = null
                        dataClass.apiCode = apiCode
                        return dataClass
                    }
                } else {
                    dataClass.result = null
                    dataClass.apiCode = apiCode
                    dataClass.apiError = getError(response)
                    return dataClass
                }
            } catch (e: Exception) {
                if(e.message?.contains("StandaloneCoroutine was cancelled") == true){
                    dataClass.apiCode = apiCode
                    dataClass.isInternetOn = true
                    dataClass.result = null
                    Log.d("exec", e.message.toString())
                    dataClass.apiError = setApiError(true)
                    dataClass.message="cancelled"
                    return dataClass
                }else {
                    dataClass.apiCode = apiCode
                    dataClass.isInternetOn = true
                    dataClass.result = null
                    Log.d("exec", e.message.toString())
                    dataClass.apiError = setApiError(true)
                    return dataClass
                }
            }
            /*
            finally {
                val response: Response<Any> = call.invoke() as Response<Any>

            }

             */
        } else {
            dataClass.apiCode = apiCode
            dataClass.isInternetOn = false
            dataClass.result = null
            dataClass.apiError = setApiError(false)
            return dataClass
        }
    }

    private fun setApiError(isInternetOn: Boolean): ApiError? {
        val apiError = ApiError()
        if (isInternetOn)
            apiError.message = "Request failed. Please retry"
        else
            apiError.message =
                "It seems like you are not connected with a stable internet connection"
        return apiError

    }

    private fun getError(
        response: Response<BaseResponse<T>>
    ): ApiError {
        val error = ApiError()
        if (response.body() != null) {
            error.message = response.body()!!.message
            error.status_code = response.body()!!.status_code
            error.result = response.body()!!.result
        } else {
            val jObjError = JSONObject(response.errorBody()!!.string())
            //error = ErrorUtils.parseError(response)!!
            error.status_code = jObjError.optInt("status_code")
            error.message = jObjError.optString("message")
                ?: "Unable to process your request. Please try again."
        }
        return error
    }
}