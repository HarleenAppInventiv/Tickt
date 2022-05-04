package com.example.ticktapp.mvvm.view.builder.postjob

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.R
import com.example.ticktapp.databinding.ActivityTemplateMilestoneBinding
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.model.MilestoneResponesIdData
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.getPojoData
import com.example.ticktapp.util.toJsonString

class TemplateMilestoneActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTemplateMilestoneBinding
    private var data: JobRecModelRepublish? = null
    var startDate = ""
    var endDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_template_milestone)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        getIntentData()
    }

    private fun getIntentData() {
        if (intent.hasExtra("start_date")) {
            startDate = intent.extras!!.getString("start_date", "")
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE)
                .isNullOrEmpty()
        ) {
            startDate =
                PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE) ?: ""
        }
        if (intent.hasExtra("end_date")) {
            endDate = intent.extras!!.getString("end_date", "")
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_END_DATE)
                .isNullOrEmpty()
        ) {

            endDate = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_END_DATE) ?: ""
        }
        Log.i("datesExtra", "getIntentData: Start Date- $startDate    End Date- $endDate")
        if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE)
                .isNullOrEmpty()
        ) {
            startActivity(
                Intent(this, AllMilestoneActivity::class.java)
            )
            finish()
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .isNullOrEmpty()
        ) {
            data = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .getPojoData(JobRecModelRepublish::class.java)
            if (data?.milestones != null && data?.milestones!!.size > 0) {
                val newMilestone = ArrayList<MilestoneData>()
                data?.milestones?.forEach {
                    val milestone = MilestoneData(
                        it.milestoneName.toString(),
                        it.isPhotoevidence,
                        false,
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_8,
                            it.fromDate
                        ).toString(),
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_8,
                            it.toDate
                        ).toString(),
                        it.recommendedHours.toString()
                    )
                    newMilestone.add(milestone)
                }
                startActivity(
                    Intent(this, AllMilestoneActivity::class.java).putExtra(
                        "data",
                        newMilestone
                    )
                        .putExtra("rData", data)
                )
                finish()
            }
        } else if (intent.hasExtra("data")) {
                data = intent.getSerializableExtra("data") as JobRecModelRepublish
                if (data?.milestones != null && data?.milestones!!.size > 0) {
                    val newMilestone = ArrayList<MilestoneData>()
                    data?.milestones?.forEach {
                        val milestone = MilestoneData(
                            it.milestoneName.toString(),
                            it.isPhotoevidence,
                            false,
                            DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_8,
                                it.fromDate
                            ).toString(),
                            DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_8,
                                it.toDate
                            ).toString(),
                            it.recommendedHours.toString()
                        )
                        newMilestone.add(milestone)
                    }
                    startActivity(
                        Intent(this, AllMilestoneActivity::class.java).putExtra(
                            "data",
                            newMilestone
                        )/*.putExtra(
                        "jobName",
                        intent.getStringExtra("jobName")
                    ).putExtra(
                        "categories",
                        intent.getSerializableExtra("categories")
                    ).putExtra(
                        "job_type",
                        intent.getSerializableExtra("job_type")
                    ).putExtra(
                        "specialization",
                        intent.getSerializableExtra("specialization")
                    ).putExtra(
                        "lat",
                        intent.getStringExtra("lat")
                    ).putExtra(
                        "lng",
                        intent.getStringExtra("lng")
                    )
                        .putExtra("location_name", intent.getStringExtra("location_name"))
                        .putExtra(
                            "job_description",
                            intent.getStringExtra("job_description")
                        )
                        .putExtra(
                            "amount",
                            intent.getStringExtra("amount")
                        ).putExtra(
                            "isSearchType",
                            intent.getIntExtra("isSearchType", 1)
                        ).putExtra(
                            "isJobType",
                            intent.getIntExtra("isJobType", -1)
                        ).putExtra("start_date", startDate)
                        .putExtra("end_date", endDate)*/
                            .putExtra("rData", data)
                    )
                    finish()
                }
            }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
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

    private fun listener() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.btnAddMilestone.setOnClickListener {
            Log.i("datesExtra", "getIntentData: Start Date- $startDate    End Date- $endDate")
            startActivity(
                Intent(
                    this,
                    AddMilestoneActivity::class.java
                )/*.putExtra(
                    "jobName",
                    intent.getStringExtra("jobName")
                ).putExtra(
                    "categories",
                    intent.getSerializableExtra("categories")
                ).putExtra(
                    "job_type",
                    intent.getSerializableExtra("job_type")
                ).putExtra(
                    "specialization",
                    intent.getSerializableExtra("specialization")
                ).putExtra(
                    "lat",
                    intent.getStringExtra("lat")
                ).putExtra(
                    "lng",
                    intent.getStringExtra("lng")
                )
                    .putExtra("location_name", intent.getStringExtra("location_name"))
                    .putExtra(
                        "job_description",
                        intent.getStringExtra("job_description")
                    )
                    .putExtra(
                        "amount",
                        intent.getStringExtra("amount")
                    ).putExtra(
                        "isSearchType",
                        intent.getIntExtra("isSearchType", 1)
                    ).putExtra(
                        "isJobType",
                        intent.getIntExtra("isJobType", -1)
                    ).putExtra("start_date", startDate)
                    .putExtra("end_date", endDate), 1310*/
            )

            finish()
        }
        mBinding.jobMileUseTemp.setOnClickListener {
            startActivityForResult(Intent(this, MilestoneTemplateActivity::class.java), 1310)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data?.hasExtra("allData") == true) {
                val milestoneData =
                    data.getSerializableExtra("allData") as ArrayList<MilestoneResponesIdData>

                val newMilestone = ArrayList<MilestoneData>()
                milestoneData.forEach {
                    val milestone = MilestoneData(
                        it.milestoneName,
                        it.isPhotoevidence,
                        false,
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_8,
                            it.fromDate
                        ).toString(),
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_8,
                            it.toDate
                        ).toString(),
                        it.recommendedHours
                    )
                    newMilestone.add(milestone)
                }
                PreferenceManager.putString(
                    PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE,
                    newMilestone.toJsonString()
                )
                startActivity(
                    Intent(this, AllMilestoneActivity::class.java)/*.putExtra(
                        "data",
                        newMilestone
                    ).putExtra(
                        "jobName",
                        intent.getStringExtra("jobName")
                    ).putExtra(
                        "categories",
                        intent.getSerializableExtra("categories")
                    ).putExtra(
                        "job_type",
                        intent.getSerializableExtra("job_type")
                    ).putExtra(
                        "specialization",
                        intent.getSerializableExtra("specialization")
                    ).putExtra(
                        "lat",
                        intent.getStringExtra("lat")
                    ).putExtra(
                        "lng",
                        intent.getStringExtra("lng")
                    )
                        .putExtra("location_name", intent.getStringExtra("location_name"))
                        .putExtra(
                            "job_description",
                            intent.getStringExtra("job_description")
                        )
                        .putExtra(
                            "amount",
                            intent.getStringExtra("amount")
                        ).putExtra(
                            "isSearchType",
                            intent.getIntExtra("isSearchType", 1)
                        ).putExtra(
                            "isJobType",
                            intent.getIntExtra("isJobType", -1)
                        ).putExtra("start_date", intent.getStringExtra("start_date"))
                        .putExtra("end_date", intent.getStringExtra("end_date"))*/
                )
                finish()
            }
        }
    }
}