package com.airhireme.firebase.model

import java.io.Serializable
import java.util.*

/**
 * Model class for chat room
 */
class ChatRoomBean : Serializable {
    var chatRoomId: String? = null
    var chatRoomType: String? = null
    var chatLastUpdate: Any? = null
    var chatLastUpdates: HashMap<String, Any>? = null
    var chatRoomMembers: HashMap<String, Any>? = null
}