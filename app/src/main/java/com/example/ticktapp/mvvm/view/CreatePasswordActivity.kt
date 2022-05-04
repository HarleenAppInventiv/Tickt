package com.example.ticktapp.mvvm.view

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
import com.example.ticktapp.databinding.ActivityCreatePasswordBinding
import com.example.ticktapp.mvvm.viewmodel.CreatePasswordViewModel
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class CreatePasswordActivity: BaseActivity(), View.OnFocusChangeListener, View.OnClickListener {
    private lateinit var mBinding: ActivityCreatePasswordBinding
    private var email: String? = null
    private var name: String? = null
    private var phoneno: String? = null
    private var from: Int? = null

    private val mViewModel by lazy { ViewModelProvider(this).get(CreatePasswordViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_password)
        mBinding.model = mViewModel
        getIntentData()
        initView()
        getIntentData()
        setObservers()
        setListeners()
    }

    fun setProgressDots()
    {
        val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
        if(userType!!.toInt() == UserType.TRAIDIE) {
            mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
            mBinding.rlToolbar.v1.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v2.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v3.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
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
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
            mBinding.rlToolbar.v3.background =
                ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
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

    private fun getIntentData() {
        email = intent.getStringExtra(IntentConstants.EMAIL)
        name = intent.getStringExtra(IntentConstants.FIRST_NAME)
        phoneno = intent.getStringExtra(IntentConstants.MOBILE_NUMBER)
        from = intent.getIntExtra(IntentConstants.FROM,0)
    }

    private fun setListeners() {
        mBinding.icEye.setOnClickListener(this)
        mBinding.rlToolbar.ivBack.setOnClickListener(this)
    }

    private fun initView() {
        mBinding.tvYellowBtn.setText(getString(R.string.next))
        if (from!=Constants.FORGOT_PASSWORD) {
            setProgressDots()
        }
        mBinding.rlToolbar.tvTitle.setText(getString(R.string.create_password))
    }

    private fun nextActivity(){
        val clickintent = intent.getStringExtra("clicked")

        if(clickintent != null){
            startActivity(Intent(this, DoneActivity::class.java))
        }
        else if(PreferenceManager.USER_TYPE == "1"){
            startActivity(Intent(this, TradeActivity::class.java))

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
            mBinding.tvPasswordError.visibility = View.INVISIBLE
            when (it.type) {
                ValidationsConstants.PASSWORD_EMPTY, ValidationsConstants.PASSWORD_INVALID -> {
                    mBinding.tvPasswordError.visibility = View.VISIBLE
                    mBinding.tvPasswordError.text = it.message
                }
                ValidationsConstants.VALIDATE_SUCCESS-> {
                    if (from == Constants.REGISTER) {
                        val userType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                        if (!userType.isNullOrEmpty() && userType.equals("1")) {
                            startActivity(Intent(this, TradeActivity::class.java).apply {
                                putExtra(IntentConstants.MOBILE_NUMBER, phoneno)
                                putExtra(IntentConstants.FIRST_NAME, name)
                                putExtra(IntentConstants.EMAIL, email)
                                putExtra(IntentConstants.PASSWORD, mBinding.edtPass.text.toString())
                            })
                        } else {
                            startActivity(Intent(this, ABNActivity::class.java).apply {
                                putExtra(IntentConstants.MOBILE_NUMBER, phoneno)
                                putExtra(IntentConstants.FIRST_NAME, name)
                                putExtra(IntentConstants.EMAIL, email)
                                putExtra(IntentConstants.PASSWORD, mBinding.edtPass.text.toString())
                            })
                        }
                    }
                    else if(from == Constants.FORGOT_PASSWORD)
                    { email?.let { it1 -> mViewModel.hitResetPassword(it1) }
                        startActivity(Intent(this, DoneActivity::class.java)
                            .apply {
                            putExtra(IntentConstants.FROM, from)

                        })

                    }
                }
            }
        })
    }


    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {

            }
        }



    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CREATE_PASSWORD -> {
                finishAffinity()
                startActivity(Intent(this, DoneActivity::class.java).apply {
                    putExtra(IntentConstants.FROM, from)
                })
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v) {
            mBinding.edtPass -> if (hasFocus) mBinding.tvPasswordError.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.rlToolbar.ivBack -> {
                onBackPressed()
            }
        }
    }
}