package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.CancelReason
import com.app.core.model.chat.ChatMessageBean
import com.app.core.model.chat.UserBean
import com.app.core.model.jobmodel.*
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.bumptech.glide.Glide
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.*
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobDetailsBinding
import com.example.ticktapp.firebase.FirebaseDatabaseQueries
import com.example.ticktapp.firebase.FirebaseMessageListener
import com.example.ticktapp.firebase.FirebaseUserListener
import com.example.ticktapp.interfaces.DialogCallback
import com.example.ticktapp.model.registration.*
import com.example.ticktapp.mvvm.view.builder.*
import com.example.ticktapp.mvvm.view.builder.milestone.MilestoneEditListingActivity
import com.example.ticktapp.mvvm.view.builder.postjob.PostNewJobActivity
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.mvvm.viewmodel.NewQuoteListRequestViewModel
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
public class JobDetailsActivity : BaseActivity(),
    OnClickListener, SpecializationAdapter.SpecListAdapterListener,
    JobsSmallAdapter.JobAdapterListener, JobsMoreSelectionAdapter.JobAdapterListener {
    private var isFullDate: Boolean = false
    private var isRefresh: Boolean? = false
    private var isDisputed: Boolean? = false
    private var isExpired: Boolean? = false
    private var jobDashBoard: JobDashboardModel? = null
    private lateinit var data: JobRecModel
    private lateinit var status: String

    private lateinit var mBinding: ActivityJobDetailsBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val mViewModelQuote by lazy { ViewModelProvider(this).get(NewQuoteListRequestViewModel::class.java) }
    private val jobList by lazy { ArrayList<JobModel>() }
    private lateinit var mHomeAdapter: JobsMoreSelectionAdapter
    private lateinit var mSpecAdapter: SpecializationAdapter
    private lateinit var milestoneAdapter: RowMilestoneAdapter

    private lateinit var imageAdapter: MediaAdapter
    private var specList: ArrayList<Specialisation>? = null
    private var milestoneData: ArrayList<JobMilestone>? = null
    private var photos: ArrayList<Photos>? = null
    private var isSaved: Boolean = false
    private var builderId: String = ""
    private var isChat: Boolean = false
    private var isPast: Boolean = false
    private var isOpen: Boolean = false
    private var isEditJob: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_details)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setupView()
        setData()
        setObservers()
        if (intent.getSerializableExtra("data") is JobRecModel) {
            if (intent.hasExtra("isBuilder") && intent.getBooleanExtra("isBuilder", false)) {
                mViewModel.jobDetailsFromBuilder(
                    true,
                    data?.jobId,
                    data.tradeId, data.specializationId
                )
            } else {
                mViewModel.getJobsDetails(true, data.jobId, data.tradeId, data.specializationId)
            }
        } else {
            mViewModel.jobDetailsFromBuilder(
                true,
                jobDashBoard?.jobId,
                jobDashBoard?.tradeId,
                jobDashBoard?.specializationId
            )
        }
    }

    private fun getIntentData() {
        isOpen = intent.getBooleanExtra("isOpen", false)
        isPast = intent.getBooleanExtra("isPast", false)
        isChat = intent.getBooleanExtra("isChat", false)
        if (intent.getSerializableExtra("data") is JobDashboardModel) {
            jobDashBoard = intent.getSerializableExtra("data") as JobDashboardModel
            status = jobDashBoard?.status.toString()
        } else {
            data = intent.getSerializableExtra("data") as JobRecModel
            status = data.status.toString()
        }

        if (intent.hasExtra("isEdit") && intent.getBooleanExtra("isEdit", false)) {
            mBinding.ivEdit.visibility = View.VISIBLE
            isDisputed = intent.getBooleanExtra("isDisputed", false)
        } else {
            if (status.equals("EXPIRED")) {
                isExpired = true
                mBinding.ivEdit.visibility = View.VISIBLE
            } else if (isOpen) {
                mBinding.ivEdit.visibility = View.VISIBLE
            }
        }
        isFullDate = intent.getBooleanExtra("isFullDate", false)
    }


    override fun refreshData(newIntent: Intent) {
        runOnUiThread {
            if (newIntent.hasExtra("notificationType")) {
                if (newIntent.getStringExtra("notificationType")
                        .equals("3") || newIntent.getStringExtra("notificationType")
                        .equals("9")
                ) {
                    mBinding.srLayout.isRefreshing = true
                    if (intent.getSerializableExtra("data") is JobRecModel) {
                        if (intent.hasExtra("isBuilder") && intent.getBooleanExtra(
                                "isBuilder",
                                false
                            )
                        ) {
                            mViewModel.jobDetailsFromBuilder(
                                false,
                                data?.jobId,
                                data?.tradeId,
                                data?.specializationId
                            )
                        } else {
                            mViewModel.getJobsDetails(
                                false,
                                data.jobId,
                                data.tradeId,
                                data.specializationId
                            )
                        }
                    } else {
                        mViewModel.jobDetailsFromBuilder(
                            false,
                            jobDashBoard?.jobId,
                            jobDashBoard?.tradeId,
                            jobDashBoard?.specializationId
                        )
                    }
                }
            }
        }
    }

    private fun showJobMenu(view: View) {
        val viewGroup = view.findViewById<LinearLayout>(R.id.ll_popup)
        val layoutInflater =
            (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val layout: View =
            layoutInflater.inflate(R.layout.layout_pop_up_milestone_option, viewGroup)
        val popup = PopupWindow(view.context)
        popup.contentView = layout
        popup.setBackgroundDrawable(ColorDrawable())
        popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        val tvOptionMain = layout.findViewById<TextView>(R.id.tv_option_main)
        val tvOptionOne = layout.findViewById<TextView>(R.id.tv_option_one)
        val tvOptionTwo = layout.findViewById<TextView>(R.id.tv_option_two)

        tvOptionMain.visibility = View.GONE
        tvOptionOne.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_edit_milestone,
            0,
            0,
            0
        )
        tvOptionTwo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_cancel_filled,
            0,
            0,
            0
        )
        tvOptionOne.text = view.context.getString(R.string.edit)
        tvOptionTwo.text = view.context.getString(R.string.delete)

        tvOptionOne.setOnClickListener {
            popup.dismiss()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                mViewModel.jobDetailsFromBuilderOpen(
                    true,
                    data.jobId,
                    data.tradeId,
                    data.specializationId
                )
            } else {
                mViewModel.jobDetailsFromBuilderOpen(
                    true,
                    jobDashBoard?.jobId,
                    jobDashBoard?.tradeId,
                    jobDashBoard?.specializationId
                )
            }
        }
        tvOptionTwo.setOnClickListener {
            popup.dismiss()
            showAppPopupDialog(
                getString(R.string.are_you_want_to_delete_job),
                getString(R.string.yes),
                getString(R.string.no),
                getString(R.string.delete),
                {
                    if (intent.getSerializableExtra("data") is JobRecModel) {
                        mViewModel.jobDetailsRemove(
                            true,
                            data?.jobId
                        )
                    } else {
                        mViewModel.jobDetailsRemove(
                            true,
                            jobDashBoard?.jobId
                        )
                    }
                },
                {})

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

    private fun getRepublishDataForEdit() {
        if (intent.getSerializableExtra("data") is JobRecModel) {
            mViewModel.jobDetailsForEditJob(
                true,
                data.jobId,
                isRespJob = true
            )
        } else {
            mViewModel.jobDetailsForEditJob(
                true,
                jobId = jobDashBoard?.jobId,
                isRespJob = true
            )
        }
    }

    private fun showMenu(view: View) {
        val viewGroup = view.findViewById<LinearLayout>(R.id.ll_popup)
        val layoutInflater =
            (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val layout: View =
            layoutInflater.inflate(R.layout.layout_pop_up_milestone_option, viewGroup)
        val popup = PopupWindow(view.context)
        popup.contentView = layout
        popup.setBackgroundDrawable(ColorDrawable())
        popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        val tvOptionMain = layout.findViewById<TextView>(R.id.tv_option_main)
        val tvOptionOne = layout.findViewById<TextView>(R.id.tv_option_one)
        val tvOptionTwo = layout.findViewById<TextView>(R.id.tv_option_two)
        tvOptionOne.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lounge, 0, 0, 0)
        tvOptionTwo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_cancel_filled,
            0,
            0,
            0
        )
        tvOptionMain.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.ic_edit_milestone,
            0,
            0,
            0
        )
