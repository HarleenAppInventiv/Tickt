package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAskQuestionBinding
import com.example.ticktapp.mvvm.viewmodel.TradieQuestionsViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
public class TradieAskQuestionActivity : BaseActivity(), OnClickListener {

    private lateinit var question: String
    private var isUpdate: Boolean = false
    private lateinit var jobId: String
    private lateinit var specializationId: String
    private lateinit var tradeId: String
    private lateinit var builderId: String
    private lateinit var questinID: String
    private lateinit var mBinding: ActivityAskQuestionBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieQuestionsViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ask_question)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
    }

    private fun getIntentData() {
        builderId = intent.getStringExtra("builderId").toString()
        tradeId = intent.getStringExtra("tradeId").toString()
        specializationId = intent.getStringExtra("specializationId").toString()
        jobId = intent.getStringExtra("jobId").toString()
        questinID = intent.getStringExtra("questinID").toString()
        isUpdate = intent.getBooleanExtra("isUpdate", false)
        if (intent.hasExtra("question"))
            question = intent.getStringExtra("question").toString()
        else
            question = ""

        mBinding.edAnswer.setText(question)
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


    private fun listener() {
        mBinding.saveTemplateBack.setOnClickListener { onBackPressed() }
        mBinding.tvCancel.setOnClickListener { onBackPressed() }
        mBinding.tvSend.setOnClickListener {
            if (mBinding.edAnswer.text.toString().isNotEmpty()) {
                val reviewData = HashMap<String, Any>()
                reviewData.put("question", mBinding.edAnswer.text.toString())
                if (isUpdate) {
                    reviewData.put("questionId", questinID)
                    mViewModel.updateQuestion(reviewData)
                } else {
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_popup)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val title: TextView = dialog?.findViewById(R.id.tv_title) as TextView
                    title.text = getString(R.string.ask_question)

                    val msg: TextView = dialog.findViewById(R.id.tv_msg) as TextView
                    msg.text = getString(R.string.are_you_sure_you_want_to_ask_a_question)

                    val dialogBtn_okay: TextView = dialog.findViewById(R.id.tvAccept) as TextView
                    dialogBtn_okay.text = getString(R.string.ok)
                    dialogBtn_okay.setOnClickListener {
                        dialog.dismiss()
                        reviewData.put("jobId", jobId)
                        reviewData.put("builderId", builderId)
                        reviewData.put("tradeId", tradeId)
                        reviewData.put("specializationId", specializationId)
                        mViewModel.addQuestion(reviewData)
                    }

                    val dialogBtn_cancel: TextView = dialog.findViewById(R.id.tvReject) as TextView
                    dialogBtn_cancel.text = getString(R.string.cancel)
                    dialogBtn_cancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            } else {
                showToastShort(getString(R.string.please_enter_question))
            }
        }

    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.TRADIE_ADD_QUESTION -> {
                showToastShort(exception.message)
            }
            ApiCodes.TRADIE_UPDATE_QUESTION -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.TRADIE_ADD_QUESTION -> {
                mViewModel.answerData.let {
                    askQuestionMoEngage()
                    askQuestionMixPanel()
                    val intent = Intent()
                    intent.putExtra("data", it)
                    intent.putExtra("questinID", questinID)
                    intent.putExtra("isUpdate", false)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            ApiCodes.TRADIE_UPDATE_QUESTION -> {
                mViewModel.answerData.let {
                    val intent = Intent()
                    intent.putExtra("data", it)
                    intent.putExtra("questinID", questinID)
                    intent.putExtra("isUpdate", true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

    private fun askQuestionMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_TRADIE_ASKED_QUESTION,
            signUpProperty
        )
    }

    private fun askQuestionMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mix_panel_token))
        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_TRADIE_ASKED_QUESTION, props)
    }
}