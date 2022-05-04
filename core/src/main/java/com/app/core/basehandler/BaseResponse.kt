package com.app.core.basehandler

import com.app.core.util.ApiError
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import java.io.Closeable

open class BaseResponse<T> {
    var apiCode: Int= 0
    var apiError: ApiError? = null
    var isInternetOn = true

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("status_code")
    @Expose
    var status_code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("result")
    @Expose
    var result: T? = null




}