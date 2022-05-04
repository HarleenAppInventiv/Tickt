package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobApplyBinding
import com.example.ticktapp.mvvm.view.LoginActivity

class ChangePasswordSuccessActivity : BaseActivity() {
    lateinit var mBinding: ActivityJobApplyBinding
    private var from: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        from = intent.getIntExtra(IntentConstants.FROM, 0)
        setFullScreen()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_apply)
        screenSelect()
        mBinding.tvYellowBtn.setOnClickListener {
            val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
            val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
            PreferenceManager.clearAllPrefs()
            PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
            PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
            finishAffinity()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun screenSelect() {
        mBinding.rlBackground.setBackgroundResource(R.drawable.change_password_success)
        if (from == 1) {
            mBinding.tvHeading.setText(R.string.done_)
            mBinding.tvSubheading.setText(getString(R.string.updated_password_builder))
        } else {
            mBinding.tvHeading.setText(R.string.nice)
            mBinding.tvSubheading.setText(getString(R.string.updated_password))
        }
        mBinding.tvYellowBtn.setText(getString(R.string.ok))
    }

    override fun onBackPressed() {
        val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
        val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        PreferenceManager.clearAllPrefs()
        PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
        PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
        finishAffinity()
        startActivity(Intent(this, LoginActivity::class.java))
    }


}









