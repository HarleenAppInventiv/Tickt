package com.app.core.model.chat

import com.airhireme.firebase.model.ChatRoomBean


/**
 * Created by Sachin on 22-May-17.
 */
class InboxMessageBean {
    var roomId = ""
    var unreadCount = 0
    var chatRoomBean: ChatRoomBean? = null
    var userBean: UserBean? = null
    var chatLastMessageBean: ChatMessageBean? = null
}