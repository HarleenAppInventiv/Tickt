package com.example.ticktapp.mvvm.view

import CoreUtils
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.model.registrationmodel.LocationFinder
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAbnBinding
import com.example.ticktapp.model.registration.TokenModel
import com.example.ticktapp.mvvm.viewmodel.ABNViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties
import kotlinx.android.synthetic.main.toolbar_onboarding.*
import org.json.JSONObject

class ABNActivity : BaseActivity(), View.OnFocusChangeListener, View.OnClickListener, TextWatcher {
    private lateinit var mBinding: ActivityAbnBinding
    private var socialId: String? = null
    private var accountType: String? = null
    private var authType: String? = null
    private var password: String? = null
    private var firstName: String? = null
    private var mobileNumber: String? = null
    private var email: String? = null
    private lateinit var mLocationManager: LocationManager
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var loc: LocationFinder? = null
    private var onBoardingData: OnBoardingData? = null
    private val mViewModel by lazy { ViewModelProvider(this).get(ABNViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_abn)
        mBinding.model = mViewModel
        curreLocationFinder()
        getIntentData()
        getIntentDataForBuilder()
        initView()
        setProgressDots()
        setObservers()
        setListeners()
    }

    private fun getIntentData() {
        onBoardingData = intent.getParcelableExtra<OnBoardingData>(IntentConstants.DATA)
        when (PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)) {
            SocialType.GOOGLE -> {
                authType = "signup"
                socialId = PreferenceManager.getString(PreferenceManager.SOCIAL_ID)
                accountType = "google"
                //   password = ""
            }
            SocialType.LINKEDIN -> {
                authType = "signup"
                socialId = PreferenceManager.getString(PreferenceManager.SOCIAL_ID)
                accountType = "linkedin"
                // password = ""
            }

            SocialType.NORMAL -> {
                // password = onBoardingData?.password
            }
        }

    }

    private fun getIntentDataForBuilder() {
        firstName = intent.getStringExtra(IntentConstants.FIRST_NAME)
        mobileNumber = intent.getStringExtra(IntentConstants.MOBILE_NUMBER)
        email = intent.getStringExtra(IntentConstants.EMAIL)
        password = intent.getStringExtra(IntentConstants.PASSWORD)
        if (this.password == null) {
            password = onBoardingData?.password
        }
    }


    private fun setListeners() {
        iv_back.setOnClickListener(this)
        mBinding.edtName.onFocusChangeListener = this
        mBinding.edtPos.onFocusChangeListener = this
        mBinding.edtAbn.onFocusChangeListener = this

        mBinding.tvYellowBtn.setOnClickListener {
            if (onBoardingData?.email != null)
                PreferenceManager.putString(PreferenceManager.EMAIL, onBoardingData?.email)
            CoreUtils.getDeviceToken(
                {
                    mBinding.tvNameError.visibility = View.INVISIBLE
                    mBinding.tvPosError.visibility = View.INVISIBLE
                    mBinding.tvAbnError.visibility = View.INVISIBLE
                    mBinding.edtName.clearFocus()
                    mBinding.edtPos.clearFocus()
                    mBinding.edtAbn.clearFocus()
                    if (loc == null) {
                        val arr: ArrayList<Double> = arrayListOf()
                        arr.add(144.9631)
                        arr.add(-37.8136)
                        loc = LocationFinder("Point", arr)
                    }
                    if (onBoardingData?.qualification == null || onBoardingData?.qualification!!.size == 0) {
                        onBoardingData?.qualification = ArrayList()
                    }
                    it?.let {
                        if (PreferenceManager.getString(PreferenceManager.USER_TYPE)
                                ?.toInt() == 2
                        ) {
                            onBoardingData = OnBoardingData(
                                firstName = firstName,
                                email = email,
                                mobileNumber = mobileNumber?.replace(" ", ""),
                                password = password,
                                trade = onBoardingData?.trade,
                                specialization = onBoardingData?.specialization,
                                abn = mBinding.edtAbn.text.toString().replace(" ", ""),
                                deviceToken = PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN),
                                user_type = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                                    ?.toInt(),
                                company_name = mBinding.edtName.text.toString(),
                                position = mBinding.edtPos.text.toString(),
                                socialId = socialId,
                                accountType = accountType,
                                qualification = onBoardingData?.qualification,
                                authType = authType,
                                location = loc
                            )
                        } else {
                            onBoardingData = OnBoardingData(
                                firstName = onBoardingData?.firstName,
                                email = onBoardingData?.email,
                                mobileNumber = onBoardingData?.mobileNumber?.replace(" ", ""),
                                password = password,
                                trade = onBoardingData?.trade,
                                specialization = onBoardingData?.specialization,
                                qualification = onBoardingData?.qualification,
                                abn = mBinding.edtAbn.text.toString().replace(" ", ""),
                                deviceToken = PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN),
                                user_type = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                                    ?.toInt(),
                                socialId = socialId,
                                accountType = accountType,
                                authType = authType,
                                location = loc
                            )
                        }
                        mViewModel.registerUser(onBoardingData!!)
                    }
                }, {

                })
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
        if (tokenModel.firstName != null)
            people["first_name"] = tokenModel.firstName
        people["last_name"] = tokenModel.firstName
        if (tokenModel.userName != null)
            people["first_name"] = tokenModel.userName
        people["last_name"] = tokenModel.userName


        people["\$email"] = tokenModel.email

        val props = JSONObject()

        props.put(MoEngageConstants.SUCCESS_STATUS, true)
        props.put(MoEngageConstants.SIGN_UP_SOURCE, source)
        if (tokenModel.userName != null) {
            props.put(MoEngageConstants.NAME, tokenModel.userName!!)
        } else {
            props.put(MoEngageConstants.NAME, tokenModel.firstName!!)
        }
        props.put(MoEngageConstants.EMAIL, tokenModel.email!!)
        props.put(MoEngageConstants.USER_TYPE, userType)
        props.put(MoEngageConstants.PLATFORM, "Android")

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_SIGN_UP, props)
    }

    fun setProgressDots() {
        val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        if (userType!!.toInt() == UserType.TRAIDIE) {

            mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
            mBinding.rlToolbar.v1.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v2.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v3.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v4.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v5.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v6.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)

            if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 1) {
                mBinding.rlToolbar.v7.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            } else {
                mBinding.rlToolbar.v7.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            }

            if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 1) {
                mBinding.rlToolbar.v8.visibility = View.VISIBLE
                mBinding.rlToolbar.v8.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            }
        } else {
            mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
            mBinding.rlToolbar.v1.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v2.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v3.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v4.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v5.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 2) {
                mBinding.rlToolbar.v8.visibility = View.GONE
                mBinding.rlToolbar.v7.visibility = View.GONE
                mBinding.rlToolbar.v6.visibility = View.GONE
                mBinding.rlToolbar.v5.visibility = View.VISIBLE
                mBinding.rlToolbar.v5.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            }

        }


    }

    private fun initView() {
        if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 2) {
            mBinding.llCompany.visibility = View.VISIBLE
            mBinding.edtName.inputType =
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            mBinding.edtPos.inputType =
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        } else
            mBinding.llCompany.visibility = View.GONE

        mBinding.tvYellowBtn.text = getString(R.string.create_account)
        mBinding.rlToolbar.tvTitle.text = getString(R.string.almost_done)
        mBinding.edtAbn.addTextChangedListener(this)
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
            mBinding.tvAbnError.visibility = View.INVISIBLE
            mBinding.tvPosError.visibility = View.INVISIBLE
            when (it.type) {
                ValidationsConstants.COMPANY_EMPTY -> {
                    mBinding.tvNameError.visibility = View.VISIBLE
                    mBinding.tvNameError.text = it.message
                }
                ValidationsConstants.POSITION_EMPTY -> {
                    mBinding.tvPosError.visibility = View.VISIBLE
                    mBinding.tvPosError.text = it.message
                }
                ValidationsConstants.ABN_EMPTY, ValidationsConstants.ABN_INVALID -> {
                    mBinding.tvAbnError.visibility = View.VISIBLE
                    mBinding.tvAbnError.text = it.message
                }
            }
        })
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.REGISTER_USER -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.REGISTER_USER -> {
                mViewModel.registrationModel?.let {
                    var userType = "Tradie"
                    if (it.user_type == 1) {
                        userType = "Tradie"
                    } else {
                        userType = "Builder"
                    }
                    addUserOnMoEngage(it, "Normal", userType)
                    addUserMixPanel(it, "Normal", userType)
                }
                finishAffinity()
                startActivity(Intent(this, DoneActivity::class.java))
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            mBinding.edtName -> if (hasFocus) mBinding.tvNameError.visibility = View.INVISIBLE
            mBinding.edtPos -> if (hasFocus) mBinding.tvPosError.visibility = View.INVISIBLE
            mBinding.edtAbn -> if (hasFocus) mBinding.tvAbnError.visibility = View.INVISIBLE
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> onBackPressed()
        }
    }

    private fun curreLocationFinder() {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS()
        } else {
            getLocation()
        }
    }

    private fun onGPS() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
        title.text = getString(R.string.alert)

        val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
        msg.text = getString(R.string.enable_gps)

        val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
        dialogBtn_okay.text = getString(R.string.yes)
        dialogBtn_okay.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
        dialogBtn_cancel.text = getString(R.string.no)
        dialogBtn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@ABNActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@ABNActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, Array(2) { Manifest.permission.ACCESS_FINE_LOCATION },
                Constants.REQUEST_LOCATION
            )
        } else {
            val locationGPS: Location? =
                mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (locationGPS != null) {
                val lat = locationGPS.latitude
                val long = locationGPS.longitude
                latitude = lat
                longitude = long
            } else {
                val lat = PreferenceManager.getString(PreferenceManager.LAT)
                val lan = PreferenceManager.getString(PreferenceManager.LAN)
                if (lat != null && !lat.equals("") && lan != null && !lan.equals("")) {
                    latitude = lat.toDouble()
                    longitude = lan.toDouble()
                }
            }

            val arr: ArrayList<Double> = arrayListOf()
            longitude?.let { arr.add(it) }
            latitude?.let { arr.add(it) }
            loc = LocationFinder("Point", arr)
        }

    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val origin: String = s.toString().replace(" ", "")
        val formatStr: String = formatStrWithSpaces(origin)
        if (!s.toString().equals(formatStr)) {
            editTextSetContentMemorizeSelection(mBinding.edtAbn, formatStr)
            if (before == 0 && count == 1 && formatStr[mBinding.edtAbn.selectionEnd - 1] == ' ') {
                mBinding.edtAbn.setSelection(mBinding.edtAbn.selectionEnd + 1)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    private fun editTextSetContentMemorizeSelection(
        editText: EditText,
        charSequence: CharSequence
    ) {
        var selectionStart = editText.selectionStart
        var selectionEnd = editText.selectionEnd
        editText.setText(charSequence.toString())
        if (selectionStart > charSequence.toString().length) {
            selectionStart = charSequence.toString().length
        }
        if (selectionStart < 0) {
            selectionStart = 0
        }
        if (selectionEnd > charSequence.toString().length) {
            selectionEnd = charSequence.toString().length
        }
        if (selectionEnd < 0) {
            selectionEnd = 0
        }
        editText.setSelection(selectionStart, selectionEnd)
    }

    private fun formatStrWithSpaces(can: CharSequence): String {
        val sb = StringBuffer()
        for (i in can.indices) {
            if (i != 0 && (i == 2 || i == 5 || i == 8)) {
                sb.append(' ')
            }
            sb.append(can[i])
        }
        return sb.toString()
    }
}
