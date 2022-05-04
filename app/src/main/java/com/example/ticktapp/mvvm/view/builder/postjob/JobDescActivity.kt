package com.example.ticktapp.mvvm.view.builder.postjob

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
import androidx.databinding.DataBindingUtil
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityJobDescBinding
import com.example.ticktapp.util.getPojoData

class JobDescActivity : BaseActivity() {

    private var isReturn: Boolean = false
    private lateinit var mBinding: ActivityJobDescBinding
    private var data: JobRecModelRepublish? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_job_desc)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
    }

    private fun getIntentData() {
        mBinding.jobDescEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                mBinding.jobDescTvCount.text = "${p0?.length} Characters"
                mBinding.jobDescTvCount.text = "${p0?.length}/1000"
//                if (p0?.length ?: 0 > 0) {
//                    mBinding.jobDescTvCount.visibility = View.VISIBLE
//                } else {
//                    mBinding.jobDescTvCount.visibility = View.INVISIBLE
//
//                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })


        if (intent.hasExtra("isReturn")) {
            isReturn = intent.getBooleanExtra("isReturn", false)
            val desc = intent.getStringExtra("job_description")
            mBinding.jobDescEd.setText(desc)
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DESCRIPTION)
                .isNullOrEmpty()
        ) {
            mBinding.jobDescEd.setText(PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DESCRIPTION))
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .isNullOrEmpty()
        ) {
            data = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .getPojoData(JobRecModelRepublish::class.java)

            mBinding.jobDescEd.setText(data?.jobDescription)

        } else if (intent.hasExtra("data")) {
            data = intent.getSerializableExtra("data") as JobRecModelRepublish
            mBinding.jobDescEd.setText(data?.jobDescription)

        }
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

    private fun listener() {
        mBinding.jobDescBack.setOnClickListener { onBackPressed() }
        mBinding.jobDescBtn.setOnClickListener {
            if (isValid())
                PreferenceManager.putString(
                    PreferenceManager.NEW_JOB_PREF.JOB_DESCRIPTION,
                    mBinding.jobDescEd.text.toString()
                )
            if (isReturn) {
                setResult(
                    Activity.RESULT_OK, Intent()
                        .putExtra("job_description", mBinding.jobDescEd.text.toString())
                )
                finish()
            } else {


                startActivity(
                    Intent(this, PayActivity::class.java).apply {
                        if (this@JobDescActivity.data != null) {
                            putExtra("data", this@JobDescActivity.data)
                        }
                    })
//                    if (data != null) {
//                        startActivity(
//                            Intent(this, PayActivity::class.java)
//                                .putExtra(
//                                    "jobName",
//                                    intent.getStringExtra("jobName")
//                                ).putExtra(
//                                    "categories",
//                                    intent.getSerializableExtra("categories")
//                                ).putExtra(
//                                    "job_type",
//                                    intent.getSerializableExtra("job_type")
//                                ).putExtra(
//                                    "specialization",
//                                    intent.getSerializableExtra("specialization")
//                                ).putExtra(
//                                    "lat",
//                                    intent.getStringExtra("lat")
//                                ).putExtra(
//                                    "lng",
//                                    intent.getStringExtra("lng")
//                                )
//                                .putExtra("location_name", intent.getStringExtra("location_name"))
//                                .putExtra("job_description", mBinding.jobDescEd.text.toString())
//                                .putExtra("data", data)
//                        )
//                    } else {
//                        startActivity(
//                            Intent(this, PayActivity::class.java)
//                                .putExtra(
//                                    "jobName",
//                                    intent.getStringExtra("jobName")
//                                ).putExtra(
//                                    "categories",
//                                    intent.getSerializableExtra("categories")
//                                ).putExtra(
//                                    "job_type",
//                                    intent.getSerializableExtra("job_type")
//                                ).putExtra(
//                                    "specialization",
//                                    intent.getSerializableExtra("specialization")
//                                ).putExtra(
//                                    "lat",
//                                    intent.getStringExtra("lat")
//                                ).putExtra(
//                                    "lng",
//                                    intent.getStringExtra("lng")
//                                )
//                                .putExtra("location_name", intent.getStringExtra("location_name"))
//                                .putExtra("job_description", mBinding.jobDescEd.text.toString())
//                        )
//                    }

            }
        }

    }

    private fun isValid(): Boolean {
        if (mBinding.jobDescEd.text.toString().length == 0) {
            showToastShort(getString(R.string.please_enter_job_desc))
            return false
        }
        return true
    }
}