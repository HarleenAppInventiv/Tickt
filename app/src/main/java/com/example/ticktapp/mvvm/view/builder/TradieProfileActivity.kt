package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.TradeData
import com.app.core.model.tradesmodel.TradeHome
import com.app.core.model.tradie.BuilderModel
import com.app.core.model.tradie.PortFolio
import com.app.core.model.tradie.ReviewData
import com.app.core.model.tradie.VouchesData
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.*
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.dialog.SelectJobDialog
import com.example.ticktapp.firebase.FirebaseDatabaseQueries
import com.example.ticktapp.firebase.FirebaseMessageListener
import com.example.ticktapp.firebase.FirebaseUserListener
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.databinding.ActivityTradieProfileBinding
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
public class TradieProfileActivity : BaseActivity(),
    OnClickListener, SpecializationAdapter.SpecListAdapterListener,
    JobsSmallAdapter.JobAdapterListener, SpecializationMixSmallAdapter.OnSpecializationClicked {
    private var otherUserID: String = ""
    private lateinit var data: JobRecModel
    private var jobDashBoard: JobDashboardModel? = null
    private var tradeHome: TradeHome? = null
    private var isRefresh: Boolean? = false

    private var tradieData: BuilderModel? = null
    private var category: String? = null
    private lateinit var mBinding: ActivityTradieProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val tradeData: ArrayList<TradeData> = ArrayList()
    private val specializatinData: ArrayList<SpecialisationData> = ArrayList()
    private val reviewData: ArrayList<ReviewData> = ArrayList()
    private val voucherData: ArrayList<VouchesData> = ArrayList()

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var vounchesAdapter: VounchesAdapter

    private lateinit var imageAdapter: PortfolioAdapter
    private var photos: ArrayList<PortFolio>? = null
    private var photosHolderList: ArrayList<PortFolio>? = ArrayList()
    private var isChat: Boolean = false
    var isTradieSaved: Boolean = false

    companion object {
        public var specializatinDataEditable: ArrayList<SpecialisationData> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_profile)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setupView()
        setObservers()
    }

    private fun getIntentData() {
        isChat = intent.getBooleanExtra("isChat", false)
        if (intent.hasExtra("otherUserID")) {
            otherUserID = intent.getStringExtra("otherUserID").toString()
        }
        if (otherUserID.isEmpty()) {
            if (intent.getSerializableExtra("data") is JobDashboardModel) {
                mBinding.srLayout.visibility = View.GONE
                jobDashBoard = intent.getSerializableExtra("data") as JobDashboardModel
                mViewModel.getTradieProfile(
                    true,
                    jobDashBoard?.tradieId,
                    jobDashBoard?.jobId
                )
            } else if (intent.getSerializableExtra("data") is TradeHome) {
                mBinding.srLayout.visibility = View.GONE
                tradeHome = intent.getSerializableExtra("data") as TradeHome
                mViewModel.getTradieProfile(
                    true,
                    tradeHome?.tradieId
                )
            } else {
                mBinding.srLayout.visibility = View.GONE
                data = intent.getSerializableExtra("data") as JobRecModel
                mViewModel.getTradieProfile(
                    true,
                    data?.tradieId,
                    data?.jobId
                )
            }
        } else {
            mBinding.srLayout.visibility = View.GONE
            mViewModel.getTradieProfile(
                true,
                otherUserID,
                intent.getStringExtra("jobID")
            )
        }
        if (isChat) {
            mBinding.ivMsg.visibility = View.VISIBLE
        } else {
            mBinding.ivMsg.visibility = View.GONE
        }
    }

    private fun setData() {
        if (tradieData != null) {
            mBinding.tvName.text = tradieData?.builderName
            mBinding.tvDetails.text = tradieData?.position
            mBinding.tvBusinessName.text = tradieData?.businessName
            if (tradieData?.about?.length!! > 0) {
                mBinding.tvDescTitle.visibility = View.VISIBLE
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDesc.text = tradieData?.about
                mBinding.tvDescTitleMore.visibility = View.VISIBLE
            } else {
                mBinding.tvDescTitle.visibility = View.GONE
                mBinding.tvDesc.visibility = View.GONE
                mBinding.tvDescTitleMore.visibility = View.GONE
            }
            if (tradieData?.jobCompletedCount!! > 1) {
                mBinding.tvJobCompletedText.text = getString(R.string.jobs_completed)
            } else {
                mBinding.tvJobCompletedText.text = getString(R.string.job_completed)
            }
            mBinding.tvJobCompleted.text = tradieData?.jobCompletedCount!!.toInt().toString()
            mBinding.tvRateCount.text = tradieData?.ratings.toString()
            if (tradieData?.reviewsCount!! > 1) {
                mBinding.tvReviewCount.text =
                    tradieData?.reviewsCount?.toInt().toString() + " " + getString(R.string.reviews)
            } else {
                mBinding.tvReviewCount.text =
                    tradieData?.reviewsCount?.toInt().toString() + " " + getString(R.string.review)
            }
            Glide.with(mBinding.root.context).load(tradieData?.builderImage)
                .placeholder(R.drawable.placeholder_profile)
                .error(R.drawable.placeholder_profile)
                .into(mBinding.ivUserProfile)
            tradeData.clear()
            specializatinData.clear()
            tradieData?.areasOfSpecialization?.tradeData?.let { tradeData.addAll(it) }

            tradieData?.areasOfSpecialization?.specializationData?.let {
                specializatinDataEditable.clear()
                specializatinDataEditable.addAll(it)
            }
            if (tradeData.size > 0 || specializatinData.size > 0)
                mBinding.llJobSpec.visibility = View.VISIBLE


            setUpListWithShowMore(specializatinData, specializatinDataEditable)
            Log.i("listSize", specializatinData.size.toString())
            Log.i("listSize", "editable " + specializatinDataEditable.size.toString())
            Log.i("listSize", specializatinData[specializatinData.size - 1].specializationName!!)

            photosHolderList?.clear()
            photos?.clear()
            tradieData?.portfolio?.let {
                photosHolderList!!.addAll(it)
//                photos?.addAll(it)
            }
            if (photosHolderList!!.size > 3) {
                for (index in photosHolderList!!.indices) {
                    if (index < 3)
                        photos!!.add(photosHolderList!![index])
                    else
                        break
                }
                mBinding.tvPortfolioMore.visibility = View.VISIBLE
            } else {
                mBinding.tvPortfolioMore.visibility = View.GONE
                photos!!.addAll(photosHolderList!!)
            }
            if (photos?.size!! > 0)
                mBinding.llPortfolioData.visibility = View.VISIBLE

            mBinding.rvPhotos.adapter?.notifyDataSetChanged()
            reviewData.clear()
            tradieData?.reviewData?.let { reviewData?.addAll(it) }
            if (tradieData?.reviewData?.size!! > 0) {
                mBinding.llReviewData.visibility = View.VISIBLE
            } else {
                mBinding.llReviewData.visibility = View.GONE
            }
            mBinding.tvReview.text =
                getString(R.string.reviews_) + " (" + tradieData?.reviewsCount!!.toInt()
                    .toString() + ")"
            mBinding.rvReviews.adapter?.notifyDataSetChanged()
            voucherData.clear()
            tradieData?.vouchesData?.let { voucherData?.addAll(it) }
            mBinding.llVouchData.visibility = View.VISIBLE
            mBinding.tvVouches.text =
                getString(R.string.voucher) + " (" + tradieData?.voucherCount + ")"
            mBinding.rvVouches.adapter?.notifyDataSetChanged()
            if (tradieData?.isRequested == true) {
                mBinding.tvAccept.visibility = View.VISIBLE
                mBinding.tvDecline.visibility = View.VISIBLE
                mBinding.llJobApplied.visibility = View.GONE
            } else {
                mBinding.tvAccept.visibility = View.GONE
                mBinding.tvDecline.visibility = View.GONE
                mBinding.llJobApplied.visibility = View.VISIBLE
            }
            if (voucherData.size > 2) {
                mBinding.tvVounchListLeave.visibility = View.GONE
                mBinding.tvVounchList.visibility = View.VISIBLE
                mBinding.tvVounchList.text =
                    getString(R.string.show_all_vouchers, voucherData.size.toString())
            } else {
                mBinding.tvVounchListLeave.visibility = View.VISIBLE
                mBinding.tvVounchList.visibility = View.GONE
            }
            if (reviewData.size > 3) {
                mBinding.tvReviewList.visibility = View.VISIBLE
                mBinding.tvReviewList.text =
                    getString(R.string.show_all_reviews, reviewData.size.toString())
            } else {
                mBinding.tvReviewList.visibility = View.GONE
            }
            mBinding.llJobApplied.visibility = View.VISIBLE
            if (tradieData!!.isInvited && tradeHome == null) {
                mBinding.tvApplyTradie.setText(getString(R.string.cancel_invitation))
            } else if (tradeHome != null) {
                mBinding.tvApplyTradie.setText(getString(R.string.invite_for_job))
            } else {
                mBinding.llJobApplied.visibility = View.GONE
            }
            if (tradieData!!.isSaved) {
                mBinding.ivSaveTradie.setImageResource(R.drawable.ic_save_job)
            } else {
                mBinding.ivSaveTradie.setImageResource(R.drawable.ic_unsaved_job)
            }
        }

        var stringBuilder = StringBuilder()
        if (!tradieData?.tradeData.isNullOrEmpty()) {
            for (index in tradieData?.tradeData!!.indices) {
                if (index > 0 && index < tradieData?.tradeData!!.size - 1) {
                    stringBuilder.append("${tradieData?.tradeData!![index]}  ,")
                } else {
                    stringBuilder.append(tradieData?.tradeData!![index].tradeName)
                }
            }
        }
        category = ""
        if (!stringBuilder.isNullOrEmpty()) {
            category = stringBuilder.toString()
        } else {
            category = ""
        }

        var lat = PreferenceManager.getString(PreferenceManager.LAT)
        var lng = PreferenceManager.getString(PreferenceManager.LAN)
        var location = ""
        if (lat.isNullOrEmpty() || lng.isNullOrEmpty()) {
            location = "$lat , $lng"
        }
        viewedTradieProfileMoEngage(
            tradieData?.builderName,
            category!!,
            location
        )  //viewed tradie profile mo engage
        viewedTradieProfileMixPanel(
            tradieData?.builderName,
            category!!,
            location
        )  //viewed tradie profile mix panel
    }

    private fun viewedTradieProfileMoEngage(tradieName: String?, cate: String, location: String) {
        var isBuilder = PreferenceManager.getString(PreferenceManager.USER_TYPE) == "2"
        if (isBuilder) {
            val signUpProperty = Properties()
            signUpProperty.addAttribute(MoEngageConstants.NAME, tradieName)
            signUpProperty.addAttribute(MoEngageConstants.CATEGORY, cate)
            signUpProperty.addAttribute(MoEngageConstants.LOCATION, location)

            MoEngageUtils.sendEvent(
                this,
                MoEngageConstants.MOENGAGE_EVENT_VIEWED_TRADIE_PROFILE,
                signUpProperty
            )
        }
    }

    private fun viewedTradieProfileMixPanel(tradieName: String?, cate: String, location: String) {
        var isBuilder = PreferenceManager.getString(PreferenceManager.USER_TYPE) == "2"
        if (isBuilder) {
            val mixpanel = MixpanelAPI.getInstance(
                this,
                getString(R.string.mix_panel_token)
            )

            val props = JSONObject()
            props.put(MoEngageConstants.NAME, tradieName)
            props.put(MoEngageConstants.CATEGORY, cate)
            props.put(MoEngageConstants.LOCATION, location)

            mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_VIEWED_TRADIE_PROFILE, props)
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setupView() {
        val layountManager = GridLayoutManager(this, 3)
        photos = ArrayList()
        imageAdapter = photos?.let { PortfolioAdapter(it) }!!
        mBinding.rvPhotos.layoutManager = layountManager
        mBinding.rvPhotos.adapter = imageAdapter

        val reviewLayountManager = LinearLayoutManager(this)
        reviewAdapter = reviewData?.let { ReviewAdapter(it, false) }!!
        mBinding.rvReviews.layoutManager = reviewLayountManager
        mBinding.rvReviews.adapter = reviewAdapter

        val vounchLayountManager = LinearLayoutManager(this)
        vounchesAdapter = voucherData?.let { VounchesAdapter(it) }!!
        mBinding.rvVouches.layoutManager = vounchLayountManager
        mBinding.rvVouches.adapter = vounchesAdapter

        val mHomeAdapter = TradieMixSmallAdapter(tradeData)
        val jobLayoutManager = FlexboxLayoutManager(mBinding.root.context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }

        val mSpecAdapter =
            SpecializationMixSmallAdapter(specializatinData, isShowMore = true, this)
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)

        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobTypes, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvReviews, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvVouches, false)

    }


    private fun listener() {
        mBinding.tvDescTitleMore.setOnClickListener { manageMoreLessAbout() }
        mBinding.tvPortfolioMore.setOnClickListener { manageMoreLessPortfolio() }
        mBinding.tradieProfileIvBack.setOnClickListener { onBackPressed() }
        mBinding.srLayout.setOnRefreshListener {
            if (otherUserID.isEmpty()) {
                if (intent.getSerializableExtra("data") is JobDashboardModel) {
                    mViewModel.getTradieProfile(
                        false,
                        jobDashBoard?.tradieId,
                        jobDashBoard?.jobId
                    )
                } else if (intent.getSerializableExtra("data") is TradeHome) {
                    mViewModel.getTradieProfile(
                        true,
                        tradeHome?.tradieId
                    )
                } else {
                    mViewModel.getTradieProfile(
                        false,
                        data?.tradieId,
                        data?.jobId
                    )
                }
            } else {
                mViewModel.getTradieProfile(
                    false,
                    otherUserID,
                    intent.getStringExtra("jobID")
                )
            }
        }
        mBinding.tvAccept.setOnClickListener {
            if (otherUserID.isEmpty()) {
                if (intent.getSerializableExtra("data") is JobDashboardModel) {
                    mViewModel.acceptDeclineRequest(
                        jobDashBoard?.jobId,
                        jobDashBoard?.tradieId,
                        1,
                    )
                } else if (intent.getSerializableExtra("data") is TradeHome) {
                } else {
                    mViewModel.acceptDeclineRequest(
                        data?.jobId,
                        data?.tradieId,
                        1,
                    )
                }
            } else {
                mViewModel.acceptDeclineRequest(
                    intent.getStringExtra("jobID"),
                    otherUserID,
                    1,
                )
            }
        }
        mBinding.tvDecline.setOnClickListener {
            if (otherUserID.isEmpty()) {
                if (intent.getSerializableExtra("data") is JobDashboardModel) {
                    mViewModel.acceptDeclineRequest(
                        jobDashBoard?.jobId,
                        jobDashBoard?.tradieId,
                        2,
                    )
                } else if (intent.getSerializableExtra("data") is TradeHome) {
                } else {
                    mViewModel.acceptDeclineRequest(
                        data?.jobId,
                        data?.tradieId,
                        2,
                    )
                }
            } else {
                mViewModel.acceptDeclineRequest(
                    intent.getStringExtra("jobID"),
                    otherUserID,
                    2,
                )
            }
        }
        mBinding.ivSaveTradie.setOnClickListener {
            ApplicationClass.isSaveRefresh = true
            isRefresh = true
            if (tradieData != null) {
                if (tradieData?.isSaved == true) {
                    isTradieSaved = false
                    mBinding.ivSaveTradie.setImageResource(R.drawable.ic_unsaved_job)
                    mViewModel.saveTradie(tradieData!!.builderId, false)
                } else {
                    isTradieSaved = true
                    mBinding.ivSaveTradie.setImageResource(R.drawable.ic_save_job)
                    mViewModel.saveTradie(tradieData!!.builderId, true)
                }
            }
        }
        mBinding.tvApplyTradie.setOnClickListener {
            if (tradieData != null) {
                if (tradieData!!.isInvited && tradeHome == null) {
                    if (otherUserID.isEmpty()) {
                        if (intent.getSerializableExtra("data") is JobDashboardModel) {
                            mViewModel.cancelInvite(
                                tradieData!!.invitationId,
                                tradieData!!.builderId,
                                jobDashBoard?.jobId
                            )
                        } else if (intent.getSerializableExtra("data") is TradeHome) {
                        } else {
                            mViewModel.cancelInvite(
                                tradieData!!.invitationId,
                                tradieData!!.builderId,
                                data.jobId
                            )
                        }
                    } else {
                        mViewModel.cancelInvite(
                            tradieData!!.invitationId,
                            tradieData!!.builderId,
                            intent.getStringExtra("jobID")
                        )
                    }
                } else {
                    if (tradieData != null) {
                        startActivity(
                            Intent(this, ChooseJobActivity::class.java).putExtra(
                                "data",
                                tradieData!!.builderId
                            )
                        )
                    }
                }
            }
        }

        mBinding.tvReviewList.setOnClickListener {
            startActivityForResult(
                Intent(this, ReviewListActivity::class.java).putExtra(
                    "data",
                    tradieData!!.reviewData
                ).putExtra(
                    "title",
                    (tradieData!!.reviewsCount!!.toInt()).toString() + " review(s)"
                ).putExtra("count", tradieData!!.reviewsCount!!.toInt()), 2610
            )
        }
        mBinding.tvVounchList.setOnClickListener {
            startActivity(
                Intent(this, VounchListActivity::class.java).putExtra(
                    "data",
                    tradieData!!.vouchesData
                ).putExtra("title", tradieData?.voucherCount.toString() + " voucher(s)")
                    .putExtra("id", tradieData?.builderId)
            )
        }
        mBinding.tvVounchListLeave.setOnClickListener {
            startActivityForResult(
                Intent(this, AddVoucherBuilderActivity::class.java).putExtra(
                    "id",
                    tradieData?.builderId
                ), 1310
            )
        }
        mBinding.ivMsg.setOnClickListener {
            mViewModel.jobsList(1, true)
        }
    }

    var isPortfolioAllClicked: Boolean = false
    private fun manageMoreLessPortfolio() {
        if (!isPortfolioAllClicked) {
            isPortfolioAllClicked = true
            if (photosHolderList!!.size > 3) {
                photos!!.clear()
                photos!!.addAll(photosHolderList!!)
                mBinding.rvPhotos.adapter?.notifyDataSetChanged()

                mBinding.tvPortfolioMore.text = "Less"
            }
        } else {
            isPortfolioAllClicked = false
            photos!!.clear()
            for (index in photosHolderList!!.indices) {
                if (index < 3)
                    photos!!.add(photosHolderList!![index])
                else
                    break
            }
            mBinding.rvPhotos.adapter?.notifyDataSetChanged()
            mBinding.tvPortfolioMore.text = "All"
        }
    }

    var isAboutMoreClicked: Boolean = false
    private fun manageMoreLessAbout() {
        if (!isAboutMoreClicked) {
            isAboutMoreClicked = true
            mBinding.tvDescTitleMore.text = "Less"
            mBinding.tvDesc.maxLines = Integer.MAX_VALUE
            mBinding.tvDesc.ellipsize = null
        } else {
            isAboutMoreClicked = false
            mBinding.tvDescTitleMore.text = getString(R.string.more_)
            mBinding.tvDesc.maxLines = 3
            mBinding.tvDesc.ellipsize = TextUtils.TruncateAt.END
        }
    }

    override fun onBackPressed() {
        if (isRefresh == true) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.TRADIE_PROFILE -> {
                mBinding.srLayout.isRefreshing = false
            }
            ApiCodes.ACCEPT_DECLINE_REQUEST -> {
                showToastShort(exception.message)
            }
            ApiCodes.CANCEL_INVITE -> {
                showToastShort(exception.message)
            }
            ApiCodes.SAVE_TRADIE -> {
                showToastShort(exception.message)
            }
            ApiCodes.JOB_LIST -> {
                showToastShort(exception.message)
            }

        }
    }

    private fun getLastMessages(jobId: String, jobName: String) {
        val inBoxMessage = ChatMessageBean()
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)

        if (otherUserID.isEmpty()) {
            if (intent.getSerializableExtra("data") is JobDashboardModel) {
                inBoxMessage.messageRoomId =
                    jobId + "_" + jobDashBoard?.tradieId + "-" + loginUserId
            } else if (intent.getSerializableExtra("data") is TradeHome) {
                inBoxMessage.messageRoomId =
                    jobId + "_" + tradeHome?.tradieId + "-" + loginUserId
            } else {
                inBoxMessage.messageRoomId = jobId + "_" + data.tradieId + "_" + loginUserId
            }
        } else {
            inBoxMessage.messageRoomId = jobId + "_" + otherUserID + "_" + loginUserId
        }
        FirebaseDatabaseQueries.instance?.getLastMessageInfo(
            null,
            inBoxMessage,
            object : FirebaseMessageListener {
                override fun getMessages(message: ChatMessageBean?) {
                    getUsersData(jobId, jobName, message)
                }

                override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {}
                override fun newMessagesListing() {

                }

                override fun noData() {
                    getUsersData(jobId, jobName, null)
                }
            }
        )
    }

    private fun getUsersData(jobId: String, jobName: String, chatModels: ChatMessageBean?) {
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        var chatModel = ChatMessageBean();
        if (chatModels != null) {
            chatModel = chatModels
        }
        if (otherUserID.isEmpty()) {
            if (intent.getSerializableExtra("data") is JobDashboardModel) {
                chatModel.jobId = jobId
                chatModel.jobName = jobName
                if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
                    chatModel.receiverId = jobDashBoard?.tradieId
                if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
                    chatModel.senderId = loginUserId
                jobDashBoard?.tradieId?.let {
                    FirebaseDatabaseQueries.instance?.getUser(
                        it,
                        object : FirebaseUserListener {
                            override fun getUser(user: UserBean?) {
                                try {
                                    user?.let {
                                        // setting image
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
                                            mBinding.root.context,
                                            ChatBuilderActivity::class.java
                                        ).putExtra("data", chatModel)
                                            .putExtra("senderName", tradieData?.builderName)
                                    )
                                } catch (e: Exception) {
                                }

                            }
                        })
                }
            } else if (intent.getSerializableExtra("data") is TradeHome) {
                chatModel.jobId = jobId
                chatModel.jobName = jobName
                if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
                    chatModel.receiverId = tradeHome?.tradieId
                if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
                    chatModel.senderId = loginUserId
                tradeHome?.tradieId?.let {
                    FirebaseDatabaseQueries.instance?.getUser(
                        it,
                        object : FirebaseUserListener {
                            override fun getUser(user: UserBean?) {
                                try {
                                    user?.let {
                                        // setting image
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
                                            mBinding.root.context,
                                            ChatBuilderActivity::class.java
                                        ).putExtra("data", chatModel)
                                            .putExtra("senderName", tradieData?.builderName)
                                    )
                                } catch (e: Exception) {
                                }

                            }
                        })
                }
            } else {
                chatModel.jobId = jobId
                chatModel.jobName = jobName
                if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
                    chatModel.receiverId = data?.tradieId
                if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
                    chatModel.senderId = loginUserId
                data?.tradieId?.let {
                    FirebaseDatabaseQueries.instance?.getUser(
                        it,
                        object : FirebaseUserListener {
                            override fun getUser(user: UserBean?) {
                                try {
                                    user?.let {
                                        // setting image
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
                                        startActivity(
                                            Intent(
                                                mBinding.root.context,
                                                ChatBuilderActivity::class.java
                                            ).putExtra("data", chatModel)
                                                .putExtra("senderName", tradieData?.builderName)
                                        )
                                    }
                                } catch (e: Exception) {
                                }
                            }
                        })
                }
            }

        } else {
            chatModel.jobId = jobId
            chatModel.jobName = jobName
            if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
                chatModel.receiverId = otherUserID
            if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
                chatModel.senderId = loginUserId
            data?.tradieId?.let {
                FirebaseDatabaseQueries.instance?.getUser(
                    it,
                    object : FirebaseUserListener {
                        override fun getUser(user: UserBean?) {
                            try {
                                user?.let {
                                    // setting image
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
                                    startActivity(
                                        Intent(
                                            mBinding.root.context,
                                            ChatBuilderActivity::class.java
                                        ).putExtra("data", chatModel)
                                            .putExtra("senderName", tradieData?.builderName)
                                    )
                                }
                            } catch (e: Exception) {
                            }
                        }
                    })
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.TRADIE_PROFILE -> {
                mBinding.srLayout.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mViewModel.builderModel.let {
                    tradieData = it
                }
                setData()
            }
            ApiCodes.JOB_LIST -> {
                mViewModel.mJsonResponseModelList.let {
                    SelectJobDialog(this, it as ArrayList<JobRecModel>) { clickData ->
                        val pos = clickData.tag as Int
                        getLastMessages(it[pos].jobId!!, it[pos].jobName!!)
                    }
                }.show()
            }
            ApiCodes.ACCEPT_DECLINE_REQUEST -> {
                tradieData?.isRequested = false
                mBinding.tvAccept.visibility = View.GONE
                mBinding.tvDecline.visibility = View.GONE
                mViewModel.status.let {

                    if (it == 1) {
                        startActivity(Intent(this, QuoteAcceptedTradieActivity::class.java))
                    }/*  if (it == 1) {
                        startActivity(
                            Intent(this, HomeBuilderActivity::class.java).putExtra(
                                "pos",
                                1
                            )
                        )
                        ActivityCompat.finishAffinity(this)
                    }*/
                    else {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                }
            }
            ApiCodes.SAVE_TRADIE -> {
                if (isTradieSaved) {
                    savedTradieMoEngage(category!!)
                    savedTradieMixPanel(category!!)
                }
            }
            ApiCodes.CANCEL_INVITE -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun savedTradieMoEngage(category: String) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_SAVED_TRADIE,
            signUpProperty
        )
    }

    private fun savedTradieMixPanel(category: String) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_SAVED_TRADIE,
            props
        )
    }

    override fun onClick(p0: View?) {
    }

    override fun onSpecCLick(position: Int) {
    }

    override fun onJobClick(position: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val vouchesData = data.getSerializableExtra("data") as VouchesData
                voucherData.add(vouchesData)
                vounchesAdapter.notifyDataSetChanged()
                mBinding.tvVouches.text =
                    getString(R.string.voucher) + " (" + voucherData?.size + ")"
                if (voucherData.size > 2) {
                    mBinding.tvVounchListLeave.visibility = View.GONE
                    mBinding.tvVounchList.visibility = View.VISIBLE
                    mBinding.tvVounchList.text =
                        getString(R.string.show_all_vouchers, voucherData.size.toString())
                } else {
                    mBinding.tvVounchListLeave.visibility = View.VISIBLE
                    mBinding.tvVounchList.visibility = View.GONE
                }
            }
        }
        if (requestCode == 2610 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val reviewDatas = data.getSerializableExtra("data") as ArrayList<ReviewData>
                val count = data.getIntExtra("count", 0)
                if (reviewDatas.size > 3) {
                    mBinding.tvReviewList.visibility = View.VISIBLE
                    mBinding.tvReviewList.text =
                        getString(R.string.show_all_reviews, reviewData.size.toString())
                } else {
                    mBinding.tvReviewList.visibility = View.GONE
                }
                reviewData.clear()
                reviewData.addAll(reviewDatas)
                tradieData?.reviewData = reviewData
                reviewAdapter = reviewData?.let { ReviewAdapter(it, false) }!!
                mBinding.rvReviews.adapter = reviewAdapter
            }
        }
    }

    override fun onExpandClick(position: Int, specialisationData: SpecialisationData) {
        Log.i(TAG, "isShowMore $position")
        Log.i(TAG, "isShowMore ${specialisationData.specializationName}")
        var tagSpecName: String = specialisationData.specializationName!!.toLowerCase()
        if (tagSpecName == "show less") {
            setUpListWithShowMore(specializatinData, specializatinDataEditable)
        } else if (tagSpecName == "show more") {
            specializatinData.clear()
            specializatinData.addAll(specializatinDataEditable)
            specializatinData.add(SpecialisationData("SpecId Less", "Show Less"))
            val mHomeAdapter = TradieMixSmallAdapter(tradeData)
            val mSpecAdapter = SpecializationMixSmallAdapter(specializatinData, true, this)
            mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
            Log.i(TAG, "isShowMore ${specializatinData.size}")
        }
    }

    private val TAG: String = "showMoreData"

    fun setUpListWithShowMore(
        listMain: ArrayList<SpecialisationData>,
        listEditable: ArrayList<SpecialisationData>
    ) {
        listMain.clear()
        if (listEditable.size > 5) {
            for (index in listEditable.indices) {
                if (index <= 4) {
                    listMain.add(listEditable[index])
                } else if (index == 5) {
                    listMain.add(SpecialisationData("SpecId", "Show More"))
                    break
                }
            }
        } else {
            listMain.addAll(listEditable)
        }
        val jobLayoutManager = FlexboxLayoutManager(mBinding.root.context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        val mHomeAdapter = TradieMixSmallAdapter(tradeData)
        val mSpecAdapter =
            SpecializationMixSmallAdapter(specializatinData, isShowMore = true, this)
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
        mBinding.rvJobTypes.adapter?.notifyDataSetChanged()
    }
}