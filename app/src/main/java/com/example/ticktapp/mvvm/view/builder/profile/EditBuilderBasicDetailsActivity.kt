package com.example.ticktapp.mvvm.view.builder.profile

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
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityEditBuilderBasicProfileBinding
import com.example.ticktapp.mvvm.view.tradie.ChangeEmailActivity
import com.example.ticktapp.mvvm.view.tradie.ChangePasswordActivity
import com.example.ticktapp.mvvm.viewmodel.ProfileViewModel

class EditBuilderBasicDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityEditBuilderBasicProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_edit_builder_basic_profile)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        setObservers()
        mViewModel.getBasicBuilerProfileDetails()
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        val socialId = PreferenceManager.getString(PreferenceManager.SOCIAL_ID)
        if (!socialId.isNullOrEmpty())
            mBinding.tvChange.visibility = View.GONE
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
        mBinding.jobDescBack.setOnClickListener(this)
        mBinding.tvChangePass.setOnClickListener(this)
        mBinding.tvChange.setOnClickListener(this)
        mBinding.tvSave.setOnClickListener(this)
        mBinding.edtName.addTextChangedListener(GenricWatcher(mBinding.edtName))
        mBinding.edtEmail.addTextChangedListener(GenricWatcher(mBinding.edtEmail))
        mBinding.edtAbn.addTextChangedListener(GenricWatcher(mBinding.edtAbn))

    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.BASIC_PROFILE -> {
                mViewModel.inItProfileModel.let {
                    mBinding.model = mViewModel.inItProfileModel
                    mBinding.edtName.setText(mViewModel.inItProfileModel.fullName)
                    mBinding.edtName.setSelection(mBinding.edtName.text.toString().length)
                }

            }
            ApiCodes.BUILDER_EDIT_BASIC_PROFILE -> {
                val intent = Intent()
                intent.putExtra("name", mBinding.edtName.text.toString())
                intent.putExtra("companyName", mBinding.edtCompanyName.text.toString())
                intent.putExtra("position", mBinding.edtPosition.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.jobDescBack -> onBackPressed()
            mBinding.tvChangePass -> {
                startActivity(
                    Intent(this, ChangePasswordActivity::class.java).putExtra(
                        IntentConstants.FROM,
                        1
                    )
                )
            }

            mBinding.tvChange -> {
                startActivity(
                    Intent(this, ChangeEmailActivity::class.java)
                        .putExtra("email", mBinding.edtEmail.text.toString())
                )
            }
            mBinding.tvSave -> {
                if (isValid()) {
                    val param = HashMap<String, Any>()
                    param.put("fullName", mBinding.edtName.text.toString())
                    param.put("mobileNumber", mBinding.edtPhoneNum.text.toString())
                    param.put("email", mBinding.edtEmail.text.toString())
                    param.put("companyName", mBinding.edtCompanyName.text.toString())
                    param.put("position", mBinding.edtPosition.text.toString())
                    param.put("abn", mBinding.edtAbn.text.toString().replace(" ", ""))
                    mViewModel.builderEditBasicProfile(param)
                }
            }
        }
    }

    private fun isValid(): Boolean {
        mBinding.tvNameError.visibility = View.INVISIBLE
        mBinding.tvEmailError.visibility = View.INVISIBLE
        mBinding.tvAbnError.visibility = View.INVISIBLE

        if (mBinding.edtName.text.toString().trim().isEmpty()) {
            mBinding.tvNameError.visibility = View.VISIBLE
            mBinding.tvNameError.setText(getString(R.string.please_enter_full_name))
            return false
        } else if (mBinding.edtEmail.text.toString().trim().isEmpty()) {
            mBinding.tvEmailError.visibility = View.VISIBLE
            mBinding.tvEmailError.setText(getString(R.string.please_enter_email_address))
            return false

        } else if (!CoreUtils.isEmailValid(mBinding.edtEmail.text.toString())) {
            mBinding.tvEmailError.visibility = View.VISIBLE
            mBinding.tvEmailError.setText(getString(R.string.email_is_not_valid))
            return false

        } else if (mBinding.edtCompanyName.text.toString().trim().isEmpty()) {
            mBinding.tvCompanyNameError.visibility = View.VISIBLE
            mBinding.tvCompanyNameError.setText(getString(R.string.please_enter_company_name))
            return false

        } else if (mBinding.edtPosition.text.toString().trim().isEmpty()) {
            mBinding.tvPositionError.visibility = View.VISIBLE
            mBinding.tvPositionError.setText(getString(R.string.please_enter_position))
            return false
        } else if (mBinding.edtAbn.text.toString().trim().isEmpty()) {
            mBinding.tvAbnError.visibility = View.VISIBLE
            mBinding.tvAbnError.setText(getString(R.string.please_enter_abn))
            return false

        } else if (mBinding.edtAbn.text.toString().replace(" ", "").length != 11) {
            mBinding.tvAbnError.visibility = View.VISIBLE
            mBinding.tvAbnError.setText(getString(R.string.abn_invalid_11))
            return false

        } else if (!CoreUtils.validABN(
                mBinding.edtAbn.text.toString() ?: " "
            )
        ) {
            mBinding.tvAbnError.visibility = View.VISIBLE
            mBinding.tvAbnError.setText(getString(R.string.abn_invalid))
            return false
        }
        return true
    }

    inner class GenricWatcher(var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view) {
                mBinding.edtAbn -> {
                    val origin: String = s.toString().replace(" ", "")
                    val formatStr: String = formatStrWithSpaces(origin)
                    if (!s.toString().equals(formatStr)) {
                        editTextSetContentMemorizeSelection(mBinding.edtAbn, formatStr)
                        if (before == 0 && count == 1 && formatStr[mBinding.edtAbn.selectionEnd - 1] == ' ') {
                            mBinding.edtAbn.setSelection(mBinding.edtAbn.selectionEnd + 1)
                        }
                    }
                }
            }
        }


        override fun afterTextChanged(p0: Editable?) {
            when (view) {
                mBinding.edtName -> {
                    mBinding.tvNameError.visibility = View.INVISIBLE
                }
                mBinding.edtEmail -> {
                    mBinding.tvEmailError.visibility = View.INVISIBLE
                }
                mBinding.edtAbn -> {
                    mBinding.tvAbnError.visibility = View.INVISIBLE
                }

            }
        }

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


}
