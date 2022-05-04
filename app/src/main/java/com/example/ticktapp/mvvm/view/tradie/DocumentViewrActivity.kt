package com.example.ticktapp.mvvm.view.tradie

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityStaticPagesBinding


class DocumentViewrActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mbinding: ActivityStaticPagesBinding
    private lateinit var url: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_static_pages)
        setStatusBarColor()
        setLightStatusBar(mbinding.root)
        getIntentData()

    }


    private fun getIntentData() {
        mbinding.pbLoading.visibility = View.VISIBLE
        url = intent?.getStringExtra("url").toString()
        Glide.with(this).load(url).placeholder(R.drawable.bg_drawable_rect_dfe5ef)
            .error(R.drawable.bg_drawable_rect_dfe5ef).into(mbinding.ivDoc)
        Glide.with(this)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: com.bumptech.glide.request.target.Target<Drawable>?,
                    p3: Boolean
                ): Boolean {
                    mbinding.pbLoading.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    p0: Drawable?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: DataSource?,
                    p4: Boolean
                ): Boolean {
                    mbinding.pbLoading.visibility = View.GONE

                    //do something when picture already loaded
                    return false
                }
            })
            .into(mbinding.ivDoc)

    }

    fun openLink(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build() as CustomTabsIntent
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        mbinding.webView.webViewClient = AppWebViewClients()
        mbinding.webView.settings.javaScriptEnabled = true
        mbinding.webView.settings.useWideViewPort = true
        mbinding.webView.loadUrl(
            "http://docs.google.com/gview?embedded=true&url="
                    + url
        )


    }


    inner class AppWebViewClients : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            mbinding.pbLoading.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {

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

}
