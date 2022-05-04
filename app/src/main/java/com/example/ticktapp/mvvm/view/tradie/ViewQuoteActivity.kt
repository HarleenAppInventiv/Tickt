package com.example.ticktapp.mvvm.view.tradie

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.tradie.QuoteItem
import com.app.core.model.tradie.QuoteTradie
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.QuoteItemTradieAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityViewQuoteBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel

class ViewQuoteActivity : BaseActivity(),
    View.OnClickListener {
    private lateinit var mainData: QuoteTradie
    private var data: JobRecModel? = null
    private var jobDashBoard: JobDashboardModel? = null
    private lateinit var mBinding: ActivityViewQuoteBinding
    private lateinit var mAdapter: QuoteItemTradieAdapter
    private val list by lazy { ArrayList<QuoteItem>() }
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_quote)
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
        }
        ViewCompat.setNestedScrollingEnabled(mBinding.rvQuotes, false)
    }


    private fun getIntentData() {
        if (intent.getSerializableExtra("data") is JobDashboardModel) {
            jobDashBoard = intent.getSerializableExtra("data") as JobDashboardModel
        } else {
            data = intent.getSerializableExtra("data") as JobRecModel
        }
        mainData = intent.getSerializableExtra("mainData") as QuoteTradie
    }

    private fun setUpListeners() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
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
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun initRecyclerView() {
        mainData.quote_item?.let { list.addAll(it) }
        mAdapter = QuoteItemTradieAdapter(list, false,this)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvQuotes.layoutManager = layoutRecManager
        mBinding.rvQuotes.adapter = mAdapter
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

}