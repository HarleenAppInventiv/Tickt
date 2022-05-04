package com.example.ticktapp.mvvm.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityStaticDataBinding
import com.example.ticktapp.mvvm.viewmodel.StaticViewModel
import kotlinx.android.synthetic.main.activity_static_data.*
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class StaticDataActivity : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivityStaticDataBinding
    private var from: Int? = null

    private val mViewModel by lazy { ViewModelProvider(this).get(StaticViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_static_data)
        mBinding.model = mViewModel
        getIntentData()
        setListeners()
        initView()
        setObservers()
        setUpWebView()

    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        mBinding.webview.settings.javaScriptEnabled = true
        mBinding.webview.settings.javaScriptCanOpenWindowsAutomatically = true
        mBinding.webview.settings.setSupportMultipleWindows(true)
    }


    private fun setListeners() {
        iv_back.setOnClickListener(this)
    }

    private fun getIntentData() {
        from = intent.getIntExtra(IntentConstants.FROM, 0)
    }

    private fun initView() {
        if (from == Constants.TERMS) {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.action_terms_and_condition))
            mViewModel.getTermsAndCondition()
        } else {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.action_privacy_policy))
            mViewModel.getPrivacyPolicy()
        }
        mBinding.rlToolbar.tvTitle.visibility = View.GONE
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.PRIVACY -> {
                mViewModel.getStaticUrl().let {
                    it?.privacyPolicy_url?.let { it1 -> webview.loadUrl(it1) }
                }
            }
            ApiCodes.TNC -> {
                mViewModel.getStaticUrl().let {
                    it?.tnc_url?.let { it1 -> webview.loadUrl(it1) }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.PRIVACY, ApiCodes.TNC -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            iv_back -> {
                onBackPressed()
            }
        }
    }

}