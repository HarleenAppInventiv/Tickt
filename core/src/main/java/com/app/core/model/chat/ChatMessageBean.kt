package com.app.core.model.chat

import java.io.Serializable


class ChatMessageBean(
    var jobId: String? = null,
    var jobName: String? = null,
    var unreadMessages: Long = 0,
    var messageId: String? = null,
    var messageCaption: String? = null,
    var mediaDuration: String? = null,
    var mediaUrl: String? = null,
    var messageText: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var messageStatus: String? = null,
    var thumbnail: String? = null,
    var messageTimestamp: Any? = null,
    var messageType: String? = null,
    var messageRoomId: String? = null,
    var isBlock: Boolean = false,
    var isDeleted: Boolean = false,
    private val isAdmin: Boolean? = null,
    var isSuccess: String? = null,
    var progress: Int = 0,
    var senderName: String? = null,
    var senderImage: String? = null,
    var senderType: String? = null,
    var itemId: String? = null,
):Serializable