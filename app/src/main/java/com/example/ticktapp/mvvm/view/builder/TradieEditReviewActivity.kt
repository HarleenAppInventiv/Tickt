package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityEditRateReviewBuilderBinding
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel


@Suppress("DEPRECATION")
public class TradieEditReviewActivity : BaseActivity(), OnClickListener {

    private lateinit var reviewId: String
    private lateinit var review: String
    private var rating = 0.0
    private lateinit var mBinding: ActivityEditRateReviewBuilderBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_rate_review_builder)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
    }

    private fun getIntentData() {
        reviewId = intent.getSerializableExtra("reviewId").toString()
        review = intent.getStringExtra("reviews").toString()
        rating = intent.getDoubleExtra("rating", 0.0)
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
        mBinding.tvCancel.setOnClickListener { onBackPressed() }

        mBinding.tvLeaveReview.setOnClickListener {
            if (mBinding.ratingBar.rating > 0) {
                val reviewData = HashMap<String, Any>()
                reviewData.put("reviewId", reviewId)
                reviewData.put("rating", mBinding.ratingBar.rating)
                if (mBinding.postEdReview.text?.isNotEmpty() == true)
                    reviewData.put("review", mBinding.postEdReview.text.toString())
                mViewModel.reviewUpdateTradie(reviewData)
            } else {
                showToastShort(getString(R.string.please_select_the_rating))
            }
        }
        mBinding.postEdReview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.jobDescTvCount.text = "${p0?.length}/500"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        mBinding.postEdReview.setText(review)
        mBinding.ratingBar.rating = rating.toFloat()
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.REVIEW_UPDATE_TRADIE -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.REVIEW_UPDATE_TRADIE -> {
                val bundle = Intent()
                bundle.putExtra("id", reviewId)
                bundle.putExtra("msg", mBinding.postEdReview.text.toString())
                bundle.putExtra("rating", mBinding.ratingBar.rating.toDouble())
                setResult(Activity.RESULT_OK, bundle)
                finish()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

}