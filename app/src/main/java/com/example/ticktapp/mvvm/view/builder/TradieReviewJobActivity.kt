package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.tradie.BuilderModel
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityGiveRateReviewBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.util.DateUtils


@Suppress("DEPRECATION")
public class TradieReviewJobActivity : BaseActivity(), OnClickListener {

    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityGiveRateReviewBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_give_rate_review)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
        setData()
    }

    private fun getIntentData() {
        data = intent.getSerializableExtra("data") as JobRecModel
        val senderId = intent.getStringExtra("senderId")
        mBinding.tvTradiesData.visibility = View.INVISIBLE
        mBinding.llTradiesData.visibility = View.INVISIBLE
        data.tradieId = senderId
        mViewModel.getTradieProfile(true, data.tradieId, data.jobId)
    }

    private fun setData() {
        mBinding.tvJobTitle.text = data.tradeName
        mBinding.tvJobDetails.text = data.jobName
        if (data.fromDate != null) {
            if (data?.fromDate == data?.toDate) {
                mBinding.tvJobDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    data?.fromDate
                )
            } else if (data?.toDate == null || data?.toDate.equals("null") || data?.toDate.equals(
                    ""
                )
            ) {
                mBinding.tvJobDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    data?.fromDate
                )
                data?.toDate = ""
            } else {
                if (data?.toDate!!.split("-")[0] == data?.fromDate?.split("-")!![0]) {
                    mBinding.tvJobDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        data?.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        data?.toDate
                    )
                } else {
                    mBinding.tvJobDate.text = DateUtils.changeDateFormat(
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
        }

        Glide.with(mBinding.root.context).load(data.tradeSelectedUrl)
            .placeholder(R.drawable.bg_circle_grey)
            .into(mBinding.ivJobProfile)
    }

    private fun tradieData(builderModel: BuilderModel) {
        mBinding.tvTradiesData.visibility = View.VISIBLE
        mBinding.llTradiesData.visibility = View.VISIBLE
        mBinding.tvJobUserTitle.text = builderModel.builderName
        mBinding.tvJobUserDetails.text =
            builderModel.ratings.toString() + ", " + builderModel.reviewsCount
        if (builderModel.reviewsCount == 0.0 || builderModel.reviewsCount == 1.0) {
            mBinding.tvJobUserDetails.append(
                " " + getString(
                    R.string.review
                )
            )
        } else {
            mBinding.tvJobUserDetails.append(
                " " + getString(
                    R.string.reviews
                )
            )
        }
        Glide.with(mBinding.root.context).load(builderModel.builderImage)
            .placeholder(R.drawable.placeholder_profile)
            .into(mBinding.ivJobUserProfile)
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
            window.statusBarColor = Color.WHITE
        }
    }


    private fun listener() {
        mBinding.rateReviewBack.setOnClickListener { onBackPressed() }
        mBinding.userIvRedirection.setOnClickListener {
            data!!.tradieId = data.tradieId
            startActivity(
                Intent(
                    this,
                    TradieProfileActivity::class.java
                ).putExtra("data", data)
                    .putExtra("isBuilder", true)
            )
        }
        mBinding.jobIvRedirection.setOnClickListener {
            startActivity(
                Intent(this, JobDetailsActivity::class.java)
                    .putExtra("data", data).putExtra("isBuilder", true)
            )
        }
        mBinding.tvLeaveReview.setOnClickListener {
            if (mBinding.ratingBar.rating > 0) {
                val reviewData = HashMap<String, Any>()
                reviewData.put("jobId", data.jobId.toString())
                reviewData.put("tradieId", data.tradieId.toString())
                reviewData.put("rating", mBinding.ratingBar.rating)
                if (mBinding.postEdReview.text?.isNotEmpty() == true)
                    reviewData.put("review", mBinding.postEdReview.text.toString())
                mViewModel.reviewTradie(reviewData)
            } else {
                showToastShort(getString(R.string.please_select_the_rating))
            }
        }
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.REVIEW_TRADIE -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.REVIEW_TRADIE -> {
                startActivityForResult(
                    Intent(this, RatedTradieActivity::class.java), 1310
                )
            }
            ApiCodes.TRADIE_PROFILE -> {
                mViewModel.builderModel.let {
                    tradieData(it)
                }

            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
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