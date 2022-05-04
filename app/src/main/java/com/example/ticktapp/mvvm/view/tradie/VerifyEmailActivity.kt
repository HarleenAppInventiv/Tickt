package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityVerifyEmailBinding
import com.example.ticktapp.databinding.ActivityVerifyNumberBinding
import com.example.ticktapp.mvvm.view.LoginActivity
import com.example.ticktapp.mvvm.viewmodel.ChangeEmailViewModel
import com.example.ticktapp.mvvm.viewmodel.VerifyNumberViewModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class VerifyEmailActivity : BaseActivity(), View.OnClickListener,
    TextWatcher,
    View.OnKeyListener {
    private var email: String? = null
    private var newEmail: String? = null
    private var password: String? = null
    private var from: Int? = null
    private lateinit var mBinding: ActivityVerifyEmailBinding
    private var focusPlace: Int? = null


    private val mEmailViewModel by lazy { ViewModelProvider(this).get(ChangeEmailViewModel::class.java) }
    private val mViewModel by lazy { ViewModelProvider(this).get(VerifyNumberViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify_email)
        mBinding.model = mViewModel
        getIntentData()
        initView()
        setObservers()
        setListeners()
        mViewModel.startTimer()
        mBinding.etOtpOne.postDelayed({
            mBinding.etOtpOne.isSelected = true
            mBinding.etOtpOne.requestFocus()
            showSoftKeyboard()
        }, 300)
    }


    private fun getIntentData() {
        email = intent.getStringExtra("email")
        newEmail = intent.getStringExtra("newEmail")
        password = intent.getStringExtra("password")

    }

    private fun setListeners() {

        mBinding.tvYellowBtn.setOnClickListener(this)
        mBinding.tvResendCode.setOnClickListener(this)
        iv_back.setOnClickListener(this)

        mBinding.etOtpOne.addTextChangedListener(this)
        mBinding.etOtpTwo.addTextChangedListener(this)
        mBinding.etOtpThree.addTextChangedListener(this)
        mBinding.etOtpFour.addTextChangedListener(this)
        mBinding.etOtpFive.addTextChangedListener(this)

        mBinding.etOtpOne.setOnKeyListener(this)
        mBinding.etOtpTwo.setOnKeyListener(this)
        mBinding.etOtpThree.setOnKeyListener(this)
        mBinding.etOtpFour.setOnKeyListener(this)
        mBinding.etOtpFive.setOnKeyListener(this)

        mBinding.etOtpOne.onFocusChangeListener =
            View.OnFocusChangeListener(fun(_: View, hasFocus: Boolean) {
                if (hasFocus) {
                    focusPlace = 1
                    selectOtpBox(p1 = true, p2 = false, p3 = false, p4 = false, p5 = false)
                }
            })
        mBinding.etOtpTwo.onFocusChangeListener =
            View.OnFocusChangeListener(fun(_: View, hasFocus: Boolean) {
                if (hasFocus) {
                    focusPlace = 2
                    selectOtpBox(p1 = false, p2 = true, p3 = false, p4 = false, p5 = false)
                }
            })
        mBinding.etOtpThree.onFocusChangeListener =
            View.OnFocusChangeListener(fun(_: View, hasFocus: Boolean) {
                if (hasFocus) {
                    focusPlace = 3
                    selectOtpBox(p1 = false, p2 = false, p3 = true, p4 = false, p5 = false)
                }
            })
        mBinding.etOtpFour.onFocusChangeListener =
            View.OnFocusChangeListener(fun(_: View, hasFocus: Boolean) {
                if (hasFocus) {
                    focusPlace = 4
                    selectOtpBox(p1 = false, p2 = false, p3 = false, p4 = true, p5 = false)
                }
            })
        mBinding.etOtpFive.onFocusChangeListener =
            View.OnFocusChangeListener(fun(_: View, hasFocus: Boolean) {
                if (hasFocus) {
                    focusPlace = 5
                    selectOtpBox(p1 = false, p2 = false, p3 = false, p4 = false, p5 = true)
                }
            })

    }


    private fun initView() {
        mBinding.tvYellowBtn.setText(getString(R.string.next))
        mBinding.rlToolbar.tvTitle.setText(getString(R.string.verify_your_email))
    }


    override fun onClick(v: View) {
        when (v.id) {
            iv_back.id -> {
                onBackPressed()
            }
            mBinding.tvYellowBtn.id -> {

                if(!valid())
                    return
                val mObject = JsonObject()
                mObject.addProperty("newEmail", newEmail)
                mObject.addProperty(
                    "otp", mBinding.etOtpOne.text.toString() + mBinding.etOtpTwo.text.toString()
                            + mBinding.etOtpThree.text.toString() + mBinding.etOtpFour.text.toString()
                            + mBinding.etOtpFive.text.toString()
                )
                mEmailViewModel.verifyEmail(mObject)

            }
            mBinding.tvResendCode.id -> {
                val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                val mObject = JsonObject()
                mObject.addProperty("user_type", userType?.toInt())
                mObject.addProperty("newEmail", newEmail)
                mObject.addProperty("password", password)
                mObject.addProperty("currentEmail", email)
                mEmailViewModel.changeEmail(mObject)
            }
        }
    }


    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mEmailViewModel)
        mEmailViewModel.getResponseObserver().observe(this, this)



        mViewModel.getTimerTextLiveData()
            .observe(this, { timerText ->
                if (timerText == "00:00") {
                    mBinding.tvResendCode.text = getString(R.string.action_resend_code)
                    mBinding.tvResendCode.isEnabled = true
                } else {
                    mBinding.tvResendCode.text = timerText
                }
            })
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CHANGE_EMAIL -> {
                mBinding.tvResendCode.isEnabled = false
                mViewModel.startTimer()
                clearOtpBoxes()
            }
            ApiCodes.VERIFY_EMAIL -> {
                val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
                val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                PreferenceManager.clearAllPrefs()
                PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
                PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
                finishAffinity()
                startActivity(Intent(this, LoginActivity::class.java))
            }


        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    override fun beforeTextChanged(
        charSequence: CharSequence?,
        i: Int,
        i1: Int,
        i2: Int
    ) {
    }

    override fun onTextChanged(
        charSequence: CharSequence?,
        i: Int,
        i1: Int,
        i2: Int
    ) {

    }

    override fun afterTextChanged(s: Editable?) {
        if ((mBinding.etOtpOne.text.toString() + mBinding.etOtpTwo.text.toString() + mBinding.etOtpThree.text.toString() + mBinding.etOtpFour.text.toString() + mBinding.etOtpFive.text.toString()).length == 5) {
            hideKeyboard()
        }
    }

    private fun changeFocus(): Boolean {
        when {
            mBinding.etOtpOne.length() != 1 -> {
                mBinding.etOtpOne.requestFocus()
                handleOtpBoxSelector(1)
            }
            mBinding.etOtpTwo.length() != 1 -> {
                mBinding.etOtpTwo.requestFocus()
                handleOtpBoxSelector(2)
            }
            mBinding.etOtpThree.length() != 1 -> {
                mBinding.etOtpThree.requestFocus()
                handleOtpBoxSelector(3)
            }
            mBinding.etOtpFour.length() != 1 -> {
                mBinding.etOtpFour.requestFocus()
                handleOtpBoxSelector(4)
            }
            mBinding.etOtpFive.length() != 1 -> {
                mBinding.etOtpFive.requestFocus()
                handleOtpBoxSelector(5)
            }
            else -> {
                mBinding.etOtpFive.requestFocus()
                handleOtpBoxSelector(5)
                return true
            }
        }
        return false
    }

    override fun onKey(
        view: View,
        i: Int,
        keyEvent: KeyEvent
    ): Boolean {
        if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_DEL) {
            when (view.id) {
                mBinding.etOtpOne.id -> mBinding.etOtpOne.text = null
                mBinding.etOtpTwo.id -> if (mBinding.etOtpTwo.length() == 1) mBinding.etOtpTwo.text =
                    null else {
                    mBinding.etOtpOne.requestFocus()
                    handleOtpBoxSelector(1)
                }
                mBinding.etOtpThree.id -> if (mBinding.etOtpThree.length() == 1) mBinding.etOtpThree.text =
                    null else {
                    mBinding.etOtpTwo.requestFocus()
                    handleOtpBoxSelector(2)
                }
                mBinding.etOtpFour.id -> if (mBinding.etOtpFour.length() == 1) mBinding.etOtpFour.text =
                    null else {
                    mBinding.etOtpThree.requestFocus()
                    handleOtpBoxSelector(3)
                }
                mBinding.etOtpFive.id -> if (mBinding.etOtpFive.length() == 1) mBinding.etOtpFive.text =
                    null else {
                    mBinding.etOtpFour.requestFocus()
                    handleOtpBoxSelector(4)
                }
            }
            return true
        } else if (i >= KeyEvent.KEYCODE_0 && i <= KeyEvent.KEYCODE_9) {
            changeFocus()
        }
        return false
    }

    private fun clearOtpBoxes() {
        mBinding.etOtpOne.text = null
        mBinding.etOtpTwo.text = null
        mBinding.etOtpThree.text = null
        mBinding.etOtpFour.text = null
        mBinding.etOtpFive.text = null
        mBinding.etOtpOne.postDelayed({
            mBinding.etOtpOne.requestFocus()
            showSoftKeyboard()
        }, 300)
    }

    private fun selectOtpBox(
        p1: Boolean,
        p2: Boolean,
        p3: Boolean,
        p4: Boolean,
        p5: Boolean,
    ) {
        mBinding.etOtpOne.isSelected = p1
        mBinding.etOtpTwo.isSelected = p2
        mBinding.etOtpThree.isSelected = p3
        mBinding.etOtpFour.isSelected = p4
        mBinding.etOtpFive.isSelected = p5
    }

    private fun handleOtpBoxSelector(place: Int) {
        when (place) {
            1 -> {
                selectOtpBox(p1 = true, p2 = false, p3 = false, p4 = false, p5 = false)
            }
            2 -> {
                selectOtpBox(p1 = false, p2 = true, p3 = false, p4 = false, p5 = false)
            }
            3 -> {
                selectOtpBox(p1 = false, p2 = false, p3 = true, p4 = false, p5 = false)
            }
            4 -> {
                selectOtpBox(p1 = false, p2 = false, p3 = false, p4 = true, p5 = false)
            }
            5 -> {
                selectOtpBox(p1 = false, p2 = false, p3 = false, p4 = false, p5 = true)
            }
        }
    }

    fun valid() :Boolean{
        mBinding.tvOtpError.visibility = View.INVISIBLE
        val otp =
            mBinding.etOtpOne.text.toString() + mBinding.etOtpTwo.text.toString() + mBinding.etOtpThree.text.toString() + mBinding.etOtpFour.text.toString() + mBinding.etOtpFive.text.toString()
        if (otp.isEmpty()) {
            mBinding.tvOtpError.visibility = View.VISIBLE
            mBinding.tvOtpError.setText(getString(R.string.please_enter_otp))
            return false

        } else if (otp.length != 5) {
            mBinding.tvOtpError.visibility = View.VISIBLE
            mBinding.tvOtpError.setText(getString(R.string.please_enter_complete_OTP))
            return false
        }
        return true
    }


}