package com.app.core.model

import com.google.gson.annotations.SerializedName

class FileUploadResponse(
    @SerializedName("result")
    var result: Result? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("status_code")
    var status_code: Int? = null

)

class Result(
    @SerializedName("url")
    var url: ArrayList<String> ?= null
)
