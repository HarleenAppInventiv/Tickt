package com.example.ticktapp.mvvm.view.builder.postjob

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobApplyBinding
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity

class PostedJobActivity : BaseActivity() {
    lateinit var mBinding: ActivityJobApplyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_apply)
        screenSelect()
        mBinding.tvYellowBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun screenSelect() {
        mBinding.tvHeading.setText(R.string.job_posted)
        mBinding.tvSubheading.setText(getString(R.string.posted_job_msg))
        mBinding.tvYellowBtn.setText(getString(R.string.ok))
        mBinding.rlBackground.background = ContextCompat.getDrawable(this, R.drawable.post_job)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, HomeBuilderActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }
}









