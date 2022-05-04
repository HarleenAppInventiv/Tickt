package com.example.ticktapp.mvvm.view.tradie.completemilestone

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityDoneBinding
import com.example.ticktapp.databinding.ActivitySuccessBinding
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.tradie.HomeActivity

class JobCompletedSuccessActivity : BaseActivity() {
    lateinit var mBinding: ActivitySuccessBinding
    private var jobCount: String = ""
    private var isJobCompleted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        getIntentData()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_success)
        screenSelect()

        mBinding.tvYellowBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra(IntentConstants.IS_JOB_COMPLETED, isJobCompleted)
            setResult(RESULT_OK, intent)
            finish()
        }
        mBinding.tvWhiteBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra(IntentConstants.IS_JOB_COMPLETED, isJobCompleted)
            if (isJobCompleted)
                intent.putExtra(IntentConstants.PAST_JOBS, true)
            setResult(RESULT_OK, intent)
            finish()

        }
    }

    private fun getIntentData() {
        jobCount = intent.getStringExtra(IntentConstants.JOB_COUNT).toString()
        isJobCompleted = intent.getBooleanExtra(IntentConstants.IS_JOB_COMPLETED, false)
    }

    private fun screenSelect() {
        if (isJobCompleted) {
            mBinding.tvHeading.setText(
                getString(
                    R.string.job_completed_,
                    jobCount + getDayOfMonthSuffix(jobCount.toInt())
                )
            )
            mBinding.tvSubheading.setText(
                getString(
                    R.string.job_completed_msg,
                    jobCount + getDayOfMonthSuffix(jobCount.toInt())
                )
            )
            mBinding.tvYellowBtn.visibility = View.VISIBLE
            mBinding.tvYellowBtn.setText(getString(R.string.ok))
            mBinding.tvWhiteBtn.setText(getString(R.string.see_completed_jobs))
            DrawableCompat.setTint(
                mBinding.tvWhiteBtn.background,
                ContextCompat.getColor(this, R.color.white)
            );
            mBinding.rlBackground.setBackgroundResource(R.drawable.job_complete_success)

        } else {
            mBinding.rlBackground.setBackgroundResource(R.drawable.milestone_complete_success)
            DrawableCompat.setTint(
                mBinding.tvWhiteBtn.background,
                ContextCompat.getColor(this, R.color.color_fee600)
            );
            mBinding.tvYellowBtn.visibility = View.GONE
            mBinding.tvWhiteBtn.setText(getString(R.string.ok))
            mBinding.tvSubheading.setText(getString(R.string.milestone_completed_msg))
            mBinding.tvHeading.setText(getString(R.string.milestone_completed))

        }


    }

    fun getDayOfMonthSuffix(n: Int): String? {
        return if (n >= 11 && n <= 13) {
            "th"
        } else when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

}









