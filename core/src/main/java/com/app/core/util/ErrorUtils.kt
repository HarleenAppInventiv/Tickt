@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.app.core.util

import com.app.core.retrofit.RetrofitManager
import retrofit2.Response
import java.io.IOException

object ErrorUtils {
    fun parseError(response: Response<*>): ApiError? {
        val converter = RetrofitManager.getRetroInstance()
            .retrofit
            .responseBodyConverter<ApiError>(ApiError::class.java, arrayOfNulls(0))
        val error: ApiError?
        try {
            error = converter.convert(response.errorBody())
        } catch (e: IOException) {
            return ApiError()
        }
        return error
    }
}