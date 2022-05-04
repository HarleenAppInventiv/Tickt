package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAskQuestionReplyBinding
import com.example.ticktapp.mvvm.viewmodel.QuestionsViewModel


@Suppress("DEPRECATION")
public class AskQuestionReplyActivity : BaseActivity(), OnClickListener {

    private lateinit var answerID: String
    private lateinit var question: String
    private var isUpdate: Boolean = false
    private lateinit var jobId: String
    private lateinit var questinID: String
    private lateinit var mBinding: ActivityAskQuestionReplyBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(QuestionsViewModel::class.java) }
    private var isTradie: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ask_question_reply)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
    }

    private fun getIntentData() {
        isTradie = intent.getBooleanExtra("isTradie", false)
        jobId = intent.getStringExtra("jobId").toString()
        questinID = intent.getStringExtra("questinID").toString()
        isUpdate = intent.getBooleanExtra("isUpdate", false)

        if (intent.hasExtra("question")) {
            question = intent.getStringExtra("question").toString()
            mBinding.tvTitle.text = getString(R.string.edit_a_answer)
            mBinding.tvSend.text = getString(R.string.save)
        } else
            question = ""

        if (intent.hasExtra("answerID"))
            answerID = intent.getStringExtra("answerID").toString()
        else
            answerID = ""

        mBinding.edAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.jobDescTvCount.text = "${p0?.length}/500"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        mBinding.edAnswer.setText(question)

        if (intent.hasExtra("answerEdit")) {
            mBinding.edAnswer.setText(intent.extras!!.getString("answerEdit",""))
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


    private fun listener() {
        mBinding.saveTemplateBack.setOnClickListener { onBackPressed() }
        mBinding.tvCancel.setOnClickListener { onBackPressed() }
        mBinding.tvSend.setOnClickListener {
            if (mBinding.edAnswer.text.toString().isNotEmpty()) {
                val reviewData = HashMap<String, Any>()
                reviewData.put("questionId", questinID)
                reviewData.put("answer", mBinding.edAnswer.text.toString())

                if (isTradie) {
                    if (isUpdate) {
                        reviewData.put("answerId", answerID)
                        mViewModel.updateTradieAnswer(reviewData)
                    } else {
//                    reviewData.put("jobId", jobId)
                        reviewData.put("builderId", intent!!.extras!!.getString("builderId")!!)
                        reviewData.put("tradieId", intent!!.extras!!.getString("tradieId")!!)
                        mViewModel.addTradieAnswer(reviewData)


                    }
                } else {

                    if (isUpdate) {
                        reviewData.put("answerId", answerID)
                        mViewModel.updateAnswer(reviewData)
                    } else {
//                    reviewData.put("jobId", jobId)
                        reviewData.put("builderId", intent!!.extras!!.getString("builderId")!!)
                        reviewData.put("tradieId", intent!!.extras!!.getString("tradieId")!!)
                        mViewModel.addAnswer(reviewData)


                    }
                }
            } else {
                showToastShort(getString(R.string.please_enter_answer))
            }
        }

    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ADD_ANSWER -> {
                showToastShort(exception.message)
            }
            ApiCodes.UPDATE_ANSWER -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.ADD_ANSWER -> {
                mViewModel.answerData.let {
                    val intent = Intent()
                    intent.putExtra("data", it)
                    intent.putExtra("questinID", questinID)
                    if (isUpdate) {
                        intent.putExtra("msg", mBinding.edAnswer.text.toString())
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            ApiCodes.UPDATE_ANSWER -> {
                mViewModel.answerData.let {
                    val intent = Intent()
                    intent.putExtra("data", it)
                    intent.putExtra("questinID", questinID)
                    if (isUpdate) {
                        intent.putExtra("msg", mBinding.edAnswer.text.toString())
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }


}