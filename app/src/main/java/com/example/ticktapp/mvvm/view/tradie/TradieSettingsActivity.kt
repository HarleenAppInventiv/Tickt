package com.example.ticktapp.mvvm.view.tradie

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySettingsBinding
import com.example.ticktapp.mvvm.viewmodel.TradieSettingViewModel

class TradieSettingsActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySettingsBinding
    private var isChat = false
    private var isPayment = false
    private var isUpdateAdmin = false
    private var isMilestoneUpdate = false
    private var isJobReview = false
    private var isChangeRequest = false
    private var isChancellation = false
    private var isJobDispute = false
    private var isJobQuery = false
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieSettingViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        setObservers()
        listener()
        mViewModel.getSetting()
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun callAPI() {
        val obj = HashMap<String, Any>()
        val dataArray = ArrayList<Int>()
        if (isChat)
            dataArray.add(1)
        if (isPayment)
            dataArray.add(2)
        if (isUpdateAdmin)
            dataArray.add(3)
        if (isMilestoneUpdate)
            dataArray.add(4)
        if (isJobReview)
            dataArray.add(5)
        if (isChangeRequest)
            dataArray.add(6)
        if (isChancellation)
            dataArray.add(7)
        if (isJobDispute)
            dataArray.add(8)
        if (isJobQuery)
            dataArray.add(9)
        obj.put("pushNotificationCategory", dataArray)
        mViewModel.putSetting(obj)
    }

    private fun listener() {
        mBinding.ivBack.setOnClickListener { onBackPressed() }
        mBinding.llChat.setOnClickListener {
            if (isChat)
                mBinding.ivSwitchChat.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchChat.setImageResource(R.drawable.ic_slider)
            isChat = !isChat
            callAPI()
        }
        mBinding.llPayment.setOnClickListener {
            if (isPayment)
                mBinding.ivPayment.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivPayment.setImageResource(R.drawable.ic_slider)
            isPayment = !isPayment
            callAPI()
        }
        mBinding.llAdminUpdate.setOnClickListener {
            if (isUpdateAdmin)
                mBinding.ivSwitchAdminUpdate.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchAdminUpdate.setImageResource(R.drawable.ic_slider)
            isUpdateAdmin = !isUpdateAdmin
            callAPI()
        }
        mBinding.llMilestoneUpdate.setOnClickListener {
            if (isMilestoneUpdate)
                mBinding.ivSwitchMilestoneUpdate.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchMilestoneUpdate.setImageResource(R.drawable.ic_slider)
            isMilestoneUpdate = !isMilestoneUpdate
            callAPI()
        }
        mBinding.llJobReview.setOnClickListener {
            if (isJobReview)
                mBinding.ivSwitchJobReview.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchJobReview.setImageResource(R.drawable.ic_slider)
            isJobReview = !isJobReview
            callAPI()
        }
        mBinding.llChangeRequest.setOnClickListener {
            if (isChangeRequest)
                mBinding.ivSwitchChangeRequest.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchChangeRequest.setImageResource(R.drawable.ic_slider)
            isChangeRequest = !isChangeRequest
            callAPI()
        }
        mBinding.llCancellationUpdate.setOnClickListener {
            if (isChancellation)
                mBinding.ivSwitchCancellationUpdate.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchCancellationUpdate.setImageResource(R.drawable.ic_slider)
            isChancellation = !isChancellation
            callAPI()
        }
        mBinding.llDisputeUpdate.setOnClickListener {
            if (isJobDispute)
                mBinding.ivSwitchDisputeUpdate.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchDisputeUpdate.setImageResource(R.drawable.ic_slider)
            isJobDispute = !isJobDispute
            callAPI()
        }
        mBinding.llJobQueryUpdate.setOnClickListener {
            if (isJobQuery)
                mBinding.ivSwitchJobQueryUpdate.setImageResource(R.drawable.ic_slider_disable)
            else
                mBinding.ivSwitchJobQueryUpdate.setImageResource(R.drawable.ic_slider)
            isJobQuery = !isJobQuery
            callAPI()
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.TRADIE_GET_SETTING -> {
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.TRADIE_GET_SETTING -> {
                mViewModel.settings?.let {
                    it.pushNotificationCategory.forEach {
                        if (it == 1) {
                            isChat = true
                            mBinding.ivSwitchChat.setImageResource(R.drawable.ic_slider)
                        } else if (it == 2) {
                            isPayment = true
                            mBinding.ivPayment.setImageResource(R.drawable.ic_slider)
                        } else if (it == 3) {
                            isUpdateAdmin = true
                            mBinding.ivSwitchAdminUpdate.setImageResource(R.drawable.ic_slider)
                        } else if (it == 4) {
                            isMilestoneUpdate = true
                            mBinding.ivSwitchMilestoneUpdate.setImageResource(R.drawable.ic_slider)
                        } else if (it == 5) {
                            isJobReview = true
                            mBinding.ivSwitchJobReview.setImageResource(R.drawable.ic_slider)
                        } else if (it == 6) {
                            isChangeRequest = true
                            mBinding.ivSwitchChangeRequest.setImageResource(R.drawable.ic_slider)
                        } else if (it == 7) {
                            isChancellation = true
                            mBinding.ivSwitchCancellationUpdate.setImageResource(R.drawable.ic_slider)
                        } else if (it == 8) {
                            isJobDispute = true
                            mBinding.ivSwitchDisputeUpdate.setImageResource(R.drawable.ic_slider)
                        } else if (it == 9) {
                            isJobQuery = true
                            mBinding.ivSwitchJobQueryUpdate.setImageResource(R.drawable.ic_slider)
                        }
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }
}