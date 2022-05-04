package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobApplyBinding

class QuoteAppliedActivity : BaseActivity() {
    lateinit var mBinding: ActivityJobApplyBinding
    private var builderName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_apply)
        screenSelect()
        mBinding.tvYellowBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                )
            )
            ActivityCompat.finishAffinity(this)
        }
    }

    private fun screenSelect() {
        builderName = intent.extras!!.getString("builderName", " builder")
        mBinding.tvHeading.setText(R.string.nice)
        mBinding.tvSubheading.text = "${getString(R.string.success_quote_sent)} $builderName"
        /*mBinding.tvSubheading.setText(getString(R.string.success_quote))*/
        mBinding.tvYellowBtn.text = getString(R.string.ok)
        mBinding.rlBackground.background =
            AppCompatResources.getDrawable(this, R.drawable.bg_quote_succes)
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









