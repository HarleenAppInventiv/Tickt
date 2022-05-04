package com.example.ticktapp.mvvm.view.builder

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityWebviewBinding
import com.example.ticktapp.mvvm.viewmodel.StaticViewModel

class WebViewActivity : BaseActivity() {
    lateinit var mBinding: ActivityWebviewBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(StaticViewModel::class.java) }
    private var from: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        initView()
        setObservers()
        setUpWebView()
        mBinding.payBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getIntentData() {
        from = intent.getIntExtra(IntentConstants.FROM, 0)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun initView() {
        if (from == Constants.TERMS) {
            mBinding.tvTitle.setText(getString(R.string.terms_of_use))
            mViewModel.getTermsAndCondition()
        } else {
            mBinding.tvTitle.setText(getString(R.string.privacy_policy))
            mViewModel.getPrivacyPolicy()
        }
        mBinding.tvTitle.visibility = View.GONE
    }

    private fun setUpWebView() {
        mBinding.webView.settings.domStorageEnabled=true
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        mBinding.webView.settings.setSupportMultipleWindows(true)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.PRIVACY -> {
                mViewModel.getStaticUrl().let {
                    it?.privacyPolicy_url?.let { it1 -> mBinding.webView.loadUrl(it1) }
                }
            }
            ApiCodes.TNC -> {
                mViewModel.getStaticUrl().let {
                    it?.tnc_url?.let { it1 -> mBinding.webView.loadUrl(it1) }
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
}




























