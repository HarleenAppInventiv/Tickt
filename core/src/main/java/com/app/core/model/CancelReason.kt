package com.app.core.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CancelReason(
    @SerializedName("id") val id: Int,
    @SerializedName("reason") val reason: String,
    var checked:Boolean
):Serializable