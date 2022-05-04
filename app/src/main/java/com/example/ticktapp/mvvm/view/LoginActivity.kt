package com.example.ticktapp.mvvm.view

import CoreUtils
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.airhireme.data.model.onboarding.social.SocialResponse
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.preferences.PreferenceManager
import com.app.core.preferences.PreferenceManager.IS_LOGIN
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.base.LoaderType
import com.example.ticktapp.databinding.ActivityLoginBinding
import com.example.ticktapp.linkedIn.LinkedInConstants
import com.example.ticktapp.linkedIn.LinkedInSign
import com.example.ticktapp.model.registration.TokenModel
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.tradie.HomeActivity
import com.example.ticktapp.mvvm.viewmodel.LoginViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.example.ticktapp.util.makeLinks
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.googlelibrary.GoogleSignInAI
import com.googlelibrary.interfaces.GoogleSignInCallback
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import kotlinx.android.synthetic.main.toolbar_onboarding.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit


/**
 * Activity used to login the user
 */
class LoginActivity : BaseActivity(), View.OnFocusChangeListener,
    View.OnClickListener, GoogleSignInCallback {
    private var lastTime: Long = 0
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mLinkedInSign: LinkedInSign
    private lateinit var mGoogleSignInAI: GoogleSignInAI
    private val socialResponse: SocialResponse = SocialResponse()
    private var onBoardingData: OnBoardingData? = null
    private lateinit var mactivity: LoginActivity
    lateinit var linkedinAuthURLFull: String
    lateinit var linkedIndialog: Dialog
    lateinit var linkedinCode: String
    private var from: Int? = null
    private var LOGIN_TYPE = "Normal"


    private val mViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mBinding.model = mViewModel
        mactivity = this
        initializeGoogle()
        initializeLinkedin()
        getIntentData()
        initView()
        setObservers()
        setListeners()
        setSpannable()
    }

    private fun getIntentData() {
        from = intent.getIntExtra(IntentConstants.LINKEDINCLICK, 0)

        if (from == Constants.LINKEDINRESULT) {
            linkdinSignInSuccessResult(mLinkedInSign)
        }
    }


    /**
     * Initialize Google instance
     */
    private fun initializeGoogle() {
        mGoogleSignInAI = GoogleSignInAI()
        mGoogleSignInAI.setActivity(this)
        mGoogleSignInAI.setRequestCode(Constants.GOOGLE)
        mGoogleSignInAI.setUpGoogleClientForGoogleLogin()
    }

    private fun initializeLinkedin() {
        mLinkedInSign = LinkedInSign()
        val state = "linkedin" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        linkedinAuthURLFull =
            LinkedInConstants.AUTHURL + "?response_type=code&client_id=" + LinkedInConstants.CLIENT_ID + "&scope=" + LinkedInConstants.SCOPE + "&state=" + state + "&redirect_uri=" + LinkedInConstants.REDIRECT_URI
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupLinkedinWebviewDialog(url: String) {
        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        linkedIndialog = Dialog(this)
        val webView = WebView(this)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = LinkedInWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.loadUrl(url)
        linkedIndialog.setContentView(webView)
        linkedIndialog.show()

    }


    private fun setSpannable() {
        mBinding.tvSignIn.makeLinks(
            //color_0b41a8
            Pair(getString(R.string.sign_up), View.OnClickListener {
                startActivity(
                    Intent(
                        this,
                        SignupProcessActivity::class.java
                    )
                )
                finish()
            })
        )
    }

    private fun setListeners() {
        mBinding.edtPass.onFocusChangeListener = this
        mBinding.edtEmail.onFocusChangeListener = this
        iv_back.setOnClickListener(this)
        mBinding.flGoogle.setOnClickListener(this)
        mBinding.flLinkedin.setOnClickListener(this)

        mBinding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, EmailVerificationActivity::class.java).apply {
                putExtra(IntentConstants.EMAIL, "")
                putExtra(IntentConstants.FIRST_NAME, "")
                putExtra(IntentConstants.FROM, Constants.FORGOT_PASSWORD)
            })

        }
        mBinding.tvYellowBtn.setOnClickListener {
            CoreUtils.getDeviceToken({
                mBinding.tvPasswordError.visibility = View.INVISIBLE
                mBinding.tvEmailError.visibility = View.INVISIBLE
                mBinding.edtEmail.clearFocus()
                mBinding.edtPass.clearFocus()
                it?.let { it1 ->
                    LOGIN_TYPE = "Normal"
                    mViewModel.hitLoginApi(
                        it1,
                        PreferenceManager.getString(PreferenceManager.USER_TYPE).toString()
                    )
                }
            },
                {
                })

        }
    }


    private fun initView() {
        mBinding.tvYellowBtn.setText(getString(R.string.log_in))
        mBinding.rlToolbar.tvTitle.setText(getString(R.string.log_in))
        PreferenceManager.putString(IntentConstants.TOKEN, "")

    }

    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
            mBinding.tvEmailError.visibility = View.INVISIBLE
            mBinding.tvPasswordError.visibility = View.INVISIBLE
            when (it.type) {
                ValidationsConstants.EMAIL_EMPTY, ValidationsConstants.EMAIL_INVALID -> {
                    mBinding.tvEmailError.visibility = View.VISIBLE
                    mBinding.tvEmailError.text = it.message
                }
                ValidationsConstants.PASSWORD_EMPTY, ValidationsConstants.PASSWORD_INVALID -> {
                    mBinding.tvPasswordError.visibility = View.VISIBLE
                    mBinding.tvPasswordError.text = it.message
                }
            }
        })
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.LOGIN, ApiCodes.REGISTER_USER, ApiCodes.SOCIAL_ID -> {
                showToastLong(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.LOGIN -> {
                finish()
                PreferenceManager.putBoolean(IS_LOGIN, true)
                val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                PreferenceManager.putString(
                    PreferenceManager.EMAIL,
                    mBinding.edtEmail.text.toString()
                )
                if (!userType.isNullOrEmpty() && userType.equals("1")) {
                    mViewModel.registrationModel?.let {
                        addUserOnMoEngage(it, "Normal", "Tradie")
                        addUserMixPanel(it, "Normal", "Tradie")
                    }

                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                    ActivityCompat.finishAffinity(this)
                } else {
                    mViewModel.registrationModel?.let {
                        addUserOnMoEngage(it, "Normal", "Builder")
                        addUserMixPanel(it, "Normal", "Builder")
                    }
                    startActivity(Intent(this, HomeBuilderActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                    ActivityCompat.finishAffinity(this)
                }
            }
            ApiCodes.SOCIAL_ID -> {
                mViewModel.getVerifyEmailData()?.let {
                    if (it.isProfileCompleted == true) {
                        CoreUtils.getDeviceToken({
                            it?.let {
                                onBoardingData = OnBoardingData(
                                    //  firstName = socialResponse.name,
                                    email = socialResponse.email,
                                    deviceToken = PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN),
                                    user_type = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                                        ?.toInt(),
                                    socialId = socialResponse.id,
                                    accountType = socialResponse.accountType,
                                    authType = "login"
                                )
                                mViewModel.socialAuth(onBoardingData!!)

                            }
                        }, {

                        })
                    } else {
                        startActivity(Intent(this, PhoneNumberActivity::class.java).apply {
                            if (socialResponse.accountType.equals("linkedIn")) {
                                PreferenceManager.putInt(
                                    PreferenceManager.SOCIAL_TYPE, SocialType.LINKEDIN
                                )
                            } else {
                                PreferenceManager.putInt(
                                    PreferenceManager.SOCIAL_TYPE, SocialType.GOOGLE
                                )
                            }
                            PreferenceManager.putString(
                                PreferenceManager.SOCIAL_ID,
                                socialResponse.id
                            )
                            putExtra(IntentConstants.EMAIL, socialResponse.email)
                            putExtra(IntentConstants.FIRST_NAME, socialResponse.name)
                            putExtra(IntentConstants.FROM, Constants.REGISTER)
                        })
                    }
                }
            }
            ApiCodes.REGISTER_USER -> {
                if (socialResponse.accountType.equals("linkedIn")) {
                    LOGIN_TYPE = "LinkedIn"
                    PreferenceManager.putInt(
                        PreferenceManager.SOCIAL_TYPE, SocialType.LINKEDIN
                    )
                } else {
                    LOGIN_TYPE = "Google"
                    PreferenceManager.putInt(
                        PreferenceManager.SOCIAL_TYPE, SocialType.GOOGLE
                    )
                }
                PreferenceManager.putString(
                    PreferenceManager.SOCIAL_ID,
                    socialResponse.id
                )
                finish()
                PreferenceManager.putBoolean(IS_LOGIN, true)
                val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                if (!userType.isNullOrEmpty() && userType.equals("1")) {
                    mViewModel.registrationModel?.let {
                        addUserOnMoEngage(
                            it,
                            LOGIN_TYPE,
                            "Tradie"
                        )


                        addUserMixPanel(
                            it,
                            LOGIN_TYPE,
                            "Tradie"
                        )
                    }
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                    ActivityCompat.finishAffinity(this)
                } else {
                    mViewModel.registrationModel?.let {
                        addUserOnMoEngage(
                            it,
                            LOGIN_TYPE,
                            "Builder"
                        )
                        addUserMixPanel(
                            it,
                            LOGIN_TYPE,
                            "Tradie"
                        )
                    }
                    startActivity(Intent(this, HomeBuilderActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                    ActivityCompat.finishAffinity(this)
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            mBinding.edtEmail -> if (hasFocus) mBinding.tvEmailError.visibility = View.INVISIBLE
            mBinding.edtPass -> if (hasFocus) {
                mBinding.tvPasswordError.visibility = View.INVISIBLE
                mBinding.rlPasswordViewgroup.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_round_dfe5ef)
            } else {
                mBinding.rlPasswordViewgroup.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_round_dfe5ef)
            }
        }

    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> onBackPressed()
            mBinding.flGoogle -> {
                LOGIN_TYPE = "Google"
                PreferenceManager.putInt(
                    PreferenceManager.SOCIAL_TYPE, SocialType.GOOGLE
                )
                socialResponse.accountType = "google"
                logoutFromGoogle()
                mGoogleSignInAI.setSignInCallback(this)
                mGoogleSignInAI.doSignIn()
            }
            mBinding.flLinkedin -> {
                LOGIN_TYPE = "LinkedIn"
                if (lastTime + 2000 < System.currentTimeMillis()) {
                    lastTime = System.currentTimeMillis()
                    PreferenceManager.putInt(
                        PreferenceManager.SOCIAL_TYPE, SocialType.LINKEDIN
                    )
                    socialResponse.accountType = "linkedIn"
                    setupLinkedinWebviewDialog(linkedinAuthURLFull)
                    linkdinSignInSuccessResult(mLinkedInSign)


                    /*    LinkedInBuilder.getInstance(this)
                        .setClientID(LinkedInConstants.CLIENT_ID)
                        .setClientSecret(LinkedInConstants.CLIENT_SECRET)
                        .setRedirectURI(LinkedInConstants.REDIRECT_URI)
                        .authenticate(Constants.LINKEDIN);

                 */
                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.GOOGLE -> {
                mGoogleSignInAI.onActivityResult(data)
            }
            /*      Constants.LINKEDIN -> {
                      val user = data?.getParcelableExtra<LinkedInUser>("social_login")
                      user?.let { linkdinSignInSuccessResult(it) }
                  }

             */
        }
    }

    fun linkdinSignInSuccessResult(linkedInSign: LinkedInSign?) {
        val fulllName = linkedInSign?.linkedinFirstName + " " + linkedInSign?.linkedinLastName
        socialResponse.id = linkedInSign?.linkedinId
        socialResponse.name = fulllName
        socialResponse.email = linkedInSign?.linkedinEmail
        socialResponse.isEmailVerified = true
        socialResponse.socialType = SocialType.LINKEDIN.toString()
        socialResponse.accountType = "linkedIn"
        /*   socialResponse.profileUrl?.let {
               socialResponse.profileUrl = it.toString()
               Log.d("linkedIn Response: ", socialResponse.profileUrl.toString())
           } ?: let {
               socialResponse.profileUrl = null
           }

         */

        Log.d("linkedIn Response: ", socialResponse.id.toString())
        Log.d("linkedIn Response: ", socialResponse.name.toString())
        Log.d("linkedIn Response: ", socialResponse.email.toString())

        // hit social sign in api
        socialResponse.id?.let {
            mViewModel.checkSocialId(
                it, socialResponse.email.toString(),
                PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE).toString()
            )
        }
    }

    override fun googleSignInSuccessResult(googleSignInAccount: GoogleSignInAccount?) {
        socialResponse.id = googleSignInAccount?.id
        socialResponse.name = googleSignInAccount?.displayName
        socialResponse.email = googleSignInAccount?.email
        socialResponse.isEmailVerified = true
        socialResponse.socialType = SocialType.GOOGLE.toString()
        socialResponse.accountType = "google"
        googleSignInAccount?.photoUrl?.let {
            socialResponse.profileUrl = it.toString()
            Log.d("Google Response: ", socialResponse.profileUrl.toString())
        } ?: let {
            socialResponse.profileUrl = null
        }
        Log.d("Google Response: ", socialResponse.id.toString())
        Log.d("Google Response: ", socialResponse.name.toString())
        Log.d("Google Response: ", socialResponse.email.toString())

        // hit social sign in api
        socialResponse.id?.let {
            mViewModel.checkSocialId(
                it, socialResponse.email.toString(),
                PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE).toString()
            )
        }
    }

    override fun googleSignInFailureResult(message: String?) {
        Log.d("Google Error Response: ", message.toString())
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    @Suppress("OverridingDeprecatedMember")
    inner class LinkedInWebViewClient : WebViewClient() {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request?.url.toString().startsWith(LinkedInConstants.REDIRECT_URI)) {
                handleUrl(request?.url.toString())

                // Close the dialog after getting the authorization code
                if (request?.url.toString().contains("?code=")) {
                    linkedIndialog.dismiss()

                }
                return true
            }
            return false
        }

        // Check webview url for access token code or error
        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)

            if (url.contains("code")) {
                showProgressDialog(LoaderType.NORMAL, "")
                LinkedInConstants.CODE = uri.getQueryParameter("code") ?: ""
                mLinkedInSign.linkedInRequestForAccessToken(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        hideProgressDialog(LoaderType.NORMAL, "")
                        linkdinSignInSuccessResult(mLinkedInSign)
                    }
                })
            } else if (url.contains("error")) {
                val error = uri.getQueryParameter("error") ?: ""
                Log.e("Error: ", error)
            }
        }
    }

    private fun addUserOnMoEngage(tokenModel: TokenModel, source: String, userType: String) {
        if (tokenModel._id != null)
            MoEHelper.getInstance(this).setUniqueId(tokenModel._id!!)
        if (tokenModel.userName != null) {
                MoEHelper.getInstance(this).setFullName(tokenModel.userName!!)
            } else {
                MoEHelper.getInstance(this).setFullName(tokenModel.firstName!!)
            }
        if (tokenModel.email != null)
            MoEHelper.getInstance(this).setEmail(tokenModel.email!!)


        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.SUCCESS_STATUS, true)
        signUpProperty.addAttribute(MoEngageConstants.SIGN_UP_SOURCE, source)
        if (tokenModel.userName != null) {
            signUpProperty.addAttribute(MoEngageConstants.NAME, tokenModel.userName!!)
        } else {
            signUpProperty.addAttribute(MoEngageConstants.NAME, tokenModel.firstName!!)
        }
        signUpProperty.addAttribute(MoEngageConstants.EMAIL, tokenModel.email!!)
        signUpProperty.addAttribute(MoEngageConstants.USER_TYPE, userType)
        signUpProperty.addAttribute(MoEngageConstants.PLATFORM, "Android")

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_SIGN_UP,
            signUpProperty
        )
    }

    private fun addUserMixPanel(tokenModel: TokenModel, source: String, userType: String) {
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )
        mixpanel.identify(tokenModel._id)
        mixpanel.getPeople().identify(tokenModel._id)

        val people: MixpanelAPI.People = mixpanel.getPeople()

        if (tokenModel.firstName != null) {
            people["first_name"] = tokenModel.firstName
            people["last_name"] = tokenModel.firstName
        } else {
            people["first_name"] = tokenModel.userName
            people["last_name"] = tokenModel.userName
        }


        people["\$email"] = tokenModel.email

        val props = JSONObject()

        props.put(MoEngageConstants.SUCCESS_STATUS, true)
        props.put(MoEngageConstants.SIGN_UP_SOURCE, source)

        if (tokenModel.firstName != null) {
            props.put(MoEngageConstants.NAME, tokenModel.firstName!!)
        }else{
            props.put(MoEngageConstants.NAME, tokenModel.userName!!)
        }

        props.put(MoEngageConstants.EMAIL, tokenModel.email!!)
        props.put(MoEngageConstants.USER_TYPE, userType)
        props.put(MoEngageConstants.PLATFORM, "Android")

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_SIGN_UP, props)
    }
}