package com.example.ticktapp.mvvm.view.builder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ViewPagerAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityHomeBuilderBinding
import com.example.ticktapp.firebase.FirebaseAuthListener
import com.example.ticktapp.firebase.FirebaseDatabaseQueries
import com.example.ticktapp.firebase.FirebaseMessageListener
import com.example.ticktapp.firebase.FirebaseUserListener
import com.example.ticktapp.mvvm.view.WelcomeActivity
import com.example.ticktapp.mvvm.view.builder.postjob.CheckAndApproveMilestoneActivity
import com.example.ticktapp.mvvm.view.builder.postjob.PostNewJobActivity
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.mvvm.view.tradie.ProfileOfBuilderFragment
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.mvvm.viewmodel.PhoneNumberViewModel
import com.example.ticktapp.mvvm.viewmodel.ProfileViewModel
import com.example.ticktapp.mvvm.viewmodel.ReviewListViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.identity.Registration
import org.json.JSONObject

public class HomeBuilderActivity : BaseActivity() {
    private var pos: Int = 0
    private lateinit var mBinding: ActivityHomeBuilderBinding
    private lateinit var adapter: ViewPagerAdapter
    private var tutorialPos = -1
    private val viewModel by lazy { ViewModelProvider(this).get(PhoneNumberViewModel::class.java) }
    private val mJobViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val mViewModelList by lazy { ViewModelProvider(this).get(ReviewListViewModel::class.java) }
    private val mMyProfileViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_builder)
        ApplicationClass.isSaveRefresh = false
        (application as ApplicationClass).setCurrentChatJobID("")
        getIntentData()
        appOpenMoEngage()
        appOpenMixPanel()
        setListener()
        setUpAdapter()
        setupInterCom()
        setUpAppGuide()
        signInFirebase()
        updateDeviceToken()
        managePushNotification()
    }

    private fun updateDeviceToken() {
        setBaseViewModel(viewModel)
        setBaseViewModel(mViewModelList)
        setBaseViewModel(mJobViewModel)
        setBaseViewModel(mMyProfileViewModel)
        viewModel.getResponseObserver().observe(this, this)
        mViewModelList.getResponseObserver().observe(this, this)
        mJobViewModel.getResponseObserver().observe(this, this)
        mMyProfileViewModel.getResponseObserver().observe(this, this)
        if (PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN)?.length == 0) {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {

                    if (it.isSuccessful) {
                        Log.d("DEVICE_TOKEN", "${it.result}")
                        PreferenceManager.putString(PreferenceManager.DEVICE_TOKEN, it.result)
                    }
                }.addOnFailureListener {
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            val token = PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN)
            Log.d("DEVICE_TOKEN", "${token}")

        }
        viewModel.addDeviceToken()
    }

    fun signInFirebase() {
        if (PreferenceManager.getString(PreferenceManager.EMAIL) != null && PreferenceManager.getString(
                PreferenceManager.EMAIL
            )?.length!! > 0
        ) {
            FirebaseDatabaseQueries.instance?.signInFirebaseDatabase(
                PreferenceManager.getString(PreferenceManager.EMAIL),
                FireStore.FireStoreConstPassword.DEFAULT_PASS,
                object : FirebaseAuthListener {
                    override fun onAuthSuccess(task: Task<AuthResult?>?, user: FirebaseUser?) {
                    }

                    override fun onAuthError(task: Task<AuthResult?>?) {

                    }
                })
        }
    }

    fun setUpAppGuide() {
        if (PreferenceManager.getString(PreferenceManager.IS_APP_GUIDE).equals("")) {
            Handler().postDelayed({
                PreferenceManager.putString(PreferenceManager.IS_APP_GUIDE, "1")
                setTutorial(0)
            }, 3000)
        }
    }

    fun setupInterCom() {
        Intercom.client().registerIdentifiedUser(
            Registration.create()
                .withUserId(PreferenceManager.getString(PreferenceManager.USER_ID).toString())
        )
    }

    override fun onResume() {
        super.onResume()
        Intercom.client().handlePushMessage()
        getMessageCount()
    }

    fun getIntentData() {
        pos = intent.getIntExtra("pos", 0)
        val isTransacation = intent.getBooleanExtra("isTransacation", false)
        if (isTransacation) {
            startActivity(Intent(this, BuilderMyRevenueActivity::class.java))
        }
    }

    override fun refreshData(newIntent: Intent) {
        runOnUiThread {
            try {
                if (newIntent.hasExtra("notificationType")) {
                    if (newIntent.getStringExtra("notificationType")
                            .equals("3") || newIntent.getStringExtra("notificationType")
                            .equals("9")
                    ) {
                        (adapter.getItem(1) as JobDashboardBuilderFragment).refreshAll()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun managePushNotification() {
        try {
            if (intent.hasExtra("notificationType")) {
                if (intent.getStringExtra("notificationType").equals("13")) {
                    val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
                    val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                    PreferenceManager.clearAllPrefs()
                    PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
                    PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
                    startActivity(
                        Intent(this, WelcomeActivity::class.java).putExtra(
                            "isBlock",
                            true
                        )
                    )
                    finish()
                } else if (intent.getStringExtra("notificationType")
                        .equals("3")
                ) {
                    mJobViewModel.jobDetailsFromBuilder(
                        true,
                        intent.getStringExtra("jobId"),
                        "",
                        ""
                    )
                } else if (intent.getStringExtra("notificationType").equals("12")
                ) {
                    showAppPopupDialog(
                        intent.getStringExtra("body").toString(),
                        getString(R.string.ok),
                        "",
                        intent.getStringExtra("title").toString(),
                        {},
                        {},
                        false
                    )
                } else if (intent.getStringExtra("notificationType").equals("18")) {
                    showAppPopupDialog(
                        intent.getStringExtra("body").toString(),
                        getString(R.string.ok),
                        "",
                        intent.getStringExtra("title").toString(),
                        {},
                        {},
                        false
                    )
                } else if (intent.getStringExtra("notificationType").equals("11")) {
                    startActivity(
                        Intent(this, WebViewActivity::class.java)
                            .putExtra(IntentConstants.FROM, Constants.TERMS)
                    )
                } else if (intent.getStringExtra("notificationType").equals("19")) {
                    startActivity(
                        Intent(this, WebViewActivity::class.java)
                            .putExtra(IntentConstants.FROM, Constants.PRIVACY)
                    )
                } else if (intent.getStringExtra("notificationType").equals("31")) {
                    showAppPopupDialog(
                        intent.getStringExtra("body").toString(),
                        getString(R.string.ok),
                        "",
                        intent.getStringExtra("title").toString(),
                        {},
                        {},
                        false
                    )
                } else if (intent.getStringExtra("notificationType").equals("1")) {
//                    startActivity(
//                        Intent(this, TradieProfileActivity::class.java)
//                            .putExtra("isBuilder", true)
//                            .putExtra("otherUserID", intent.getStringExtra("senderId"))
//                            .putExtra("jobID", intent.getStringExtra("jobId"))
//                    )
                    startActivity(Intent(this, NewApplicantActivity::class.java))
                } else if (intent.getStringExtra("notificationType").equals("9")) {
                    mJobViewModel.jobDetailsFromBuilder(
                        true,
                        intent.getStringExtra("jobId"),
                        "",
                        ""
                    )
                } else if (intent.getStringExtra("notificationType").equals("16")) {
                    mViewModelList.getBuilderReviewList(
                        intent.getStringExtra("receiverId").toString(),
                        1
                    )
                } else if (intent.getStringExtra("notificationType").equals("14")) {
                    mJobViewModel.jobDetailsFromBuilder(
                        true,
                        intent.getStringExtra("jobId"),
                        "",
                        ""
                    )
                } else if (intent.getStringExtra("notificationType").equals("7")) {
                    mJobViewModel.jobDetailsFromBuilder(
                        true,
                        intent.getStringExtra("jobId"),
                        "",
                        ""
                    )
                } else if (intent.getStringExtra("notificationType").equals("20")) {
                    mJobViewModel.jobDetailsFromBuilder(
                        true,
                        intent.getStringExtra("jobId"),
                        "",
                        ""
                    )
                } else if (intent.getStringExtra("notificationType").equals("21")) {
                    mJobViewModel.jobDetailsFromBuilder(
                        true,
                        intent.getStringExtra("jobId"),
                        "",
                        ""
                    )
                } else if (intent.getStringExtra("notificationType").equals("25")) {
                    if (intent.hasExtra("jobId")) {
                        intent.getStringExtra("jobId")?.let {
                            getLastMessages(
                                it,
                                intent.getStringExtra("senderId")!!
                            )
                        }
                    } else {
                        val roomId = intent.getStringExtra("roomId")
                        val ids = roomId?.split("_")
                        ids?.get(0)?.let { getLastMessages(it, ids.get(1)) }
                    }
                } else if (intent.getStringExtra("notificationType").equals("50")) {
                    if (intent.hasExtra("jobId")) {
                        intent.getStringExtra("jobId")?.let {
                            getLastMessages(
                                it,
                                intent.getStringExtra("senderId")!!
                            )
                        }
                    } else {
                        val roomId = intent.getStringExtra("roomId")
                        val ids = roomId?.split("_")
                        ids?.get(0)?.let { getLastMessages(it, ids.get(1)) }
                    }
                } else if (intent.getStringExtra("notificationType").equals("10")) {
                    mViewModelList.getBuilderReviewList(
                        intent.getStringExtra("receiverId").toString(),
                        1
                    )
                }

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getLastMessages(jobId: String, tradieId: String) {
        val inBoxMessage = ChatMessageBean()
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        inBoxMessage.messageRoomId =
            jobId + "_" + tradieId + "_" + loginUserId

        FirebaseDatabaseQueries.instance?.getLastMessageInfo(
            null,
            inBoxMessage,
            object : FirebaseMessageListener {
                override fun getMessages(message: ChatMessageBean?) {
                    getUsersData(message, jobId, tradieId)
                }

                override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {}
                override fun newMessagesListing() {

                }

                override fun noData() {
                }
            }
        )
    }

    private fun getUsersData(chatModels: ChatMessageBean?, jobId: String, tradieId: String) {
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        var chatModel = ChatMessageBean();
        if (chatModels != null) {
            chatModel = chatModels
        }
        chatModel.jobId = jobId
        if (intent.hasExtra("jobName")) {
            chatModel.jobName = intent.getStringExtra("jobName")
        } else {
            chatModel.jobName = ""
        }
        if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
            chatModel.receiverId = tradieId
        if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
            chatModel.senderId = loginUserId
        tradieId.let {
            FirebaseDatabaseQueries.instance?.getUser(
                it,
                object : FirebaseUserListener {
                    override fun getUser(user: UserBean?) {
                        try {
                            user?.let {

                                if (!it.image.isNullOrEmpty()) {
                                    chatModel?.senderImage = it.image!!
                                }

                                if (!it.name.isNullOrEmpty()) {
                                    chatModel?.senderName = it.name
                                }
                                if (it.userType != null) {
                                    chatModel?.senderType = it.userType.toString()
                                }
                                if (!it.image.isNullOrEmpty()) {
                                    chatModel?.senderImage = it.image
                                }
                            }
                            startActivity(
                                Intent(
                                    mBinding.homeTvChat.context,
                                    ChatBuilderActivity::class.java
                                ).putExtra("data", chatModel)
                            )
                        } catch (e: Exception) {
                        }

                    }
                })
        }

    }


    override fun onException(exception: ApiError, apiCode: Int) {
        super.onException(exception, apiCode)
        showToastShort(exception.message)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        super.onResponseSuccess(statusCode, apiCode, msg)
        when (apiCode) {
            ApiCodes.JOB_DETAILS -> {
                if (intent.getStringExtra("notificationType")
                        .equals("3")
                ) {
                    mJobViewModel.mJsonResponseModel.let {
                        startActivity(
                            Intent(this, JobDetailsActivity::class.java).putExtra("data", it)
                                .putExtra("isBuilder", true)
                        )
                    }
                } else if (intent.getStringExtra("notificationType")
                        .equals("9")
                ) {
                    mJobViewModel.mJsonResponseModel.let {
                        startActivity(
                            Intent(this, JobDetailsActivity::class.java).putExtra("data", it)
                                .putExtra("isBuilder", true).putExtra("isQuestionList", true)
                        )
                    }
                } else if (intent.getStringExtra("notificationType")
                        .equals("14")
                ) {
                    mJobViewModel.mJsonResponseModel.let {
                        startActivity(
                            Intent(this, CheckAndApproveMilestoneActivity::class.java)
                                .putExtra("data", it).putExtra("isBuilder", true)
                        )
                    }
                } else if (intent.getStringExtra("notificationType")
                        .equals("7")
                ) {
                    mJobViewModel.mJsonResponseModel.let {
                        startActivity(
                            Intent(this, TradieReviewJobActivity::class.java)
                                .putExtra("data", it).putExtra("isBuilder", true)
                                .putExtra("senderId", intent.getStringExtra("senderId"))
                        )
                    }
                } else if (intent.getStringExtra("notificationType")
                        .equals("20")
                ) {
                    mJobViewModel.mJsonResponseModel.let {
                        startActivity(
                            Intent(
                                this,
                                QuoteListActivity::class.java
                            ).putExtra("data", it).putExtra("isAction", true)
                                .putExtra("fromHome", true)
                        )
                    }
                } else if (intent.getStringExtra("notificationType")
                        .equals("21")
                ) {
                    mJobViewModel.mJsonResponseModel.let {
                        startActivity(
                            Intent(
                                this,
                                QuoteListActivity::class.java
                            ).putExtra("data", it).putExtra("isAction", false)
                                .putExtra("fromHome", true)
                        )
                    }
                }
            }

            ApiCodes.REVIEW_LIST -> {
                if (intent.getStringExtra("notificationType")
                        .equals("16")
                ) {
                    mViewModelList.reviewDataList.let {
                        startActivity(
                            Intent(this, ReviewListBuilderActivity::class.java).putExtra(
                                "data",
                                it
                            ).putExtra("title", (it.size).toString() + " review(s)")
                        )
                    }
                } else if (intent.getStringExtra("notificationType").equals("10")) {
                    mViewModelList.reviewDataList.let {
                        startActivity(
                            Intent(this, ReviewListBuilderActivity::class.java).putExtra(
                                "data",
                                it
                            ).putExtra("title", (it.size).toString() + " review(s)")
                        )
                    }
                }
            }
        }
    }

    private fun setTutorial(pos: Int) {
        mBinding.tvTutorialHomeText.text = getString(
            R.string.home_tutorial_text,
            PreferenceManager.getString(PreferenceManager.NAME)
        )
        if (pos >= 0) {
            mBinding.llHomeTutorial.visibility = View.VISIBLE
            mBinding.llTutorialHome.visibility = if (pos == 0) View.VISIBLE else View.GONE
            mBinding.llTutorialJobs.visibility = if (pos == 1) View.VISIBLE else View.GONE
            mBinding.llTutorialAddPost.visibility = if (pos == 2) View.VISIBLE else View.GONE
            mBinding.llTutorialChat.visibility = if (pos == 3) View.VISIBLE else View.GONE
            mBinding.llTutorialProfile.visibility = if (pos == 4) View.VISIBLE else View.GONE
            mBinding.llTutorialNotification.visibility = if (pos == 5) View.VISIBLE else View.GONE

            mBinding.ivTutorialHome.visibility = if (pos == 0) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialJob.visibility = if (pos == 1) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialAddJob.visibility = if (pos == 2) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialChat.visibility = if (pos == 3) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialProfile.visibility = if (pos == 4) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialNotification.visibility = if (pos == 5) View.VISIBLE else View.GONE
            mBinding.viewHomeLine.visibility = View.VISIBLE
            mBinding.viewProfileLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.INVISIBLE
            mBinding.viewJobLine.visibility = View.INVISIBLE
        } else {
            mBinding.llHomeTutorial.visibility = View.GONE
        }
    }

    private fun setListener() {
        mBinding.homeTvHome.setOnClickListener {
            setHomeMenu(0)
        }
        mBinding.homeTvProfile.setOnClickListener {
            setHomeMenu(3)
        }
        mBinding.homeTvJobs.setOnClickListener {
            setHomeMenu(1)
        }
        mBinding.homeTvChat.setOnClickListener {
            setHomeMenu(2)
        }
        mBinding.homeBuilderTvPostJob.setOnClickListener {
            startActivity(Intent(this, PostNewJobActivity::class.java))
        }

        mBinding.viewTutorialNext.setOnClickListener {
            if (tutorialPos < 5) {
                tutorialPos++
                setTutorial(tutorialPos)
            } else {
                tutorialPos = -1
                setTutorial(tutorialPos)
            }
        }

        mBinding.viewTutorialPrev.setOnClickListener {
            if (tutorialPos > 0) {
                tutorialPos--
                setTutorial(tutorialPos)
            }
        }

        mBinding.tvTutorialSkip.setOnClickListener {
            tutorialPos = -1
            setTutorial(tutorialPos)
        }
    }

    fun setUpTutorial() {
        mBinding.vpHomeActivity.setCurrentItem(0)
        tutorialPos = -1
        mBinding.viewTutorialNext.performClick()
    }

    private fun setUpAdapter() {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeBuilderFragment.getInstance(), "")
        adapter.addFragment(JobDashboardBuilderFragment.getInstance(), "")
        adapter.addFragment(BuilderMessageFragment.getInstance(), "")
        adapter.addFragment(ProfileOfBuilderFragment.getInstance(), "")
        mBinding.vpHomeActivity.adapter = adapter
        mBinding.vpHomeActivity.offscreenPageLimit = 4
        mBinding.vpHomeActivity.post {
            mBinding.vpHomeActivity.currentItem = pos
        }
        if (pos == 1) {
            mBinding.viewHomeLine.visibility = View.INVISIBLE
            mBinding.viewProfileLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.INVISIBLE
            mBinding.viewJobLine.visibility = View.VISIBLE
        }
    }

    private fun setHomeMenu(pos: Int) {
        if (pos == 0) {
            mBinding.homeTvHome.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.home_selected,
                0,
                0
            )
            mBinding.homeTvProfile.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.profile_unselected,
                0,
                0
            )
            mBinding.homeTvChat.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.chat_unselected,
                0,
                0
            )
            mBinding.homeTvJobs.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.jobs_unselected,
                0,
                0
            )
            mBinding.viewHomeLine.visibility = View.VISIBLE
            mBinding.viewProfileLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.INVISIBLE
            mBinding.viewJobLine.visibility = View.INVISIBLE
        }
        if (pos == 1) {
            mBinding.homeTvHome.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.home_unselected,
                0,
                0
            )
            mBinding.homeTvProfile.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.profile_unselected,
                0,
                0
            )
            mBinding.homeTvChat.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.chat_unselected,
                0,
                0
            )
            mBinding.homeTvJobs.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.job_selected,
                0,
                0
            )
            mBinding.viewHomeLine.visibility = View.INVISIBLE
            mBinding.viewProfileLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.INVISIBLE
            mBinding.viewJobLine.visibility = View.VISIBLE
        }
        if (pos == 2) {
            mBinding.homeTvHome.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.home_unselected,
                0,
                0
            )
            mBinding.homeTvChat.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.chat_selected,
                0,
                0
            )
            mBinding.homeTvProfile.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.profile_unselected,
                0,
                0
            )
            mBinding.homeTvJobs.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.jobs_unselected,
                0,
                0
            )
            mBinding.viewHomeLine.visibility = View.INVISIBLE
            mBinding.viewProfileLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.VISIBLE
            mBinding.viewJobLine.visibility = View.INVISIBLE
        }
        if (pos == 3) {
            mBinding.homeTvHome.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.home_unselected,
                0,
                0
            )
            mBinding.homeTvProfile.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.profile_selected,
                0,
                0
            )
            mBinding.homeTvChat.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.chat_unselected,
                0,
                0
            )
            mBinding.homeTvJobs.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.jobs_unselected,
                0,
                0
            )
            mBinding.viewHomeLine.visibility = View.INVISIBLE
            mBinding.viewProfileLine.visibility = View.VISIBLE
            mBinding.viewChatLine.visibility = View.INVISIBLE
            mBinding.viewJobLine.visibility = View.INVISIBLE
        }
        mBinding.vpHomeActivity.currentItem = pos
    }

    override fun onDestroy() {
        super.onDestroy()
        Intercom.client().logout()
    }

    public fun unReadMessagesCount(unReadCount: Int = 0) {
        if (unReadCount > 0) {
            mBinding.tvUnreadMessageCountBottomTab.visibility = View.VISIBLE
            mBinding.tvUnreadMessageCountBottomTab.text = unReadCount.toString()
        } else {
            mBinding.tvUnreadMessageCountBottomTab.visibility = View.GONE
        }
    }

    private fun appOpenMoEngage() {
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.APP_OPEN, true)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_APP_OPEN,
            signUpProperty
        )
    }

    private fun appOpenMixPanel() {
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.APP_OPEN, true)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_APP_OPEN, props)
    }

    private fun getMessageCount() {
        val mCurrentUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        val mCurrentUserEmail = PreferenceManager.getString(PreferenceManager.EMAIL)
        updateFirebaseUserData(mCurrentUserId, mCurrentUserEmail)


    }

    private fun getChatsCount(mCurrentUserId: String?) {
        FirebaseDatabaseQueries.instance?.getInboxMessagesList(
            userId = mCurrentUserId,
            messageListener = object : FirebaseMessageListener {
                override fun getMessages(message: ChatMessageBean?) {

                }

                override fun getMessagesList(messageList: List<ChatMessageBean?>?) {
                    if (messageList?.isNotEmpty() == true) {
                        var unreadMessages = 0
                        for (i in messageList!!.indices) {
                            unreadMessages += Integer.parseInt(messageList[i]?.unreadMessages.toString())
                        }
                        unReadMessagesCount(unreadMessages)


                    }

                }

                override fun newMessagesListing() {

                }

                override fun noData() {
                }
            })
    }

    private fun checkForNewChatMessages(mCurrentUserId: String?) {
        FirebaseDatabaseQueries.instance?.checkInboxNewMessagesList(
            userId = mCurrentUserId,
            messageListener = object : FirebaseMessageListener {
                override fun getMessages(message: ChatMessageBean?) {

                }

                override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {

                }

                override fun newMessagesListing() {

                    getChatsCount(mCurrentUserId)

                }

                override fun noData() {
                }
            })
    }

    private fun updateFirebaseUserData(mCurrentUserId: String?, mCurrentUserEmail: String?) {
        if (mCurrentUserEmail != null && mCurrentUserEmail?.length!! > 0) {
            FirebaseDatabaseQueries.instance?.signInFirebaseDatabase(
                mCurrentUserEmail,
                FireStore.FireStoreConstPassword.DEFAULT_PASS,
                object : FirebaseAuthListener {
                    override fun onAuthSuccess(task: Task<AuthResult?>?, user: FirebaseUser?) {
                        getChatsCount(mCurrentUserId)
                        checkForNewChatMessages(mCurrentUserId)
                    }

                    override fun onAuthError(task: Task<AuthResult?>?) {
                        signUpUserInFirebase(mCurrentUserId, mCurrentUserEmail!!)
                    }
                })
        }
    }

    private fun signUpUserInFirebase(mCurrentUserId: String?, mCurrentUserEmail: String) {
        FirebaseDatabaseQueries.instance?.createUserInFirebaseDatabase(
            mCurrentUserEmail,
            FireStore.FireStoreConstPassword.DEFAULT_PASS,
            object : FirebaseAuthListener {
                override fun onAuthSuccess(task: Task<AuthResult?>?, user: FirebaseUser?) {
                    getChatsCount(mCurrentUserId)
                    checkForNewChatMessages(mCurrentUserId)
                }

                override fun onAuthError(task: Task<AuthResult?>?) {

                }
            })
    }
}