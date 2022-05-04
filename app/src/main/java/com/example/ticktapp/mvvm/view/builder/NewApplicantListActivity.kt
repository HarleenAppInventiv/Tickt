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
import com.app.core.model.tradie.BuilderModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ApplicantTradieAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityNewApplicantListBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.mvvm.viewmodel.NewApplicantRequestViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import com.example.ticktapp.util.DateUtils

class NewApplicantListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener,
    View.OnClickListener {
    private var data: JobRecModel? = null
    private var jobDashBoard: JobDashboardModel? = null
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityNewApplicantListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(NewApplicantRequestViewModel::class.java) }
    private lateinit var mAdapter: ApplicantTradieAdapter
    private val list by lazy { ArrayList<BuilderModel>() }
    private var pageNumber = 1
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var sortBy: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_applicant_list)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setData()
        mViewModel.newApplicationRequestListing(getParamData(), true)
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
            if (data?.fromDate == data?.toDate) {
                mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    data?.fromDate
                )
            } else if (data?.toDate == null || data?.toDate.equals("null") || data?.toDate.equals(
                    ""
                )
            ) {
                mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    data?.fromDate
                )
                data?.toDate = ""
            } else {
                if (data?.toDate?.split("-")!![0] == data?.fromDate?.split("-")!![0]) {
                    mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        data?.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        data?.toDate
                    )
                } else {
                    mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
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
        } else {
            mBinding.tvTitle.text = jobDashBoard?.tradeName
            mBinding.tvDetails.text = jobDashBoard?.jobName
            if (jobDashBoard?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(jobDashBoard?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            }
            if (jobDashBoard?.fromDate == jobDashBoard?.toDate) {
                mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    jobDashBoard?.fromDate
                )
            } else if (jobDashBoard?.toDate == null || jobDashBoard?.toDate.equals("null") || jobDashBoard?.toDate.equals(
                    ""
                )
            ) {
                mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    jobDashBoard?.fromDate
                )
                jobDashBoard?.toDate = ""
            } else {
                if (jobDashBoard?.toDate!!.split("-")[0] == jobDashBoard?.fromDate?.split("-")!![0]) {
                    mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobDashBoard?.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobDashBoard?.toDate
                    )
                } else {
                    mBinding.tvDetailsDate.text = DateUtils.changeDateFormat(
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
    }

    private fun getParamData(): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        if (data != null)
            params.put("jobId", data?.jobId.toString())
        else
            params.put("jobId", jobDashBoard?.jobId.toString())

        params.put("page", pageNumber)
        params.put("sortBy", sortBy)
        val coordinates = arrayListOf(lng.toDouble(), lat.toDouble())
        val location = HashMap<String, Any>()
        location.put("type", "Point")
        location.put("coordinates", coordinates)
        if (lat != 0.0)
            params.put("location", location)
        return params
    }

    private fun getIntentData() {
        if (intent.getSerializableExtra("data") is JobDashboardModel) {
            jobDashBoard = intent.getSerializableExtra("data") as JobDashboardModel
        } else {
            data = intent.getSerializableExtra("data") as JobRecModel
        }
        if (PreferenceManager.getString(PreferenceManager.LAT) != null &&
            !PreferenceManager.getString(PreferenceManager.LAT).equals("")
        ) {
            PreferenceManager.getString(PreferenceManager.LAT)?.let {
                PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                    lat = PreferenceManager.getString(PreferenceManager.LAT).toString().toDouble()
                    lng = PreferenceManager.getString(PreferenceManager.LAN).toString().toDouble()
                }
            }
        } else {
            lat = -37.8136
            lng = 144.9631
        }
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.llTimePicker.setOnClickListener {
            showPopup(it)
        }
        mBinding.newApplicantListIv.setOnClickListener {
            if (data != null) {
                startActivity(
                    Intent(this, JobDetailsActivity::class.java).putExtra("data", data)
                        .putExtra("isBuilder", true)
                )
            } else {
                startActivity(
                    Intent(this, JobDetailsActivity::class.java).putExtra("data", jobDashBoard)
                        .putExtra("isBuilder", true)
                )
            }
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun showPopup(view: View) {
        mBinding.ivDropDown.animate().rotation(180f).start();
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.sort_by_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.tv_highest_rated -> {
                    mBinding.tvTitleSortBy.text = getString(R.string.highest_rated)
                    pageNumber = 1
                    sortBy = 1
                    mViewModel.newApplicationRequestListing(getParamData(), true)

                }
                R.id.tv_closed_me -> {
                    mBinding.tvTitleSortBy.text = getString(R.string.closest_to_me)
                    pageNumber = 1
                    sortBy = 2
                    mViewModel.newApplicationRequestListing(getParamData(), true)

                }
                R.id.tv_most_job_completed -> {
                    mBinding.tvTitleSortBy.text = getString(R.string.most_job_completed)
                    pageNumber = 1
                    sortBy = 3
                    mViewModel.newApplicationRequestListing(getParamData(), true)

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
        mAdapter = ApplicantTradieAdapter(list, this)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvNewJobs.layoutManager = layoutRecManager
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)
        mBinding.rvNewJobs.addOnScrollListener(endlessScrollListener!!)
        mBinding.rvNewJobs.adapter = mAdapter

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.NEW_APPLICANT_LIST -> {
                if (pageNumber == 1)
                    showToastShort(exception.message)

            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.NEW_APPLICANT_LIST -> {
                if (mAdapter.itemCount == 0 || pageNumber == 1)
                    mAdapter.setData(mViewModel.getNewTradieListing())
                else
                    mAdapter.addData(mViewModel.getNewTradieListing())
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
        pageNumber = page + 1
        mViewModel.newApplicationRequestListing(getParamData(), false)
    }

    override fun onRefresh() {
        endlessScrollListener?.resetState()
        pageNumber = 1
        list.clear()
        mViewModel.newApplicationRequestListing(getParamData(), false)
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
            data!!.tradieId = list.get(pos).builderId
            startActivityForResult(
                Intent(
                    this,
                    TradieProfileActivity::class.java
                ).putExtra("data", data)
                    .putExtra("isBuilder", true), 1310
            )
        } else {
            jobDashBoard!!.tradieId = list.get(pos).builderId
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
            mViewModel.newApplicationRequestListing(getParamData(), true)
        }
    }

}
