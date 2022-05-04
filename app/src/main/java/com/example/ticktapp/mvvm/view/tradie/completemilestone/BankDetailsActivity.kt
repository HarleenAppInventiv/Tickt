package com.example.ticktapp.mvvm.view.tradie.completemilestone

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityBankDetailsBinding
import com.app.core.model.jobmodel.Photos
import com.app.core.model.jobmodel.TradieBankDetails
import com.app.core.util.*
import com.example.ticktapp.mvvm.viewmodel.BankDetailsViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import kotlinx.android.synthetic.main.activity_bank_details.view.*
import org.json.JSONObject
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BankDetailsActivity : BaseActivity() {
    private var isForResult: Boolean = false
    private var isEdit: Boolean = false
    private var shouldSubmit: Boolean = false
    private var bankDetails: TradieBankDetails? = null
    private lateinit var files: java.util.ArrayList<String>
    private lateinit var mBinding: ActivityBankDetailsBinding
    private var jobId: String = ""
    private var desc: String? = null
    private var jobName: String = ""
    private var milestoneId: String = ""
    private var jobCount: String = ""
    private var amount: String = ""
    private var payType: String = ""
    private var hours: String = ""
    private var isJobCompleted: Boolean = false
    private var category: String? = null
    private var milestoneNumber: Int = 0
    private val mViewModel by lazy { ViewModelProvider(this).get(BankDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bank_details)
        setUpListeners()
        getIntentData()
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
            mBinding.tvNameError.visibility = View.GONE
            mBinding.tvAccntNoError.visibility = View.GONE
            mBinding.tvBsbError.visibility = View.GONE
            when (it.type) {
                ValidationsConstants.ACCOUNT_NAME_EMPTY -> {
                    mBinding.tvNameError.visibility = View.VISIBLE
                    mBinding.tvNameError.text = it.message
                }
                ValidationsConstants.ACCOUNT_NUMBER_EMPTY, ValidationsConstants.ACCOUNT_NUMBER_LENGTH -> {
                    mBinding.tvAccntNoError.visibility = View.VISIBLE
                    mBinding.tvAccntNoError.text = it.message
                }
                ValidationsConstants.BSB_EMPTY, ValidationsConstants.BSB_NUMBER_LENGTH -> {
                    mBinding.tvBsbError.visibility = View.VISIBLE
                    mBinding.tvBsbError.text = it.message
                }
            }
        })
    }

    private fun getIntentData() {
        desc = intent.getStringExtra(IntentConstants.DESCRIPTION)
        jobId = intent.getStringExtra(IntentConstants.JOB_ID).toString()
        jobName = intent.getStringExtra(IntentConstants.JOB_NAME).toString()
        milestoneId = intent.getStringExtra(IntentConstants.MILESTONE_ID).toString()
        isJobCompleted = intent.getBooleanExtra(IntentConstants.IS_JOB_COMPLETED, false)
        jobCount = intent.getStringExtra(IntentConstants.JOB_COUNT).toString()
        amount = intent.getStringExtra(IntentConstants.AMOUNT).toString()
        payType = intent.getStringExtra(IntentConstants.PAY_TYPE).toString()
        hours = intent.getStringExtra("hours").toString()

        if (intent.hasExtra(IntentConstants.IMAGES))
            files = intent.getSerializableExtra(IntentConstants.IMAGES) as ArrayList<String>
        else
            files = ArrayList<String>()
        if (intent.hasExtra("bankDetails")) {
            bankDetails = intent.getSerializableExtra("bankDetails") as TradieBankDetails
            bankDetails?.let {
                setData(it)
                mBinding.ilHeader.ivEdit.visibility = View.VISIBLE
                mBinding.btnBnkAccnt.text = getString(R.string.submit)
                enableDisable(false)

            }
            shouldSubmit = true
        } else {
            mBinding.ilHeader.ivEdit.visibility = View.GONE
            mBinding.btnBnkAccnt.text = getString(R.string.save)

        }
        mBinding.ilHeader.tvTitle.visibility = View.GONE
        mBinding.ilHeader.ivBack.visibility = View.INVISIBLE
        mBinding.ilHeader.ivBack1.visibility = View.VISIBLE
    }

    private fun setData(tradieBankDetails: TradieBankDetails) {
        mBinding.edtActName.setText(tradieBankDetails.account_name)
        mBinding.edtActNo.setText(tradieBankDetails.account_number)
        mBinding.edtBsbNo.setText(tradieBankDetails.bsb_number)
    }

    private fun enableDisable(isEnable: Boolean) {
        mBinding.edtBsbNo.isEnabled = isEnable
        mBinding.edtActName.isEnabled = isEnable
        mBinding.edtActNo.isEnabled = isEnable

    }

    private fun setUpListeners() {
        mBinding.btnBnkAccnt.setOnClickListener {
            val jsonObject = com.google.gson.JsonObject()
            jsonObject.addProperty("account_name", mBinding.edtActName.text.toString())
            jsonObject.addProperty("account_number", mBinding.edtActNo.text.toString())
            jsonObject.addProperty("bsb_number", mBinding.edtBsbNo.text.toString())
            if (!shouldSubmit) {
                if (!isEdit)
                    mViewModel.addBankDetails(jsonObject, false)
                else {
                    jsonObject.addProperty("userId", bankDetails?.userId)
                    mViewModel.addBankDetails(jsonObject, true)
                }
            } else {
                val params = HashMap<String, Any>()
                params.put("jobId", jobId)
                params.put("milestoneId", milestoneId)
                params.put("actualHours", hours)
                params.put("totalAmount", amount)
                if (!desc.isNullOrEmpty()) {
                    params.put("description", desc!!)

                }
                val evidenceList = ArrayList<Photos>()
                for (data in files) {
                    val photo = Photos()
                    photo.mediaType = 1
                    photo.link = data
                    evidenceList.add(photo)
                }
                params.put("evidence", evidenceList)
                mViewModel.markJobComplete(params)
            }
        }
        mBinding.ilHeader.ivEdit.setOnClickListener {
            showMenu(mBinding.ilHeader.ivEdit)
        }
        mBinding.ilHeader.ivBack1.setOnClickListener {
            onBackPressed()
        }
        mBinding.edtBsbNo.addTextChangedListener(GenricWatcher(mBinding.edtBsbNo))
        mBinding.edtActNo.addTextChangedListener(GenricWatcher(mBinding.edtActNo))
        mBinding.edtActName.addTextChangedListener(GenricWatcher(mBinding.edtActName))

    }

    override fun onBackPressed() {
        if (isForResult) {
            val intent = Intent()
            intent.putExtra("isForFinish", false)
            setResult(RESULT_OK, intent)
            finish()
        } else
            super.onBackPressed()

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ADD_BANK_DETAILS, ApiCodes.REMOVE_BANK_DETAILS, ApiCodes.MARK_JOB_COMPLETE -> {
                showToastShort(exception.message)

            }

        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {

        when (apiCode) {
            ApiCodes.ADD_BANK_DETAILS -> {
                if (mViewModel.mTradieBankModel != null) {
                    mBinding.btnBnkAccnt.text = getString(R.string.submit)
                    mBinding.ilHeader.ivEdit.visibility = View.VISIBLE
                    showToastShort(msg)
                    bankDetails = mViewModel.mTradieBankModel
                    if (isEdit) {
                        enableDisable(false)
                        shouldSubmit = true
                        isEdit = true
                    } else {
                        isForResult = true
                        isEdit = false
                        shouldSubmit = true
                    }

                }

            }

            ApiCodes.REMOVE_BANK_DETAILS -> {
                enableDisable(true)
                mBinding.edtActNo.setText("")
                mBinding.edtActName.setText("")
                mBinding.edtBsbNo.setText("")
                mBinding.btnBnkAccnt.text = getString(R.string.save)
                mBinding.ilHeader.ivEdit.visibility = View.GONE
                isEdit = false
                shouldSubmit = false
                isForResult = true
            }
            ApiCodes.MARK_JOB_COMPLETE -> {
                category = intent!!.extras!!.getString(IntentConstants.CATEGORY, "")
                milestoneNumber = intent!!.extras!!.getInt(IntentConstants.MILESTONE_NUMBER, 0)

                milestoneCompletedMoEngage(category!!, milestoneNumber)
                milestoneCompletedMixpanel(category!!, milestoneNumber)

                intent = Intent(this, JobCompletedSuccessActivity::class.java)
                intent.putExtra(IntentConstants.IS_JOB_COMPLETED, isJobCompleted)
                intent.putExtra(IntentConstants.JOB_COUNT, jobCount)
                resultLauncher.launch(intent)
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun milestoneCompletedMoEngage(category: String, MilestoneNumber: Int) {
        val signUpProperty = Properties()
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)
        signUpProperty.addAttribute(MoEngageConstants.MILESTONE_NUMBER, MilestoneNumber)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_MILESTONE_COMPLETED,
            signUpProperty
        )
    }

    private fun milestoneCompletedMixpanel(category: String, MilestoneNumber: Int) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)
        props.put(MoEngageConstants.MILESTONE_NUMBER, MilestoneNumber)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_MILESTONE_COMPLETED, props)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.let {
                    data.putExtra("isForFinish", true)
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

    inner class GenricWatcher(var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }


        override fun afterTextChanged(p0: Editable?) {
            when (view.id) {
                R.id.edt_ActName -> {
                    mViewModel.mAccountNameEditText = p0.toString()
                    mBinding.tvNameError.visibility = View.GONE
                }
                R.id.edt_ActNo -> {
                    mViewModel.mAccountNumberEditText = p0.toString()
                    mBinding.tvAccntNoError.visibility = View.GONE
                }
                R.id.edt_BsbNo -> {
                    mViewModel.mBsbNumberEditText = p0.toString()
                    val sb = StringBuilder(p0)
                    mBinding.tvBsbError.visibility = View.GONE
                    if (mBinding.edtBsbNo.text.toString().length == 4) {
                        if (mBinding.edtBsbNo.text.toString().contains("-"))
                            return
                        sb.insert(3, "-")
                        mBinding.edtBsbNo.setText(sb.toString())
                        mBinding.edtBsbNo.edt_BsbNo.setSelection(mBinding.edtBsbNo.text.toString().length)
                    }
                }

            }
        }

    }

    private fun showMenu(view: View) {
        val viewGroup = view.findViewById<LinearLayout>(R.id.ll_popup)
        val layoutInflater =
            (view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val layout: View =
            layoutInflater.inflate(R.layout.layout_pop_up_bank_details_edit, viewGroup)
        val popup = PopupWindow(view.context)
        popup.contentView = layout
        popup.setBackgroundDrawable(ColorDrawable())
        popup.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        val tvOptionOne = layout.findViewById<TextView>(R.id.tv_option_one)
        val tvOptionTwo = layout.findViewById<TextView>(R.id.tv_option_two)
        tvOptionOne.text = view.context.getString(R.string.edit)
        tvOptionTwo.text = view.context.getString(R.string.delete)
        tvOptionOne.setOnClickListener {
            popup.dismiss()
            enableDisable(true)
            mBinding.ilHeader.ivEdit.visibility = View.GONE
            mBinding.btnBnkAccnt.text = getString(R.string.save)
            isEdit = true
            shouldSubmit = false
        }
        tvOptionTwo.setOnClickListener {
            popup.dismiss()
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_popup)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
            title.text = getString(R.string.delete)

            val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
            msg.text = getString(R.string.are_you_sure_you_want_to_remove_bank_details)

            val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
            dialogBtn_okay.text = getString(R.string.ok)
            dialogBtn_okay.setOnClickListener {
                dialog.dismiss()
                mViewModel.getRemoveBankDetails()
            }

            val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
            dialogBtn_cancel.text = getString(R.string.cancel)
            dialogBtn_cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        popup.setTouchInterceptor { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popup.dismiss()
                return@setTouchInterceptor true
            }
            false
        }
        popup.animationStyle = R.style.popupWindowAnim
        popup.showAtLocation(layout, Gravity.TOP or Gravity.END, 50, 280)

    }

}