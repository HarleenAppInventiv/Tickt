package com.example.ticktapp.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.tradie.HomeActivity

/**
 * Launcher UI that is opened first time when app is opened
 *
 */
class SplashActivity : BaseActivity() {
    private val mViewModel by lazy {
        ViewModelProvider(this).get(BaseViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        /* FirebaseFirestore.getInstance().collection("dffs").document("rahul")
             .set(mapOf("ra" to "fasd"))
             .addOnCompleteListener {
             }
             .addOnFailureListener {
                 Log.d("onCreate: ", "")
             }*/

        checkInstallMoengage()

        if (intent.data != null)
            onNewIntent(intent)
        else
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
                if (PreferenceManager.getBoolean(PreferenceManager.IS_LOGIN)) {
                    if (PreferenceManager.getString(PreferenceManager.USER_TYPE).equals("1")) {
                        startActivity(
                            Intent(
                                this,
                                HomeActivity::class.java
                            )
                        )
                    } else {
                        startActivity(
                            Intent(
                                this,
                                HomeBuilderActivity::class.java
                            )
                        )
                    }
                } else {
                    startActivity(
                        Intent(
                            this,
                            WelcomeActivity::class.java
                        )
                    )
                }
            }, 3000)


    }

    private fun checkInstallMoengage() {
        ApplicationClass.checkAppInstall(object : ApplicationClass.AppInstallsListener {
            override fun onFreshInstall() {
                Log.i("moengage_install_check", "Fresh Install")
            }

            override fun onAppUpdate() {
                Log.i("moengage_install_check", "Update Install")
            }
        })
    }


    override fun onNewIntent(intent: Intent?) {
        //dispatchIntent(intent)
        super.onNewIntent(intent)
    }


/*
    fun dispatchIntent(intent: Intent?) {
        val uri = intent?.data
        if (uri != null && uri.scheme != null && uri.host != null) {
            if (uri.scheme == "http" || uri.host == getString(R.string.host)) {
                var type = uri.pathSegments[0]

                when (type) {
                    DeepLinkTypes.REGISTER -> {
                        if (uri.getQueryParameter(DeepLinkTypes.DEEPLINK_TOKEN) != null && uri
                                .getQueryParameter(DeepLinkTypes.DEEPLINK_TOKEN) != null
                        ) {
                            val resetToken =
                                uri.getQueryParameter(DeepLinkTypes.DEEPLINK_TOKEN)
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    RegisterUserActivity::class.java
                                ).apply {
                                    putExtra(IntentConstants.TOKEN, resetToken)
                                    putExtra(IntentConstants.TYPE, IntentConstants.DEEPLINK)
                                }
                            )
                        }
                    }
                    DeepLinkTypes.RESET_PASSWORD -> {
                        if (uri.getQueryParameter(DeepLinkTypes.DEEPLINK_TOKEN) != null && uri.getQueryParameter(
                                "resettoken"
                            ) != null
                        ) {
                            val resetToken = uri.getQueryParameter(DeepLinkTypes.DEEPLINK_TOKEN)
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    ResetPasswordActivity::class.java
                                ).apply {
                                    putExtra(IntentConstants.TOKEN, resetToken)
                                    putExtra(IntentConstants.TYPE, IntentConstants.DEEPLINK)
                                }
                            )
                        }
                    }
                    DeepLinkTypes.UNBLOCK -> {
                        val otp =
                            uri.pathSegments[1]
                        val resetToken =
                            uri.getQueryParameter(DeepLinkTypes.DEEPLINK_TOKEN)


                        startActivity(
                            Intent(
                                this@SplashActivity,
                                OTPConfimationAcitvity::class.java
                            ).apply {
                                putExtra(IntentConstants.FROM, IntentConstants.ACCOUNT_BLOCKED)
//                                putExtra(IntentConstants.DATA, exception.message)
                                putExtra(
                                    IntentConstants.TOKEN,
                                    resetToken
                                )
                                putExtra(IntentConstants.OTP, otp)
                            }
                        )
                    }


                }
                finish()


            }
        }
    }
*/
}