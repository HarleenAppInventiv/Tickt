package com.example.ticktapp.mvvm.view.builder.categories

import android.app.Activity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobApplyBinding

class ChoosedJobSuccessActivity : BaseActivity() {
    lateinit var mBinding: ActivityJobApplyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_apply)
        screenSelect()
        mBinding.tvYellowBtn.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun screenSelect() {
        mBinding.tvHeading.setText(R.string.thanks__)
        mBinding.tvSubheading.setText(getString(R.string.choose_job_msg))
        mBinding.tvYellowBtn.setText(getString(R.string.ok))
        mBinding.rlBackground.background =
            ContextCompat.getDrawable(this, R.drawable.bg_choose_job)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}









