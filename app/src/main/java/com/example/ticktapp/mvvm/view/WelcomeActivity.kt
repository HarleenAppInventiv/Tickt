package com.example.ticktapp.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.app.core.preferences.PreferenceManager
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityWelcomeBinding
import com.example.ticktapp.mvvm.view.onboarding.OnboardingActivity
import com.example.ticktapp.util.BottomSheetPermissionFragment
import com.example.ticktapp.util.MoEngageUtils
import com.example.ticktapp.util.SingleShotLocationProvider
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class WelcomeActivity : BaseActivity() {
    lateinit var mBinding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        /*appOpenMoEngage()
        appOpenMixPanel()*/
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        mBinding.tvYellowBtn.setOnClickListener {
            PreferenceManager.putString(PreferenceManager.USER_TYPE, "2")
            startActivity(
                Intent(
                    this,
                    OnboardingActivity::class.java
                )
            )
        }
        mBinding.tvWhiteBtn.setOnClickListener {
            PreferenceManager.putString(PreferenceManager.USER_TYPE, "1")
            startActivity(
                Intent(
                    this,
                    OnboardingActivity::class.java
                )
            )
        }
        Handler(Looper.getMainLooper()).postDelayed({ askPermission() }, 1000)
        checkIntentData()
    }

    private fun checkIntentData() {
        if (intent.hasExtra("isBlock")) {
            showAppPopupDialog(
                getString(R.string.admin_blocked_account),
                getString(R.string.ok),
                getString(R.string.cancel),
                getString(R.string.account_blocked),
                {},
                {},
                false
            )
        }
    }

    fun askPermission() {
        try {
            BottomSheetPermissionFragment(
                this,
                object : BottomSheetPermissionFragment.OnPermissionResult {
                    override fun onPermissionAllowed() {
                        SingleShotLocationProvider.requestSingleUpdate(false, this@WelcomeActivity,
                            object : SingleShotLocationProvider.LocationCallback {
                                override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates?) {
                                }

                                override fun onCurrentLocationNotFound() {
                                }
                            })
                    }

                    override fun onPermissionDenied() {
                        PreferenceManager.putString(PreferenceManager.LAT, "-37.8136")
                        PreferenceManager.putString(PreferenceManager.LAN, "144.9631")
                    }
                },
                arrayOf(
                    BottomSheetPermissionFragment.ACCESS_COARSE_LOCATION,
                    BottomSheetPermissionFragment.ACCESS_FINE_LOCATION
                )
            ).show(supportFragmentManager, "")
        } catch (e: Exception) {
        }
    }

    private fun appOpenMoEngage() {
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.APP_OPEN, true)

        MoEngageUtils.sendEvent(this, MoEngageConstants.MOENGAGE_EVENT_APP_OPEN, signUpProperty)
    }

    private fun appOpenMixPanel() {
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )
        val props = JSONObject()
        props.put(MoEngageConstants.APP_OPEN, true)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_APP_OPEN, props)
    }
}