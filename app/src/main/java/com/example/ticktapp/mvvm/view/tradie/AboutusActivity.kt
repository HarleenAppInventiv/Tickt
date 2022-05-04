package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAboutUsBinding

class AboutusActivity : BaseActivity() {

    private lateinit var mBinding: ActivityAboutUsBinding
    private var desc = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_us)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
    }

    private fun getIntentData() {
        if (intent.hasExtra("about_you")) {
            desc = intent.getStringExtra("about_you").toString()
        }
        if (intent.hasExtra("isBuilder") && intent.getBooleanExtra("isBuilder", false)) {
            mBinding.tvTitle.text = getString(R.string.about_company)
            mBinding.tvDesc.text = getString(R.string.builder_about_me)
            mBinding.tvInputTitle.text = getString(R.string.about_us)
            mBinding.jobDescEd.setHint(getString(R.string.our_company_spec))
        }
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun listener() {
        mBinding.jobDescBack.setOnClickListener { onBackPressed() }
        mBinding.jobDescBtn.setOnClickListener {
            if (isValid()) {
                setResult(
                    Activity.RESULT_OK, Intent()
                        .putExtra("about_you", mBinding.jobDescEd.text.toString())
                )
                finish()
            }

        }
        mBinding.jobDescEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.jobDescTvCount.text = "${p0?.length}/1000           "
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        mBinding.jobDescEd.setText(desc)
    }

    private fun isValid(): Boolean {
        if (mBinding.jobDescEd.text.toString().length == 0) {
            showToastShort(getString(R.string.please_enter_about_you))
            return false
        }
        return true
    }
}