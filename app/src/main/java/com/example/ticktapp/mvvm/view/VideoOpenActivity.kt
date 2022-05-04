package com.example.ticktapp.mvvm.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityVideoOpenBinding
import com.jarvanmo.exoplayerview.media.SimpleMediaSource
import com.jarvanmo.exoplayerview.orientation.OnOrientationChangedListener.SENSOR_LANDSCAPE
import com.jarvanmo.exoplayerview.orientation.OnOrientationChangedListener.SENSOR_PORTRAIT


class VideoOpenActivity : BaseActivity() {

    private var videoUrl = ""
    private lateinit var mBinding: ActivityVideoOpenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_open)
        getIntentData()
        initVideoView()
        listener()
    }

    private fun listener() {
        mBinding.videoView.setBackListener { _, isPortrait ->
            if (isPortrait) {
                onBackPressed()
            }
            false
        }
        mBinding.videoView.setOrientationListener { orientation ->
            if (orientation == SENSOR_PORTRAIT) {
                changeToPortrait()
            } else if (orientation == SENSOR_LANDSCAPE) {
                changeToLandscape()
            }
        }
    }

    private fun changeToPortrait() {
        val attr: WindowManager.LayoutParams = window.attributes
        val window: Window = window
        window.attributes = attr
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        mBinding.wrapper.visibility = View.VISIBLE
    }


    private fun changeToLandscape() {
        val lp: WindowManager.LayoutParams = window.attributes
        val window: Window = window
        window.attributes = lp
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        mBinding.wrapper.visibility = View.GONE
    }

    private fun getIntentData() {
        videoUrl = intent.getStringExtra("data").toString()
    }

    private fun initVideoView() {
        mBinding.videoView.play(SimpleMediaSource(videoUrl))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mBinding.videoView.releasePlayer()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            mBinding.videoView.resume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23) {
            mBinding.videoView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            mBinding.videoView.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            mBinding.videoView.pause()
        }
    }

}