package com.example.ticktapp.mvvm.view.tradie

import CoreUtils
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityEmailChangeBinding
import com.example.ticktapp.mvvm.viewmodel.ChangeEmailViewModel
import com.google.gson.JsonObject

class ChangeEmailActivity : BaseActivity() {

    private lateinit var mBinding: ActivityEmailChangeBinding
    private var email = ""
    private val mViewModel by lazy { ViewModelProvider(this).get(ChangeEmailViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_email_change)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        setObservers()
        getDataIntent()
    }

    private fun getDataIntent() {
        email = intent?.getStringExtra("email").toString()
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun listener() {
        mBinding.jobDescBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.tvSave.setOnClickListener {
            if (isValid()) {
                val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                val mObject = JsonObject()
                mObject.addProperty("user_type", userType?.toInt())
                mObject.addProperty("newEmail", mBinding.edtEmail.text.toString())
                mObject.addProperty("password", mBinding.edtPass.text.toString())
                mObject.addProperty("currentEmail", email)
                mViewModel.changeEmail(mObject)

            }
        }

        mBinding.edtPass.addTextChangedListener(GenricWatcher(mBinding.edtPass))
        mBinding.edtEmail.addTextChangedListener(GenricWatcher(mBinding.edtEmail))
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CHANGE_EMAIL -> {
                startActivity(
                    Intent(this, VerifyEmailActivity::class.java)
                        .putExtra("email", email)
                        .putExtra("newEmail", mBinding.edtEmail.text.toString())
                        .putExtra("password", mBinding.edtPass.text.toString())
                )

            }
        }
    }

    private fun isValid(): Boolean {
        mBinding.tvPasswordError.visibility = View.INVISIBLE
        mBinding.tvEmailError.visibility = View.INVISIBLE


        if (mBinding.edtEmail.text.toString().trim().isEmpty()) {
            mBinding.tvEmailError.visibility = View.VISIBLE
            mBinding.tvEmailError.setText(getString(R.string.please_enter_email_address_))
            return false

        } else if (!CoreUtils.isEmailValid(mBinding.edtEmail.text.toString())) {
            mBinding.tvEmailError.visibility = View.VISIBLE
            mBinding.tvEmailError.setText(getString(R.string.email_is_not_valid))
            return false

        }

        if (mBinding.edtPass.text.toString().trim().isEmpty()) {
            mBinding.tvPasswordError.visibility = View.VISIBLE
            mBinding.tvPasswordError.setText(getString(R.string.please_enter_the_pass))
            return false
        }
        return true
    }

    inner class GenricWatcher(var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }


        override fun afterTextChanged(p0: Editable?) {
            when (view) {
                mBinding.edtPass -> {
                    mBinding.tvPasswordError.visibility = View.INVISIBLE
                }
                mBinding.edtEmail -> {
                    mBinding.tvEmailError.visibility = View.INVISIBLE
                }


            }
        }

    }
}