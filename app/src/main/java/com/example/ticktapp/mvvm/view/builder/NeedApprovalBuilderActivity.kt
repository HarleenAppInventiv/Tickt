package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
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
import com.example.ticktapp.adapters.NeedApprovalJobsAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityNeedApprovalBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.example.ticktapp.mvvm.viewmodel.NewApplicantRequestViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation

class NeedApprovalBuilderActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var isUpdate: Boolean = false
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityNeedApprovalBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(NewApplicantRequestViewModel::class.java) }
    private lateinit var mAdapter: NeedApprovalJobsAdapter
    private val list by lazy { ArrayList<JobDashboardModel>() }
    private var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_need_approval)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        mViewModel.needApproval(pageNumber, true)
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = NeedApprovalJobsAdapter(list)
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
            ApiCodes.NEED_APPROVAL -> {
                showToastShort(exception.message)

            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        mBinding.srLayout.isRefreshing = false
        when (apiCode) {
            ApiCodes.NEED_APPROVAL -> {
                if (mAdapter.itemCount == 0 || pageNumber == 1)
                    mAdapter.setData(mViewModel.getNeedApprovalJobs())
                else
                    mAdapter.addData(mViewModel.getNeedApprovalJobs())
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
        mViewModel.needApproval(pageNumber, false)
    }

    override fun onRefresh() {
        endlessScrollListener?.resetState()
        pageNumber = 1
        list.clear()
        mViewModel.needApproval(pageNumber, false)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            endlessScrollListener?.resetState()
            pageNumber = 1
            list.clear()
            mViewModel.needApproval(pageNumber, true)
            isUpdate = true
        }
    }

    override fun onBackPressed() {
        if (isUpdate) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

}
