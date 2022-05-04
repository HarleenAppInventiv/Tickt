package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobApplyBinding

class QuoteAcceptedTradieActivity : BaseActivity() {
    lateinit var mBinding: ActivityJobApplyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_apply)
        screenSelect()
        mBinding.tvYellowBtn.setOnClickListener {
            startActivity(
                Intent(this, HomeBuilderActivity::class.java).putExtra(
                    "pos",
                    1
                )
            )
            ActivityCompat.finishAffinity(this)
        }
    }

    private fun screenSelect() {
        mBinding.tvHeading.setText(R.string.quote_accepted)
        mBinding.tvSubheading.setText(getString(R.string.quote_accepted_msg))
        mBinding.tvYellowBtn.setText(getString(R.string.ok))
        mBinding.rlBackground.background =
            ContextCompat.getDrawable(this, R.drawable.bg_payment_failure)
    }

    override fun onBackPressed() {
        startActivity(
            Intent(this, HomeBuilderActivity::class.java).putExtra(
                "pos",
                1
            )
        )
        ActivityCompat.finishAffinity(this)
    }
}









