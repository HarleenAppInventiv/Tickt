package com.example.ticktapp.mvvm.view.builder.milestone

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.app.core.util.IntentConstants
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySuccessBinding
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.util.MoEngageUtils
import com.moengage.core.Properties
import java.text.SimpleDateFormat
import java.util.*

class PaymentSuccessActivity : BaseActivity() {
    lateinit var mBinding: ActivitySuccessBinding
    private var isJobCompleted: Boolean = false
    private var isLastJob: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        getIntentData()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_success)
        screenSelect()

        mBinding.tvYellowBtn.setOnClickListener {
            if (isLastJob) {
                startActivity(
                    Intent(this, HomeBuilderActivity::class.java).putExtra(
                        "pos",
                        1
                    )
                )
                ActivityCompat.finishAffinity(this)
            } else {
                startActivity(
                    Intent(this, HomeBuilderActivity::class.java).putExtra(
                        "pos",
                        1
                    )
                )
                ActivityCompat.finishAffinity(this)
            }
        }
        mBinding.tvWhiteBtn.setOnClickListener {
            if (isLastJob) {
                startActivity(
                    Intent(this, HomeBuilderActivity::class.java).putExtra(
                        "pos",
                        1
                    ).putExtra("isTransacation", true)
                )
                ActivityCompat.finishAffinity(this)
            } else {
                onBackPressed()
            }
        }
    }



    private fun getIntentData() {
        isJobCompleted = intent.getBooleanExtra(IntentConstants.IS_JOB_COMPLETED, false)
        isLastJob = intent.getBooleanExtra(IntentConstants.IS_LAST_JOB, false)
    }

    private fun screenSelect() {
        if (isJobCompleted) {
            mBinding.tvHeading.setText(
                getString(
                    R.string.payment_sent
                )
            )
            mBinding.tvSubheading.setText(
                getString(
                    R.string.payment_success
                )
            )
            mBinding.tvYellowBtn.setText(getString(R.string.continue_))
            if (!isLastJob) {
                mBinding.tvWhiteBtn.visibility = View.INVISIBLE
            }
            mBinding.tvWhiteBtn.setText(getString(R.string.see_your_transacations))
            DrawableCompat.setTint(
                mBinding.tvWhiteBtn.background,
                ContextCompat.getColor(this, R.color.white)
            )
            DrawableCompat.setTint(
                mBinding.tvYellowBtn.background,
                ContextCompat.getColor(this, R.color.color_fee600)
            )
        } else {
            mBinding.tvHeading.setText(
                getString(
                    R.string.please_check_again
                )
            )
            mBinding.tvSubheading.setText(
                getString(
                    R.string.payment_failure
                )
            )

            mBinding.tvYellowBtn.setText(getString(R.string.ok))
            mBinding.tvWhiteBtn.setText(getString(R.string.change_payment_method))
            DrawableCompat.setTint(
                mBinding.tvWhiteBtn.background,
                ContextCompat.getColor(this, R.color.white)
            )
            DrawableCompat.setTint(
                mBinding.tvYellowBtn.background,
                ContextCompat.getColor(this, R.color.color_fee600)
            )
        }

    }


}









