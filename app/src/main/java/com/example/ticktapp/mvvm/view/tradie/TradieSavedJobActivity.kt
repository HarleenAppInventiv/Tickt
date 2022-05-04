package com.example.ticktapp.mvvm.view.tradie

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.NewJobRequestAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieSavedJobBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.viewmodel.SavedJobViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation

class TradieSavedJobActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {

    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityTradieSavedJobBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(SavedJobViewModel::class.java) }
    private lateinit var mAdapter: NewJobRequestAdapter
    private val list by lazy { ArrayList<JobRecModel>() }
    private var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_saved_job)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        mViewModel.getSavedJobRequestList(pageNumber, true)
    }

    override fun onRestart() {
        super.onRestart()
       onRefresh()
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = NewJobRequestAdapter(list, true)
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
            ApiCodes.SAVE_JOB_REQUEST -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.SAVE_JOB_REQUEST -> {
                if (mAdapter.itemCount == 0 || pageNumber == 1)
                    mAdapter.setData(mViewModel.getSavedJobListing())
                else
                    mAdapter.addData(mViewModel.getSavedJobListing())
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
        mViewModel.getSavedJobRequestList(pageNumber, false)
    }

    override fun onRefresh() {
        endlessScrollListener?.resetState()
        pageNumber = 1
        list.clear()
        mViewModel.getSavedJobRequestList(pageNumber, false)
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

}