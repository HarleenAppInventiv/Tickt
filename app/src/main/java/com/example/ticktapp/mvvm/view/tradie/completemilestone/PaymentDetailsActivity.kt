package com.example.ticktapp.mvvm.view.tradie.completemilestone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityPaymentDetailsBinding
import com.example.ticktapp.mvvm.viewmodel.BankDetailsViewModel

class PaymentDetailsActivity : BaseActivity() {
    private lateinit var mBinding: ActivityPaymentDetailsBinding
    private var jobId: String? = null
    private var jobName: String? = null
    private var desc: String? = null
    private var milestoneId: String? = null
    private var jobCount: String? = null
    private var amount: String? = null
    private var payType: String? = null
    private var hours: String? = null
    private var isJobCompleted: Boolean = false
    private val mViewModel by lazy { ViewModelProvider(this).get(BankDetailsViewModel::class.java) }
    private lateinit var files: java.util.ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_details)
        setUpListeners()
        getIntentData()
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        mViewModel.getBankDetails()

    }

    override fun onResume() {
        super.onResume()

    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)

    }

    private fun getIntentData() {
        jobId = intent.getStringExtra(IntentConstants.JOB_ID)
        jobName = intent.getStringExtra(IntentConstants.JOB_NAME)
        desc = intent.getStringExtra(IntentConstants.DESCRIPTION)
        milestoneId = intent.getStringExtra(IntentConstants.MILESTONE_ID)
        isJobCompleted = intent.getBooleanExtra(IntentConstants.IS_JOB_COMPLETED, false)
        jobCount = intent.getStringExtra(IntentConstants.JOB_COUNT)
        amount = intent.getStringExtra(IntentConstants.AMOUNT)
        payType = intent.getStringExtra(IntentConstants.PAY_TYPE)
        hours = intent.getStringExtra("hours")
        if (intent.hasExtra(IntentConstants.IMAGES))
            files = intent.getSerializableExtra(IntentConstants.IMAGES) as ArrayList<String>
        else
            files = ArrayList<String>()
        mBinding.ilHeader.tvTitle.visibility = View.GONE
        payType?.let {
            if (payType.equals(IntentConstants.Fixed_price, true)) {
                amount = roundOfPrice(amount!!.toDouble())
                mBinding.tvAmt.text = "$" + amount
            } else {
                amount = roundOfPrice(amount!!.toDouble() * hours!!.replace(":", ".").toDouble())
                mBinding.tvAmt.text = "$" + amount

            }
        }
    }

    private fun setUpListeners() {

        mBinding.btnBnkAccnt.setOnClickListener {
            val intent = Intent(this, BankDetailsActivity::class.java)
                .putExtra(IntentConstants.JOB_ID, jobId)
                .putExtra(IntentConstants.JOB_NAME, jobName)
                .putExtra(IntentConstants.MILESTONE_ID, milestoneId)
                .putExtra(IntentConstants.IS_JOB_COMPLETED, isJobCompleted)
                .putExtra(IntentConstants.JOB_COUNT, jobCount)
                .putExtra(IntentConstants.AMOUNT, amount)
                .putExtra(IntentConstants.PAY_TYPE, payType)
                .putExtra(IntentConstants.DESCRIPTION, desc)
                .putExtra("hours", hours)
                .putExtra(IntentConstants.CATEGORY, intent!!.extras!!.getString(IntentConstants.CATEGORY,""))
                .putExtra(IntentConstants.MILESTONE_NUMBER, intent!!.extras!!.getString(IntentConstants.MILESTONE_NUMBER,""))

                .putExtra(IntentConstants.IMAGES, files)
            if (mViewModel.mTradieBankModel != null)
                intent.putExtra("bankDetails", mViewModel.mTradieBankModel)
            resultLauncher.launch(intent)

        }

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.GET_BANK_DETAILS -> {
                showToastShort(exception.message)

            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {

        when (apiCode) {
            ApiCodes.GET_BANK_DETAILS -> {
                if (mViewModel.mTradieBankModel != null) {
                    mBinding.tvBank.visibility = View.VISIBLE
                    mBinding.btnBnkAccnt.text = getString(R.string.continue_)
                    mBinding.locTv.text = "Job complete"
                } else {
                    mBinding.tvBank.visibility = View.GONE
                    mBinding.btnBnkAccnt.text = getString(R.string.add_bank_details)
                    mBinding.locTv.text = getString(R.string.add_payment_details)


                }

            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    if (data.getBooleanExtra("isForFinish", true)) {
                        setResult(RESULT_OK, data)
                        finish()
                    } else {
                        mViewModel.mTradieBankModel = null
                        mViewModel.getBankDetails()
                    }
                }
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
            window.setStatusBarColor(Color.WHITE)
        }
    }

    fun roundOfPrice(price: Double): String {
        val data = price.toString().split(".")
        if (data.size == 1) {
            return (data.get(0) + "." + String.format("%02d", 0))
        } else if (data.size > 1 && data.get(1).length == 1) {
            return (data.get(0) + "." + String.format("%2d0", data.get(1).toInt()))

        } else {
            return String.format("%.2f", price)
        }
    }

}