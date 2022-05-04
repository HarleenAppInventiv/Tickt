package com.app.core.model.profile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TradieSettingsModel(
    @SerializedName("messages") val messages: Messages,
    @SerializedName("reminders") val reminders: Messages,
    @SerializedName("pushNotificationCategory") val pushNotificationCategory: ArrayList<Int>,
) : Serializable

data class Messages(
    @SerializedName("email") val email: Boolean,
    @SerializedName("pushNotification") val pushNotification: Boolean,
    @SerializedName("smsMessages") val smsMessages: Boolean,
) : Serializable