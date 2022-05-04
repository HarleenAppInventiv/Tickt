package com.app.core.model.profile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Settings(
    @SerializedName("pushNotificationCategory") val pushNotificationCategory: ArrayList<Int>,
) : Serializable