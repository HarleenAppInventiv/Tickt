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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.TemplatesAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneTemplateListBinding
import com.example.ticktapp.model.TemplateData
import com.example.ticktapp.model.TemplateMilestoneData
import com.example.ticktapp.mvvm.view.builder.milestone.MilestoneEditProfileListingActivity
import com.example.ticktapp.mvvm.viewmodel.TemplateViewModel


class MilestoneTemplateActivity : BaseActivity() {
    private var pos: Int = -1
    private lateinit var mAdapter: TemplatesAdapter
    private lateinit var mBinding: ActivityMilestoneTemplateListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(TemplateViewModel::class.java) }
    private val templateData by lazy { java.util.ArrayList<TemplateData>() }
    private var fromProfile = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_milestone_template_list)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setUpData()
        listener()
        setObservers()
        mViewModel.getTemplateList()
    }

    private fun getIntentData() {
        fromProfile = intent.getBooleanExtra("fromProfile", false)
    }


    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setUpData() {
        val layoutManager = LinearLayoutManager(this)
        mAdapter =
            TemplatesAdapter(templateData, object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    mViewModel.getTemplateMilestoneList(templateData.get(p0?.getTag() as Int).templateId)
                }
            }) {
                showAppPopupDialog(
                    getString(R.string.are_you_want_to_delete_template),
                    getString(R.string.delete),
                    getString(R.string.cancel),
                    getString(R.string.delete),
                    {
                        val pos = it?.tag as Int
                        this.pos = pos
                        val milesData = HashMap<String, Any>()
                        milesData.put("milestoneId", templateData.get(pos).templateId)
                        mViewModel.deleteTemplateMilestoneList(milesData)
                    },
                    {
                    },
                    true
                )
            }
        mBinding.rvMilestoneList.layoutManager = layoutManager
        mBinding.rvMilestoneList.adapter = mAdapter
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun listener() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }

    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.TEMPLATE_LIST -> {
                mBinding.tvResultTitleNoData.visibility = View.GONE
            }
            ApiCodes.TEMPLATE_MILESTONE_LIST -> {
                showToastShort(exception.message)
            }
            ApiCodes.TEMPLATE_DELETE_MILESTONE_LIST -> {
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.TEMPLATE_LIST -> {
                templateData.clear()
                mViewModel.templateData.let { templateData.addAll(it) }

                if (templateData.size == 0) {
                    mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                } else {
                    mBinding.tvResultTitleNoData.visibility = View.GONE
                }
                mAdapter.notifyDataSetChanged()
            }
            ApiCodes.TEMPLATE_MILESTONE_LIST -> {
                mViewModel.templateMilestoneData.let {
                    if (fromProfile) {
                        startActivityForResult(
                            Intent(
                                this,
                                MilestoneEditProfileListingActivity::class.java
                            ).putExtra("data", it), 1310
                        )
                    } else {
                        val intent = Intent()
                        intent.putExtra("allData", it.milestones)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
            ApiCodes.TEMPLATE_DELETE_MILESTONE_LIST -> {
                templateData.removeAt(pos)
                if (templateData.size == 0) {
                    mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                } else {
                    mBinding.tvResultTitleNoData.visibility = View.GONE
                }
                mAdapter.notifyDataSetChanged()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                val tmpMilestone = data.getSerializableExtra("data") as TemplateMilestoneData
                templateData.forEach {
                    if (it.templateId == tmpMilestone.templateId) {
                        it.milestoneCount = tmpMilestone.milestones.size.toString()
                    }
                }
                mAdapter.notifyDataSetChanged()
            }
        }
    }
}