package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.tradesmodel.TradeHome
import com.app.core.model.tradie.QuoteItem
import com.app.core.model.tradie.QuoteTradie
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.QuoteItemTradieAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityQuoteDetailsBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.model.registration.TokenModel
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class QuoteDetailsActivity : BaseActivity(),
    View.OnClickListener {
    private var isAction: Boolean = false
    private lateinit var mainData: QuoteTradie
    private var data: JobRecModel? = null
    private var jobDashBoard: JobDashboardModel? = null
    private lateinit var mBinding: ActivityQuoteDetailsBinding
    private lateinit var mAdapter: QuoteItemTradieAdapter
    private val list by lazy { ArrayList<QuoteItem>() }
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quote_details)
        getIntentData()
        initRecyclerView()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        setData()
        setObservers()
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun setData() {
        mBinding.tvMainTitle.text = mainData.builderName
        mBinding.tvJobUserDetails.text =
            mainData.ratings.toString() + ", " + mainData.reviews
        if (mainData.builderImage != null) {
            Glide.with(mBinding.root.context).load(mainData.builderImage)
                .placeholder(R.drawable.placeholder_profile)
                .into(mBinding.ivMainUserProfile)
        }
        mBinding.tvQuoteTotalAmount.text =
            getString(R.string.total) + ": $" + mainData.totalQuoteAmount
        if (data != null) {
            mBinding.tvTitle.text = data?.tradeName
            mBinding.tvDetails.text = data?.jobName
            if (data?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(data?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            }
            if (data?.time != null && data?.time?.length!! > 0)
                mBinding.tvTime.text = data?.time
            else if (data?.timeLeft != null && data?.timeLeft?.length!! > 0)
                mBinding.tvTime.text = data?.timeLeft
            mBinding.tvMoney.text = data?.amount
            if (data?.locationName != null && data?.locationName?.length!! > 0)
                mBinding.tvPlace.text = data?.locationName
            else if (data?.location_name != null && data?.location_name?.length!! > 0)
                mBinding.tvPlace.text = data?.location_name
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
            mBinding.tvStatus.visibility = View.GONE
            mBinding.tvDays.visibility = View.VISIBLE
            if (data?.status != null && data?.status!!.length > 0) {
                if (!(data?.status.equals("EXPIRED") || data?.status.equals("COMPLETED") ||
                            data?.status.equals("CANCELLED"))
                ) {

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
                    mBinding.tvTime.text = mBinding.tvDays.text
                    mBinding.tvDays.visibility = View.GONE
                    mBinding.tvStatus.visibility = View.VISIBLE
                    mBinding.tvStatus.text = data?.status
                }
            }
            if (intent.hasExtra("fromHome") && mBinding.tvTime.text.isEmpty()) {
                mBinding.tvTime.text = data?.duration
                mBinding.tvMoney.text = getString(R.string.for_quoting)
            }
        } else {
            mBinding.tvTitle.text = jobDashBoard?.tradeName
            mBinding.tvDetails.text = jobDashBoard?.jobName
            if (jobDashBoard?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(jobDashBoard?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            } else if (jobDashBoard?.jobData != null && jobDashBoard?.jobData?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(jobDashBoard?.jobData?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            }
            mBinding.tvTime.text = jobDashBoard?.timeLeft
            mBinding.tvMoney.text = jobDashBoard?.amount
            if (jobDashBoard?.locationName != null && jobDashBoard?.locationName?.length!! > 0)
                mBinding.tvPlace.text = jobDashBoard?.locationName
            else if (jobDashBoard?.location_name != null && jobDashBoard?.location_name?.length!! > 0)
                mBinding.tvPlace.text = jobDashBoard?.location_name
            else
                mBinding.tvPlace.text = jobDashBoard?.location

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
                    ) + " - " + DateUtils.changeDateFormat(
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
            mBinding.tvStatus.visibility = View.GONE
            mBinding.tvDays.visibility = View.VISIBLE
            if (jobDashBoard?.status != null && jobDashBoard?.status!!.length > 0) {
                if (!(jobDashBoard?.status.equals("EXPIRED") || jobDashBoard?.status.equals("COMPLETED") ||
                            jobDashBoard?.status.equals("CANCELLED"))
                ) {

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
                    mBinding.tvTime.text = mBinding.tvDays.text
                    mBinding.tvDays.visibility = View.GONE
                    mBinding.tvStatus.visibility = View.VISIBLE
                    mBinding.tvStatus.text = jobDashBoard?.status
                }
            }
            if (intent.hasExtra("fromHome") && mBinding.tvTime.text.isEmpty()) {
                mBinding.tvTime.text = jobDashBoard?.timeLeft
                mBinding.tvMoney.text = getString(R.string.for_quoting)
            }
        }
        ViewCompat.setNestedScrollingEnabled(mBinding.rvQuotes, false)
    }


    private fun getIntentData() {
        isAction = intent.getBooleanExtra("isAction", false)
        if (intent.getSerializableExtra("data") is JobDashboardModel) {
            jobDashBoard = intent.getSerializableExtra("data") as JobDashboardModel
        } else {
            data = intent.getSerializableExtra("data") as JobRecModel
        }
        mainData = intent.getSerializableExtra("mainData") as QuoteTradie
    }

    private fun setUpListeners() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.ivGo.setOnClickListener {
            if (intent.getSerializableExtra("data") is JobDashboardModel) {
                startActivityForResult(
                    Intent(
                        this,
                        TradieProfileActivity::class.java
                    ).putExtra("data", jobDashBoard)
                        .putExtra("isBuilder", true), 1310
                )
            } else {
                startActivityForResult(
                    Intent(
                        this,
                        TradieProfileActivity::class.java
                    ).putExtra("data", data)
                        .putExtra("isBuilder", true), 1310
                )
            }
        }
        mBinding.tvAcceptQuote.setOnClickListener {
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
        }
        mBinding.tvDeclineQuote.setOnClickListener {
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
        }
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ACCEPT_DECLINE_REQUEST -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.ACCEPT_DECLINE_REQUEST -> {

                mBinding.tvAcceptQuote.visibility = View.GONE
                mBinding.tvDeclineQuote.visibility = View.GONE
                mViewModel.status.let {
                    if (it == 1) {
                        acceptQuoteMoEngage()   //MO engage accept quote
                        acceptQuotenMixPanel()
                        startActivity(Intent(this, QuoteAcceptedTradieActivity::class.java))
                    } else {
                        declineQuoteMoEngage()
                        declineQuotenMixPanel()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun acceptQuoteMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_ACCEPT_QUOTE,
            signUpProperty
        )
    }

    private fun acceptQuotenMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_ACCEPT_QUOTE, props)
    }


    private fun declineQuoteMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_CANCEL_QUOTE_JOB,
            signUpProperty
        )
    }

    private fun declineQuotenMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_CANCEL_QUOTE_JOB, props)
    }

    private fun initRecyclerView() {
        mBinding.tvCancelled.visibility = View.GONE
        if (!isAction) {
            mBinding.tvAcceptQuote.visibility = View.GONE
            mBinding.tvDeclineQuote.visibility = View.GONE
        } else if (mainData.status == "CANCELLED") {
            mBinding.tvCancelled.visibility = View.VISIBLE
            mBinding.tvAcceptQuote.visibility = View.GONE
            mBinding.tvDeclineQuote.visibility = View.GONE
        } else if (mainData.job_status != null && mainData.job_status != "1") {
            mBinding.tvAcceptQuote.visibility = View.GONE
            mBinding.tvDeclineQuote.visibility = View.GONE
        }
        mainData.quote_item?.let { list.addAll(it) }
        mAdapter = QuoteItemTradieAdapter(list, false, this)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvQuotes.layoutManager = layoutRecManager
        mBinding.rvQuotes.adapter = mAdapter
    }


    private fun hideShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
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

    override fun onClick(p0: View?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
        }
    }
}
