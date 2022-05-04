package com.example.ticktapp.mvvm.view.builder

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobCancelledQuoteBinding
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel

class QuoteJobCancelledActivity : BaseActivity() {
    private var jobID: String? = ""
    lateinit var mBinding: ActivityJobCancelledQuoteBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_cancelled_quote)
        getIntentData()
        screenSelect()
        setObservers()
        mBinding.tvYellowBtn.setOnClickListener {
            mViewModel.closeQuoteJob(true, jobID)
        }
        mBinding.tvWhiteBtn.setOnClickListener {
            mViewModel.closeOpenJob(true, jobID)

        }
    }

    private fun getIntentData() {
        jobID = intent.getStringExtra("jobID")
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun screenSelect() {
        mBinding.tvHeading.setText(R.string.job_cancelled)
        mBinding.tvSubheading.setText(getString(R.string.job_cancel_msg))
        mBinding.tvYellowBtn.setText(getString(R.string.close_the_job))
        mBinding.tvWhiteBtn.setText(getString(R.string.keep_the_job_open))
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.CLOSE_OPEN_JOB -> {
                showToastShort(exception.message)
            }
            ApiCodes.CLOSE_QUOTE_JOB -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        super.onResponseSuccess(statusCode, apiCode, msg)
        when (apiCode) {
            ApiCodes.CLOSE_OPEN_JOB -> {
                startActivity(
                    Intent(
                        this,
                        HomeBuilderActivity::class.java
                    )
                )
            }
            ApiCodes.CLOSE_QUOTE_JOB -> {
                startActivity(
                    Intent(
                        this,
                        HomeBuilderActivity::class.java
                    )
                )
                ActivityCompat.finishAffinity(this)

            }
        }
    }

    override fun onBackPressed() {
        startActivity(
            Intent(
                this,
                HomeBuilderActivity::class.java
            )
        )
        ActivityCompat.finishAffinity(this)
    }
}









