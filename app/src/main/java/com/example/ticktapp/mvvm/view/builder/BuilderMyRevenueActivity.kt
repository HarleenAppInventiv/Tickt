package com.example.ticktapp.mvvm.view.builder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.myrevenue.RevenueList
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.MyEarningAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityBuilderMyRevenueBinding
import com.example.ticktapp.mvvm.viewmodel.MyRevenueViewModel
import kotlin.math.roundToInt

class BuilderMyRevenueActivity : BaseActivity() {

    private lateinit var mBinding: ActivityBuilderMyRevenueBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MyRevenueViewModel::class.java) }
    private var pageNumber = 1
    private lateinit var mAdapter: MyEarningAdapter
    private val list by lazy { ArrayList<RevenueList>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_builder_my_revenue)
        initRecyclerView()
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        mBinding.clMain.visibility = View.GONE
        mViewModel.myBuilderRevenueList(pageNumber, true)
    }

    private fun listener() {
        mBinding.ivBack.setOnClickListener { onBackPressed() }
        mBinding.ivSearch.setOnClickListener {
            startActivity(Intent(this, SearchBuilderPastJobsActivity::class.java))
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = MyEarningAdapter(list, true)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvPastJobs.layoutManager = layoutRecManager
        mBinding.rvPastJobs.adapter = mAdapter
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.MY_REVENUE_REQUEST -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.MY_REVENUE_REQUEST -> {
                mBinding.clMain.visibility = View.VISIBLE
                mBinding.tvTotalRevenueValue.text =
                    "$" + mViewModel.getSavedJobListing()[0].totalEarnings?.roundToInt().toString()
                mBinding.tvTotalJobsValue.text =
                    mViewModel.getSavedJobListing()[0].totalJobs?.roundToInt().toString()

                if (mAdapter.itemCount == 0 || pageNumber == 1)
                    mViewModel.getSavedJobListing()[0].revenue?.revenueList?.let {
                        mAdapter.setData(
                            it
                        )
                    }
                else
                    mViewModel.getSavedJobListing()[0].revenue?.revenueList?.let {
                        mAdapter.setData(
                            it
                        )
                    }
                hideShowNoData()
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun hideShowNoData() {
        /*if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE*/
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

}
