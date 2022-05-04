package com.app.core.model

import com.google.gson.annotations.SerializedName

data class VerifyEmailModel (
    @SerializedName("isProfileCompleted")
    var isProfileCompleted: Boolean? = null
)