//        if (isDisputed == true) {
//            tvOptionOne.visibility = View.GONE
//        }
        tvOptionMain.text = view.context.getString(R.string.edit_milestone)
        tvOptionOne.text = view.context.getString(R.string.lodge_dispute)
        tvOptionTwo.text = view.context.getString(R.string.cancel_job)
        tvOptionOne.setOnClickListener {
            popup.dismiss()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                startActivity(
                    Intent(this, LodgeDisputeBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, data?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, data?.jobName)
                )
            } else {
                startActivity(
                    Intent(this, LodgeDisputeBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, jobDashBoard?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, jobDashBoard?.jobName)
                )
            }
        }
        tvOptionTwo.setOnClickListener {
            popup.dismiss()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                startActivity(
                    Intent(this, CancelJobByBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, data?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, data?.jobName)
                )
            } else {
                startActivity(
                    Intent(this, CancelJobByBuilderActivity::class.java)
                        .putExtra(IntentConstants.JOB_ID, jobDashBoard?.jobId)
                        .putExtra(IntentConstants.JOB_NAME, jobDashBoard?.jobName)
                )
            }
        }
        tvOptionMain.setOnClickListener {
            popup.dismiss()
            if (intent.getSerializableExtra("data") is JobRecModel) {
                startActivity(
                    Intent(
                        this,
                        MilestoneEditListingActivity::class.java
                    ).putExtra("data", data)
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        MilestoneEditListingActivity::class.java
                    ).putExtra("data", jobDashBoard)
                )
            }

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


    private fun setData() {
        mBinding.llMain.visibility = View.GONE
        if (jobDashBoard != null && jobDashBoard?.jobId != null && jobDashBoard?.jobId != "") {
            mBinding.tvTitle.text = jobDashBoard?.tradeName
            mBinding.tvDetails.text = jobDashBoard?.jobName
            mBinding.tvTime.text = jobDashBoard?.timeLeft
            mBinding.tvMoney.text = jobDashBoard?.amount
            mBinding.tvPlace.text = jobDashBoard?.location
            if (jobDashBoard?.jobDescription != null && jobDashBoard?.jobDescription!!.length > 0)
                mBinding.tvDesc.text = jobDashBoard?.jobDescription
            else if (jobDashBoard?.details != null && jobDashBoard?.details!!.length > 0)
                mBinding.tvDesc.text = jobDashBoard?.details
            else
                mBinding.tvDesc.text = jobDashBoard?.jobDescription
            if (jobDashBoard != null && jobDashBoard!!.fromDate != null) {
                if (jobDashBoard?.fromDate == jobDashBoard?.toDate) {
                    mBinding.tvDays.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobDashBoard?.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobDashBoard?.fromDate
                    )
                } else if (jobDashBoard?.toDate == null || jobDashBoard?.toDate.equals("null") || jobDashBoard?.toDate.equals(
                        ""
                    )
                ) {
                    mBinding.tvDays.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobDashBoard?.fromDate
                    )
                    jobDashBoard?.toDate = ""
                } else {
                    if (jobDashBoard?.toDate!!.split("-")[0] == jobDashBoard?.fromDate?.split("-")!![0]) {
                        mBinding.tvDays.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            jobDashBoard?.fromDate
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            jobDashBoard?.toDate
                        )
                    } else {
                        mBinding.tvDays.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            jobDashBoard?.fromDate
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            jobDashBoard?.toDate
                        )
                    }
                }
            }

            if (!jobDashBoard!!.jobDescription.isNullOrEmpty()) {
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDescTitle.visibility = View.VISIBLE
            } else if (!jobDashBoard!!.details.isNullOrEmpty()) {
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDescTitle.visibility = View.VISIBLE
            } else {
                mBinding.tvDesc.visibility = View.GONE
                mBinding.tvDescTitle.visibility = View.GONE
            }

            if (jobDashBoard?.tradeSelectedUrl != null && jobDashBoard?.tradeSelectedUrl!!.isNotEmpty()) {
                Glide.with(mBinding.root.context).load(jobDashBoard?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            } else if (jobDashBoard?.jobData != null && jobDashBoard?.jobData?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(jobDashBoard?.jobData?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            } else {
                Glide.with(mBinding.root.context).load(jobDashBoard?.tradieImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            }
            mBinding.tvJobQuestionCount.text = "0 " + getString(R.string.questions_)
            mBinding.tvMilestoneCount.visibility = View.VISIBLE
            mBinding.progressBarHorMilestone.visibility = View.VISIBLE
            mBinding.tvMilestoneCount.text =
                jobDashBoard?.milestoneNumber.toString() + " of " + jobDashBoard?.totalMilestones
            if (jobDashBoard?.status != null && (jobDashBoard?.status == "PENDING" || jobDashBoard?.status == "AWAITING" || jobDashBoard?.status == "REQUEST ACCEPTED")) {
                mBinding.tvMilestoneStatus.visibility = View.GONE
            } else {
                mBinding.tvMilestoneStatus.visibility = View.VISIBLE
            }
            mBinding.tvStatus.visibility = View.GONE
            mBinding.tvDays.visibility = View.VISIBLE
            if (jobDashBoard?.status != null && jobDashBoard?.status!!.length > 0) {
                if (!(jobDashBoard?.status.equals("EXPIRED") || jobDashBoard?.status.equals("COMPLETED") ||
                            jobDashBoard?.status.equals("CANCELLED"))
                ) {
                    if (!(jobDashBoard?.status == "PENDING" || jobDashBoard?.status == "AWAITING" || jobDashBoard?.status == "REQUEST ACCEPTED")) {
                        mBinding.tvMilestoneStatus.visibility = View.VISIBLE
                    }
                    mBinding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_quick,
                        0,
                        0,
                        0
                    )
                    mBinding.tvStatus.visibility = View.GONE
                    mBinding.tvDays.visibility = View.VISIBLE
                } else {
                    mBinding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_calendar,
                        0,
                        0,
                        0
                    )
                    mBinding.tvDays.visibility = View.GONE
                    mBinding.tvStatus.visibility = View.VISIBLE
                    mBinding.tvStatus.text = jobDashBoard?.status
                    mBinding.tvMilestoneStatus.visibility = View.GONE
                }
            } else {
                mBinding.tvMilestoneStatus.visibility = View.GONE
            }
            mBinding.tvMilestoneStatus.text = jobDashBoard?.status
            try {
                mBinding.progressBarHorMilestone.progress =
                    (((jobDashBoard?.milestoneNumber?.toDouble()
                        ?.div(jobDashBoard?.totalMilestones?.toDouble()!!))?.times(100))?.toInt()!!)
            } catch (e: Exception) {
            }
        } else {
            mBinding.tvTitle.text = data.tradeName
            mBinding.tvDetails.text = data.jobName
            if (data.time != null && data.time?.length!! > 0)
                mBinding.tvTime.text = data.time
            else if (data.timeLeft != null && data.timeLeft?.length!! > 0)
                mBinding.tvTime.text = data?.timeLeft
            mBinding.tvMoney.text = data.amount
            if (data.locationName != null && data.locationName?.length!! > 0)
                mBinding.tvPlace.text = data.locationName
            else if (data.location_name != null && data.location_name?.length!! > 0)
                mBinding.tvPlace.text = data.location_name
            if (data?.jobDescription != null && data?.jobDescription!!.length > 0)
                mBinding.tvDesc.text = data?.jobDescription
            else if (data?.details != null && data?.details!!.length > 0)
                mBinding.tvDesc.text = data?.details
            else
                mBinding.tvDesc.text = data?.jobDescription

            if (data != null && data!!.fromDate != null) {
                if (data?.fromDate == data?.toDate) {
                    mBinding.tvDays.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        data?.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        data?.fromDate
                    )
                } else if (data?.toDate == null || data?.toDate.equals("null") || data?.toDate.equals(
                        ""
                    )
                ) {
                    mBinding.tvDays.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        data?.fromDate
                    )
                    data?.toDate = ""
                } else {
                    if (data?.toDate!!.split("-")[0] == data?.fromDate?.split("-")!![0]) {
                        mBinding.tvDays.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            data?.fromDate
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            data?.toDate
                        )
                    } else {
                        mBinding.tvDays.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            data?.fromDate
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            data?.toDate
                        )
                    }
                }
            }
            if (!data.jobDescription.isNullOrEmpty()) {
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDescTitle.visibility = View.VISIBLE
            } else if (!data.details.isNullOrEmpty()) {
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDescTitle.visibility = View.VISIBLE
            } else {
                mBinding.tvDesc.visibility = View.GONE
                mBinding.tvDescTitle.visibility = View.GONE
            }
            if (data.tradeSelectedUrl != null && data.tradeSelectedUrl!!.isNotEmpty()) {
                Glide.with(mBinding.root.context).load(data.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_blue_circle)
                    .into(mBinding.ivUserProfile)
            } else {
                Glide.with(mBinding.root.context).load(data.userImage)
                    .placeholder(R.drawable.bg_blue_circle)
                    .into(mBinding.ivUserProfile)
            }
            if (intent.hasExtra("isBuilder") && intent.getBooleanExtra("isBuilder", false)) {
                mBinding.tvMilestoneCount.visibility = View.VISIBLE
                mBinding.progressBarHorMilestone.visibility = View.VISIBLE
                mBinding.tvMilestoneCount.text =
                    data?.milestoneNumber.toString() + " of " + data?.totalMilestones
                if (data?.status != null && (data?.status == "PENDING" || data?.status == "AWAITING" || data?.status == "REQUEST ACCEPTED")) {
                    mBinding.tvMilestoneStatus.visibility = View.GONE
                } else {
                    mBinding.tvMilestoneStatus.visibility = View.VISIBLE
                }
                mBinding.tvStatus.visibility = View.GONE
                mBinding.tvDays.visibility = View.VISIBLE
                if (data?.status != null && data?.status!!.length > 0) {
                    if (!(data?.status.equals("EXPIRED") || data?.status.equals("COMPLETED") ||
                                data?.status.equals("CANCELLED"))
                    ) {
                        if (!(data?.status == "PENDING" || data?.status == "AWAITING" || data?.status == "REQUEST ACCEPTED")) {
                            mBinding.tvMilestoneStatus.visibility = View.VISIBLE
                        }
                        mBinding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_quick,
                            0,
                            0,
                            0
                        )
                        mBinding.tvStatus.visibility = View.GONE
                        mBinding.tvDays.visibility = View.VISIBLE
                    } else {
                        mBinding.tvTime.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.ic_calendar,
                            0,
                            0,
                            0
                        )
                        mBinding.tvStatus.visibility = View.VISIBLE
                        mBinding.tvDays.visibility = View.GONE
                        mBinding.tvStatus.text = data.status
                        mBinding.tvMilestoneStatus.visibility = View.GONE
                    }
                } else {
                    mBinding.tvMilestoneStatus.visibility = View.GONE
                }
                mBinding.tvMilestoneStatus.text = data?.status
            }
            if (data.questionsCount != null && (data.questionsCount == "0" || data.questionsCount == "1" || data.questionsCount == "0.0" || data.questionsCount == "1.0"))
                mBinding.tvJobQuestionCount.text =
                    data.questionsCount.toString().toDouble().toInt()
                        .toString() + " " + getString(R.string.question_)
            else if (data.questionsCount != null)
                mBinding.tvJobQuestionCount.text =
                    data.questionsCount.toString().toDouble().toInt()
                        .toString() + " " + getString(R.string.questions_)
            if (data.postedBy != null && PreferenceManager.getString(PreferenceManager.USER_ID)
                    .equals(data.postedBy.builderId)
            ) {
                if (data.quoteJob == false)
                    mBinding.llJobApplied.visibility = View.GONE
                else
                    mBinding.tvApply.text = getString(R.string.quote_sent)
                mBinding.userIvMsg.visibility = View.GONE
                mBinding.userIvRedirection.visibility = View.GONE
            }
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
        mHomeAdapter = JobsMoreSelectionAdapter(this, jobList)
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
        milestoneAdapter = milestoneData?.let { RowMilestoneAdapter(it) }!!
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
        mBinding.jobDetailsIvBack.setOnClickListener { onBackPressed() }
        mBinding.ivEdit.setOnClickListener {
            if (isOpen) {
                showJobMenu(it)
            } else {
                if (isExpired == true) {
                    if (intent.getSerializableExtra("data") is JobRecModel) {
                        mViewModel.jobDetailsFromBuilderRepublish(
                            true,
                            data.jobId,
                            data.tradeId,
                            data.specializationId
                        )
                    } else {
                        mViewModel.jobDetailsFromBuilderRepublish(
                            true,
                            jobDashBoard?.jobId,
                            jobDashBoard?.tradeId,
                            jobDashBoard?.specializationId
                        )
                    }
                } else {
                    showMenu(it)
                }
            }
        }

        mBinding.llQuoteJob.setOnClickListener {
            if (intent.getSerializableExtra("data") is JobRecModel) {
                startActivity(Intent(this, QuoteListActivity::class.java).putExtra("data", data))
            } else {
                startActivity(
                    Intent(this, QuoteListActivity::class.java).putExtra(
                        "data",
                        jobDashBoard
                    )
                )
            }
        }
        mBinding.srLayout.setOnRefreshListener {
            if (intent.getSerializableExtra("data") is JobRecModel) {
                if (intent.hasExtra("isBuilder") && intent.getBooleanExtra("isBuilder", false)) {
                    mViewModel.jobDetailsFromBuilder(
                        false,
                        data?.jobId,
                        data?.tradeId,
                        data?.specializationId
                    )
                } else {
                    mViewModel.getJobsDetails(
                        false,
                        data.jobId,
                        data.tradeId,
                        data.specializationId
                    )
                }
            } else {
                mViewModel.jobDetailsFromBuilder(
                    false,
                    jobDashBoard?.jobId,
                    jobDashBoard?.tradeId,
                    jobDashBoard?.specializationId
                )
            }
        }
        mBinding.ivSaveJob.setOnClickListener {
            isRefresh = true
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
            try {
                if (mViewModel.mJsonResponseModel.quoteJob) {
                    startActivity(Intent(this, AddQuoteActivity::class.java).putExtra("data", data))
                } else if (mBinding.tvApply.text.toString() == getString(R.string.apply)) {
                    if (data.tradeId.equals(PreferenceManager.getString(PreferenceManager.TRADE_ID))) {
                        val params = HashMap<String, Any>()
                        params[ApiParams.JOB_ID] = data.jobId!!
                        params[ApiParams.TRADE_ID] = data.tradeId!!

                        if (!data.specializationId.isNullOrEmpty())   params[ApiParams.SPECIALIZATION_ID] = data.specializationId!!
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

                        val dialogBtn_okay: TextView =
                            dialog.findViewById(R.id.tvAccept) as TextView
                        dialogBtn_okay.text = getString(R.string.yes)
                        dialogBtn_okay.setOnClickListener {
                            dialog.dismiss()
                            val params = HashMap<String, Any>()
                            params[ApiParams.JOB_ID] = data.jobId!!
                            params[ApiParams.TRADE_ID] = data.tradeId!!
                            if (!data.specializationId.isNullOrEmpty())   params[ApiParams.SPECIALIZATION_ID] = data.specializationId!!
                            if (builderId != null && builderId.isNotEmpty())
                                params[ApiParams.BUILDER_ID] = builderId
                            mViewModel.applyJob(params)
                        }

                        val dialogBtn_cancel: TextView =
                            dialog.findViewById(R.id.tvReject) as TextView
                        dialogBtn_cancel.text = getString(R.string.no)
                        dialogBtn_cancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }

                }
            } catch (e: Exception) {

            }
        }
        mBinding.tvCancelAccept.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_popup)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
            title.text = getString(R.string.cancel)

            val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
            msg.text = getString(R.string.are_you_sure_you_want_to_end_job)

            val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
            dialogBtn_okay.text = getString(R.string.ok)
            dialogBtn_okay.setOnClickListener {
                dialog.dismiss()
                val mObject = JsonObject()
                if (intent.getSerializableExtra("data") is JobRecModel) {
                    mObject.addProperty("jobId", data.jobId)
                } else {
                    mObject.addProperty("jobId", jobDashBoard?.jobId)
                }
                mObject.addProperty("status", 1)
                mViewModel.replyCancellationRequestBuilder(mObject)
            }

            val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
            dialogBtn_cancel.text = getString(R.string.cancel)
            dialogBtn_cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

        }
        mBinding.tvCancelReject.setOnClickListener {
            DialogUtils.setCustomAlert(this, object : DialogCallback {
                override fun onPositiveClick(reason: String) {
                    val mObject = JsonObject()
                    if (intent.getSerializableExtra("data") is JobRecModel) {
                        mObject.addProperty("jobId", data.jobId)
                    } else {
                        mObject.addProperty("jobId", jobDashBoard?.jobId)
                    }
                    mObject.addProperty("status", 2)
                    mObject.addProperty("note", reason)
                    mViewModel.replyCancellationRequestBuilder(mObject)
                }

                override fun onNegativeClick() {
                }

            })
        }

        mBinding.userIvRedirection.setOnClickListener {
            startActivity(
                Intent(this, BuilderProfileActivity::class.java)
                    .putExtra("data", data)
                    .putExtra("builderId", builderId)
            )
        }
        mBinding.userIvMsg.setOnClickListener {
            getLastMessages()
        }
        mBinding.llQuoteViewJob.setOnClickListener {
            if (intent.getSerializableExtra("data") is JobRecModel) {
                data?.jobId?.let { it1 -> getParamData(it1) }?.let { it2 ->
                    mViewModelQuote.getQuoteList(
                        it2, true
                    )
                }
            } else {
                jobDashBoard?.jobId?.let { it1 -> getParamData(it1) }?.let { it2 ->
                    mViewModelQuote.getQuoteList(
                        it2, true
                    )
                }
            }
        }
    }

    private fun getParamData(jobId: String): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        params.put("jobId", jobId)
        params.put("page", 1)
        params.put("sortBy", 1)
        return params
    }

    private fun getLastMessages() {
        val inBoxMessage = ChatMessageBean()
        val loginUserId = PreferenceManager.getString(PreferenceManager.USER_ID)

        if (intent.getSerializableExtra("data") is JobRecModel) {
            inBoxMessage.messageRoomId =
                data?.jobId + "_" + data?.tradieId + "-" + loginUserId
        } else {
            inBoxMessage.messageRoomId =
                jobDashBoard?.jobId + "_" + jobDashBoard?.tradieId + "_" + loginUserId
        }
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
        if (intent.getSerializableExtra("data") is JobRecModel) {
            chatModel.jobId = data?.jobId
            chatModel.jobName = data?.jobName
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

                                }
                                startActivity(
                                    Intent(
                                        mBinding.rvJobMilestone.context,
                                        ChatBuilderActivity::class.java
                                    ).putExtra("data", chatModel)
                                        .putExtra("senderName", mBinding.tvJobUserTitle.text)
                                )
                            } catch (e: Exception) {
                            }

                        }
                    })
            }
        } else {
            chatModel.jobId = jobDashBoard?.jobId
            chatModel.jobName = jobDashBoard?.jobName
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
                                    startActivity(
                                        Intent(
                                            mBinding.rvJobMilestone.context,
                                            ChatBuilderActivity::class.java
                                        ).putExtra("data", chatModel)
                                            .putExtra("senderName", mBinding.tvJobUserTitle.text)
                                    )
                                }
                            } catch (e: Exception) {
                            }
                        }
                    })
            }
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

        setBaseViewModel(mViewModelQuote)
        mViewModelQuote.getResponseObserver().observe(this, this)

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.JOB_DETAILS -> {
                mBinding.srLayout.isRefreshing = false
                showToastShort(exception.message)
                onBackPressed()
            }
            ApiCodes.GET_QUOTE -> {
                showToastShort(exception.message)
            }
            ApiCodes.OPEN_JOB_DETAILS -> {
                showToastShort(exception.message)
            }
            ApiCodes.REMOVE_JOB_DETAILS -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.JOB_REPUBLISH_EDIT_JOB -> {

            }

            ApiCodes.JOB_DETAILS -> {
                mBinding.llMain.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mBinding.llJobSpec.visibility = View.VISIBLE
                mBinding.llMilestoneData.visibility = View.VISIBLE
                mViewModel.mJsonResponseModel.let {
                    data = it
                    Log.i("builderId_job_details", ": ${data.postedBy.builderId}")
                    if (it.specializationData.size > 0) {
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
                    if (mBinding.tvPlace.text.toString().isEmpty())
                        mBinding.tvPlace.text = it.locationName

                    if (mBinding.tvTime.text.toString()
                            .isEmpty() || mBinding.tvTime.text.toString() == "0"
                    ) {
                        if (it.fromDate != null) {
                            if (it?.fromDate == it?.toDate) {
                                mBinding.tvTime.text = DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_14,
                                    it?.fromDate
                                ) + " - " + DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_14,
                                    it?.fromDate
                                )
                            } else if (it?.toDate == null || it?.toDate.equals("null") || it?.toDate.equals(
                                    ""
                                )
                            ) {
                                mBinding.tvTime.text = DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_14,
                                    it?.fromDate
                                )
                                it?.toDate = ""
                            } else {
                                if (it?.toDate!!.split("-")[0] == it?.fromDate?.split("-")!![0]) {
                                    mBinding.tvTime.text = DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_14,
                                        it?.fromDate
                                    ) + " - " + DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_14,
                                        it?.toDate
                                    )
                                } else {
                                    mBinding.tvTime.text = DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_15,
                                        it?.fromDate
                                    ) + " - " + DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_15,
                                        it?.toDate
                                    )
                                }
                            }
                        }
                    }
                    if (isFullDate) {
                        if (it.duration != null && it.duration?.length!! > 0)
                            mBinding.tvTime.text = it.duration
                        else if (it.durations != null && it.durations?.length!! > 0)
                            mBinding.tvTime.text = it.durations
                        if (it.fromDate != null) {
                            if (it?.fromDate == it?.toDate) {
                                mBinding.tvDays.text = DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_14,
                                    it?.fromDate
                                ) + " - " + DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_14,
                                    it?.fromDate
                                )
                            } else if (it?.toDate == null || it?.toDate.equals("null") || it?.toDate.equals(
                                    ""
                                )
                            ) {
                                mBinding.tvDays.text = DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_14,
                                    it?.fromDate
                                )
                                it?.toDate = ""
                            } else {
                                if (it?.toDate!!.split("-")[0] == it?.fromDate?.split("-")!![0]) {
                                    mBinding.tvDays.text = DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_14,
                                        it?.fromDate
                                    ) + " - " + DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_14,
                                        it?.toDate
                                    )
                                } else {
                                    mBinding.tvDays.text = DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_15,
                                        it?.fromDate
                                    ) + " - " + DateUtils.changeDateFormat(
                                        DateUtils.DATE_FORMATE_8,
                                        DateUtils.DATE_FORMATE_15,
                                        it?.toDate
                                    )
                                }
                            }
                        }
                    }
                    if (mBinding.tvDays.text.length == 0 && it.duration != null && it.duration!!.length > 0) {
                        mBinding.tvDays.text = it.duration
                    }

                    if (it.isCancelJobRequest) {
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
                            if (!it.reasonNoteForCancelJobRequest.isEmpty())
                                mBinding.tvCancelNote.text = it.reasonNoteForCancelJobRequest
                            else if (!it.rejectReasonNoteForCancelJobRequest.isEmpty())
                                mBinding.tvCancelNote.text = it.rejectReasonNoteForCancelJobRequest
                            else
                                mBinding.tvCancelNote.text = ""

                        }

                        mBinding.tvCancelReason.visibility = View.VISIBLE

                        if (it.reasonForCancelJobRequest >= 0)
                            mBinding.tvCancelReason.text =
                                cancelReasonList.get(it.reasonForCancelJobRequest - 1).reason
                    } else if (!it.rejectReasonNoteForCancelJobRequest.isNullOrEmpty() && !it?.jobStatus.equals(
                            "CANCELLED",
                            true
                        )
                    ) {
                        if (!it.status.equals("COMPLETED"))
                            mBinding.cvCancellationReason.visibility = View.VISIBLE
                        mBinding.tvCancelTitle.text =
                            getString(R.string.job_cancellation_rejected_reason)
                        mBinding.tvCancelReason.visibility = View.GONE
                        mBinding.linCancelReq.visibility = View.GONE
                        mBinding.tvCancelNote.text = it.rejectReasonNoteForCancelJobRequest

                    } else {
                        mBinding.cvCancellationReason.visibility = View.GONE
                        if (it.changeRequestDeclineReason != null && it.changeRequestDeclineReason!!.isNotEmpty()) {
                            mBinding.cvCancellationReason.visibility = View.VISIBLE
                            mBinding.tvCancelTitle.text =
                                getString(R.string.change_request_decline_reason)
                            mBinding.linCancelReq.visibility = View.GONE
                            mBinding.tvCancelReason.visibility = View.GONE
                            mBinding.tvCancelNote.text = it.changeRequestDeclineReason
                        } else {
                            mBinding.cvCancellationReason.visibility = View.GONE
                        }
                    }
                    if (!it.jobDescription.isNullOrEmpty()) {
                        mBinding.tvDesc.visibility = View.VISIBLE
                        mBinding.tvDescTitle.visibility = View.VISIBLE
                        mBinding.tvDesc.text = it.jobDescription
                    } else if (!it.details.isNullOrEmpty()) {
                        mBinding.tvDesc.visibility = View.VISIBLE
                        mBinding.tvDescTitle.visibility = View.VISIBLE
                        mBinding.tvDesc.text = it.details
                    } else {
                        mBinding.tvDesc.visibility = View.GONE
                        mBinding.tvDescTitle.visibility = View.GONE
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
                        if (it.applyButtonDisplay) {
                            mBinding.tvApply.text = getString(R.string.quotes)
                        } else {
                            mBinding.tvApply.text = getString(R.string.quotes_sent)
                        }
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

                    if (PreferenceManager.getString(PreferenceManager.USER_ID)
                            .equals(it.postedBy.builderId)
                    ) {
                        if (it.quoteJob == false)
                            mBinding.llJobApplied.visibility = View.GONE
                        else
                            mBinding.tvApply.text = getString(R.string.quote_sent)
                        mBinding.userIvMsg.visibility = View.GONE
                        mBinding.userIvRedirection.visibility = View.GONE
                    }
                    if (jobDashBoard != null && jobDashBoard?.jobId != null && jobDashBoard?.jobId != "") {
                        mBinding.tvMilestoneCount.visibility = View.VISIBLE
                        mBinding.progressBarHorMilestone.visibility = View.VISIBLE
                        mBinding.tvMilestoneCount.text =
                            it?.milestoneNumber.toString() + " of " + it?.totalMilestones
                        if (it.status != null && (it?.status == "PENDING" || it?.status == "AWAITING" || it?.status == "REQUEST ACCEPTED")) {
                            mBinding.tvMilestoneStatus.visibility = View.GONE
                        } else {
                            mBinding.tvMilestoneStatus.visibility = View.VISIBLE
                        }
                        if (it?.status != null && it?.status!!.length > 0) {
                            if (!(it?.status.equals("EXPIRED") || it?.status.equals("COMPLETED") ||
                                        it?.status.equals("CANCELLED"))
                            ) {
                                if (!(it?.status == "PENDING" || it?.status == "AWAITING" || it?.status == "REQUEST ACCEPTED")) {
                                    mBinding.tvMilestoneStatus.visibility = View.VISIBLE
                                }
                            } else {
                                mBinding.tvMilestoneStatus.visibility = View.GONE
                            }
                        } else {
                            mBinding.tvMilestoneStatus.visibility = View.GONE
                        }
                        mBinding.tvMilestoneStatus.text = it?.status
                        try {
                            mBinding.progressBarHorMilestone.progress =
                                (((it?.milestoneNumber?.toDouble()
                                    ?.div(it?.totalMilestones?.toDouble()!!))?.times(100))?.toInt()!!)
                        } catch (e: Exception) {
                        }
                    }
                    if (intent.hasExtra("isBuilder") && intent.getBooleanExtra(
                            "isBuilder",
                            false
                        )
                    ) {
                        mBinding.tvMilestoneCount.visibility = View.VISIBLE
                        mBinding.progressBarHorMilestone.visibility = View.VISIBLE
                        mBinding.tvMilestoneCount.text =
                            it?.milestoneNumber.toString() + " of " + it?.totalMilestones
                        if (it.status != null && (it?.status == "PENDING" || it?.status == "AWAITING" || it?.status == "REQUEST ACCEPTED")) {
                            mBinding.tvMilestoneStatus.visibility = View.GONE
                        } else {
                            mBinding.tvMilestoneStatus.visibility = View.VISIBLE
                        }
                        if (it?.status != null && it?.status!!.length > 0) {
                            if (!(it?.status.equals("EXPIRED") || it?.status.equals("COMPLETED") ||
                                        it?.status.equals("CANCELLED"))
                            ) {
                                if (!(it?.status == "PENDING" || it?.status == "AWAITING" || it?.status == "REQUEST ACCEPTED")) {
                                    mBinding.tvMilestoneStatus.visibility = View.VISIBLE
                                }
                            } else {
                                mBinding.tvMilestoneStatus.visibility = View.GONE
                            }
                        } else {
                            mBinding.tvMilestoneStatus.visibility = View.GONE
                        }
                        mBinding.tvMilestoneStatus.text = it?.status
                        try {
                            mBinding.progressBarHorMilestone.progress =
                                (((it?.milestoneNumber?.toDouble()
                                    ?.div(it?.totalMilestones?.toDouble()!!))?.times(100))?.toInt()!!)
                        } catch (e: Exception) {
                        }
                        mBinding.llQuestion.setOnClickListener { p0 ->
                            startActivityForResult(
                                Intent(
                                    p0?.context,
                                    QuestionListActivity::class.java
                                ).putExtra("data", it).putExtra("status", status), 1310
                            )
                        }
                    }
                    if (!isPast) {
                        if (it.quoteJob != null && it.quoteJob == true && (it.status.equals("AWAITING") || it.status.equals(
                                "PENDING"
                            ) || it.status.equals("REQUEST ACCEPTED"))
                        ) {
                            mBinding.llQuoteJob.visibility = View.VISIBLE
                            mBinding.llQuoteViewJob.visibility = View.GONE
                            mBinding.tvQuoteJobCount.text =
                                it.quoteCount.toString() + " " + getString(R.string.quotes)
                        } else if (it.quoteJob != null && it.quoteJob == true && it.quoteCount > 0) {
                            mBinding.llQuoteJob.visibility = View.GONE
                            mBinding.llQuoteViewJob.visibility = View.VISIBLE
                        } else {
                            mBinding.llQuoteViewJob.visibility = View.GONE
                            mBinding.llQuoteJob.visibility = View.GONE
                        }
                    } else {
                        mBinding.llQuoteViewJob.visibility = View.GONE
                        mBinding.llQuoteJob.visibility = View.GONE
                    }
                }
                if (isChat) {
                    mBinding.userIvMsg.visibility = View.VISIBLE
                } else {
                    mBinding.userIvMsg.visibility = View.GONE
                }

                if (intent.hasExtra("isQuestionList")) {
                    mBinding.llQuestion.performClick()
                }
            }
            ApiCodes.GET_QUOTE -> {
                mViewModelQuote.getQuoteList().let {
                    if (it.size > 0 && it.get(0).quote_item != null) {
                        if (intent.getSerializableExtra("data") is JobRecModel) {
                            startActivity(
                                Intent(
                                    mBinding.root.context,
                                    QuoteDetailsActivity::class.java
                                ).putExtra("data", data)
                                    .putExtra("mainData", it.get(0))
                                    .putExtra("isBuilder", true).putExtra("isAction", false)
                            )
                        } else {
                            startActivity(
                                Intent(
                                    mBinding.root.context,
                                    QuoteDetailsActivity::class.java
                                ).putExtra("data", jobDashBoard)
                                    .putExtra("mainData", it.get(0))
                                    .putExtra("isBuilder", true).putExtra("isAction", false)
                            )
                        }
                    }
                }
            }
            ApiCodes.JOB_REPULISH_DETAILS -> {
                republishedJobMoEngage()   // republish job mo engage
                rePublishJobMixPanel()
                mViewModel.mJsonResponseRepublishModel.let {
                    startActivity(
                        Intent(this, PostNewJobActivity::class.java)
                            .putExtra("data", mViewModel.mJsonResponseRepublishModel)
                    )
                }
            }

            ApiCodes.OPEN_JOB_DETAILS -> {
                mViewModel.mJsonResponseRepublishModel.let {
                    it.isEdit = true
                    startActivity(
                        Intent(this, PostNewJobActivity::class.java)
                            .putExtra("data", it)
                    )
                }
            }
            ApiCodes.APPLY -> {
                jobAppliedMoEngage()
                jobAppliedMixPanel()
                mBinding.tvApply.text = getString(R.string.applied)
                startActivity(Intent(this, JobAppliedActivity::class.java))
            }
            ApiCodes.CANCELLATION_REPLY -> {
                if (intent.getSerializableExtra("data") is JobDashboardModel) {
                    if (jobDashBoard?.quoteJob == false) {
                        startActivity(
                            Intent(
                                this,
                                HomeBuilderActivity::class.java
                            ).putExtra("pos", 1)
                        )
                        ActivityCompat.finishAffinity(this)
                    } else {
                        startActivity(
                            Intent(
                                this,
                                QuoteJobCancelledActivity::class.java
                            ).putExtra("jobID", jobDashBoard?.jobId)
                        )
                        ActivityCompat.finishAffinity(this)

                    }
                } else {
                    if (data.quoteJob == false) {
                        startActivity(
                            Intent(
                                this,
                                HomeBuilderActivity::class.java
                            ).putExtra("pos", 1)
                        )
                        ActivityCompat.finishAffinity(this)
                    } else {
                        startActivity(
                            Intent(
                                this,
                                QuoteJobCancelledActivity::class.java
                            ).putExtra("jobID", data.jobId)
                        )
                        ActivityCompat.finishAffinity(this)

                    }
                }

            }
            ApiCodes.REMOVE_JOB_DETAILS -> {
                setResult(Activity.RESULT_OK)
                finish()
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

    private fun jobAppliedMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_APPLIED_FOR_JOB, props
        )
    }

    private fun republishedJobMoEngage() {

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_REPUBLISHED_JOB,
            signUpProperty
        )
    }

    private fun rePublishJobMixPanel() {
        val mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mix_panel_token))
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_REPUBLISHED_JOB, props)
    }

    override fun onClick(p0: View?) {
    }

    override fun onSpecCLick(position: Int) {
    }

    override fun onJobClick(position: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, datas: Intent?) {
        super.onActivityResult(requestCode, resultCode, datas)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (intent.getSerializableExtra("data") is JobRecModel) {
                if (intent.hasExtra("isBuilder") && intent.getBooleanExtra("isBuilder", false)) {
                    mViewModel.jobDetailsFromBuilder(
                        false,
                        data?.jobId,
                        data?.tradeId,
                        data?.specializationId
                    )
                } else {
                    mViewModel.getJobsDetails(
                        false,
                        data.jobId,
                        data.tradeId,
                        data.specializationId
                    )
                }
            } else {
                mViewModel.jobDetailsFromBuilder(
                    false,
                    jobDashBoard?.jobId,
                    jobDashBoard?.tradeId,
                    jobDashBoard?.specializationId
                )
            }
        }
    }
}