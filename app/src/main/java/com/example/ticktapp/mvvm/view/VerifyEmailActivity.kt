package com.example.ticktapp.mvvm.view

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
import com.example.ticktapp.databinding.ActivityVerifyNumberBinding
import com.example.ticktapp.mvvm.viewmodel.VerifyNumberViewModel
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class VerifyEmailActivity : BaseActivity(), View.OnClickListener,
    TextWatcher,
    View.OnKeyListener {
    private var email: String? = null
    private var name: String? = null
    private var phoneno: String? = null
    private var from: Int? = null
    private lateinit var mBinding: ActivityVerifyNumberBinding
    private var focusPlace: Int? = null


    private val mViewModel by lazy { ViewModelProvider(this).get(VerifyNumberViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify_number)
        mBinding.model = mViewModel
        getIntentData()
        initView()
        setObservers()
        if (from == Constants.REGISTER) {
            setProgressDots()
        } else {
            email?.let { mViewModel.checkEmailId(it, false) }
        }
        setListeners()
        mViewModel.startTimer()
        mBinding.etOtpOne.postDelayed({
            mBinding.etOtpOne.isSelected = true
            mBinding.etOtpOne.requestFocus()
            showSoftKeyboard()
        }, 300)
    }


    private fun getIntentData() {
        email = intent.getStringExtra(IntentConstants.EMAIL)
        name = intent.getStringExtra(IntentConstants.FIRST_NAME)
        phoneno = intent.getStringExtra(IntentConstants.MOBILE_NUMBER)
        from = intent.getIntExtra(IntentConstants.FROM, 0)
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
        mBinding.tvVerificationInstructions.text = getString(R.string.email_verification_msg)
    }

    fun setProgressDots() {
        val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        if (userType!!.toInt() == UserType.TRAIDIE) {
            mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
            mBinding.rlToolbar.v1.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v2.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v3.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v4.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v5.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v6.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v7.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)

            if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 1) {
                mBinding.rlToolbar.v8.visibility = View.VISIBLE
                mBinding.rlToolbar.v8.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            }
        } else {
            mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
            mBinding.rlToolbar.v1.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v2.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v3.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v4.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
            mBinding.rlToolbar.v5.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
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


    override fun onClick(v: View) {
        when (v.id) {
            iv_back.id -> {
                onBackPressed()
            }
            mBinding.tvYellowBtn.id -> {
                email?.let {
                    mViewModel.hitVerifyEmailOtp(
                        mBinding.etOtpOne.text.toString() + mBinding.etOtpTwo.text.toString()
                                + mBinding.etOtpThree.text.toString() + mBinding.etOtpFour.text.toString()
                                + mBinding.etOtpFive.text.toString(), it
                    )
                }
            }
            mBinding.tvResendCode.id -> {
                email?.let { mViewModel.checkEmailId(it, true) }
            }
        }
    }


    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
            mBinding.tvOtpError.visibility = View.INVISIBLE

            when (it.type) {
                ValidationsConstants.OTP_EMPTY, ValidationsConstants.OTP_INVALID -> {
                    mBinding.tvOtpError.visibility = View.VISIBLE
                    mBinding.tvOtpError.text = it.message
                }
            }
        })

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


    override fun onException(exception: ApiError, apiCode: Int) {
        super.onException(exception, apiCode)
        when (apiCode) {
            ApiCodes.VERIFY_OTP -> {
                if (exception.status_code == ApiCodes.ACCOUNT_LOCKED) {
                }
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.VERIFY_OTP -> {
                finish()
                if (from == Constants.REGISTER) {
                    startActivity(Intent(this, PhoneNumberActivity::class.java).apply {
                        putExtra(IntentConstants.FROM, Constants.REGISTER)
                        PreferenceManager.putInt(
                            PreferenceManager.SOCIAL_TYPE,
                            SocialType.NORMAL
                        )
                        PreferenceManager.putString(
                            PreferenceManager.SOCIAL_ID,
                            ""
                        )
                        putExtra(IntentConstants.EMAIL, email)
                        putExtra(IntentConstants.FIRST_NAME, name)
                    })
                } else {
                    startActivity(Intent(this, CreatePasswordActivity::class.java).apply {
                        putExtra(IntentConstants.MOBILE_NUMBER, phoneno)
                        putExtra(IntentConstants.FIRST_NAME, name)
                        putExtra(IntentConstants.EMAIL, email)
                        putExtra(IntentConstants.FROM, from)
                    })
                }
            }

            ApiCodes.CHECK_PHONE_NUMBER -> {
                mViewModel.getVerifyPhoneData()?.let {
                    mBinding.tvResendCode.isEnabled = false
                    mViewModel.startTimer()
                    clearOtpBoxes()
                }
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


}