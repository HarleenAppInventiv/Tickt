package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobApplyBinding

class JobAppliedActivity : BaseActivity() {
    lateinit var mBinding: ActivityJobApplyBinding
    private var from: Int? = null
    private var page: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        from = intent.getIntExtra(IntentConstants.FROM, 0)
        setFullScreen()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_apply)
        screenSelect()
        mBinding.tvYellowBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                ).putExtra("pos",page)
            )
            ActivityCompat.finishAffinity(this)
        }
    }

    private fun screenSelect() {
        if (from == Constants.TRADIE_JOB_CANCEL) {
            mBinding.tvHeading.setText(R.string.got_it)
            mBinding.tvSubheading.setText(getString(R.string.send_it_to_builder))
            mBinding.tvYellowBtn.setText(getString(R.string.ok))
            page = 1
        } else {
            page = 0
            mBinding.tvHeading.setText(R.string.application_sent_)
            mBinding.tvSubheading.setText(getString(R.string.we_ll_you_know_if_you_have_been))
            mBinding.tvYellowBtn.setText(getString(R.string.ok))
        }
    }

    override fun onBackPressed() {
        startActivity(
            Intent(
                this,
                HomeActivity::class.java
            )
        )
        ActivityCompat.finishAffinity(this)
    }
}









