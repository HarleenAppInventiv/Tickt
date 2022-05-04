package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.builderreview.BuilderProfileModel
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.TradeData
import com.app.core.model.tradie.PortFolio
import com.app.core.model.tradie.ReviewData
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.*
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityBuilderProfileBinding
import com.example.ticktapp.firebase.FirebaseDatabaseQueries
import com.example.ticktapp.firebase.FirebaseMessageListener
import com.example.ticktapp.firebase.FirebaseUserListener
import com.example.ticktapp.mvvm.view.builder.ReviewListActivity
import com.example.ticktapp.mvvm.viewmodel.BuilderProfileViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BuilderProfileActivity : BaseActivity(),
    JobsSmallAdapter.JobAdapterListener, SpecializationMixSmallAdapter.OnSpecializationClicked {

    private lateinit var data: JobRecModel

    private var tradieData: BuilderProfileModel? = null

    private lateinit var mBinding: ActivityBuilderProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(BuilderProfileViewModel::class.java) }
    private val tradeData: ArrayList<TradeData> = ArrayList()
    private val specializatinData: ArrayList<SpecialisationData> = ArrayList()
    private val reviewData: ArrayList<ReviewData> = ArrayList()
    private val jobPosted: ArrayList<JobRecModel> = ArrayList()

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var jobPostedAdapter: NewJobRequestBuilderAdapter

    companion object {
        public var specializatinDataEditable: ArrayList<SpecialisationData> = ArrayList()
    }

    private lateinit var imageAdapter: PortfolioAdapter
    private var photos: ArrayList<PortFolio>? = null
    private var photosHolderList: ArrayList<PortFolio>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_builder_profile)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setupView()
        setObservers()
    }

    private fun getIntentData() {
        mBinding.srLayout.visibility = View.GONE
        if (intent.hasExtra("data"))
            data = intent.getSerializableExtra("data") as JobRecModel
        if (intent.hasExtra("builderId"))
            mViewModel.builderProfile(true, intent.getStringExtra("builderId"))
        else if (data.builderData != null)
            mViewModel.builderProfile(true, data.builderData.builderId)
        else if (data.postedBy != null)
            mViewModel.builderProfile(true, data.postedBy.builderId)
        else if (data.builderId != null)
            mViewModel.builderProfile(true, data.builderId)
    }

    private fun setData() {
        if (tradieData != null) {
            mBinding.tvName.text = tradieData?.builderName
            mBinding.tvDetails.text = tradieData?.position
            mBinding.tvCompanyName.text = tradieData?.companyName
            if (tradieData?.about?.length!! > 0) {
                mBinding.tvDesc.text = tradieData?.about
                mBinding.tvDescTitleMore.visibility = View.VISIBLE
            } else {
                mBinding.tvDesc.visibility = View.GONE
                mBinding.tvDescTitleMore.visibility = View.GONE
                mBinding.tvDescTitle.visibility = View.GONE
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
            tradieData?.areasOfjobs?.let {   specializatinDataEditable.clear()
                specializatinDataEditable.addAll(it) }

            if (tradeData.size > 0 || specializatinData.size > 0)
                mBinding.llJobSpec.visibility = View.VISIBLE

            setUpListWithShowMore(
                specializatinData,
                specializatinDataEditable
            )
            Log.i("listSize", specializatinData.size.toString())
            Log.i("listSize", "editable " + specializatinDataEditable.size.toString())
            Log.i("listSize", specializatinData[specializatinData.size - 1].specializationName!!)


            photos?.clear()
            photosHolderList?.clear()
            tradieData?.portfolio?.let {
                photosHolderList!!.addAll(it)
//                photos?.addAll(it)
            }
            if (photosHolderList?.size ?: 0 > 3) {
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
           /* mBinding.tvPhotos.text =
                getString(R.string.portfolio) + " (" + photos?.size + " " + getString(R.string.jobs) + ")"*/

            mBinding.rvPhotos.adapter?.notifyDataSetChanged()
            reviewData.clear()
            tradieData?.reviewData?.let { reviewData.addAll(it) }
            if (tradieData?.reviewData?.size!! > 0) {
                mBinding.llReviewData.visibility = View.VISIBLE
            } else {
                mBinding.llReviewData.visibility = View.GONE
            }
            jobPosted.clear()
            tradieData?.jobPostedData?.let { jobPosted.addAll(it) }
            if (tradieData?.jobPostedData?.size!! > 0) {
                mBinding.llJobsPosted.visibility = View.VISIBLE
            } else {
                mBinding.llJobsPosted.visibility = View.GONE
            }

            if (jobPosted.size > 2) {
                mBinding.tvJobsList.visibility = View.VISIBLE
                mBinding.tvJobsList.text =
                    getString(R.string.show_all_jobs, jobPosted.size.toString())
            } else {
                mBinding.tvJobsList.visibility = View.GONE
            }

            mBinding.tvReview.text =
                getString(R.string.reviews_) + " (" + reviewData?.size + ")"
            mBinding.rvReviews.adapter?.notifyDataSetChanged()

            if (reviewData.size > 3) {
                mBinding.tvReviewList.visibility = View.VISIBLE
                mBinding.tvReviewList.text =
                    getString(R.string.show_all_reviews, reviewData.size.toString())
            } else {
                mBinding.tvReviewList.visibility = View.GONE
            }
        }

        /*var stringBuilder = StringBuilder()
        if (!tradieData?..isNullOrEmpty()) {
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
        }*/

        var lat = PreferenceManager.getString(PreferenceManager.LAT)
        var lng = PreferenceManager.getString(PreferenceManager.LAN)
        var location = ""
        if (lat.isNullOrEmpty() || lng.isNullOrEmpty()) {
            location = "$lat , $lng"
        }
        viewedBuilderProfileMoEngage(tradieData?.builderName,"",location)
        viewedBuilderProfileMixPanel(tradieData?.builderName,"",location)
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

        val jobPostedLayountManager = LinearLayoutManager(this)
        jobPostedAdapter = jobPosted?.let { NewJobRequestBuilderAdapter(it, false) }!!
        mBinding.rvJobPosted.layoutManager = jobPostedLayountManager
        mBinding.rvJobPosted.adapter = jobPostedAdapter

        val mHomeAdapter = TradieMixSmallAdapter(tradeData)
        val jobLayoutManager = FlexboxLayoutManager(mBinding.root.context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }

        val mSpecAdapter =
            SpecializationMixSmallAdapter(specializatinData, onspecializationClicked = this)
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)

        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobTypes, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvReviews, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobPosted, false)
    }


    private fun listener() {
        mBinding.tvDescTitleMore.setOnClickListener { manageMoreLessAbout() }
        mBinding.tvPortfolioMore.setOnClickListener { manageMoreLessPortfolio() }
        mBinding.tradieProfileIvBack.setOnClickListener { onBackPressed() }
        mBinding.srLayout.setOnRefreshListener {
            mViewModel.builderProfile(true, data.jobId)
        }
        mBinding.tvJobsList.setOnClickListener {
            startActivity(
                Intent(this, JobListedActivity::class.java)
                    .putExtra("data", jobPosted)
            )
        }
        mBinding.tvReviewList.setOnClickListener {
            startActivity(
                Intent(this, ReviewListActivity::class.java)
                    .putExtra("data", reviewData)
                    .putExtra("title", (tradieData!!.reviewData?.size).toString() + " review(s)")
            )
        }

        mBinding.userIvMsg.setOnClickListener {
            getLastMessages()
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
                if(index<3)
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

    private fun getLastMessages() {
        val inBoxMessage = ChatMessageBean()
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)

        inBoxMessage.messageRoomId =
            data?.jobId + "_" + loginUserId + "_" + data?.builderId

        FirebaseDatabaseQueries.instance?.getLastMessageInfo(
            null,
            inBoxMessage,
            object : FirebaseMessageListener {
                override fun getMessages(message: ChatMessageBean?) {
                    getUsersData(message)
                }

                override fun getMessagesList(messagesList: List<ChatMessageBean?>?) {}
                override fun newMessagesListing() {

                }

                override fun noData() {
                    getUsersData(null)
                }
            }
        )
    }

    private fun getUsersData(chatModels: ChatMessageBean?) {
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)
        var chatModel = ChatMessageBean();
        if (chatModels != null) {
            chatModel = chatModels
        }
        chatModel.jobId = data?.jobId
        chatModel.jobName = data?.jobName
        if (chatModel.receiverId == null || chatModel.receiverId!!.isEmpty())
            chatModel.receiverId = data?.builderId
        if (chatModel.senderId == null || chatModel.senderId!!.isEmpty())
            chatModel.senderId = loginUserId
        data?.builderId?.let {
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
                                    this@BuilderProfileActivity,
                                    ChatTradieActivity::class.java
                                ).putExtra("data", chatModel)
                            )
                        } catch (e: Exception) {
                        }

                    }
                })
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.BUILDER_PROFILE -> {
                mBinding.srLayout.isRefreshing = false
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.BUILDER_PROFILE -> {
                mBinding.srLayout.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mViewModel.mJsonResponseModel.let {
                    tradieData = it
                }
                setData()
            }
            ApiCodes.CANCEL_INVITE -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onJobClick(position: Int) {
    }

    private val TAG: String = "showMoreData"
    override fun onExpandClick(position: Int, specialisationData: SpecialisationData) {
        Log.i(TAG, "isShowMore $position")
        Log.i(TAG, "isShowMore ${specialisationData.specializationName}")
        var tagSpecName: String = specialisationData.specializationName!!.toLowerCase()
        if (tagSpecName == "show less") {
            setUpListWithShowMore(
                specializatinData,
                specializatinDataEditable
            )
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

    private fun viewedBuilderProfileMoEngage(builderName: String?, cate: String, location: String) {
        var isBuilder = PreferenceManager.getString(PreferenceManager.USER_TYPE) == "1"
        if (isBuilder) {
            val signUpProperty = Properties()
            signUpProperty.addAttribute(MoEngageConstants.NAME, builderName)
            signUpProperty.addAttribute(MoEngageConstants.CATEGORY, cate)
            signUpProperty.addAttribute(MoEngageConstants.LOCATION, location)

            MoEngageUtils.sendEvent(
                this,
                MoEngageConstants.MOENGAGE_EVENT_VIEWED_BUILDER_PROFILE,
                signUpProperty
            )
        }
    }

    private fun viewedBuilderProfileMixPanel(builderName: String?, cate: String, location: String) {
        var isBuilder = PreferenceManager.getString(PreferenceManager.USER_TYPE) == "2"
        if (isBuilder) {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val mixpanel = MixpanelAPI.getInstance(
                this,
                getString(R.string.mix_panel_token)
            )

            val props = JSONObject()

            props.put(MoEngageConstants.NAME, builderName)
            props.put(MoEngageConstants.CATEGORY, cate)
            props.put(MoEngageConstants.LOCATION, location)

            mixpanel.track(
                MoEngageConstants.MOENGAGE_EVENT_VIEWED_BUILDER_PROFILE,
                props
            )
        }
    }
}