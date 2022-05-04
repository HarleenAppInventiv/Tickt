package com.example.ticktapp.mvvm.view.builder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airhireme.firebase.model.ChatRoomBean
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.model.profile.FirebaseModel
import com.app.core.model.profile.FirebaseNotificationModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.bumptech.glide.Glide
import com.dreamg.videoTrimmer.LightVideoCompression
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ChatAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.base.LoaderType
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.dialog.CameraVideoBottomSheet
import com.example.ticktapp.firebase.*
import com.example.ticktapp.mvvm.viewmodel.FirebaseViewModel
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.example.ticktapp.util.MoEngageUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_builder_chat.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatBuilderActivity : BaseActivity(), Comparator<ChatMessageBean>,
    PermissionHelper.IGetPermissionListener, CameraVideoBottomSheet.CameraDialogCallBack {

    private var messageListener: ChildEventListener? = null
    private lateinit var singleEventListener: FirebaseEventListeners
    private var msgPath: Query? = null
    private var currentRoomId: String? = null
    private var currentChatRoom: ChatRoomBean? = null
    private var chatMessageBean: ChatMessageBean? = null
    private var otherUserID: String = ""
    private var jobID: String = ""
    private var jobName: String = ""
    private var to: String = ""
    private var senderName: String = ""
    private var loginUserId: String? = null
    private var loginUserName: String? = null
    private var imageUri: Uri? = null
    private var typeOfAction: Int = 0
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mChatAdapter: ChatAdapter
    private var pageNo: Int = 1
    private var totalPage: Int = 0
    private var shouldNextHit: Int = 0
    private var isSwipeToRefresh: Boolean = false
    private val chatMessagesList = ArrayList<ChatMessageBean>()
    private var users = arrayListOf<String>()
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )

    private val mUplodaViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private var uri: Uri? = null
    private var fPAth: String? = null
    private lateinit var permissionHelper: PermissionHelper
    private val mViewModel by lazy { ViewModelProvider(this).get(FirebaseViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_builder_chat)
        setStatusBarColor()
        setLightStatusBar(clMain)
        FirebaseDatabaseQueries.instance?.updateUser(onlineStatus = "true")
        getIntentData()
        setObservers()
        getPrefData()
        manageView()
        updateStatusUpdate()
        setListener()
        setAdapter()
        getChatUser()
        CHATtRADIEMoEngage()
        chatMixPanel()

        if (intent.hasExtra("senderName")) {
            senderName = intent.extras!!.getString("senderName", "")
            tvUserName.text=senderName
        }
    }

    private fun CHATtRADIEMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_CHAT,
            signUpProperty
        )

    }

    private fun chatMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_CHAT,
            props
        )
    }


    private fun getChatUser() {
        FirebaseDatabaseQueries.instance?.getUser(
            otherUserID,
            object : FirebaseUserListener {
                override fun getUser(user: UserBean?) {
                    try {
                        user?.let {
                            if (user.deviceToken != null)
                                to = user.deviceToken.toString()
                        }

                    } catch (e: Exception) {
                    }

                }
            })
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(mUplodaViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mUplodaViewModel.getResponseObserver().observe(this, this)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.white))
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(resources.getColor(R.color.white))
        }
    }

    private fun manageView() {
        chatMessageBean?.let { it ->
            tvJobName.text = it.jobName
            tvUserName.text = it.senderName
            senderName = it.senderName.toString()
            if (!it.senderImage.isNullOrEmpty()) {
                Glide.with(iv_profile.context).load(it.senderImage!!)
                    .placeholder(R.drawable.placeholder_profile)
                    .into(iv_profile)
            } else {
                iv_profile.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.placeholder_profile,
                        null
                    )
                )
            }
        }
        if (loginUserId == chatMessageBean?.receiverId.toString()) {
            otherUserID = chatMessageBean?.senderId.toString()
        } else {
            otherUserID = chatMessageBean?.receiverId.toString()
        }
        jobID = chatMessageBean?.jobId.toString()
        jobName = chatMessageBean?.jobName.toString()
        users.add(otherUserID)
        users.add(loginUserId!!)
        getChatList()
        (application as ApplicationClass).setCurrentChatJobID(jobID)
    }

    private fun getChatList() {
        //check room id exists or not
        if (currentRoomId != null) {
            getChatRoomDetails(currentRoomId!!)
        } else {
            getRoomId()
        }
    }

    private fun getPrefData() {
        loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        loginUserName = PreferenceManager.getString(PreferenceManager.NAME)
    }

    private fun updateStatusUpdate() {
        otherUserID?.let {
            FirebaseDatabaseQueries.instance?.getBlockStatus(
                "${jobID}_${otherUserID}_${loginUserId}",
                object :
                    FirebaseBlockStatusListener {
                    override fun isUserBlock(isBlock: Boolean) {
                        if (isBlock) {
                            rl_blockUser.visibility = View.VISIBLE
                            clMessage.visibility = View.INVISIBLE
                        } else {
                            rl_blockUser.visibility = View.INVISIBLE
                            clMessage.visibility = View.VISIBLE
                        }
                    }
                })

        }
    }

    private fun getIntentData() {
        intent?.apply {
            if (intent.hasExtra("data")) {
                chatMessageBean = intent.getSerializableExtra("data") as ChatMessageBean
            }
        }
    }


    private fun setAdapter() {
        mChatAdapter = ChatAdapter(chatMessagesList)
        mLayoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        rcv_message.adapter = mChatAdapter
        rcv_message.layoutManager = mLayoutManager
    }

    override fun onCameraClicked() {
        if (permissionHelper.hasPermission(
                this,
                permissions,
                PermissionConstants.REQ_CAMERA
            )
        ) {
            openCamera()

        }
    }

    override fun onGalleryClicked() {
        if (permissionHelper.hasPermission(
                this,
                permissions,
                PermissionConstants.REQ_CAMERA
            )
        ) {
            onGalleryVideoChoose(false)

        }
    }

    override fun onVideoCapture() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            captureVideo()
        }
    }

    private fun setListener() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)

        swipe_message.setOnRefreshListener {
            chatMessagesList.clear()
            getChatList()
        }
        iv_back.setOnClickListener {
            onBackPressed()
        }
        ivSendPic.setOnClickListener {
            if (permissionHelper.hasPermission(
                    this,
                    permissions, PermissionConstants.PERMISSION
                )
            ) {
                CameraVideoBottomSheet(this, this).show()
            }
        }
        tv_visit_profile.setOnClickListener {
            startActivity(
                Intent(this, TradieProfileActivity::class.java).putExtra(
                    "otherUserID",
                    otherUserID
                ).putExtra("jobID", jobID).putExtra("isChat", true)
            )

        }
    }


    fun onSendClick(view: View) {
        val message = etMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            etMessage.setText("")
            val chatMessage = ChatMessageBean()
            chatMessage.messageText = message
            chatMessage.messageType = FirebaseConstants.TEXT
            chatMessage.messageCaption = ""
            chatMessage.mediaDuration = ""
            chatMessage.mediaUrl = ""
            chatMessage.thumbnail = ""
            chatMessage.senderName = tvUserName.text.toString()
            createMessage(chatMessage)
//            FirebaseDatabaseQueries.instance?.readAllMsg(
//                loginUserId,
//                "${otherUserID}_${jobID}"
//            )
        } else {
            showToastShort(getString(R.string.please_enter_message))
        }
        sendPushNotifications(message, FirebaseConstants.TEXT)
    }

    fun onSendMediaClick(serverUrl: String) {
        if (serverUrl.isNotEmpty()) {
            etMessage.setText("")
            val chatMessage = ChatMessageBean()
            chatMessage.messageText = ""
            if (isVideoFile(serverUrl)) {
                chatMessage.messageType = FirebaseConstants.VIDEO
            } else {
                chatMessage.messageType = FirebaseConstants.IMAGE
            }
            chatMessage.messageCaption = ""
            chatMessage.mediaDuration = ""
            chatMessage.mediaUrl = serverUrl
            chatMessage.senderName = tvUserName.text.toString()
            chatMessage.thumbnail = ""
            createMessage(chatMessage)
//            FirebaseDatabaseQueries.instance?.readAllMsg(
//                loginUserId,
//                "${otherUserID}_${jobID}"
//            )
        } else {
            showToastShort(getString(R.string.please_select_media_file))
        }
    }

    fun sendPushNotifications(text: String, type: String) {
        if (to == null || to.length == 0)
            return
        val model = FirebaseModel()
        model.to = to
        val dataModel = FirebaseNotificationModel()
        dataModel.title = getString(R.string.app_title)
        dataModel.sound = "default"
        dataModel.app_icon =
            "https://appinventiv-development.s3.amazonaws.com/1628513615740ic-logo-yellow.png"
        dataModel.room_id = currentRoomId
        dataModel.senderName = senderName
        dataModel.notificationText = senderName + " send you a message"
        dataModel.body = senderName + " send you a message"
        dataModel.jobId = jobID
        dataModel.jobName = jobName
        dataModel.messageText = text
        dataModel.messageType = type
        dataModel.notificationType = "50"
        dataModel.userType="2"
        model.notification = dataModel
        model.data = dataModel
        mViewModel.sendPushNotifications(model)
    }

    fun isVideoFile(path: String?): Boolean {
        val mimeType: String = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("video")
    }

    /**
     * Method to get the room id
     */
    private fun getRoomId() {
        FirebaseDatabaseQueries.instance?.getRoomId(
            loginUserId,
            otherUserID, jobID,
            object : FirebaseRoomResponseListener {
                override fun getRoomId(roomId: String?) {
                    if (roomId != null) {
                        currentRoomId = roomId
                        getChatRoomDetails(roomId!!)
                    } else {
                        swipe_message.isRefreshing = false
                        manageRecyclerViewVisibility()
                    }
                }

                override fun getRoomDetails(chatRoomBean: ChatRoomBean?) {}
            })
    }

    /**
     * Method to get the chat room details
     *
     * @param roomId
     */
    private fun getChatRoomDetails(roomId: String) {
        FirebaseDatabaseQueries.instance?.getRoomDetails(
            roomId,
            object : FirebaseRoomResponseListener {
                override fun getRoomId(roomId: String?) {}

                override fun getRoomDetails(chatRoomBean: ChatRoomBean?) {
                    currentChatRoom = chatRoomBean
                    getChatMessages()
                }
            })
    }


    private fun getChatMessages() {
        messageListener = object : FirebaseEventListeners() {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                swipe_message.isRefreshing = false

                if (dataSnapshot.value != null) {
                    val chatMessage: ChatMessageBean? =
                        dataSnapshot.getValue(ChatMessageBean::class.java)
                    setChatMessageInList(chatMessage)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                swipe_message.isRefreshing = false
                if (dataSnapshot.value != null) {
                    val chatMessage: ChatMessageBean? =
                        dataSnapshot.getValue(ChatMessageBean::class.java)
                    chatMessage?.let {
                        if (chatMessagesList.isNotEmpty()) {
                            if (chatMessage.messageType.equals(FirebaseConstants.TEXT)) {
                                for (i in chatMessagesList.indices) {
                                    if (chatMessagesList[i].messageId != null
                                        && chatMessagesList[i].messageId.equals(chatMessage.messageId)
                                    ) {
                                        chatMessagesList[i] = chatMessage
                                        break
                                    }
                                }
                            } else {
                                setChatMessageInList(chatMessage)
                            }
                        } else {
                            chatMessagesList.add(it)
                        }

                        mChatAdapter.notifyDataSetChanged()
                    }
                }
                manageRecyclerViewVisibility()

            }
        }

        singleEventListener = object : FirebaseEventListeners() {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    //     include_noData.visibility = View.GONE
                } else {
                    //     include_noData.visibility = View.VISIBLE
                }
            }
        }


        msgPath = FirebaseDatabase.getInstance().reference
            .child(FirebaseConstants.MESSAGES_NODE)
            .child(currentRoomId!!)
            .orderByChild(FirebaseConstants.TIME_STAMP)
            .limitToLast(100)

        msgPath!!.addChildEventListener(messageListener!!)

        FirebaseDatabase.getInstance().reference
            .child(FirebaseConstants.MESSAGES_NODE)
            .child(currentRoomId!!)
            .orderByChild(FirebaseConstants.TIME_STAMP)
            .limitToLast(100)
            .addListenerForSingleValueEvent(singleEventListener)

        FirebaseDatabaseQueries.instance?.readAllMsg(
            loginUserId,
            "${otherUserID}_${jobID}"
        )
    }

    private fun manageRecyclerViewVisibility() {
        if (chatMessagesList.isEmpty()) {
            rcv_message.visibility = View.GONE
            //   include_noData.visibility = View.VISIBLE
        } else {
            rcv_message.visibility = View.VISIBLE
            //   include_noData.visibility = View.GONE
        }
    }

    /**
     * method to set chat message in list
     *
     * @param chatMessage
     */
    private fun setChatMessageInList(chatMessage: ChatMessageBean?) {
        var newMessage = true
        chatMessage?.let {
            if (!it.messageType.equals(FirebaseConstants.TEXT)) {
                for (i in chatMessagesList.indices) {
                    if (!chatMessagesList[i].messageType.equals(FirebaseConstants.CHAT_TIME)
                        && chatMessagesList[i].messageId.equals(it.messageId)
                    ) {
                        var previousMessage: ChatMessageBean? = null
                        //previousMessage = AppDatabase.fetchSingleMediaFilesDetails(it.getMessageId());
                        if (previousMessage == null) previousMessage = chatMessagesList[i]
                        it.mediaUrl = (previousMessage.mediaUrl)
                        it.thumbnail = (previousMessage.thumbnail)
                        if (!previousMessage.messageTimestamp.toString().contains("sv=timestamp")) {
                            it.messageTimestamp = (previousMessage.messageTimestamp)
                        }
                        chatMessagesList[i] = chatMessage
                        mChatAdapter.notifyItemChanged(i)
                        newMessage = false
                        break
                    }
                }
            }

            if (newMessage) {
                setNewChatMessage(chatMessage)
            }
        }
        manageRecyclerViewVisibility()

    }

    /**
     * Method to set new message in message list
     *
     * @param chatMessage
     */
    private fun setNewChatMessage(chatMessage: ChatMessageBean) {
        if (chatMessagesList.isNotEmpty()
            && chatMessagesList[chatMessagesList.size - 1].messageType
                .equals(FirebaseConstants.CHAT_TIME)
        ) {
            chatMessagesList.removeAt(chatMessagesList.size - 1)
        }
        chatMessagesList.add(chatMessage)
        //    Collections.sort(chatMessagesList, this)
        mChatAdapter.notifyDataSetChanged()
        rcv_message.scrollToPosition(chatMessagesList.size - 1)
//        FirebaseDatabaseQueries.instance?.readAllMsg(
//            loginUserId,
//            "${otherUserID}_${jobID}"
//        )
    }

    override fun compare(lhs: ChatMessageBean?, rhs: ChatMessageBean?): Int {
        return if (lhs?.messageTimestamp != null && rhs?.messageTimestamp != null) {
            val lhsTime: Long = lhs?.messageTimestamp.toString().toLong()
            val rhsTime: Long = rhs?.messageTimestamp.toString().toLong()
            if (lhsTime < rhsTime) {
                1
            } else {
                -1
            }
        } else {
            -1
        }
    }

    /**
     * Method to create local message
     *
     * @param chatMessage
     */
    private fun createMessage(chatMessage: ChatMessageBean) {
        BuilderMessageFragment.isChangeOccured = true
        if (currentRoomId == null) {
            /* currentRoomId = FirebaseDatabase.getInstance().reference
                 .child(FirebaseConstants.ROOM_INFO_NODE)
                 .push()
                 .key
            */

            currentRoomId = "${jobID}_${otherUserID}_${loginUserId}"

            FirebaseDatabaseQueries.instance?.createChatRoom(
                users, FirebaseConstants.SINGLE_CHAT, currentRoomId
            )
            getChatRoomDetails(currentRoomId!!)
        }
        chatMessage.receiverId = otherUserID
        chatMessage.senderId = loginUserId
        chatMessage.messageRoomId = currentRoomId
        chatMessage.jobId = jobID
        chatMessage.jobName = jobName
        FirebaseDatabaseQueries.instance?.sendChatMessage(
            chatMessage,
            users,
            FirebaseConstants.SINGLE_CHAT,
            ""
        )

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        try {
            when (apiCode) {
                ApiCodes.UPLOAD_FILE -> {
                    showToastShort(exception.message)
                }
            }
            super.onException(exception, apiCode)
        } catch (ex: Exception) {
            ex.printStackTrace()
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.UPLOAD_FILE -> {
                mUplodaViewModel.imageUploadResponse.url?.let {
                    if (it.isNotEmpty() && it.size > 0) {
                        onSendMediaClick(it.get(0))
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun permissionGiven(requestCode: Int) {
        when (requestCode) {
            PermissionConstants.PERMISSION ->
                CameraVideoBottomSheet(this, this).show()
        }
    }

    override fun permissionCancel(requestCode: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            PermissionConstants.REQ_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    uri = data.data
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
//                        var destinationFileName = System.currentTimeMillis().toString() + ""
//                        destinationFileName += ".jpeg"
//                        val file = File(this.cacheDir, destinationFileName)
//                        if (!file.exists()) {
//                            try {
//                                file.createNewFile()
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                        }
//                        UCrop.of(
//                            Uri.fromFile(File(fPAth)), Uri.fromFile(
//                                File(
//                                    this.cacheDir, destinationFileName
//                                )
//                            )
//                        ).withAspectRatio(1f, 1f)
//                            .start(this)
//
                        fPAth?.let {
                            mUplodaViewModel.hitUploadFile(fPAth!!)
                        }
                    }
                }
            }
            PermissionConstants.REQ_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
//                        var destinationFileName = System.currentTimeMillis().toString() + ""
//                        destinationFileName += ".jpeg"
//                        val file = File(this.cacheDir, destinationFileName)
//                        if (!file.exists()) {
//                            try {
//                                file.createNewFile()
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                        }
//                        UCrop.of(
//                            Uri.fromFile(File(fPAth)), Uri.fromFile(
//                                File(
//                                    this.cacheDir, destinationFileName
//                                )
//                            )
//                        ).withAspectRatio(1f, 1f)
//                            .start(this)

                        fPAth?.let {
                            mUplodaViewModel.hitUploadFile(fPAth!!)
                        }
                    }
                }
            }
            PermissionConstants.REQ_VIDEO -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                        fPAth?.let {
                            mUplodaViewModel.hitUploadFile(fPAth!!)
                        }
                    }
                }
            }
            PermissionConstants.CAPTURE_VIDEO -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }

                        if (!isFileLessThan30MB(File(fPAth))) {
                            fPAth?.let {
                                val ImageName =
                                    "Video_" + Calendar.getInstance().getTimeInMillis()
                                        .toString() + ".mp4"
                                val file = File(
                                    Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOCUMENTS
                                    ), ImageName
                                )
                                if (!file.exists()) {
                                    file.createNewFile()
                                }
                                showProgressDialog(LoaderType.NORMAL, "")
                                LightVideoCompression(this, it,
                                    file, object :
                                        LightVideoCompression.CompressListener {
                                        override fun onCompressedVideo(destPath: String) {
                                            hideProgressDialog(LoaderType.NORMAL, "")
                                            fPAth = destPath
                                            fPAth?.let {
                                                mUplodaViewModel.hitUploadFile(fPAth!!)
                                            }
                                        }

                                        override fun onCompressFail() {
                                            hideProgressDialog(LoaderType.NORMAL, "")
                                            showToastShort(getString(R.string.error_while_video_compressing))
                                        }
                                    })
                            }
                        } else {
                            showToastShort(getString(R.string.large_file_size))
                        }
                    }
                }
            }
            UCrop.REQUEST_CROP -> {
                if (data != null) {
                    val resultUri = UCrop.getOutput(data!!)
                    if (resultUri != null) fPAth = resultUri.path
                    else
                        return
                    fPAth?.let {
                        mUplodaViewModel.hitUploadFile(fPAth!!)
                    }

                }
            }
        }

    }

    private fun isFileLessThan30MB(file: File): Boolean {
        val maxFileSize = 30 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }

    private fun getImageUri(photo: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            getContentResolver(),
            photo,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as ApplicationClass).setCurrentChatJobID("")
    }

    override fun onPause() {
        super.onPause()
        messageListener?.let { msgPath?.removeEventListener(it) }
    }

    override fun onResume() {
        super.onResume()
        if (msgPath != null && messageListener != null) {
            msgPath!!.addChildEventListener(messageListener!!)
        }
    }
}