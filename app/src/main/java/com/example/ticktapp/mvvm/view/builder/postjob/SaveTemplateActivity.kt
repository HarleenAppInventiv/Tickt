package com.example.ticktapp.mvvm.view.builder.postjob

import android.app.Activity
import android.content.Intent
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
import com.example.ticktapp.databinding.ActivitySaveTemplateBinding
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.model.MilestoneRequestData
import com.example.ticktapp.mvvm.viewmodel.TemplateViewModel

class SaveTemplateActivity : BaseActivity() {

    private lateinit var data: List<MilestoneData>
    private lateinit var mBinding: ActivitySaveTemplateBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(TemplateViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_save_template)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        getIntentData()
        setObservers()
    }

    private fun getIntentData() {
        data = intent.getSerializableExtra("data") as List<MilestoneData>
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

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun listener() {
        mBinding.saveTemplateBack.setOnClickListener { onBackPressed() }
        mBinding.saveTemplateBtn.setOnClickListener {
            if (isValidate()) {
                val params = HashMap<String, Any>()
                params.put("template_name", mBinding.templateName.text.toString())
                val milestones = ArrayList<MilestoneRequestData>()
                var order = 1
                data.forEach {
                    val milestoneData = MilestoneRequestData()
                    milestoneData.to_date = it.end_date
                    milestoneData.from_date = it.start_date
                    milestoneData.isPhotoevidence = it.photoRequired
                    milestoneData.recommended_hours = it.hours
                    milestoneData.milestone_name = it.name
                    milestoneData.order = order
                    milestones.add(milestoneData)
                    order++
                }
                params.put("milestones", milestones)
                mViewModel.createTemplate(params)
            }
        }
    }

    private fun isValidate(): Boolean {
        if (mBinding.templateName.text.toString().length == 0) {
            showToastShort(getString(R.string.enter_template_name))
            return false
        }
        return true
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.CREATE_TEMPLATE -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CREATE_TEMPLATE -> {
                startActivityForResult(Intent(this, SavedTemplateActivity::class.java), 1310)
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

}