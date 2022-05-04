package com.example.ticktapp.mvvm.view.tradie

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
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityChangePasswordBinding
import com.example.ticktapp.mvvm.view.LoginActivity
import com.example.ticktapp.mvvm.viewmodel.ChangePasswordViewModel
import com.google.gson.JsonObject

class ChangePasswordActivity : BaseActivity() {

    private lateinit var mBinding: ActivityChangePasswordBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(ChangePasswordViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        setObservers()
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
                mObject.addProperty("oldPassword", mBinding.edtPass.text.toString())
                mObject.addProperty("newPassword", mBinding.edtNewpass.text.toString())
                mViewModel.changePassword(mObject)

            }
        }

        mBinding.edtPass.addTextChangedListener(GenricWatcher(mBinding.edtPass))
        mBinding.edtNewpass.addTextChangedListener(GenricWatcher(mBinding.edtNewpass))
        mBinding.edtConfirmpass.addTextChangedListener(GenricWatcher(mBinding.edtConfirmpass))
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CHANGE_PASSWORD -> {
               startActivity(Intent(this,ChangePasswordSuccessActivity::class.java).putExtra(
                   IntentConstants.FROM,intent.getIntExtra(IntentConstants.FROM,0)))

            }
        }
    }

    private fun isValid(): Boolean {
        mBinding.tvPasswordError.visibility = View.INVISIBLE
        mBinding.tvNewpasswordError.visibility = View.INVISIBLE
        mBinding.tvConfirmpasswordError.visibility = View.INVISIBLE

        if (mBinding.edtPass.text.toString().trim().isEmpty()) {
            mBinding.tvPasswordError.visibility = View.VISIBLE
            mBinding.tvPasswordError.setText(getString(R.string.please_enter_old_pass))
            return false
        } else if (mBinding.edtNewpass.text.toString().trim().isEmpty()) {
            mBinding.tvNewpasswordError.visibility = View.VISIBLE
            mBinding.tvNewpasswordError.setText(getString(R.string.please_enter_new_pass))
            return false

        } else if (!CoreUtils.isPasswordValid(mBinding.edtNewpass.text.toString())) {
            mBinding.tvNewpasswordError.visibility = View.VISIBLE
            mBinding.tvNewpasswordError.setText(getString(R.string.password_invalid))
            return false

        } else if (mBinding.edtConfirmpass.text.toString().trim().isEmpty()) {
            mBinding.tvConfirmpasswordError.visibility = View.VISIBLE
            mBinding.tvConfirmpasswordError.setText(getString(R.string.please_enter_confirm_pass))
            return false

        } else if (!mBinding.edtNewpass.text.toString()
                .equals(mBinding.edtConfirmpass.text.toString())
        ) {
            mBinding.tvConfirmpasswordError.visibility = View.VISIBLE
            mBinding.tvConfirmpasswordError.setText(getString(R.string.old_pass_not_match_new_pass))
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
                mBinding.edtNewpass -> {
                    mBinding.tvNewpasswordError.visibility = View.INVISIBLE
                }
                mBinding.edtConfirmpass -> {
                    mBinding.tvConfirmpasswordError.visibility = View.INVISIBLE
                }

            }
        }

    }
}