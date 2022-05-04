package com.example.ticktapp.firebase

import com.airhireme.firebase.model.ChatRoomBean
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.ItemBean
import com.app.core.model.chat.UserBean
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

/**
 * Interface to get authentication response
 */
interface FirebaseAuthListener {
    fun onAuthSuccess(task: Task<AuthResult?>?, user: FirebaseUser?)
    fun onAuthError(task: Task<AuthResult?>?)
}

interface FirebaseMessageListener {
    fun getMessages(message: ChatMessageBean?)
    fun getMessagesList(messagesList: List<ChatMessageBean?>?)
    fun newMessagesListing()
    fun noData()
//    fun getCallbackListeners(path: DatabaseReference?, listener: FirebaseEventListeners?)
}

interface FirebaseCallbackListener {
    fun getCallbackListeners(path: DatabaseReference?, listener: FirebaseEventListeners?)
}

interface FirebaseBlockStatusListener {
    fun isUserBlock(isBlock: Boolean)
}

interface FirebaseUnreadCountListener {
    fun getCount(count: Int, listener: FirebaseEventListeners?, path: DatabaseReference?)
}

interface FirebaseRoomResponseListener {
    fun getRoomId(roomId: String?)
    fun getRoomDetails(chatRoomBean: ChatRoomBean?)
}

interface FirebaseUserListener {
    fun getUser(user: UserBean?)
}

interface FirebaseItemListener {
    fun getItem(user: ItemBean?)
}

