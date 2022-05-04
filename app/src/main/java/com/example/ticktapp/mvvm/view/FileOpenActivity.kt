package com.example.ticktapp.mvvm.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityFileOpenBinding

class FileOpenActivity : BaseActivity() {

    private var pdfUrl = ""
    private lateinit var mBinding: ActivityFileOpenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_file_open)
        getIntentData()
        initWebView()
        listener()
    }

    private fun listener() {
        mBinding.ivBack.setOnClickListener { finish() }
    }

    private fun getIntentData() {
        pdfUrl = intent.getStringExtra("data").toString()
    }

    private fun initWebView() {
        mBinding.loader.visibility = View.VISIBLE
        mBinding.webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mBinding.loader.visibility = View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                mBinding.loader.visibility = View.GONE
            }
        }

        mBinding.webview.settings.javaScriptEnabled = true
        mBinding.webview.settings.domStorageEnabled = true
        mBinding.webview.overScrollMode = WebView.OVER_SCROLL_NEVER

        mBinding.webview.post {
            mBinding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
        }
        mBinding.webview.postDelayed({
            mBinding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
        }, 100)
        mBinding.webview.postDelayed({
            mBinding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
        }, 200)
        mBinding.webview.postDelayed({
            mBinding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
        }, 300)
    }

    override fun onBackPressed() {
        if (mBinding.webview.canGoBack()) {
            mBinding.webview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}