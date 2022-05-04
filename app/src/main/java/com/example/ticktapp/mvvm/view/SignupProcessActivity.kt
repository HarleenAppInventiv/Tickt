package com.example.ticktapp.mvvm.view

import CoreUtils
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.airhireme.data.model.onboarding.social.SocialResponse
import com.app.core.model.registrationmodel.LocationFinder
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.base.LoaderType
import com.example.ticktapp.databinding.ActivitySignupProcessBinding
import com.example.ticktapp.linkedIn.LinkedInConstants
import com.example.ticktapp.linkedIn.LinkedInSign
import com.example.ticktapp.model.registration.TokenModel
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.tradie.HomeActivity
import com.example.ticktapp.mvvm.viewmodel.SignupProcessViewModel
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


class SignupProcessActivity : BaseActivity(), View.OnFocusChangeListener,
    View.OnClickListener,
    GoogleSignInCallback {
    private var lastTime: Long = 0
    private lateinit var mBinding: ActivitySignupProcessBinding
    private lateinit var mGoogleSignInAI: GoogleSignInAI
    private lateinit var mLinkedInSign: LinkedInSign
    private lateinit var mLocationManager: LocationManager
    private val socialResponse: SocialResponse = SocialResponse()
    private var onBoardingData: OnBoardingData? = null

    private var isAgree: Boolean? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var loc: LocationFinder? = null
    lateinit var linkedinAuthURLFull: String
    lateinit var linkedIndialog: Dialog
    lateinit var linkedinCode: String
    private var LOGIN_TYPE = "Normal"


    private val mViewModel by lazy { ViewModelProvider(this).get(SignupProcessViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup_process)
        mBinding.model = mViewModel
        // CurreLocationFinder()
        initializeGoogle()
        initializeLinkedin()
        initView()
        setObservers()
        setListeners()
        setSpannable()
    }

    private fun setSpannable() {
        mBinding.tvTermAndServices.makeLinks(
            Pair(getString(R.string.action_terms_and_condition), View.OnClickListener {
                startActivity(Intent(this, StaticDataActivity::class.java).apply {
                    putExtra(IntentConstants.FROM, Constants.TERMS)
                })
            }),
            Pair(getString(R.string.action_privacy_policy), View.OnClickListener {
                startActivity(Intent(this, StaticDataActivity::class.java).apply {
                    putExtra(IntentConstants.FROM, Constants.PRIVACY)
                })
            })
        )


        mBinding.tvLogIn.makeLinks(
            //color_0b41a8
            Pair(getString(R.string.log_in), View.OnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            })
        )
    }

    private fun setListeners() {
        iv_back.setOnClickListener(this)
        mBinding.edtName.onFocusChangeListener = this
        mBinding.edtEmail.onFocusChangeListener = this
        mBinding.flGoogle.setOnClickListener(this)
        mBinding.flLinkedin.setOnClickListener(this)

        //   iv_back.setOnClickListener(this)
        mBinding.cbTermsServices.setOnCheckedChangeListener { _, state ->
            mViewModel.isTermsAndConditionAccepted = state
        }
    }


    private fun initView() {
        mBinding.tvYellowBtn.setText(getString(R.string.sign_up))
        mBinding.rlToolbar.tvTitle.setText(getString(R.string.create_account))
    }


    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
            mBinding.tvNameError.visibility = View.INVISIBLE
            mBinding.tvEmailError.visibility = View.INVISIBLE
            when (it.type) {
                ValidationsConstants.FULL_NAME_EMPTY -> {
                    mBinding.tvNameError.visibility = View.VISIBLE
                    mBinding.tvNameError.text = it.message
                }
                ValidationsConstants.EMAIL_EMPTY, ValidationsConstants.EMAIL_INVALID -> {
                    mBinding.tvEmailError.visibility = View.VISIBLE
                    mBinding.tvEmailError.text = it.message
                }
                ValidationsConstants.CHECK_TERMS_CONDITION -> {
                    showToastShort(it.message)
                }
            }
        })


    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            mBinding.edtEmail -> if (hasFocus) mBinding.tvEmailError.visibility = View.INVISIBLE
            mBinding.edtName -> if (hasFocus) mBinding.tvNameError.visibility = View.INVISIBLE
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            iv_back -> {
                onBackPressed()
            }
            mBinding.flGoogle -> {

                logoutFromGoogle()
                mGoogleSignInAI.setSignInCallback(this)
                mGoogleSignInAI.doSignIn()

            }
            mBinding.flLinkedin -> {
                if (lastTime + 2000 < System.currentTimeMillis()) {
                    lastTime = System.currentTimeMillis()
                    PreferenceManager.putInt(
                        PreferenceManager.SOCIAL_TYPE, SocialType.LINKEDIN
                    )
                    socialResponse.accountType = "linkedIn"
                    setupLinkedinWebviewDialog(linkedinAuthURLFull)
                    linkdinSignInSuccessResult(mLinkedInSign)

                    /*   LinkedInBuilder.getInstance(this)
                       .setClientID(LinkedInConstants.CLIENT_ID)
                       .setClientSecret(LinkedInConstants.CLIENT_SECRET)
                       .setRedirectURI(LinkedInConstants.REDIRECT_URI)
                       .authenticate(Constants.LINKEDIN);

                 */
                }

            }


        }
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.VERIFY_EMAIL, ApiCodes.REGISTER_USER, ApiCodes.SOCIAL_ID -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun noInternetConnection(apiCode: Int, msg: String?) {
        showToastShort(msg)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.VERIFY_EMAIL -> {
                mViewModel.getVerifyEmailData()?.let {
                    if (it.isProfileCompleted == true)
                        showToastShort(msg)
                    else {
                        startActivity(Intent(this, VerifyEmailActivity::class.java).apply {
                            putExtra(
                                IntentConstants.FIRST_NAME,
                                mBinding.edtName.text.toString().replace(" ", "")
                            )
                            putExtra(
                                IntentConstants.EMAIL,
                                mBinding.edtEmail.text.toString().replace(" ", "")
                            )
                            putExtra(IntentConstants.FROM, Constants.REGISTER)
                        })
                    }
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
                var tokenModel: TokenModel = TokenModel()
                PreferenceManager.putBoolean(PreferenceManager.IS_LOGIN, true)
                val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                if (!userType.isNullOrEmpty() && userType.equals("1")) {

                    tokenModel._id = socialResponse.id ?: " "
                    tokenModel.firstName = socialResponse.name ?: " "
                    tokenModel.email = socialResponse.email ?: " "

                    addUserOnMoEngage(tokenModel, LOGIN_TYPE, "Tradie")
                    addUserMixPanel(tokenModel, LOGIN_TYPE, "Tradie")

                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                    ActivityCompat.finishAffinity(this)
                } else {
                    tokenModel._id = socialResponse.id ?: " "
                    tokenModel.firstName = socialResponse.name ?: " "
                    tokenModel.email = socialResponse.email ?: " "

                    addUserOnMoEngage(tokenModel, LOGIN_TYPE, "Builder")
                    addUserMixPanel(tokenModel, LOGIN_TYPE, "Builder")
                    startActivity(Intent(this, HomeBuilderActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.LOGIN)
                    })
                    ActivityCompat.finishAffinity(this)
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
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
        } else {
            props.put(MoEngageConstants.NAME, tokenModel.userName!!)
        }
        props.put(MoEngageConstants.EMAIL, tokenModel.email!!)
        props.put(MoEngageConstants.USER_TYPE, userType)
        props.put(MoEngageConstants.PLATFORM, "Android")

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_SIGN_UP, props)
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
                it,
                socialResponse.email.toString(),
                PreferenceManager.getString(PreferenceManager.USER_TYPE).toString()
            )
        }
    }

    override fun googleSignInFailureResult(message: String?) {
        Log.d("Google Error Response: ", message.toString())
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
        linkedIndialog = Dialog(this)
        val webView = WebView(this)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = LinkedInWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        linkedIndialog.setContentView(webView)
        linkedIndialog.show()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.GOOGLE -> {
                mGoogleSignInAI.onActivityResult(data)
            }
            /*    Constants.LINKEDIN -> {
                    val user = data?.getParcelableExtra<LinkedInUser>("social_login")
               //     user?.let { linkdinSignInSuccessResult(it) }
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


}




