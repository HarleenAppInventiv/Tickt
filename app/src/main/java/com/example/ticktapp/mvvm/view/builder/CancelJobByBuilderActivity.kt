package com.example.ticktapp.mvvm.view.builder

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.CancelReason
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.CancelAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobCancelledByBuilderBinding
import com.example.ticktapp.mvvm.viewmodel.TradieJobCancelViewModel
import com.google.gson.JsonObject

class CancelJobByBuilderActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieJobCancelViewModel::class.java) }
    private lateinit var mBinding: ActivityJobCancelledByBuilderBinding
    private lateinit var mAdapter: CancelAdapter
    private lateinit var cancelReasonList: ArrayList<CancelReason>
    private var jobId = ""
    private var jobName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_cancelled_by_builder)
        setUpListeners()
        getIntentData()
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        enableDisableSubmit()
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun getIntentData() {
        jobId = intent.getStringExtra(IntentConstants.JOB_ID).toString()
        jobName = intent.getStringExtra(IntentConstants.JOB_NAME).toString()
        mBinding.ilHeader.tvTitle.text = jobName
    }

    private fun setUpListeners() {
        mBinding.btnSubmit.setOnClickListener(this)
        cancelReasonList = ArrayList()
        cancelReasonList.add(CancelReason(1, getString(R.string.experiecing_delay), false))
        cancelReasonList.add(CancelReason(2, getString(R.string.current_project_has_taken), false))
        cancelReasonList.add(CancelReason(3, getString(R.string.staff_shortages), false))
        cancelReasonList.add(CancelReason(4, getString(R.string.injury_unwell), false))
        cancelReasonList.add(CancelReason(5, getString(R.string.no_longer_available), false))
        mAdapter = CancelAdapter(cancelReasonList, object : CancelAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
                enableDisableSubmit()
            }
        })
        mBinding.rvCancel.layoutManager = LinearLayoutManager(this)
        mBinding.rvCancel.adapter = mAdapter
        mBinding.jobDescEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.jobDescTvCount.text = "${p0?.length}/1000"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.BUILDER_JOB_CANCEL -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.BUILDER_JOB_CANCEL -> {
                startActivity(Intent(this, CancelledJobActivity::class.java))
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
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
            window.setStatusBarColor(Color.WHITE)
        }
    }

    private fun enableDisableSubmit() {
//        if (mAdapter.getSelectedId() != 0) {
        mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_btn_yellow)
        mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_161d4a))
        mBinding.btnSubmit.isEnabled = true
//        } else {
//            mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_drawable_rect_dfe5ef)
//            mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_99a4b6))
//            mBinding.btnSubmit.isEnabled = false
//        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.btnSubmit -> {
                if (mAdapter.getSelectedId() == 0) {
                    showToastShort(getString(R.string.please_select_one_reason))
                } else {
                    val mObject = JsonObject()
                    mObject.addProperty("jobId", jobId)
                    mObject.addProperty("reason", mAdapter.getSelectedId())
                    if (!mBinding.jobDescEd.text.toString().trim().isEmpty())
                        mObject.addProperty("note", mBinding.jobDescEd.text.toString())
                    mViewModel.builderCancelJob(mObject)
                }
            }

        }
    }

}