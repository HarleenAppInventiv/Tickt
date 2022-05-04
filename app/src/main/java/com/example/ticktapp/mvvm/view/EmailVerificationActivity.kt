package com.example.ticktapp.mvvm.view

import CoreUtils
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityEmailVerificationBinding
import com.example.ticktapp.mvvm.viewmodel.SignupProcessViewModel
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class EmailVerificationActivity : BaseActivity(), View.OnFocusChangeListener, View.OnClickListener {
    private lateinit var mBinding: ActivityEmailVerificationBinding
    private var email: String? = null
    private var name: String? = null
    private var from: Int? = null


    private val mViewModel by lazy { ViewModelProvider(this).get(SignupProcessViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_email_verification)
        getIntentData()
        initView()
        setObservers()
        setListeners()
    }


    private fun setListeners() {
        iv_back.setOnClickListener(this)
        mBinding.edtEmail.onFocusChangeListener = this
    }

    private fun getIntentData() {
        email = intent.getStringExtra(IntentConstants.EMAIL)
        name = intent.getStringExtra(IntentConstants.FIRST_NAME)
        from = intent.getIntExtra(IntentConstants.FROM, 0)
    }


    private fun initView() {
        mBinding.tvYellowBtn.setText(getString(R.string.next))
        if (from == Constants.FORGOT_PASSWORD) {
            mBinding.tvPhoneSubheading.visibility = View.VISIBLE
        } else {
            setProgressDots()
            mBinding.tvPhoneSubheading.visibility = View.GONE
        }
        toolbarText()
        mBinding.tvYellowBtn.setOnClickListener {
            if (mBinding.edtEmail.text.toString().trim().isEmpty()) {
                mBinding.tvPhoneError.visibility = View.VISIBLE
                mBinding.tvPhoneError.setText(getString(R.string.please_enter_email_address_))
            } else if (!CoreUtils.isEmailValid(mBinding.edtEmail.text.toString())) {
                mBinding.tvPhoneError.visibility = View.VISIBLE
                mBinding.tvPhoneError.setText(getString(R.string.email_is_not_valid))
            } else {
                if (from == Constants.FORGOT_PASSWORD) {
                    mViewModel.hitVerfiyEmail(mBinding.edtEmail.text.toString())
                }
            }
        }
    }

    private fun toolbarText() {
        if (from == Constants.FORGOT_PASSWORD) {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.reset_password))
        } else {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.phone_number))
        }
    }


    fun setProgressDots() {

        val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        if (userType!!.toInt() == UserType.TRAIDIE) {

            mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
            mBinding.rlToolbar.v1.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
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
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
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

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.VERIFY_EMAIL -> {
                if (exception.status_code == 409) {
                    startActivity(Intent(this, VerifyEmailActivity::class.java).apply {
                        putExtra(
                            IntentConstants.MOBILE_NUMBER,
                            ""
                        )
                        putExtra(IntentConstants.FIRST_NAME, name)
                        putExtra(IntentConstants.EMAIL, mBinding.edtEmail.text.toString().trim())
                        putExtra(IntentConstants.FROM, from)
                    })
                } else {
                    showToastShort(exception.message)
                }
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.VERIFY_EMAIL -> {
                showToastShort(getString(R.string.user_not_exist))
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            mBinding.edtEmail -> if (hasFocus) mBinding.tvPhoneError.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            iv_back -> {
                onBackPressed()
            }
        }
    }


}