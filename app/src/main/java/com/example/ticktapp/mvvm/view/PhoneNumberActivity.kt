package com.example.ticktapp.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityPhoneNumberBinding
import com.example.ticktapp.mvvm.viewmodel.PhoneNumberViewModel
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class PhoneNumberActivity : BaseActivity(), View.OnFocusChangeListener, View.OnClickListener,
    TextWatcher {
    private lateinit var mBinding: ActivityPhoneNumberBinding
    private var email: String? = null
    private var name: String? = null
    private var from: Int? = null
    private val mphoneviewmodel : PhoneNumberViewModel? = null



    private val mViewModel by lazy { ViewModelProvider(this).get(PhoneNumberViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_phone_number)
        mBinding.model = mViewModel
        getIntentData()
        initView()
        setObservers()
        setListeners()
    }


    private fun setListeners() {
        iv_back.setOnClickListener(this)
        mBinding.edtPhoneNum.onFocusChangeListener = this
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
        mBinding.edtPhoneNum.addTextChangedListener(this)

        toolbarText()

    }

    private fun toolbarText(){
        if(from == Constants.FORGOT_PASSWORD) {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.reset_password))
        }
        else {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.phone_number))
        }
    }



    fun setProgressDots() {

        val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        if(userType!!.toInt() == UserType.TRAIDIE) {

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
        }

        else {
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

    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        from?.let { mViewModel.getFrom(it) }
        mViewModel.getValidationLiveData().observe(this, {
            mBinding.tvPhoneError.visibility = View.INVISIBLE
            when (it.type) {
                ValidationsConstants.CONTACT_EMPTY, ValidationsConstants.CONTACT_INVALID -> {
                    mBinding.tvPhoneError.visibility = View.VISIBLE
                    mBinding.tvPhoneError.text = it.message
                }
            }
        })
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.CHECK_PHONE_NUMBER, ApiCodes.FORGOT_PASSWORD -> {
                showToastShort(exception.message)
            }
        }
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val origin: String = s.toString().replace(" ", "")
        val formatStr: String = formatStrWithSpaces(origin)
        if (!s.toString().equals(formatStr)) {
            editTextSetContentMemorizeSelection(mBinding.edtPhoneNum, formatStr)
            if (before == 0 && count == 1 && formatStr[mBinding.edtPhoneNum.getSelectionEnd() - 1] == ' ') {
                mBinding.edtPhoneNum.setSelection(mBinding.edtPhoneNum.getSelectionEnd() + 1)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    fun editTextSetContentMemorizeSelection(editText: EditText, charSequence: CharSequence) {
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

    fun formatStrWithSpaces(can: CharSequence): String {
        val sb = StringBuffer()
        for (i in 0 until can.length) {
            if (i != 0 && i % 3 == 0) {
                sb.append(' ')
            }
            sb.append(can[i])
        }
        return sb.toString()
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CHECK_PHONE_NUMBER -> {
                mViewModel.getVerifyPhoneData()?.let {
                    if (it.isProfileCompleted == true) showToastShort(msg)
                    else {
                        startActivity(Intent(this, VerifyNumberActivity::class.java).apply {
                            putExtra(
                                IntentConstants.MOBILE_NUMBER,
                                mBinding.edtPhoneNum.text.toString().replace(" ","")
                            )
                            putExtra(IntentConstants.FIRST_NAME, name)
                            putExtra(IntentConstants.EMAIL, email)
                            putExtra(IntentConstants.FROM, from)
                        })
                    }
                }
            }
            ApiCodes.FORGOT_PASSWORD -> {
                if (statusCode == 200) {
                    startActivity(Intent(this, VerifyNumberActivity::class.java).apply {
                        putExtra(
                            IntentConstants.MOBILE_NUMBER,
                            mBinding.edtPhoneNum.text.toString().replace(" ","")
                        )
                        putExtra(IntentConstants.FIRST_NAME, name)
                        putExtra(IntentConstants.EMAIL, email)
                        putExtra(IntentConstants.FROM, from)
                    })
                } else {
                    showToastShort(msg)
                }

            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            mBinding.edtPhoneNum -> if (hasFocus) mBinding.tvPhoneError.visibility = View.GONE
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