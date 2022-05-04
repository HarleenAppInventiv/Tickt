package com.example.ticktapp.mvvm.view.tradie.completemilestone

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneHoursBinding
import com.example.ticktapp.dialog.SelectMilestoneHours

class MilestoneHoursActivity : BaseActivity() {
    private lateinit var files: java.util.ArrayList<String>
    private lateinit var mBinding: ActivityMilestoneHoursBinding
    private var jobId: String? = null
    private var jobName: String? = null
    private var desc: String? = null
    private var milestoneId: String? = null
    private var jobCount: String? = null
    private var amount: String? = null
    private var payType: String? = null
    private var isJobCompleted: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_milestone_hours)
        setUpListeners()
        getIntentData()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
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
        if (intent.hasExtra(IntentConstants.IMAGES))
            files = intent.getSerializableExtra(IntentConstants.IMAGES) as ArrayList<String>
        else
            files = ArrayList<String>()
        mBinding.ilHeader.tvTitle.text = jobName
    }

    private fun openHourPicker() {
        SelectMilestoneHours(
            this,
            mBinding.edtTime.text.toString(),
            object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val date = p0?.getTag().toString()
                    mBinding.edtTime.text = date
                }
            }).show()
    }

    private fun setUpListeners() {
        mBinding.linHours.setOnClickListener {
            openHourPicker()
        }

        mBinding.btnSend.setOnClickListener {


            if (mBinding.edtTime.text.toString().isEmpty()) {
                mBinding.tvAmountError.visibility = View.VISIBLE
                mBinding.tvAmountError.setText(getString(R.string.time_spent_is_required))
            } else if (mBinding.edtTime.text.toString() == "00:00" || mBinding.edtTime.text.toString() == "0:0") {
                mBinding.tvAmountError.visibility = View.VISIBLE
                mBinding.tvAmountError.setText(getString(R.string.invalid_time))
            } else {
                mBinding.tvAmountError.visibility = View.GONE
                val intent = Intent(this, PaymentDetailsActivity::class.java)
                    .putExtra(IntentConstants.JOB_ID, jobId)
                    .putExtra(IntentConstants.JOB_NAME, jobName)
                    .putExtra(IntentConstants.MILESTONE_ID, milestoneId)
                    .putExtra(IntentConstants.IS_JOB_COMPLETED, isJobCompleted)
                    .putExtra(IntentConstants.JOB_COUNT, jobCount)
                    .putExtra(IntentConstants.AMOUNT, amount)
                    .putExtra(IntentConstants.PAY_TYPE, payType)
                    .putExtra("hours", mBinding.edtTime.text.toString())
                    .putExtra(IntentConstants.DESCRIPTION, desc)
                    .putExtra(IntentConstants.IMAGES, files)
                    .putExtra(IntentConstants.CATEGORY, intent!!.extras!!.getString(IntentConstants.CATEGORY,""))
                    .putExtra(IntentConstants.MILESTONE_NUMBER, intent!!.extras!!.getString(IntentConstants.MILESTONE_NUMBER,""))
                resultLauncher.launch(intent)
            }
        }

        mBinding.edtTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.tvAmountError.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    setResult(RESULT_OK, data)
                    finish()
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
}