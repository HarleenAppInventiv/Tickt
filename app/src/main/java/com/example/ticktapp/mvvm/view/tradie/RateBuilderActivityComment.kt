package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.builderreview.BuilderReviewModel
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityRateBuilderCommentBinding
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.mvvm.viewmodel.BuilderRatingViewModel
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RateBuilderActivityComment : BaseActivity() {
    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityRateBuilderCommentBinding
    private lateinit var reviewObject: BuilderReviewModel


    private val mViewModel by lazy { ViewModelProvider(this).get(BuilderRatingViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rate_builder_comment)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setData()
        setObservers()
        mBinding.rateBuilderBackStar.setOnClickListener {
            onBackPressed()
        }
        mBinding.tvContinue.setOnClickListener {
            if (mBinding.edtReview.text.isNullOrEmpty()) {
                reviewObject?.let { reviewObject ->
                    mViewModel.getJobsDetails(true, reviewObject)

                }
            } else {
                reviewObject?.let { reviewObject ->
                    reviewObject.review = mBinding.edtReview.text.toString()
                    mViewModel.getJobsDetails(true, reviewObject)

                }
            }
        }

        mBinding.llHeader.setOnClickListener {
            startActivity(
                Intent(this, BuilderProfileActivity::class.java)
                    .putExtra("data", data)
            )
        }

    }

    private fun giveReviewMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_TRADIE_ADDED_REVIEW,
            signUpProperty
        )
    }

    private fun addedReviewsMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_TRADIE_ADDED_REVIEW,
            props
        )
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun setData() {
        mBinding.tvJobTitle.text = data.jobName
        data.postedBy ?: return
        if (data.postedBy.builderImage != null && !data.postedBy.builderImage.isNullOrEmpty()) {
            Glide.with(mBinding.root.context).load(data.postedBy.builderImage)
                .placeholder(R.drawable.bg_circle_grey).into(mBinding.ivBuilderProfile)
        }
        mBinding.tvBuilderTitle.text = data.postedBy.builderName
        mBinding.tvDetails.text = data.jobDescription
        if (data.toDate != null && data.toDate.toString().isNotEmpty()) {
            var outputFormatFrom = DateUtils.DATE_FORMATE_15
            var outputFormatTo = DateUtils.DATE_FORMATE_15
            if (DateUtils.checkForCurrentYear(
                    DateUtils.DATE_FORMATE_8,
                    data.fromDate!!
                ) && DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, data.toDate!!)
            ) {
                outputFormatFrom = DateUtils.DATE_FORMATE_16
                outputFormatTo = DateUtils.DATE_FORMATE_16
            } else if (DateUtils.checkForCurrentYear(
                    DateUtils.DATE_FORMATE_8,
                    data.fromDate!!
                ) && !DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, data.toDate!!)
            ) {
                outputFormatFrom = DateUtils.DATE_FORMATE_16
                outputFormatTo = DateUtils.DATE_FORMATE_15
            }

            mBinding.tvDates.setText(
                DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    outputFormatFrom,
                    data.fromDate
                ) + " - " +
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            outputFormatTo,
                            data.toDate
                        )
            )

        } else {
            var outputFormat = DateUtils.DATE_FORMATE_15
            if (DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, data.fromDate!!)) {
                outputFormat = DateUtils.DATE_FORMATE_16
            }
            mBinding.tvDates.setText(
                DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    outputFormat,
                    data.fromDate
                )
            )
        }
    }

    private fun getIntentData() {
        data = intent.getSerializableExtra("data") as JobRecModel
        reviewObject = intent.getSerializableExtra("review") as BuilderReviewModel
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        super.onResponseSuccess(statusCode, apiCode, msg)
        if (apiCode == ApiCodes.BUILDER_REVIEW) {
            giveReviewMoEngage()
            addedReviewsMixPanel()
            startActivityForResult(
                Intent(this, AddBuilderReviewActivityCompleted::class.java),
                1310
            )

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.BUILDER_REVIEW -> {
                startActivityForResult(
                    Intent(this, AddBuilderReviewActivityCompleted::class.java),
                    1310
                )

                showToastLong(exception.message)
            }
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            val bundle = Intent()
            bundle.putExtra("id", this.data.jobId)
            setResult(Activity.RESULT_OK, bundle)
            finish()
        }
    }

}