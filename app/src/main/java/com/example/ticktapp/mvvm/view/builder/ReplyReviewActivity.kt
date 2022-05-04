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
import com.example.ticktapp.databinding.ActivityReplyReviewBinding
import com.example.ticktapp.mvvm.viewmodel.ReviewListViewModel


@Suppress("DEPRECATION")
public class
ReplyReviewActivity : BaseActivity(), OnClickListener {

    private lateinit var replyId: String
    private lateinit var question: String
    private var isUpdate: Boolean = false
    private lateinit var reviewId: String
    private lateinit var mBinding: ActivityReplyReviewBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(ReviewListViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reply_review)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
    }

    private fun getIntentData() {
        reviewId = intent.getStringExtra("reviewId").toString()
        isUpdate = intent.getBooleanExtra("isUpdate", false)
        if (intent.hasExtra("reviews")) {
            question = intent.getStringExtra("reviews").toString()
            mBinding.tvTitle.text = getString(R.string.edit_your_reply)
            mBinding.tvSend.text = getString(R.string.save)
        } else
            question = ""

        if (intent.hasExtra("replyID"))
            replyId = intent.getStringExtra("replyID").toString()
        else
            replyId = ""

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
                reviewData.put("reviewId", reviewId)
                reviewData.put("reply", mBinding.edAnswer.text.toString())
                if (isUpdate) {
                    reviewData.put("replyId", replyId)
                    mViewModel.updateReviewReply(reviewData)
                } else {
                    mViewModel.addReviewReply(reviewData)
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
            ApiCodes.ADD_REVIEW_REPLY -> {
                mViewModel.reviewData.let {
                    val intent = Intent()
                    intent.putExtra("data", it)
                    intent.putExtra("id", reviewId)
                    if (isUpdate) {
                        intent.putExtra("msg", mBinding.edAnswer.text.toString())
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            ApiCodes.UPDATE_REVIEW_REPLY -> {
                mViewModel.reviewData.let {
                    val intent = Intent()
                    intent.putExtra("data", it)
                    intent.putExtra("id", reviewId)
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