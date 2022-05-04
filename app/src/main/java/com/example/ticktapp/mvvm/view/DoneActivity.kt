package com.example.ticktapp.mvvm.view

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityDoneBinding
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.tradie.HomeActivity

class DoneActivity : BaseActivity() {
    lateinit var mBinding: ActivityDoneBinding
    private var from: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        from = intent.getIntExtra(IntentConstants.FROM, 0)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_done)
        screenSelect()

        mBinding.tvYellowBtn.setOnClickListener {
            if (from == Constants.FORGOT_PASSWORD) {
                finish()
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
            }  else {
                PreferenceManager.putBoolean(PreferenceManager.IS_LOGIN, true)
                val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                if (!userType.isNullOrEmpty() && userType.equals("1")) {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                } else {
                    startActivity(Intent(this, HomeBuilderActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                }
                ActivityCompat.finishAffinity(this)
            }
        }
    }

    private fun screenSelect() {
        if (from == Constants.FORGOT_PASSWORD) {
            mBinding.tvHeading.setText(R.string.nice)
            mBinding.tvSubheading.setText(getString(R.string.You_have_updated_the_password))
            mBinding.tvYellowBtn.setText(getString(R.string.log_in))
            mBinding.rlBackground.background =
                ContextCompat.getDrawable(this, R.drawable.rectangle_251)
        } else if (from == Constants.LOGIN) {
            mBinding.tvHeading.setText(R.string.nice)
            mBinding.tvSubheading.setText(getString(R.string.logged_in))
            mBinding.tvYellowBtn.setText(getString(R.string.ok))

        } else if (from == Constants.TRADIE_JOB_CANCEL) {
            mBinding.tvHeading.setText(R.string.got_it)
            mBinding.tvSubheading.setText(getString(R.string.send_it_to_builder))
            mBinding.tvYellowBtn.setText(getString(R.string.ok))

        } else if (from == Constants.BANK) {
            mBinding.tvHeading.setText(R.string.thanks__)
            mBinding.tvSubheading.setText(getString(R.string.if_we_need_anything_else_we_will_reach_out))
            mBinding.tvYellowBtn.setText(getString(R.string.ok))

        } else {
            mBinding.tvHeading.setText(R.string.nicely_done)
            if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 1)
                mBinding.tvSubheading.setText(getString(R.string.done_sing_up_traidie))
            else
                mBinding.tvSubheading.setText(getString(R.string.done_sing_up_builder))

            mBinding.tvYellowBtn.setText(getString(R.string.let_go))
        }


    }

}









