package com.example.ticktapp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.app.core.preferences.PreferenceManager
import com.app.core.util.Constants
import com.app.core.util.CoreContextWrapper
import com.app.core.util.MoEngageConstants
import com.app.core.util.StripeConstants
import com.google.firebase.FirebaseApp
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.MoEngage
import com.moengage.core.Properties
import io.intercom.android.sdk.Intercom
import com.moengage.core.model.AppStatus
import com.stripe.android.PaymentConfiguration


/**
 * Application class that is called first time when app is launched
 *
 */
class ApplicationClass : Application(), LifecycleObserver {
    init {
        instance = this
    }


    companion object {
        //        lateinit var localDb: LocalRepository
        private var instance: ApplicationClass? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        var isSaveRefresh = false

        /**
         * Method will be invoked to check app is freshly installed or app is update
         */
        fun checkAppInstall(appInstallsListener: AppInstallsListener) {
            val context = this

            var sharedPref: SharedPreferences = PreferenceManager.sharedPref
            val DOESNT_EXIST = -1

            // Get current version code
            val currentVersionCode = BuildConfig.VERSION_CODE

            val savedVersionCode =
                sharedPref.getInt(PreferenceManager.APP_VERSION_CODE, DOESNT_EXIST)

            if (currentVersionCode == savedVersionCode) {  // Check for first run or upgrade
                return                                     //This is just a normal run
            } else if (savedVersionCode == DOESNT_EXIST) {
                appInstallsListener.onFreshInstall()       //This is a new install (or the user cleared the shared preferences)
            } else if (currentVersionCode > savedVersionCode) {
                appInstallsListener.onAppUpdate()  // This is an upgrade
            }

            val nEditor = sharedPref.edit()
            nEditor.putInt(PreferenceManager.APP_VERSION_CODE, currentVersionCode)
            nEditor.apply()
        }
    }

    interface AppInstallsListener {
        fun onFreshInstall()
        fun onAppUpdate()
    }

    var jobID = ""
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        CoreContextWrapper.setContext(this)
        Intercom.initialize(
            this,
            "android_sdk-ef18f404cdc9d87dd9e73a1ae246288bad728164",
            "tvbm4bhr"
        )
        initStripe()
        moEngage()
        trackInstallOrUpdate()

    }

    private fun initStripe() {
        PaymentConfiguration.init(
            applicationContext,
            StripeConstants.PUBLISHABLE_KEY
        )
    }

    private fun moEngage() {
        val moEngage = MoEngage.Builder(this, MoEngageConstants.MOENGAGE_APP_ID)
            .enableSegmentIntegration()
            .build()
        MoEngage.initialise(moEngage)
        Log.i("MoEngage_init", MoEngage.initialise(moEngage).toString())
        Log.i("MoEngage_init", (MoEngage.initialise(moEngage) != null).toString())
    }

    private fun trackInstallOrUpdate() {
        val preferences = getSharedPreferences("ticktapp", 0)
        var appStatus = AppStatus.INSTALL
        if (preferences.getBoolean("has_sent_install", false)) {
            if (preferences.getBoolean("existing", false)) {
                appStatus = AppStatus.UPDATE
            }
            MoEHelper.getInstance(applicationContext).setAppStatus(appStatus)
            preferences.edit().putBoolean("has_sent_install", true).apply()
            preferences.edit().putBoolean("existing", true).apply()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }

    //Application going background
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {

    }


    //Application coming to foreground
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {

    }

    fun setCurrentChatJobID(jobID: String) {
        this.jobID = jobID
    }

    fun getJobChatId(): String? {
        return this.jobID
    }

    fun refreshData(intent: Intent) {
        if (refreshDataListener != null) {
            refreshDataListener?.refreshData(intent)
        }
    }

    private var refreshDataListener: OnRefreshDataListener? = null
    public fun refreshDataListener(refreshDataListener: OnRefreshDataListener?) {
        this.refreshDataListener = refreshDataListener;
    }

    interface OnRefreshDataListener {
        fun refreshData(intent: Intent)
    }

}