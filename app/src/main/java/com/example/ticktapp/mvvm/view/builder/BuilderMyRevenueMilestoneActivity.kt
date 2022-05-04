package com.example.ticktapp.mvvm.view.builder

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.myrevenue.MilestoneList
import com.app.core.model.myrevenue.RevenueList
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.RowMilestoneTransactionBuilderAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneTranscationBinding
import com.example.ticktapp.mvvm.viewmodel.MyRevenueViewModel
import com.example.ticktapp.util.DateUtils

class BuilderMyRevenueMilestoneActivity : BaseActivity() {

    private lateinit var revenues: RevenueList
    private lateinit var mBinding: ActivityMilestoneTranscationBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MyRevenueViewModel::class.java) }
    private lateinit var mAdapter: RowMilestoneTransactionBuilderAdapter
    private val list by lazy { ArrayList<MilestoneList>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_milestone_transcation)
        initRecyclerView()
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        mBinding.llMain.visibility = View.GONE
        mViewModel.myBuilderRevenueDetails(revenues.jobId.toString(), true)
    }

    private fun getIntentData() {
        revenues = intent.getSerializableExtra("data") as RevenueList
        setBasicData()
    }

    private fun setBasicData() {
        mBinding.tvDateHeading.text = DateUtils.getDateWithUpdatedFormat(
            revenues.from_date,
            DateUtils.DATE_FORMAT_18,
            DateUtils.DATE_FORMATE_19
        )
        val date1 = DateUtils.getDateWithUpdatedFormat(
            revenues.from_date,
            DateUtils.DATE_FORMAT_18,
            DateUtils.DATE_FORMATE_19
        )

        val date2 = DateUtils.getDateWithUpdatedFormat(
            revenues.from_date,
            DateUtils.DATE_FORMAT_18,
            DateUtils.DATE_FORMATE_19
        )

        if (date1 == date2) {
            mBinding.tvDateHeading.visibility = View.GONE
        } else {
            mBinding.tvDateHeading.visibility = View.VISIBLE
        }

        mBinding.tvTitle.text = revenues.jobName
        mBinding.tvPrice.text = revenues.earning

        if (revenues.from_date == revenues.to_date) {
            mBinding.tvDescription.text = DateUtils.changeDateFormat(
                DateUtils.DATE_FORMATE_8,
                DateUtils.DATE_FORMATE_14,
                revenues.from_date
            ) + " - " + DateUtils.changeDateFormat(
                DateUtils.DATE_FORMATE_8,
                DateUtils.DATE_FORMATE_14,
                revenues.to_date
            )
        } else if (revenues.to_date == null || revenues.to_date.equals("null") || revenues.to_date.equals(
                ""
            )
        ) {
            mBinding.tvDescription.text = DateUtils.changeDateFormat(
                DateUtils.DATE_FORMATE_8,
                DateUtils.DATE_FORMATE_14,
                revenues.from_date
            )
            revenues.to_date = ""
        } else {
            if (revenues.to_date!!.split("-")[0] == revenues.from_date!!.split("-")[0]) {
                mBinding.tvDescription.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    revenues.from_date
                ) + " - " + DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    revenues.to_date
                )
            } else {
                mBinding.tvDescription.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_15,
                    revenues.from_date
                ) + " - " + DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_15,
                    revenues.to_date
                )
            }
        }
    }

    private fun listener() {
        mBinding.ivBack.setOnClickListener { onBackPressed() }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = RowMilestoneTransactionBuilderAdapter(list)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvMilestoneList.layoutManager = layoutRecManager
        mBinding.rvMilestoneList.adapter = mAdapter
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.MY_REVENUE_REQUEST_DETAILS -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.MY_REVENUE_REQUEST_DETAILS -> {
                mBinding.llMain.visibility = View.VISIBLE
                mViewModel.mRevenueList.let {
                    mBinding.tvPrice.text = it.totalEarning
                    it.milestones?.let { it1 -> list.addAll(it1) }
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
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
