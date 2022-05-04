package com.app.core.model.profile

import java.io.Serializable

data class FirebaseModel(
    var to: String? = "",
    var notification: FirebaseNotificationModel? = null,
    var data: FirebaseNotificationModel? = null,
) : Serializable

data class FirebaseNotificationModel(
    var app_icon:String?="",
    var title: String? = "",
    var body: String? = "",
    var notificationText: String? = "",
    var senderName: String? = "",
    var messageType: String? = "",
    var messageText: String? = "",
    var room_id: String? = "",
    var image: String? = "",
    var notificationType: String? = "",
    var sound: String? = "",
    var jobId: String? = "",
    var jobName: String? = "",
    var userType: String? = ""
) : Serializable




