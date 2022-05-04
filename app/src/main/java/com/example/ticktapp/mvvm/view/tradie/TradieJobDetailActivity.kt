package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.CancelReason
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.model.jobmodel.JobMilestone
import com.app.core.model.jobmodel.JobModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.jobmodel.Photos
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.bumptech.glide.Glide
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.JobsSmallAdapter
import com.example.ticktapp.adapters.MediaAdapter
import com.example.ticktapp.adapters.RowMilestoneTradeJobAdapter
import com.example.ticktapp.adapters.SpecializationAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieeJobDetailsBinding
import com.example.ticktapp.firebase.FirebaseDatabaseQueries
import com.example.ticktapp.firebase.FirebaseMessageListener
import com.example.ticktapp.firebase.FirebaseUserListener
import com.example.ticktapp.interfaces.DialogCallback
import com.example.ticktapp.model.registration.*
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.DialogUtils
import com.example.ticktapp.util.MoEngageUtils
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.JsonObject
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import kotlinx.android.synthetic.main.activity_tradiee_job_details.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
public class TradieJobDetailActivity : BaseActivity(),
    OnClickListener, SpecializationAdapter.SpecListAdapterListener,
    JobsSmallAdapter.JobAdapterListener {
    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityTradieeJobDetailsBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val jobList by lazy { ArrayList<JobModel>() }
    private lateinit var mHomeAdapter: JobsSmallAdapter
    private lateinit var mSpecAdapter: SpecializationAdapter
    private lateinit var milestoneAdapter: RowMilestoneTradeJobAdapter
    private lateinit var imageAdapter: MediaAdapter
    private var specList: ArrayList<Specialisation>? = null
    private var milestoneData: ArrayList<JobMilestone>? = null
    private var photos: ArrayList<Photos>? = null
    private var isSaved: Boolean = false
    private var showMilestoneProgress: Boolean = false
    private var isQuestionAsked: Boolean = true
    private var forNewJob: Boolean = false
    private var showEditOption: Boolean = false
    private var builderId: String = ""
    var isSavedJob: Boolean = false
    var isCancellationAccept: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradiee_job_details)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setupView()
        setData()
        setObservers()
        mViewModel.getTradieJobsDetails(true, data.jobId)
    }

    private fun getIntentData() {
        data = intent.getSerializableExtra("data") as JobRecModel
        showMilestoneProgress = intent.getBooleanExtra("showMilestoneProgress", false)
        isQuestionAsked = intent.getBooleanExtra("isQuestionAsked", true)
        forNewJob = intent.getBooleanExtra("forNewJob", false)
        showEditOption = intent.getBooleanExtra("showEditOption", false)
        isSavedJob = intent.getBooleanExtra("isSavedJob", false)
        mBinding.ilHeader.tvTitle.visibility = View.GONE
        if (showEditOption)
            mBinding.ilHeader.ivEdit.visibility = View.VISIBLE
    }

    private fun setData() {
        mBinding.tvTitle.text = data.tradeName
        mBinding.tvDetails.text = data.jobName
        mBinding.tvMoney.text = data.amount
        mBinding.tvPlace.text = data.locationName
        if (data.time != null && data.time!!.length > 0 && data.time != "0") {
            mBinding.tvTime.text = data.time
        } else {
            if (data.toDate != null && data.toDate.toString().isNotEmpty()) {
                var outputFormatFrom = DateUtils.DATE_FORMATE_15
                var outputFormatTo = DateUtils.DATE_FORMATE_15
                if (DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        data.fromDate!!
                    ) && DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, data.toDate!!)
                ) {
                    outputFormatFrom = DateUtils.DATE_FORMATE_16
                    outputFormatTo = DateUtils.DATE_FORMATE_16
                } else if (DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        data.fromDate!!
                    ) && !DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, data.toDate!!)
                ) {
                    outputFormatFrom = DateUtils.DATE_FORMATE_16
                    outputFormatTo = DateUtils.DATE_FORMATE_15
                }

                mBinding.tvTime.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        outputFormatFrom,
                        data.fromDate
                    ) + " - " +
                            DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                outputFormatTo,
                                data.toDate
                            )
                )

            } else {
                var outputFormat = DateUtils.DATE_FORMATE_15
                if (DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, data.fromDate!!)) {
                    outputFormat = DateUtils.DATE_FORMATE_16
                }
                mBinding.tvTime.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        outputFormat,
                        data.fromDate
                    )
                )
            }
        }
        if (data.jobDescription.isNullOrEmpty()) {
            mBinding.tvDesc.visibility = View.GONE
            mBinding.tvDescTitle.visibility = View.GONE
        } else {
            mBinding.tvDesc.visibility = View.VISIBLE
            mBinding.tvDescTitle.visibility = View.VISIBLE
        }
        if (data.jobDescription != null && data.jobDescription!!.length > 0)
            mBinding.tvDesc.text = data.jobDescription
        else if (data.details != null && data.details!!.length > 0)
            mBinding.tvDesc.text = data.details

        if (data.tradeSelectedUrl != null && data.tradeSelectedUrl!!.isNotEmpty()) {
            Glide.with(mBinding.root.context).load(data.tradeSelectedUrl)
                .placeholder(R.drawable.bg_blue_circle)
                .into(mBinding.ivUserProfile)
        } else {
            Glide.with(mBinding.root.context).load(data.userImage)
                .placeholder(R.drawable.bg_blue_circle)
                .into(mBinding.ivUserProfile)
        }
        if (data.questionsCount != null && (data.questionsCount == "0" || data.questionsCount == "1" || data.questionsCount == "0.0" || data.questionsCount == "1.0"))
            mBinding.tvJobQuestionCount.text =
                data.questionsCount.toString().toDouble().toInt()
                    .toString() + " " + getString(R.string.question_)
        else if (data.questionsCount != null)
            mBinding.tvJobQuestionCount.text =
                data.questionsCount.toString().toDouble().toInt()
                    .toString() + " " + getString(R.string.questions_)

        if (data?.status != null && data?.status!!.length > 0) {
            if (!(data?.status.equals("EXPIRED") || data?.status.equals("COMPLETED") ||
                        data?.status.equals("CANCELLED"))
            ) {

                mBinding.tvStatus.visibility = View.GONE
                mBinding.tvDays.visibility = View.VISIBLE
            } else {
                mBinding.tvDays.visibility = View.GONE
                mBinding.tvStatus.visibility = View.VISIBLE
                mBinding.tvStatus.text = data?.status
                statusJob = data?.status!!
                mBinding.tvMilestoneStatus.visibility = View.GONE
            }
        }
    }

    var statusJob: String = ""

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
        mHomeAdapter = JobsSmallAdapter(this, jobList)
        val jobLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = mHomeAdapter

        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        specList = ArrayList()
        mSpecAdapter = specList?.let { SpecializationAdapter(it, this, false) }!!
        mBinding.rvSpecialization.layoutManager = flexboxLayoutManager
        mBinding.rvSpecialization.adapter = mSpecAdapter

        val layoutManager = LinearLayoutManager(this)
        milestoneData = ArrayList()
        milestoneAdapter = milestoneData?.let { RowMilestoneTradeJobAdapter(it) }!!
        mBinding.rvJobMilestone.layoutManager = layoutManager
        mBinding.rvJobMilestone.adapter = milestoneAdapter

        val layoutManagerHor = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        photos = ArrayList()
        imageAdapter = photos?.let { MediaAdapter(it) }!!
        mBinding.rvPhotos.layoutManager = layoutManagerHor
        mBinding.rvPhotos.adapter = imageAdapter

        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobMilestone, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobTypes, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvSpecialization, false)
    }


    private fun listener() {

        mBinding.srLayout.setOnRefreshListener {
            mViewModel.getTradieJobsDetails(true, data.jobId)
        }
        mBinding.ivSaveJob.setOnClickListener {
            if (isSaved) {
                isSaved = false
                mBinding.ivSaveJob.setImageResource(R.drawable.ic_unsaved_job)
                mViewModel.saveJob(data.jobId, data.tradeId, data.specializationId, isSaved)
            } else {
                isSaved = true
                mBinding.ivSaveJob.setImageResource(R.drawable.ic_save_job)
                mViewModel.saveJob(data.jobId, data.tradeId, data.specializationId, isSaved)
            }
            ApplicationClass.isSaveRefresh = true
        }
        mBinding.tvApply.setOnClickListener {
            if (mViewModel.mJsonResponseModel.quoteJob) {
                data.postedBy= mViewModel.mJsonResponseModel.postedBy
                startActivity(Intent(this, AddQuoteActivity::class.java).putExtra("data", data))
            } else if (mBinding.tvApply.text.toString() == getString(R.string.apply)) {
                if (data.tradeId.equals(PreferenceManager.getString(PreferenceManager.TRADE_ID))) {
                    val params = HashMap<String, Any>()
                    params[ApiParams.JOB_ID] = data.jobId!!
                    params[ApiParams.TRADE_ID] = data.tradeId!!
                    if (!data.specializationId.isNullOrEmpty())
                    params[ApiParams.SPECIALIZATION_ID] = data.specializationId!!
                    if (builderId != null && builderId.isNotEmpty())
                        params[ApiParams.BUILDER_ID] = builderId
                    mViewModel.applyJob(params)

                } else {
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_popup)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
                    title.text = getString(R.string.job_not_match)

                    val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
                    msg.text = getString(R.string.job_note_match_msg)

                    val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
                    dialogBtn_okay.text = getString(R.string.yes)
                    dialogBtn_okay.setOnClickListener {
                        dialog.dismiss()
                        val params = HashMap<String, Any>()
                        params[ApiParams.JOB_ID] = data.jobId!!
                        params[ApiParams.TRADE_ID] = data.tradeId!!
                        if (!data.specializationId.isNullOrEmpty())params[ApiParams.SPECIALIZATION_ID] = data.specializationId!!
                        if (builderId != null && builderId.isNotEmpty())
                            params[ApiParams.BUILDER_ID] = builderId
                        mViewModel.applyJob(params)
                    }

                    val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
                    dialogBtn_cancel.text = getString(R.string.no)
                    dialogBtn_cancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }

            }
        }
        mBinding.ilHeader.ivEdit.setOnClickListener {
            showMenu(mBinding.ilHeader.ivEdit)
        }
        mBinding.tvCancelAccept.setOnClickListener {
            val mObject = JsonObject()
            mObject.addProperty("jobId", data.jobId)
            mObject.addProperty("reason", 1)
            isCancellationAccept = true
            mViewModel.builderCancelJob(mObject)
        }

        mBinding.tvCancelReject.setOnClickListener {
            DialogUtils.setCustomAlert(this, object : DialogCallback {
                override fun onPositiveClick(reason: String) {
                    val mObject = JsonObject()
                    mObject.addProperty("jobId", data.jobId)
                    mObject.addProperty("reason", 2)
                    isCancellationAccept = false
                    mViewModel.builderCancelJob(mObject)
                }

                override fun onNegativeClick() {
                }

            })
        }

        mBinding.tvChangeAccept.setOnClickListener {
            val mObject = JsonObject()
            mObject.addProperty("jobId", data.jobId)
            mObject.addProperty("status", 1)
            mObject.addProperty("note", "CR Accepted")
            mViewModel.replyChangeRequest(mObject)
        }

        mBinding.tvChangeReject.setOnClickListener {
            DialogUtils.setCustomAlert(this, object : DialogCallback {
                override fun onPositiveClick(reason: String) {
                    val mObject = JsonObject()
                    mObject.addProperty("jobId", data.jobId)
                    mObject.addProperty("status", 2)
                    mObject.addProperty("note", reason)
                    mViewModel.replyChangeRequest(mObject)
                }

                override fun onNegativeClick() {
                }

            })
        }
        iv_posted_by.setOnClickListener {
            startActivity(
                Intent(this, BuilderProfileActivity::class.java)
                    .putExtra("data", data)
                    .putExtra("builderId", builderId)
            )
        }

        mBinding.llQuestion.setOnClickListener { p0 ->
            startActivityForResult(
                Intent(
                    p0?.context,
                    TradieQuestionListActivity::class.java
                ).putExtra("data", mViewModel.mJsonResponseModel)
                    .putExtra("status", statusJob)
                    .putExtra("isQuestionAsked", isQuestionAsked), 1310
            )
        }

        mBinding.userIvMsg.setOnClickListener {
            getLastMessages()
        }
    }

    private fun acceptCancellationMoEngage() {
        val signUpProperty = Properties()
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_TRADIE_ACCEPT_CANCELLATION,
            signUpProperty
        )
    }

    private fun acceptCancellationMixpanel() {

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )
        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_TRADIE_ACCEPT_CANCELLATION, props)
    }

    private fun rejectCancellationMoEngage() {
        val signUpProperty = Properties()
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_TRADIE_REJECT_CANCELLATION,
            signUpProperty
        )
    }

    private fun rejectCancellationMixpanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )
        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_TRADIE_REJECT_CANCELLATION, props)
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
                                    mBinding.rvJobMilestone.context,
                                    ChatTradieActivity::class.java
                                ).putExtra("data", chatModel)
                            )
                        } catch (e: Exception) {
                        }

                    }
                })
        }
    }

    private fun showMenu(view: View) {
        val viewGroup = view.findViewById<LinearLayout>(R.id.ll_popup)
        val layoutInflater =
            (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val layout: View =
            layoutInflater.inflate(R.layout.layout_pop_up_bank_details_edit, viewGroup)
        val popup = PopupWindow(view.context)
        popup.contentView = layout
        popup.setBackgroundDrawable(ColorDrawable())
        popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        val tvOptionOne = layout.findViewById<TextView>(R.id.tv_option_one)
        val tvOptionTwo = layout.findViewById<TextView>(R.id.tv_option_two)
        tvOptionOne.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_comment, 0, 0, 0)
        tvOptionTwo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_cancel_filled,
            0,
            0,
            0
        )
        tvOptionOne.text = view.context.getString(R.string.lodge_dispute)
        tvOptionTwo.text = view.context.getString(R.string.cancel_job)
        tvOptionOne.setOnClickListener {
            popup.dismiss()

        }
        tvOptionTwo.setOnClickListener {
            popup.dismiss()
            val intent = Intent(this, CancelJobByTradieActivity::class.java)
            intent.putExtra(IntentConstants.JOB_ID, data.jobId)
            intent.putExtra(IntentConstants.JOB_NAME, data.jobName)
            resultLauncher.launch(intent)
        }
        popup.setTouchInterceptor { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popup.dismiss()
                return@setTouchInterceptor true
            }
            false
        }
        popup.animationStyle = R.style.popupWindowAnim
        popup.showAtLocation(layout, Gravity.TOP or Gravity.END, 50, 280)

    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.let {
                    data.putExtra("isForFinish", true)
                    setResult(RESULT_OK, data)
                    finish()
                }
            }
        }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.JOB_DETAILS -> {
                mBinding.srLayout.isRefreshing = false
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CHANGEREQUEST -> {
                mViewModel.getTradieJobsDetails(true, data.jobId)
            }
            ApiCodes.JOB_DETAILS -> {
                mBinding.llMain.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mBinding.llJobSpec.visibility = View.VISIBLE
                mBinding.llMilestoneData.visibility = View.VISIBLE
                mViewModel.mJsonResponseModel.let {
                    Log.i("builderId_job_details", "tradie: ${it.postedBy.builderId}")
                    if (showMilestoneProgress) {
                        mBinding.tvJobMilestone.visibility = View.GONE
                        mBinding.relMilestoneProgress.visibility = View.VISIBLE
                        mBinding.progressBarHor.max = it.totalMilestones
                        mBinding.progressBarHor.progress = it.milestoneNumber
                        mBinding.tvMilestoneCount.text = Html.fromHtml(
                            getString(
                                R.string.remaining_milestone_count,
                                it.milestoneNumber.toString(),
                                it.totalMilestones.toString()
                            )
                        )
                        mBinding.tvMilestoneStatus.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            0
                        )
                        mBinding.tvMilestoneStatus.text = it.status

                    } /*else
                        mBinding.tvTime.text = data.time*/
                    if (mBinding.tvTime.text.toString().isEmpty()) {
                        if (it.toDate != null && it.toDate.toString().isNotEmpty()) {
                            var outputFormatFrom = DateUtils.DATE_FORMATE_15
                            var outputFormatTo = DateUtils.DATE_FORMATE_15
                            if (DateUtils.checkForCurrentYear(
                                    DateUtils.DATE_FORMATE_8,
                                    it.fromDate!!
                                ) && DateUtils.checkForCurrentYear(
                                    DateUtils.DATE_FORMATE_8,
                                    it.toDate!!
                                )
                            ) {
                                outputFormatFrom = DateUtils.DATE_FORMATE_16
                                outputFormatTo = DateUtils.DATE_FORMATE_16
                            } else if (DateUtils.checkForCurrentYear(
                                    DateUtils.DATE_FORMATE_8,
                                    it.fromDate!!
                                ) && !DateUtils.checkForCurrentYear(
                                    DateUtils.DATE_FORMATE_8,
                                    it.toDate!!
                                )
                            ) {
                                outputFormatFrom = DateUtils.DATE_FORMATE_16
                                outputFormatTo = DateUtils.DATE_FORMATE_15
                            }

                            mBinding.tvTime.setText(
                                DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    outputFormatFrom,
                                    it.fromDate
                                ) + " - " +
                                        DateUtils.changeDateFormat(
                                            DateUtils.DATE_FORMATE_8,
                                            outputFormatTo,
                                            it.toDate
                                        )
                            )

                        } else {
                            var outputFormat = DateUtils.DATE_FORMATE_15
                            if (DateUtils.checkForCurrentYear(
                                    DateUtils.DATE_FORMATE_8,
                                    it.fromDate!!
                                )
                            ) {
                                outputFormat = DateUtils.DATE_FORMATE_16
                            }
                            mBinding.tvTime.setText(
                                DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    outputFormat,
                                    it.fromDate
                                )
                            )
                        }
                    }
                    mBinding.tvDays.text = it.duration
                    if (it.jobDescription != null) {
                        mBinding.tvDesc.visibility = View.VISIBLE
                        mBinding.tvDescTitle.visibility = View.VISIBLE
                    } else if (it.details != null) {
                        mBinding.tvDesc.visibility = View.VISIBLE
                        mBinding.tvDescTitle.visibility = View.VISIBLE
                    } else {
                        mBinding.tvDesc.visibility = View.GONE
                        mBinding.tvDescTitle.visibility = View.GONE
                    }
                    if (it.jobDescription != null && it.jobDescription!!.length > 0) {
                        mBinding.tvDesc.text = it.jobDescription
                    } else if (it.details != null && it.details!!.length > 0) {
                        mBinding.tvDesc.text = it.details
                    }

                    if (it.specializationData?.size > 0) {
                        specList?.clear()
                        it.specializationData.forEach {
                            specList?.add(
                                Specialisation(
                                    id = it.specializationId,
                                    name = it.specializationName
                                )
                            )
                        }
                        mSpecAdapter.notifyDataSetChanged()
                    } else {
                        mBinding.tvSpecialization.visibility = View.GONE
                        mBinding.rvSpecialization.visibility = View.GONE
                    }
                    if (it.jobType != null) {
                        jobList.clear()
                        jobList.add(
                            JobModel(
                                _id = it.jobType.jobTypeId,
                                name = it.jobType.jobTypeName,
                                image = it.jobType.jobTypeImage
                            )
                        )
                        mHomeAdapter.notifyDataSetChanged()
                    } else {
                        mBinding.tvJobTypes.visibility = View.GONE
                        mBinding.rvJobTypes.visibility = View.GONE
                    }
                    if (it.postedBy != null) {
                        builderId = it.postedBy.builderId.toString()
                        mBinding.tvPostedBy.visibility = View.VISIBLE
                        mBinding.llPostedBy.visibility = View.VISIBLE
                        mBinding.tvJobUserTitle.text = it.postedBy.builderName
                        mBinding.tvJobUserDetails.text =
                            it.postedBy.ratings.toString() + ", " + it.postedBy.reviews
                        if (it.postedBy.reviews == 0 || it.postedBy.reviews == 1) {
                            mBinding.tvJobUserDetails.append(
                                " " + getString(
                                    R.string.review
                                )
                            )
                        } else {
                            mBinding.tvJobUserDetails.append(
                                " " + getString(
                                    R.string.reviews
                                )
                            )
                        }

                        Glide.with(mBinding.root.context).load(it.postedBy.builderImage)
                            .placeholder(R.drawable.placeholder_profile)
                            .error(R.drawable.placeholder_profile)
                            .into(mBinding.ivJobUserProfile)
                    } else {
                        mBinding.tvPostedBy.visibility = View.GONE
                        mBinding.llPostedBy.visibility = View.GONE
                    }

                    if (it.jobMilestonesData.size > 0) {
                        milestoneData?.clear()
                        milestoneData?.addAll(it.jobMilestonesData)
                        milestoneAdapter.notifyDataSetChanged()
                    } else {
                        mBinding.tvJobMilestone.visibility = View.GONE
                        mBinding.rvJobMilestone.visibility = View.GONE
                    }
                    if (it.photos.size > 0) {
                        photos?.clear()
                        photos?.addAll(it.photos)
                        imageAdapter.notifyDataSetChanged()
                    } else {
                        mBinding.tvPhotos.visibility = View.GONE
                        mBinding.rvPhotos.visibility = View.GONE
                    }
                    if (it.isSaved != null && it.isSaved == true) {
                        mBinding.ivSaveJob.setImageResource(R.drawable.ic_save_job)
                        isSaved = true
                    } else {
                        mBinding.ivSaveJob.setImageResource(R.drawable.ic_unsaved_job)
                        isSaved = false
                    }
                    if (it.appliedStatus.toString()
                            .toUpperCase() == getString(R.string.applied).toUpperCase()
                    ) {
                        mBinding.tvApply.text = getString(R.string.applied)
                    }
                    if (it.quoteJob) {
                        mBinding.llJobApplied.visibility = View.VISIBLE
                        mBinding.tvApply.text = getString(R.string.quotes)
                    }

                    if (it.questionsCount != null && (it.questionsCount == "0" || it.questionsCount == "1"
                                || it.questionsCount == "0.0" || it.questionsCount == "1.0")
                    )
                        mBinding.tvJobQuestionCount.text =
                            it.questionsCount.toString().toDouble().toInt()
                                .toString() + " " + getString(R.string.question_)
                    else if (it.questionsCount != null)
                        mBinding.tvJobQuestionCount.text =
                            it.questionsCount.toString().toDouble().toInt()
                                .toString() + " " + getString(R.string.questions_)
                    if (it.isCancelJobRequest || it.jobStatus.equals("CANCELLED", true)) {
                        val cancelReasonList = ArrayList<CancelReason>()
                        cancelReasonList.add(
                            CancelReason(
                                1,
                                getString(R.string.experiecing_delay),
                                false
                            )
                        )
                        cancelReasonList.add(
                            CancelReason(
                                2,
                                getString(R.string.current_project_has_taken),
                                false
                            )
                        )
                        cancelReasonList.add(
                            CancelReason(
                                3,
                                getString(R.string.staff_shortages),
                                false
                            )
                        )
                        cancelReasonList.add(
                            CancelReason(
                                4,
                                getString(R.string.injury_unwell),
                                false
                            )
                        )
                        cancelReasonList.add(
                            CancelReason(
                                5,
                                getString(R.string.no_longer_available),
                                false
                            )
                        )
                        if (!it.status.equals("COMPLETED"))
                            mBinding.cvCancellationReason.visibility = View.VISIBLE
                        if (it.isCancelJobRequest) {
                            mBinding.tvCancelTitle.text =
                                getString(R.string.job_cancellation_request)
                            mBinding.linCancelReq.visibility = View.VISIBLE
                            mBinding.tvCancelNote.text = it.reasonNoteForCancelJobRequest
                        } else {
                            mBinding.tvCancelTitle.text = getString(R.string.job_cancel_reason)
                            mBinding.linCancelReq.visibility = View.GONE
                            if (!it.reasonNoteForCancelJobRequest.isNullOrEmpty())
                                mBinding.tvCancelNote.text = it.reasonNoteForCancelJobRequest
                            else if (!it.rejectReasonNoteForCancelJobRequest.isNullOrEmpty())
                                mBinding.tvCancelNote.text = it.rejectReasonNoteForCancelJobRequest
                            else
                                mBinding.tvCancelNote.text = ""

                        }

                        mBinding.tvCancelReason.visibility = View.VISIBLE

                        if (it.reasonForCancelJobRequest >= 1)
                            mBinding.tvCancelReason.text =
                                cancelReasonList.get(it.reasonForCancelJobRequest - 1).reason
                    } else if (!it.rejectReasonNoteForCancelJobRequest.isNullOrEmpty() && !it?.jobStatus.equals(
                            "CANCELLED",
                            true
                        )
                    ) {
                        if (!it.jobStatus.equals(
                                "COMPLETED",
                                true
                            )
                        ) {
                            mBinding.cvCancellationReason.visibility = View.VISIBLE
                            mBinding.tvCancelTitle.text =
                                getString(R.string.job_cancellation_rejected_reason)
                            mBinding.tvCancelReason.visibility = View.GONE
                            mBinding.linCancelReq.visibility = View.GONE
                            mBinding.tvCancelNote.text = it.rejectReasonNoteForCancelJobRequest
                        } else
                            mBinding.cvCancellationReason.visibility = View.GONE

                    } else {
                        mBinding.cvCancellationReason.visibility = View.GONE
                    }

                    /*if (forNewJob) {
                        mBinding.tvAccept.visibility = View.VISIBLE
                        mBinding.tvDecline.visibility = View.VISIBLE
                    } else {*/
                    mBinding.tvAccept.visibility = View.GONE
                    mBinding.tvDecline.visibility = View.GONE

                    if (it.isChangeRequest == true) {
                        mBinding.cvChangeReason.visibility = View.VISIBLE

                        if (!it.reasonForChangeRequest.isNullOrEmpty()) {
                            mBinding.tvChangeNote.visibility = View.VISIBLE
                            mBinding.tvChangeNote.text = it.reasonForChangeRequest[0]
                        }

                    } else {
                        mBinding.cvChangeReason.visibility = View.GONE
                    }

                    if (isQuestionAsked) {
                        mBinding.userIvMsg.visibility = View.VISIBLE
                    } else {
                        mBinding.userIvMsg.visibility = View.GONE
                    }
                }
            }
            ApiCodes.CANCELLATION_REPLY -> {
                if (isCancellationAccept) {
                    acceptCancellationMoEngage()
                    acceptCancellationMixpanel()
                } else {
                    rejectCancellationMoEngage()
                    rejectCancellationMixpanel()
                }
                startActivity(
                    Intent(
                        this,
                        HomeActivity::class.java
                    ).putExtra("pos", 1)
                )
                ActivityCompat.finishAffinity(this)
            }
            ApiCodes.APPLY -> {
                jobAppliedMoEngage()
                jobAppliedMoEngage()
                mBinding.tvApply.text = getString(R.string.applied)
                startActivity(Intent(this, JobAppliedActivity::class.java))
            }
            ApiCodes.BUILDER_JOB_CANCEL -> {
                cancelJobMoEngage()
                cancelJobMixPanel()
                mBinding.cvChangeReason.visibility = View.GONE
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    private fun jobAppliedMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)


        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_APPLIED_FOR_JOB,
            signUpProperty
        )
    }

    private fun cancelJobMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_CANCEL_QUOTE_JOB,
            signUpProperty
        )
    }

    private fun jobAppliedMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_APPLIED_FOR_JOB,
            props
        )
    }

    private fun cancelJobMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_CANCEL_QUOTE_JOB,
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
                try {
                    val answerData = data.getSerializableExtra("data") as JobRecModel
                    this.data = answerData
                    mViewModel.mJsonResponseModel = answerData
                    if (this.data.questionsCount != null && (this.data.questionsCount == "0" || this.data.questionsCount == "1" || this.data.questionsCount == "0.0" || this.data.questionsCount == "1.0"))
                        mBinding.tvJobQuestionCount.text =
                            this.data.questionsCount.toString().toDouble().toInt()
                                .toString() + " " + getString(R.string.question_)
                    else if (this.data.questionsCount != null)
                        mBinding.tvJobQuestionCount.text =
                            this.data.questionsCount.toString().toDouble().toInt()
                                .toString() + " " + getString(R.string.questions_)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isSavedJob) {
            var intent = Intent()
            intent.putExtra("isSaved", isSaved)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}