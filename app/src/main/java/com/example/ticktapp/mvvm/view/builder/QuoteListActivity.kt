package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.tradie.QuoteTradie
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.QuoteTradieAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityQuoteListBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.mvvm.viewmodel.NewQuoteListRequestViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class QuoteListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener,
    View.OnClickListener {
    private var data: JobRecModel? = null
    private var jobDashBoard: JobDashboardModel? = null
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityQuoteListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(NewQuoteListRequestViewModel::class.java) }
    private lateinit var mAdapter: QuoteTradieAdapter
    private val list by lazy { ArrayList<QuoteTradie>() }
    private var pageNumber = 1
    private var sortBy: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quote_list)
        initRecyclerView()
        setObservers()
        viewQuoteMoEngage()
        viewQuoteMixPanel()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setData()
        mViewModel.getQuoteList(getParamData(), true)
    }

    private fun viewQuoteMixPanel() {
        var isBuilder = PreferenceManager.getString(PreferenceManager.USER_TYPE) == "2"

        if (isBuilder) {
            val mixpanel = MixpanelAPI.getInstance(
                this,
                getString(R.string.mix_panel_token))
            val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

            val props = JSONObject()

            props.put(MoEngageConstants.TIME_STAMP, timeStamp)

            mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_VIEW_QUOTE, props)

        }
    }

    private fun viewQuoteMoEngage() {
        var isBuilder = PreferenceManager.getString(PreferenceManager.USER_TYPE) == "2"

        if (isBuilder) {

            val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

            val signUpProperty = Properties()
            signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

            MoEngageUtils.sendEvent(this, MoEngageConstants.MOENGAGE_EVENT_VIEW_QUOTE, signUpProperty)
        }
    }

    private fun setData() {
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
    }

    private fun getParamData(): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        if (data != null)
            params.put("jobId", data?.jobId.toString())
        else
            params.put("jobId", jobDashBoard?.jobId.toString())

        params.put("page", pageNumber)
        params.put("sortBy", sortBy)
        val location = HashMap<String, Any>()
        return params
    }

    private fun getIntentData() {
        if (intent.getSerializableExtra("data") is JobDashboardModel) {
            jobDashBoard = intent.getSerializableExtra("data") as JobDashboardModel
        } else {
            data = intent.getSerializableExtra("data") as JobRecModel
        }
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.llTimePicker.setOnClickListener {
            showPopup(it)
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun showPopup(view: View) {
        mBinding.ivDropDown.animate().rotation(180f).start();
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.sort_by_quote_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.tv_lowest_quote -> {
                    mBinding.tvTitleSortBy.text = getString(R.string.lowest_quote)
                    pageNumber = 1
                    sortBy = 1
                    mViewModel.getQuoteList(getParamData(), true)

                }
                R.id.tv_highest_quote -> {
                    mBinding.tvTitleSortBy.text = getString(R.string.highest_quote)
                    pageNumber = 1
                    sortBy = -1
                    mViewModel.getQuoteList(getParamData(), true)
                }
            }

            true
        })
        popup.setOnDismissListener {
            mBinding.ivDropDown.animate().rotation(0f).start();
        }
        popup.show()
    }

    private fun initRecyclerView() {
        mAdapter = QuoteTradieAdapter(list, object : QuoteTradieAdapter.OnItemClickListener {
            override fun onTradieClick(pos: Int) {
                if (data != null) {
                    data!!.tradieId = list.get(pos).userId
                    startActivityForResult(
                        Intent(
                            mBinding.ivDropDown.context,
                            TradieProfileActivity::class.java
                        ).putExtra("data", data)
                            .putExtra("isBuilder", true), 1310
                    )
                } else {
                    jobDashBoard!!.tradieId = list.get(pos).userId
                    startActivityForResult(
                        Intent(
                            mBinding.ivDropDown.context,
                            TradieProfileActivity::class.java
                        ).putExtra("data", jobDashBoard)
                            .putExtra("isBuilder", true), 1310
                    )
                }
            }

            override fun onQuoteClick(pos: Int) {
                if (data != null) {
                    data!!.tradieId = list.get(pos).userId
                    startActivityForResult(
                        Intent(
                            mBinding.ivDropDown.context,
                            QuoteDetailsActivity::class.java
                        ).putExtra("data", data)
                            .putExtra("mainData", list.get(pos))
                            .putExtra("isBuilder", true)
                            .putExtra("isAction", intent.getBooleanExtra("isAction", false))
                            .putExtra("fromHome", intent.getBooleanExtra("fromHome", false)), 1310
                    )
                } else {
                    jobDashBoard!!.tradieId = list.get(pos).userId
                    startActivityForResult(
                        Intent(
                            mBinding.ivDropDown.context,
                            QuoteDetailsActivity::class.java
                        ).putExtra("data", jobDashBoard)
                            .putExtra("mainData", list.get(pos))
                            .putExtra("isBuilder", true)
                            .putExtra("isAction", intent.getBooleanExtra("isAction", false))
                            .putExtra("fromHome", intent.getBooleanExtra("fromHome", false)), 1310
                    )
                }
            }
        })
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvQuotes.layoutManager = layoutRecManager
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)
        mBinding.rvQuotes.addOnScrollListener(endlessScrollListener!!)
        mBinding.rvQuotes.adapter = mAdapter

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.GET_QUOTE -> {
                if (pageNumber == 1)
                    showToastShort(exception.message)

            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.GET_QUOTE -> {
                if (mAdapter.itemCount == 0 || pageNumber == 1)
                    mAdapter.setData(mViewModel.getQuoteList())
                else
                    mAdapter.addData(mViewModel.getQuoteList())
                hideShowNoData()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun hideShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        //pageNumber = page + 1
        //  mViewModel.getQuoteList(getParamData(), false)
    }

    override fun onRefresh() {
        endlessScrollListener?.resetState()
        pageNumber = 1
        list.clear()
        mViewModel.getQuoteList(getParamData(), false)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    override fun onClick(p0: View?) {
        val pos = p0?.tag as Int
        if (data != null) {
            data!!.tradieId = list.get(pos).userId
            startActivityForResult(
                Intent(
                    this,
                    TradieProfileActivity::class.java
                ).putExtra("data", data)
                    .putExtra("isBuilder", true), 1310
            )
        } else {
            jobDashBoard!!.tradieId = list.get(pos).userId
            startActivityForResult(
                Intent(
                    this,
                    TradieProfileActivity::class.java
                ).putExtra("data", jobDashBoard)
                    .putExtra("isBuilder", true), 1310
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            endlessScrollListener?.resetState()
            pageNumber = 1
            list.clear()
            mViewModel.getQuoteList(getParamData(), true)
        }
    }


}
