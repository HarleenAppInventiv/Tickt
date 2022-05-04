package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.tradie.BuilderModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ViewPagerAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityHomeBinding
import com.example.ticktapp.firebase.*
import com.example.ticktapp.mvvm.view.WelcomeActivity
import com.example.ticktapp.mvvm.view.builder.VounchListActivity
import com.example.ticktapp.mvvm.view.builder.WebViewActivity
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.mvvm.viewmodel.PhoneNumberViewModel
import com.example.ticktapp.mvvm.viewmodel.TradieProfileViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.identity.Registration
import org.json.JSONObject
import java.io.Serializable

public class HomeActivity : BaseActivity() {
    private var countPath: DatabaseReference? = null
    private var pos: Int = 0
    private lateinit var mBinding: ActivityHomeBinding
    private lateinit var adapter: ViewPagerAdapter
    private var tutorialPos = -1
    private val mJobViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val mProfileViewModel by lazy { ViewModelProvider(this).get(TradieProfileViewModel::class.java) }
    private val viewModel by lazy { ViewModelProvider(this).get(PhoneNumberViewModel::class.java) }
    private var notificationType = -1
    private var tradieData: BuilderModel? = null
    private var countListener: ValueEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setListener()
        getIntentData()
        appOpenMoEngage()
        appOpenMixPanel()
        setUpAdapter()
        setupInterCom()
        setUpAppGuide()
        updateDeviceToken()
//        unReadMessagesCount()
        managePushNotification()
    }

    fun getIntentData() {
        pos = intent.getIntExtra("pos", 0)
    }

    private fun managePushNotification() {
        try {
            if (intent.hasExtra("notificationType") || intent.hasExtra("notification_type")) {
                notificationType = Integer.parseInt(
                    intent.getStringExtra("notificationType")
                        ?: intent.getStringExtra("notification_type")
                )
                if (notificationType == 13) {
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
                } else if (notificationType == 3 ||
                    notificationType == 9 ||
                    notificationType == 8 ||
                    notificationType == 14 ||
                    notificationType == 20 ||
                    notificationType == 21
                ) {
                    mJobViewModel.getTradieJobsDetails(
                        true,
                        intent.getStringExtra("body").toString()
                    )
                } else if (notificationType == 18) {
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
                } else if (intent.getStringExtra("notificationType").equals("12")) {
                    val intent = Intent(this, NewJobsActivity::class.java)
                    startActivity(intent)
                } else if (intent.getStringExtra("notificationType").equals("31") ||
                    intent.getStringExtra("notificationType").equals("18") ||
                    intent.getStringExtra("notificationType").equals("4")
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
                } else if (intent.getStringExtra("notificationType").equals("1")) {

                } else if (intent.getStringExtra("notificationType").equals("16") ||
                    intent.getStringExtra("notificationType").equals("10") ||
                    intent.getStringExtra("notificationType").equals("17")
                ) {
                    mProfileViewModel.getTradiePublicProfile(true)
                } else if (intent.getStringExtra("notificationType").equals("25")) {

                } else if (intent.getStringExtra("notificationType").equals("50")) {
                    if (intent.hasExtra("jobId")) {
                        intent.getStringExtra("jobId")?.let {
                            getLastMessages(
                                it,
                                intent.getStringExtra("receiverId")!!
                            )
                        }
                    } else {
                        val roomId = intent.getStringExtra("roomId")
                        val ids = roomId?.split("_")
                        ids?.get(0)?.let { getLastMessages(it, ids.get(1)) }
                    }
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
            jobId + "_" + loginUserId + "_" + tradieId

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
                                    ChatTradieActivity::class.java
                                ).putExtra("data", chatModel)
                            )
                        } catch (e: Exception) {
                        }
                    }
                })
        }


    }

    override fun onException(exception: ApiError, apiCode: Int) {
        showToastShort(exception.message)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.JOB_DETAILS -> {
                mJobViewModel.mJsonResponseModel.let {
                    if (notificationType == 3 || notificationType == 14) {
                        Log.i("onResponseSuccess: ", it.details!!)
                        Log.i("onResponseSuccess: ", (it != null).toString())
                        Log.i("onResponseSuccess: ", (it is JobRecModel).toString())
                        startActivity(
                            Intent(this, TradieJobDetailActivity::class.java).putExtra(
                                "data",
                                it as Serializable
                            )
                                .putExtra("isBuilder", false)
                        )
                    } else if (notificationType == 8) {
                        startActivity(
                            Intent(this, RateBuilderActivityStar::class.java)
                                .putExtra("data", it).putExtra("showMilestoneProgress", true)
                        )
                    } else if (notificationType == 9) {
                        startActivity(
                            Intent(this, TradieQuestionListActivity::class.java).putExtra(
                                "data",
                                it
                            )
                                .putExtra("isBuilder", false).putExtra("isQuestionList", true)
                        )
                    }
                }
            }
            ApiCodes.TRADIE_PROFILE_PUBLIC -> {
                mProfileViewModel.builderModel.let {
                    tradieData = it
                    if (notificationType == 17) {
                        startActivity(
                            Intent(this, VounchListActivity::class.java).putExtra(
                                "data",
                                tradieData!!.vouchesData
                            ).putExtra("title", tradieData?.voucherCount.toString() + " voucher(s)")
                                .putExtra("id", tradieData?.builderId).putExtra("isMyVouch", true)
                        )
                    } else {
                        startActivity(
                            Intent(this, ReviewActivity::class.java).putExtra(
                                "data",
                                tradieData!!.reviewData
                            ).putExtra(
                                "title",
                                (tradieData!!.reviewsCount!!.toInt()).toString() + " review(s)"
                            ).putExtra("count", tradieData!!.reviewsCount!!.toInt())
                        )
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    private fun updateDeviceToken() {
        setBaseViewModel(viewModel)
        setBaseViewModel(mJobViewModel)
        setBaseViewModel(mProfileViewModel)
        viewModel.getResponseObserver().observe(this, this)
        mJobViewModel.getResponseObserver().observe(this, this)
        mProfileViewModel.getResponseObserver().observe(this, this)
        if (PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN)?.length == 0) {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        PreferenceManager.putString(PreferenceManager.DEVICE_TOKEN, it.result)
                    }
                }.addOnFailureListener {
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        viewModel.addDeviceToken()
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

        mBinding.tvTradieTutorialSkip.setOnClickListener {
            tutorialPos = -1
            setTutorial(tutorialPos)
        }
    }

    private fun setUpAdapter() {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment.getInstance(), "")
        adapter.addFragment(JobDashboardFragment.getInstance(), "")
        adapter.addFragment(TradieMessageFragment.getInstance(), "")
        adapter.addFragment(TradieProfileFragment.getInstance(), "")
        mBinding.vpHomeActivity.adapter = adapter
        mBinding.vpHomeActivity.offscreenPageLimit = 4
        mBinding.vpHomeActivity.post {
            setHomeMenu(pos)
        }

        mBinding.vpHomeActivity.post {
            mBinding.vpHomeActivity.currentItem = pos
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

            mBinding.homeTvJobs.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.jobs_unselected,
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
            mBinding.homeTvJobs.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.job_selected,
                0,
                0
            )
            mBinding.homeTvChat.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.chat_unselected,
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
            mBinding.homeTvChat.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.chat_selected,
                0,
                0
            )
            mBinding.viewHomeLine.visibility = View.INVISIBLE
            mBinding.viewProfileLine.visibility = View.INVISIBLE
            mBinding.viewJobLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.VISIBLE
        }

        if (pos == 3) {

            mBinding.homeTvJobs.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.jobs_unselected,
                0,
                0
            )
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
            mBinding.viewHomeLine.visibility = View.INVISIBLE
            mBinding.viewProfileLine.visibility = View.VISIBLE
            mBinding.viewJobLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.INVISIBLE
        }
        mBinding.vpHomeActivity.currentItem = pos
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
        if (countListener != null && countPath != null) {
            countListener?.let { countPath?.addValueEventListener(it) }

        } else {
            getMessageCount()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Intercom.client().logout()
    }

    fun setUpTutorial() {
        mBinding.vpHomeActivity.setCurrentItem(0)
        tutorialPos = -1
        mBinding.viewTutorialNext.performClick()
    }

    fun setUpAppGuide() {
        if (PreferenceManager.getString(PreferenceManager.IS_APP_GUIDE).equals("")) {
            Handler().postDelayed({
                PreferenceManager.putString(PreferenceManager.IS_APP_GUIDE, "1")
                setTutorial(0)
            }, 3000)
        }
    }

    private fun setTutorial(pos: Int) {
        mBinding.tvTutorialHomeText.text = getString(
            R.string.home_tradie_tutorial_text,
            PreferenceManager.getString(PreferenceManager.NAME)
        )
        if (pos >= 0) {
            mBinding.llHomeTutorial.visibility = View.VISIBLE
            mBinding.llTutorialHome.visibility = if (pos == 0) View.VISIBLE else View.GONE
            mBinding.llTutorialJobs.visibility = if (pos == 1) View.VISIBLE else View.GONE
            mBinding.llTutorialChat.visibility = if (pos == 2) View.VISIBLE else View.GONE
            mBinding.llTutorialProfile.visibility = if (pos == 3) View.VISIBLE else View.GONE
            mBinding.llTutorialNotification.visibility = if (pos == 4) View.VISIBLE else View.GONE

            mBinding.ivTutorialHome.visibility = if (pos == 0) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialJob.visibility = if (pos == 1) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialChat.visibility = if (pos == 2) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialProfile.visibility = if (pos == 3) View.VISIBLE else View.INVISIBLE
            mBinding.ivTutorialNotification.visibility = if (pos == 4) View.VISIBLE else View.GONE
            mBinding.viewHomeLine.visibility = View.VISIBLE
            mBinding.viewProfileLine.visibility = View.INVISIBLE
            mBinding.viewChatLine.visibility = View.INVISIBLE
            mBinding.viewJobLine.visibility = View.INVISIBLE
        } else {
            mBinding.llHomeTutorial.visibility = View.GONE
        }
    }

    fun unReadMessagesCount(unReadCount: Int = 0) {
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

    private fun appOpenMixPanel(){
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

        FirebaseDatabaseQueries.instance?.getUnreadMessagesCount(
            mCurrentUserId,
            object : FirebaseUnreadCountListener {
                override fun getCount(
                    count: Int,
                    listener: FirebaseEventListeners?,
                    path: DatabaseReference?
                ) {
                    unReadMessagesCount(count)
                    countListener = listener
                    countPath = path
                }

            }
        )

    }

    private fun updateFirebaseUserData(mCurrentUserId: String?, mCurrentUserEmail: String?) {
        if (mCurrentUserEmail != null && mCurrentUserEmail?.length!! > 0) {
            FirebaseDatabaseQueries.instance?.signInFirebaseDatabase(
                mCurrentUserEmail,
                FireStore.FireStoreConstPassword.DEFAULT_PASS,
                object : FirebaseAuthListener {
                    override fun onAuthSuccess(task: Task<AuthResult?>?, user: FirebaseUser?) {
                        getChatsCount(mCurrentUserId)
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
                }

                override fun onAuthError(task: Task<AuthResult?>?) {

                }
            })
    }

    override fun onPause() {
        super.onPause()
        countListener?.let { countPath?.removeEventListener(it) }
    }


}