package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.app.core.model.builderreview.BuilderReviewModel
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityRateBuilderActivtyStarBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.util.DateUtils
import kotlinx.android.synthetic.main.activity_milestone_list.*

class RateBuilderActivityStar : BaseActivity() {

    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityRateBuilderActivtyStarBinding
    private lateinit var reviewObject: BuilderReviewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_rate_builder_activty_star)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setData()
        mBinding.rateBuilderBackStar.setOnClickListener {
            onBackPressed()
        }
        mBinding.tvContinue.setOnClickListener {
            val rating = mBinding.ratingbar.rating
            if (rating == 0.0f) {
                showToastLong("Please select rating")
            } else {
                data?.let { data ->
                    if (data.jobId != null && data.postedBy != null && data.postedBy.builderId != null) {
                        reviewObject = BuilderReviewModel(
                            jobId = data.jobId!!,
                            builderId = data.postedBy.builderId!!,
                            rating = mBinding.ratingbar.rating,
                            review = ""
                        )
                        startActivityForResult(
                            Intent(
                                RateBuilderActivityStar@ this,
                                RateBuilderActivityComment::class.java
                            )
                                .putExtra("data", data).putExtra("review", reviewObject), 1310
                        )
                    }
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
        Log.i("getIntentDataRate", (data.postedBy!=null).toString())
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