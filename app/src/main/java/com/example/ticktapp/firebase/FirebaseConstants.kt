package com.example.ticktapp.firebase

/*
 * Constants used in Firebase
 */
object FirebaseConstants {
    //file type constants
    const val FILE_IMAGE = 101
    const val FILE_VIDEO = 102

    //firebase database constants
    const val PASSWORD = "YWRtaW46MTIzNDU"
    const val EMAIL = "email"
    const val OTHER_USER = "otherUser"
    const val ROOM_ID = "roomId"
    const val PENDING = "pending"
    const val SEND = "send"
    const val READ = "read"
    const val TEXT = "text"
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val CONTACT = "contact"
    const val SINGLE_CHAT = "single"
    const val TIME_STAMP = "messageTimestamp"
    const val CHAT_TIME = "chat_time"
    const val MESSAGE_STATUS = "messageStatus"
    const val SINGLE = "single"
    const val DEVICE_TOKEN = "deviceToken"
    const val FAILED = "failed"
    const val IS_DELETE = "isUserDeleted"
    const val IS_MESSAGE_DELETE = "isDeleted"
    const val NAME = "name"
    const val USER_IMAGE = "userImage"
    const val IS_BLOCK = "isUserBlocked"
    const val MESSAGE_TYPE = "messageType"

    //firebase database node constants
    const val USERS_NODE = "users"
    const val ITEMS_NODE = "items"
    const val INBOX_NODE = "inbox"
    const val UNREAD_MESSAGES_COUNT_NODE = "unreadMessages"
    const val ROOM_INFO_NODE = "room_info"
    const val MEMBER_DELETE_NODE = "memberDelete"
    const val MEMBER_JOIN_NODE = "memberJoin"
    const val MEMBER_LEAVE_NODE = "memberLeave"
    const val MESSAGES_NODE = "messages"
    const val USER_LAST_MESSAGE_NODE = "userLastMessage"
    const val LAST_MESSAGE_NODE = "lastMessage"
    const val CHAT_LAST_MESSAGE_NODE = "chatLastMessage"
    const val BLOCK_NODE = "block"
    const val CHAT_ROOM_MEMBERS_NODE = "chatRoomMembers"

    //notification constants
    const val SEND_PUSH_NOTIFICATION = "https://fcm.googleapis.com/fcm/send"
    const val NOTIFICATION = "Notification"
    const val NOTIFICATION_CHANNEL_GROUP = "notification_channel_group"
    const val PARAM_CONTENT_TYPE = "Content-Type"
    const val PARAM_AUTHORIZATION = "Authorization"
    const val APPLICATION_JSON = "application/json"
}