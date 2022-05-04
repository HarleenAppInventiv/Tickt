package com.example.ticktapp.firebase

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.airhireme.firebase.model.ChatRoomBean
import com.airhireme.firebase.model.NodeBean
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.InboxMessageBean
import com.app.core.model.chat.ItemBean
import com.app.core.model.chat.UserBean
import com.app.core.preferences.PreferenceManager

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Class contains queries of firebase database
 */
class FirebaseDatabaseQueries private constructor() {
    private var currentUser: UserBean? = null
    private fun updateUserID() {
        loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)

    }

    /**
     * sign in firebase with email password
     *
     * @param emailAddress user email address
     * @param password     user password
     * @param authListener interface for handle result
     */
    fun signInFirebaseDatabase(
        emailAddress: String?,
        password: String?,
        authListener: FirebaseAuthListener
    ) {
        emailAddress?.let {
            password?.let { it1 ->
                mAuth!!.signInWithEmailAndPassword(it, it1).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "signInWithEmail:success")
                        val user = mAuth!!.currentUser
                        authListener.onAuthSuccess(task, user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        authListener.onAuthError(task)
                    }
                }
            }
        }
    }

    /**
     * create user in firebase with email password
     *
     * @param emailAddress user email address
     * @param password     user password
     * @param authListener interface for handle result
     */
    fun createUserInFirebaseDatabase(
        emailAddress: String?,
        password: String?,
        authListener: FirebaseAuthListener
    ) {
        emailAddress?.let {
            password?.let { it1 ->
                mAuth!!.createUserWithEmailAndPassword(it, it1).addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                            val user = mAuth!!.currentUser
                            authListener.onAuthSuccess(task, user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                            authListener.onAuthError(task)
                        }
                    })
            }
        }
    }

    /**
     * Method to create user node in firebase database
     *
     * @param mContext
     * @param userBean contains user data
     */
    fun createUser(mContext: Context?, userBean: UserBean?) {
        // updateUserData(mContext);
        loginUserId = userBean?.userId
        firebaseDatabaseRef?.child(FirebaseConstants.USERS_NODE)?.child(loginUserId!!)
            ?.setValue(userBean)
    }


    /**
     * Method to create items node in firebase database
     *
     * @param mContext
     * @param userBean contains user data
     */
    fun createItem(itemBean: ItemBean) {
        firebaseDatabaseRef?.child(FirebaseConstants.ITEMS_NODE)?.child(itemBean?.itemId ?: "")
            ?.setValue(itemBean)
    }

    fun updateUser(mContext: Context? = null, onlineStatus: String? = null) {
        var userBean: UserBean? = null
        userBean = getCurrentUser(onlineStatus)
        updateUserID()
        firebaseDatabaseRef?.child(FirebaseConstants.USERS_NODE)?.child(
            (loginUserId)!!
        )?.setValue(userBean)
        /*else{
            userBean = UserBean()
            userBean.onlineStatus = onlineStatus
        }*/

    }

    /**
     * Method to get the current user
     */
    fun getCurrentUser(onlineStatus: String? = null): UserBean {
        val loginUser = UserBean()
        loginUser.userId = PreferenceManager.getString(PreferenceManager.USER_ID)

        loginUser.name = PreferenceManager.getString(PreferenceManager.NAME)
        loginUser.image = PreferenceManager.getString(PreferenceManager.PROFILE_IMAGE)
        loginUser.email = PreferenceManager.getString(PreferenceManager.EMAIL)
        loginUser.deviceToken = PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN)
        loginUser.userType = 2
        loginUser.deviceType = 3
        if (!onlineStatus.isNullOrEmpty())
            loginUser.onlineStatus = System.currentTimeMillis()
        else
            loginUser.onlineStatus = System.currentTimeMillis()

        currentUser = loginUser
        return loginUser
    }

    fun getFullName(firstName: String?, lastName: String?): String? {
        return if (!firstName.isNullOrEmpty()) {
            if (!lastName.isNullOrEmpty()) {
                return "$firstName $lastName"
            }
            return firstName
        } else null
    }

    /**
     * Method to create and send text message
     *
     * @param chatMessage
     * @param users
     * @param chatType
     */
    fun sendChatMessage(
        chatMessage: ChatMessageBean,
        users: List<String>?,
        chatType: String,
        groupName: String
    ) {

        chatMessage.messageStatus = (FirebaseConstants.SEND)
        createInboxNode(chatMessage, users, chatType)
        createMessageNode(chatMessage, users, chatType, groupName)
    }

    /**
     * Method to read all message
     *
     * @param userId
     * @param itemId
     */
    fun readAllMsg(userId: String?, itemId: String?) {
        FirebaseDatabaseQueries.firebaseDatabaseRef!!.child(FirebaseConstants.INBOX_NODE)
            .child(userId.toString())
            .child("${itemId}")
            .child(FirebaseConstants.UNREAD_MESSAGES_COUNT_NODE)
            .setValue(0)
    }

    /**
     * Method to create message node in firebase database
     *
     * @param oldChaMessage
     * @param users
     * @param groupName
     */
    private fun createMessageNode(
        oldChaMessage: ChatMessageBean,
        users: List<String>?,
        chatType: String,
        groupName: String
    ) {
        val chatMessage: ChatMessageBean = oldChaMessage
        chatMessage.messageTimestamp = (ServerValue.TIMESTAMP)
        chatMessage.isDeleted = (false)
        if (chatMessage.messageId == null || chatMessage.messageId.equals("")) {
            val messageId = firebaseDatabaseRef!!
                .child(FirebaseConstants.MESSAGES_NODE)
                .child(chatMessage.messageRoomId!!)
                .push().key
            chatMessage.messageId = messageId
        }
        firebaseDatabaseRef!!
            .child(FirebaseConstants.MESSAGES_NODE)
            .child(chatMessage.messageRoomId!!)
            .child(chatMessage.messageId!!)
            .setValue(chatMessage)
        firebaseDatabaseRef!!
            .child(FirebaseConstants.LAST_MESSAGE_NODE)
            .child(chatMessage.messageRoomId!!)
            .child(loginUserId!!)
            .removeValue()

        firebaseDatabaseRef!!
            .child(FirebaseConstants.LAST_MESSAGE_NODE)
            .child(chatMessage.messageRoomId!!)
            .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE)
            .setValue(chatMessage)

        if (!chatMessage.isBlock) {
            firebaseDatabaseRef!!
                .child(FirebaseConstants.LAST_MESSAGE_NODE)
                .child(chatMessage.messageRoomId!!)
                .child(chatMessage.receiverId!!)
                .removeValue()
            // users?.let { sendNotification(chatMessage, it) }
        }
    }

    /**
     * methed to create chat room
     *
     * @param users
     * @param chatType
     */
    fun createChatRoom(userIdList: List<String>, chatType: String?, roomId: String?) {
        val chatRoomBean = ChatRoomBean()
        chatRoomBean.chatRoomId = roomId
        chatRoomBean.chatRoomType = chatType
        chatRoomBean.chatLastUpdate = (ServerValue.TIMESTAMP)

        val lastUpdates = HashMap<String, Any>()
        val roomMembers = HashMap<String, Any>()
        for (userId in userIdList) {
            lastUpdates[userId] =
                if ((userId == loginUserId)) ServerValue.TIMESTAMP else 0
            val memberStatus = HashMap<String, Any>()
            memberStatus[FirebaseConstants.MEMBER_JOIN_NODE] = ServerValue.TIMESTAMP
            memberStatus[FirebaseConstants.MEMBER_DELETE_NODE] = ServerValue.TIMESTAMP
            memberStatus[FirebaseConstants.MEMBER_LEAVE_NODE] = 0
            roomMembers[userId] = memberStatus
        }
        chatRoomBean.chatLastUpdates = (lastUpdates)
        chatRoomBean.chatRoomMembers = (roomMembers)
        firebaseDatabaseRef!!
            .child(FirebaseConstants.ROOM_INFO_NODE)
            .child(roomId!!)
            .setValue(chatRoomBean)
    }

    /**
     * Method to create inbox node
     *
     * @param chatMessage
     * @param users
     * @param chatType
     */
    private fun createInboxNode(
        chatMessage: ChatMessageBean,
        userIdList: List<String>?,
        chatType: String
    ) {
        try {
            userIdList?.let {
                for (user in userIdList) {
                    val nodeBean = NodeBean()
                    nodeBean.roomId = chatMessage.messageRoomId
                    nodeBean.jobId = chatMessage.jobId
                    nodeBean.jobName = chatMessage.jobName
                    if (user == loginUserId) {
                        Log.d("FM_DATA", "login user")

//                        nodeBean.itemId = "${chatMessage.receiverId}_${chatMessage.jobId}"
//                        firebaseDatabaseRef!!
//                            .child(FirebaseConstants.INBOX_NODE)
//                            .child(user)
//                            .child("${nodeBean.itemId}")
//                            .setValue(nodeBean)

                    } else {
                        Log.d("FM_DATA", "other user ${user != null}")
                        nodeBean.itemId = "${loginUserId}_${chatMessage.jobId}"
                        setOtherUserInbox(
                            user,
                            loginUserId,
                            nodeBean.roomId!!,
                            nodeBean.itemId,
                            chatMessage.jobId,
                            chatMessage.jobName
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("::: error : - ${e.message}")
        }
    }


    /**
     * Method to create user last message
     *
     * @param chatMessage
     * @param users
     * @param chatType
     */
    private fun createUserLastMessageNode(
        chatMessage: ChatMessageBean
    ) {

        firebaseDatabaseRef!!
            .child(FirebaseConstants.USER_LAST_MESSAGE_NODE)
            .child(loginUserId!!)


        firebaseDatabaseRef!!
            .child(FirebaseConstants.USER_LAST_MESSAGE_NODE)
            .child(loginUserId!!)
            .child(chatMessage.receiverId!!)
            .setValue(chatMessage)

    }


    /**
     * Method used to cerate other user inbox entry node
     *
     * @param otherUserId
     * @param loginUserOrRoomId
     * @param roomId
     */
    private fun setOtherUserInbox(
        otherUserId: String,
        loginUserId: String?,
        roomId: String,
        itemId: String?,
        jobId: String?,
        jobName: String?
    ) {
        if (loginUserId != null) {

            val nodeBean = NodeBean()
            nodeBean.roomId = roomId
            nodeBean.itemId = itemId
            nodeBean.jobId = jobId
            nodeBean.jobName = jobName

            firebaseDatabaseRef!!
                .child(FirebaseConstants.INBOX_NODE)
                .child(otherUserId)
                .child("${itemId}")
                .child(FirebaseConstants.UNREAD_MESSAGES_COUNT_NODE)
                .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("FM_DATA", "unread count >> $dataSnapshot")
                        var unreadMessageCount = 0
                        if (dataSnapshot.value != null) {
                            unreadMessageCount = dataSnapshot.value.toString().toInt()
                        }
//                        firebaseDatabaseRef!!.child(FirebaseConstants.INBOX_NODE)
//                            .child(otherUserId)
//                            .child("${itemId}")
//                            .child(FirebaseConstants.UNREAD_MESSAGES_COUNT_NODE)
//                            .setValue(unreadMessageCount.plus(1))
                        nodeBean.unreadMessages = unreadMessageCount.plus(1)
                        firebaseDatabaseRef!!
                            .child(FirebaseConstants.INBOX_NODE)
                            .child(otherUserId)
                            .child("${itemId}")
                            .setValue(nodeBean)

                    }
                })
        }
    }

    /**
     * Method to get the room id between two users
     *
     * @param firstUserId
     * @param secondUserId
     * @param firebaseRoomResponseListener
     */
    fun getRoomId(
        firstUserId: String?,
        secondUserId: String?,
        jobId: String?,
        firebaseRoomResponseListener: FirebaseRoomResponseListener
    ) {
        firebaseDatabaseRef!!
            .child(FirebaseConstants.INBOX_NODE)
            .child((firstUserId)!!)
            .child((secondUserId)!! + "_" + jobId)
            .child(FirebaseConstants.ROOM_ID)
            .addListenerForSingleValueEvent(object : FirebaseEventListeners() {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        firebaseRoomResponseListener.getRoomId(dataSnapshot.value.toString());
                    } else {
                        firebaseRoomResponseListener.getRoomId(null);

                    }
                }
            })


    }

    /**
     * Method to get the room details
     *
     * @param roomId
     * @param firebaseRoomResponseListener
     */
    fun getRoomDetails(
        roomId: String?,
        firebaseRoomResponseListener: FirebaseRoomResponseListener
    ) {
        firebaseDatabaseRef!!.child(FirebaseConstants.ROOM_INFO_NODE).child(
            (roomId)!!
        ).addListenerForSingleValueEvent(object : FirebaseEventListeners() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val roomBean: ChatRoomBean? = dataSnapshot.getValue(ChatRoomBean::class.java)
                    firebaseRoomResponseListener.getRoomDetails(roomBean)
                }
            }
        })
    }

    /**
     * Method to get last seen
     *
     * @param roomId
     * @param firebaseRoomResponseListener
     */
    fun getLastMessageInfo(
        roomId: String? = null,
        inBoxMessagesBean: ChatMessageBean? = null,
        messageListener: FirebaseMessageListener
    ) {
        val rId = roomId ?: (inBoxMessagesBean?.messageRoomId)
        firebaseDatabaseRef!!
            .child(FirebaseConstants.LAST_MESSAGE_NODE)
            .child(rId!!)
            .addListenerForSingleValueEvent(
                object : FirebaseEventListeners() {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != null) {

                            val hashMap: HashMap<String, Any?>? =
                                dataSnapshot.value as HashMap<String, Any?>?

                            hashMap?.let { it ->
                                if (it.containsKey("chatLastMessage")) {
                                    val hashMapMessage =
                                        it["chatLastMessage"] as HashMap<String, Any?>?

                                    // saving UNREAD MESSAGE COUNT
                                    if (inBoxMessagesBean != null) {
                                        hashMapMessage?.let {
                                            prepareData(inBoxMessagesBean, hashMapMessage)
                                            messageListener.getMessages(inBoxMessagesBean)
                                        }
                                    }
                                    /*   getUser(
                                           inBoxMessagesBean?.receiverId!!,
                                           object : FirebaseUserListener{
                                               override fun getUser(user: UserBean?) {
                                                   user?.let {
                                                       println(":::: Receiver Id: ${inBoxMessagesBean.receiverId}, NAME: ${it.name}")
                                                       // setting image
                                                       if (!it.image.isNullOrEmpty()) {
                                                           inBoxMessagesBean.senderImage = it.image!!
                                                       }

                                                       // setting name
                                                       if (!it.name.isNullOrEmpty()) {
                                                           inBoxMessagesBean.senderName = it.name
                                                       }
                                                       // setting userType
                                                       if (!it.userType.isNullOrEmpty()) {
                                                           inBoxMessagesBean.senderType = it.userType
                                                       }
                                                   }
                                                   messageListener.getMessages(inBoxMessagesBean)

                                               }
                                           }
                                       )*/

                                }
                            }
                        } else {
                            messageListener.noData()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("TAG", "")
                    }
                }

            )
    }


    /**
     * Method to get the block status
     *
     * @param otherUserId
     * @param firebaseBlockStatusListener
     */
    fun getBlockStatus(
        otherUserId: String?,
        firebaseBlockStatusListener: FirebaseBlockStatusListener
    ) {
        firebaseDatabaseRef!!.child(FirebaseConstants.LAST_MESSAGE_NODE).child((otherUserId)!!)
            .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).child("block")
            .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        if (dataSnapshot.value != null) {
                            firebaseBlockStatusListener.isUserBlock(dataSnapshot.value as Boolean)
                        } else {
                            firebaseBlockStatusListener.isUserBlock(false)
                        }
                    } catch (ex: Exception) {
                    }
                }
            })
    }

    /**
     * Method to get all the room messages
     *
     * @param roomId
     * @param deleteStamp
     * @param timeStamp
     * @param listener
     */
    fun getPreviousMessages(
        roomId: String?,
        deleteStamp: Long,
        timeStamp: Long,
        listener: FirebaseMessageListener
    ) {
        firebaseDatabaseRef!!
            .child(FirebaseConstants.MESSAGES_NODE)
            .child(roomId!!)
            .orderByChild(FirebaseConstants.TIME_STAMP)
            .limitToLast(10)
            .startAt(deleteStamp.toDouble())
            .endAt(timeStamp.toDouble())
            .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        val chatMessageList: MutableList<ChatMessageBean?> =
                            ArrayList<ChatMessageBean?>()
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            val chatMessage: ChatMessageBean? =
                                snapshot.getValue(ChatMessageBean::class.java)
                            if (chatMessage != null && !chatMessage.isBlock) {
                                chatMessageList.add(chatMessage)
                            }
                        }
                        Collections.reverse(chatMessageList)
                        listener.getMessagesList(chatMessageList)
                    }
                }
            })
    }

    /**
     * Method to get all the room messages
     *
     * @param roomId
     * @param deleteStamp
     * @param listener
     */
    fun getAllImages(roomId: String?, deleteStamp: Long, listener: FirebaseMessageListener) {
        firebaseDatabaseRef!!.child(FirebaseConstants.MESSAGES_NODE).child(
            (roomId)!!
        )
            .orderByChild(FirebaseConstants.MESSAGE_TYPE).equalTo(FirebaseConstants.IMAGE)
            .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        val chatMessageList: MutableList<ChatMessageBean?> =
                            ArrayList<ChatMessageBean?>()
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            val chatMessage: ChatMessageBean? =
                                snapshot.getValue(ChatMessageBean::class.java)
                            if ((chatMessage != null) && !chatMessage.isBlock && (chatMessage.messageTimestamp as Long > deleteStamp)) {
                                chatMessageList.add(chatMessage)
                            }
                        }
                        listener.getMessagesList(chatMessageList)
                    }
                }
            })
    }

    /**
     * Method to change the message status to read
     *
     * @param roomId
     * @param messageId
     */
    fun changeMessageStatus(roomId: String?, messageId: String?) {
        firebaseDatabaseRef!!.child(FirebaseConstants.MESSAGES_NODE).child(
            (roomId)!!
        ).child((messageId)!!).child(FirebaseConstants.MESSAGE_STATUS)
            .setValue(FirebaseConstants.READ)
    }

    /**
     * Method to delete chat room
     *
     * @param messageBean
     */
    fun deleteChat(messageBean: InboxMessageBean) {
        firebaseDatabaseRef!!.child(FirebaseConstants.ROOM_INFO_NODE).child(messageBean.roomId)
            .child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE).child(
                (loginUserId)!!
            )
            .child(FirebaseConstants.MEMBER_DELETE_NODE).setValue(ServerValue.TIMESTAMP)
        firebaseDatabaseRef!!.child(FirebaseConstants.INBOX_NODE).child(
            (loginUserId)!!
        ).child(messageBean.userBean?.userId!!).removeValue()
    }

    /**
     * Method to delete chat room
     *
     * @param
     */
    fun deleteChat(roomId: String?) {
        firebaseDatabaseRef!!.child(FirebaseConstants.ROOM_INFO_NODE).child(
            (roomId)!!
        ).child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE).child((loginUserId)!!)
            .child(FirebaseConstants.MEMBER_DELETE_NODE).setValue(ServerValue.TIMESTAMP)
    }

    /**
     * Method used to block user
     *
     * @param roomId
     * @param isBlock
     * @param userId
     */
    fun blockUser(roomId: String?, isBlock: Boolean, userId: String?) {
        if (isBlock) {
            firebaseDatabaseRef!!.child(FirebaseConstants.BLOCK_NODE).child(
                (loginUserId)!!
            ).child((userId)!!).setValue(ServerValue.TIMESTAMP)
            if (roomId != null) {
                firebaseDatabaseRef!!.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId)
                    .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE)
                    .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.value != null) {
                                firebaseDatabaseRef!!.child(FirebaseConstants.LAST_MESSAGE_NODE)
                                    .child(roomId).child(
                                        (loginUserId)!!
                                    )
                                    .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE)
                                    .setValue(dataSnapshot.value)
                            }
                        }
                    })
            }
        } else {
            firebaseDatabaseRef!!.child(FirebaseConstants.BLOCK_NODE).child(
                (loginUserId)!!
            ).child((userId)!!).removeValue()
        }
    }

    /*
     * Method used to block user
     *
     * @param roomId
     */
    fun setLastMessageOnClearChat(roomId: String?) {
        if (roomId != null) {
            firebaseDatabaseRef!!.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId)
                .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE)
                .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != null) {
                            val chatMessageBean: ChatMessageBean? = dataSnapshot.getValue(
                                ChatMessageBean::class.java
                            )
                            if (chatMessageBean != null) {
                                chatMessageBean.messageText = "Chat Cleared"
                                firebaseDatabaseRef!!.child(FirebaseConstants.LAST_MESSAGE_NODE)
                                    .child(roomId).child(
                                        (loginUserId)!!
                                    )
                                    .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE)
                                    .setValue(chatMessageBean)
                            }
                        }
                    }
                })
        }
    }

    /**
     * Method used to block user
     *
     * @param roomId
     */
    fun getLastSendMessagesList(roomId: String?, messageListener: FirebaseMessageListener) {
        if (roomId != null) {
            firebaseDatabaseRef!!
                .child(FirebaseConstants.LAST_MESSAGE_NODE)
                .child(roomId)
                .addListenerForSingleValueEvent(
                    object : FirebaseEventListeners() {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.value != null) {

                                val chatMessageList: MutableList<ChatMessageBean?> =
                                    ArrayList<ChatMessageBean?>()
                                for (snapshot: DataSnapshot in dataSnapshot.children) {
                                    val chatMessage: ChatMessageBean? =
                                        snapshot.getValue(ChatMessageBean::class.java)
                                    chatMessageList.add(chatMessage)
                                }
                                messageListener.getMessagesList(chatMessageList)
                            }
                        }
                    })
        }
    }

    /**
     * Method used to block user
     *
     * @param roomId
     */
    fun getInboxMessagesList(
        userId: String?,
        messageListener: FirebaseMessageListener
    ) {
        val path = firebaseDatabaseRef
            ?.child(FirebaseConstants.INBOX_NODE)
            ?.child(userId!!)

        path?.addListenerForSingleValueEvent(
            object : FirebaseEventListeners() {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("FM_DATA", "getInboxMessagesList >> ")
                    if (dataSnapshot.value != null) {
                        val chatMessageList: MutableList<ChatMessageBean?> =
                            ArrayList<ChatMessageBean?>()
                        for (snapshot: DataSnapshot in dataSnapshot.children) {

                            val hashMap: HashMap<String, Any?>? =
                                snapshot.value as HashMap<String, Any?>?
                            hashMap?.let {
                                val chatMessages = ChatMessageBean()
                                if (it.containsKey("roomId")) {
                                    chatMessages.messageRoomId =
                                        it["roomId"] as String
                                }

                                if (it.containsKey("itemId")) {
                                    chatMessages.itemId =
                                        it["itemId"].toString()
                                }
                                if (it.containsKey("unreadMessages")) {
                                    chatMessages.unreadMessages =
                                        it["unreadMessages"] as Long
                                    totalUnReadMessageCount += chatMessages.unreadMessages.toInt()
                                }
                                if (it.containsKey("jobId")) {
                                    chatMessages.jobId =
                                        it["jobId"].toString()
                                }
                                if (it.containsKey("jobName")) {
                                    chatMessages.jobName =
                                        it["jobName"].toString()
                                }
                                chatMessageList.add(chatMessages)
                            }

                        }
                        messageListener.getMessagesList(chatMessageList)
                    } else {
                        messageListener.getMessagesList(null)
                    }
                }
            })
    }

    fun checkInboxNewMessagesList(
        userId: String?,
        messageListener: FirebaseMessageListener,
        callbackListener: FirebaseCallbackListener? = null
    ) {
        val path = firebaseDatabaseRef
            ?.child(FirebaseConstants.INBOX_NODE)
            ?.child(userId!!)

        var listener: FirebaseEventListeners? = null

        listener = object : FirebaseEventListeners() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Log.e("is_new_message_arrived","inside yes")
                messageListener.newMessagesListing()
                callbackListener?.getCallbackListeners(path, listener)
            }
        }
        path?.addValueEventListener(listener)
    }

    fun getUnreadMessagesCount(
        userId: String?,
        callback: FirebaseUnreadCountListener
    ) {
        val path = firebaseDatabaseRef
            ?.child(FirebaseConstants.INBOX_NODE)
            ?.child(userId!!)

        var listener: FirebaseEventListeners? = null
        listener = object : FirebaseEventListeners() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("FM_Data", "getUnreadMessagesCount ")
                if (dataSnapshot.value != null) {
                    var unreadCount = 0

                    for (snapshot: DataSnapshot in dataSnapshot.children) {

                        val hashMap: HashMap<String, Any?>? =
                            snapshot.value as HashMap<String, Any?>?
                        hashMap?.let {

                            if (it.containsKey("unreadMessages")) {

                                unreadCount += (it["unreadMessages"] as Long).toInt()
                            }

                        }

                    }
                    Log.d("FM_Data", "getUnreadMessagesCount >> $unreadCount ")

                    callback.getCount(unreadCount, listener, path)
                } else {
                    callback.getCount(0, listener, path)

                }
            }

        }

        path?.addValueEventListener(listener)
    }

    /**
     * Method to set delete status of message
     *
     * @param roomId
     * @param messageId
     */
    fun setMessagesDeleteStatus(roomId: String?, messageId: String?, isLastMessage: Boolean) {
        firebaseDatabaseRef!!.child(FirebaseConstants.MESSAGES_NODE).child(
            (roomId)!!
        ).child((messageId)!!).child(FirebaseConstants.IS_MESSAGE_DELETE).setValue(true)
        if (isLastMessage) {
            firebaseDatabaseRef!!.child(FirebaseConstants.LAST_MESSAGE_NODE).child(
                (roomId)
            ).child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE)
                .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.value != null) {
                            val chatMessageBean: ChatMessageBean? = dataSnapshot.getValue(
                                ChatMessageBean::class.java
                            )
                            if (chatMessageBean != null) {
                                chatMessageBean.messageText = "This message has deleted"
                                firebaseDatabaseRef!!.child(FirebaseConstants.LAST_MESSAGE_NODE)
                                    .child(
                                        (roomId)
                                    )
                                    .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE)
                                    .setValue(chatMessageBean)
                            }
                        }
                    }
                })
        }
    }

    /**
     * Method to send notification
     */
    private fun sendNotification(chatMessageBean: ChatMessageBean, users: List<UserBean>) {
        /*      for (user: UserBean in users) {
                  if (user.userId != null && user.userId != loginUserId) {
                      firebaseDatabaseRef!!.child(FirebaseConstants.USERS_NODE).child(user.userId!!)
                          .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                              override fun onDataChange(dataSnapshot: DataSnapshot) {
                                  if (dataSnapshot != null) {
                                      val userBean = dataSnapshot.getValue(UserBean::class.java)
                                      if (userBean != null) {
                                          try {
                                              val headerParams = HashMap<String, String>()
                                              headerParams[FirebaseConstants.PARAM_CONTENT_TYPE] =
                                                  FirebaseConstants.APPLICATION_JSON
                                              headerParams[FirebaseConstants.PARAM_AUTHORIZATION] =
                                                  BuildConfig.FIREBASE_SERVER_KEY
                                              val finalMap = JSONObject()
                                              val jsonData = JSONObject()
                                              val notificationBean = NotificationBean()
                                              notificationBean.messageId = (chatMessageBean.messageId)
                                              notificationBean.messageText = (chatMessageBean.messageText)
                                              notificationBean.roomId = (chatMessageBean.messageRoomId)
                                              notificationBean.sender = (currentUser)
                                              chatMessageBean.messageTimestamp = (Calendar.getInstance().timeInMillis)
                                              jsonData.put("body", Gson().toJson(notificationBean))
                                              finalMap.put("to", userBean.deviceToken)
                                              finalMap.put("data", jsonData)
                                              finalMap.put("priority", "High")
                                              finalMap.put("mutableContent", true)
                                              AndroidNetworking.post(FirebaseConstants.SEND_PUSH_NOTIFICATION)
                                                  .addHeaders(headerParams)
                                                  .addJSONObjectBody(finalMap)
                                                  .setTag(ContentValues.TAG)
                                                  .setPriority(Priority.HIGH)
                                                  .build()
                                                  .getAsString(object : StringRequestListener() {
                                                      fun onResponse(response: String?) {
                                                          Log.e(
                                                              "AFN Stop Time",
                                                              System.currentTimeMillis().toString() + ""
                                                          )
                                                      }

                                                      fun onError(anError: ANError) {
                                                          Log.e("error", anError.getErrorDetail())
                                                      }
                                                  })
                                          } catch (e: JSONException) {
                                              e.printStackTrace()
                                          }
                                      }
                                  }
                              }
                          })
                  }
              }*/
    }

    fun generateAndGetRoomId(): String? {
        return firebaseDatabaseRef!!
            .child(FirebaseConstants.ROOM_INFO_NODE)
            .push()
            .key
    }

    private var loginUserId: String? = null


    companion object {
        /**
         * Method ro grt instance of class
         *
         * @return
         */
        var instance: FirebaseDatabaseQueries? = null
            get() {
                if (field == null) field = FirebaseDatabaseQueries()
                return field
            }
            private set
        private var firebaseDatabaseRef: DatabaseReference? = null
        private var mAuth: FirebaseAuth? = null

        var totalUnReadMessageCount = 0
    }

    init {
        if (firebaseDatabaseRef == null) {
            firebaseDatabaseRef = FirebaseDatabase.getInstance().reference
            mAuth = FirebaseAuth.getInstance()
        }
        loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
    }


    fun getUserBeanData(
        userId: String? = null,
        fullName: String? = null,
        email: String? = null,
        profilePicture: String? = null,
        onlineStatus: Long? = null,
        userType: Int? = null,
        deviceToken: String? = null,
        deviceId: String? = null,
    )
            : UserBean {
        val userBean = UserBean()
        userBean.userId = userId
        userBean.name = fullName
        userBean.email = email
        userBean.image = profilePicture
        userBean.onlineStatus = onlineStatus
        userBean.userType = userType
        userBean.deviceToken = deviceToken

        return userBean
    }

    private fun prepareData(
        inBoxMessagesBean: ChatMessageBean,
        it: java.util.HashMap<String, Any?>
    ) {
        // media Url
        if (it.containsKey("mediaUrl")) {
            inBoxMessagesBean.mediaUrl =
                it["mediaUrl"] as String
        }

        // messageId Url
        if (it.containsKey("messageId")) {
            inBoxMessagesBean.messageId =
                it["messageId"] as String
        }

        // messageRoomId Url
        if (it.containsKey("messageRoomId")) {
            inBoxMessagesBean.messageRoomId =
                it["messageRoomId"] as String
        }
        // messageStatus Url
        if (it.containsKey("messageStatus")) {
            inBoxMessagesBean.messageStatus =
                it["messageStatus"] as String
        }

        // messageText Url
        if (it.containsKey("messageText")) {
            inBoxMessagesBean.messageText =
                it["messageText"] as String
        }

        // messageTimestamp Url
        if (it.containsKey("messageTimestamp")) {
            inBoxMessagesBean.messageTimestamp =
                it["messageTimestamp"] as Long
        }

        // messageType Url
        if (it.containsKey("messageType")) {
            inBoxMessagesBean.messageType =
                it["messageType"] as String
        }

        // receiverId Url
        if (it.containsKey("receiverId")) {
            inBoxMessagesBean.receiverId =
                it["receiverId"] as String
        }
        // senderId Url
        if (it.containsKey("senderId")) {
            inBoxMessagesBean.senderId =
                it["senderId"] as String
        }
    }

    fun getUser(
        receiverId: String,
        firebaseUserListener: FirebaseUserListener,
        jobId: String? = "",
        getCallback: (user: UserBean?, id: String?) -> Unit = { userBean: UserBean?, id: String? -> }
    ) {
        firebaseDatabaseRef!!
            .child(FirebaseConstants.USERS_NODE)
            .child(receiverId)
            .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userBean = dataSnapshot.getValue(UserBean::class.java)
                    firebaseUserListener.getUser(userBean)
                    getCallback(userBean, jobId)
                }
            })
    }

    fun getItem(itemId: String, firebaseItemListener: FirebaseItemListener) {
        firebaseDatabaseRef!!
            .child(FirebaseConstants.ITEMS_NODE)
            .child(itemId)
            .addListenerForSingleValueEvent(object : FirebaseEventListeners() {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val itemBean = dataSnapshot.getValue(ItemBean::class.java)
                    firebaseItemListener.getItem(itemBean)
                }
            })
    }

}