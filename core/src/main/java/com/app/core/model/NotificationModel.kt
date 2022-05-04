package com.app.core.model

import com.google.gson.annotations.SerializedName
data class NotificationResponse(
    @SerializedName("list")
    var list: ArrayList<NotificationModel>? = null,
    )

data class NotificationModel (
    @SerializedName("_id")
    var _id: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("sub_title")
    var sub_title: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("notificationText")
    var notificationText: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("read")
    var read: Int? = null,
    @SerializedName("app_icon")
    var app_icon: String? = null,
    @SerializedName("notificationType")
    var notificationType: Int? = null,
    @SerializedName("notificationCategory")
    var notificationCategory: Int? = null,
    @SerializedName("user_type")
    var user_type: Int? = null,
    @SerializedName("senderId")
    var senderId: String? = null,
    @SerializedName("receiverId")
    var receiverId: String? = null,
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("updatedAt")
    var updatedAt: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null
    )