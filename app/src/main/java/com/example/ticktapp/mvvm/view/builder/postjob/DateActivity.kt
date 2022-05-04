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
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityDatesBinding
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.getPojoData
import com.savvi.rangedatepicker.CalendarPickerView
import java.util.*
import kotlin.collections.ArrayList


public class DateActivity : BaseActivity() {
    private var isReturn: Boolean = false
    private lateinit var mBinding: ActivityDatesBinding
    private var start_date: String = ""
    private var end_date: String = ""
    private var data: JobRecModelRepublish? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dates)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setupCalendarView()
        listener()
    }

    private fun getIntentData() {
        isReturn = intent.getBooleanExtra("isReturn", false)
        if (intent.hasExtra("start_date")) {
            start_date = intent.getStringExtra("start_date").toString()
            end_date = intent.getStringExtra("end_date").toString()
        }else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE).isNullOrEmpty()){
            start_date =PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE)?:""
            end_date = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_END_DATE)?:""
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA).isNullOrEmpty()){
            data=PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA).getPojoData(JobRecModelRepublish::class.java)
            try {
                start_date = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_8,
                    data?.fromDate
                ).toString()
                end_date = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_8,
                    data?.toDate
                ).toString()
                if (start_date == null || start_date.equals("null")) {
                    start_date = ""
                }
                if (end_date == null || end_date.equals("null")) {
                    end_date = ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (intent.hasExtra("data")) {
            data = intent.getSerializableExtra("data") as JobRecModelRepublish
            try {
                start_date = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_8,
                    data?.fromDate
                ).toString()
                end_date = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_8,
                    data?.toDate
                ).toString()
                if (start_date == null || start_date.equals("null")) {
                    start_date = ""
                }
                if (end_date == null || end_date.equals("null")) {
                    end_date = ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun setupCalendarView() {
        val startCalendarDate = Calendar.getInstance()
        val endCalendarDate = Calendar.getInstance()
        endCalendarDate.time = startCalendarDate.time
        endCalendarDate.add(Calendar.YEAR, 2)
        mBinding.calendarView.init(
            startCalendarDate.getTime(),
            endCalendarDate.getTime()
        ).inMode(CalendarPickerView.SelectionMode.RANGE)
        if (start_date.isNotEmpty()) {
            val date = DateUtils.getDateFromString(start_date, DateUtils.DATE_FORMATE_8)
            if (date.before(startCalendarDate.time)) {
                start_date = DateUtils.getCurrentDateTime(DateUtils.DATE_FORMATE_8)
            }
            val dates = ArrayList<Date>()
            val startData = DateUtils.getDateFromString(start_date, DateUtils.DATE_FORMATE_8)
            dates.add(startData)
            mBinding.calendarView.selectDate(dates.get(0))
            if (end_date.isNotEmpty()) {
                val date1 = DateUtils.getDateFromString(end_date, DateUtils.DATE_FORMATE_8)
                if (date1.after(endCalendarDate.time)) {
                    end_date = DateUtils.getCurrentDateTime(DateUtils.DATE_FORMATE_8)
                } else if (date1.before(startCalendarDate.time)) {
                    end_date = DateUtils.getCurrentDateTime(DateUtils.DATE_FORMATE_8)
                }
                val endData = DateUtils.getDateFromString(end_date, DateUtils.DATE_FORMATE_8)
                dates.add(endData)
                mBinding.calendarView.selectDate(dates.get(1))
            }
        } else {
            mBinding.calendarView.selectDate(Date())
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
        mBinding.timingBack.setOnClickListener { onBackPressed() }
        mBinding.tvPostSubmit.setOnClickListener {
            if (isReturn) {
                val dates = mBinding.calendarView.selectedDates
                if (dates.size > 1) {
                    val intent = Intent()
                        .putExtra(
                            "start_date",
                            DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                        )
                        .putExtra(
                            "end_date",
                            DateUtils.getCurrentDateTime(
                                dates[dates.size - 1],
                                DateUtils.DATE_FORMATE_8
                            )
                        )
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    val intent = Intent()
                        .putExtra(
                            "start_date",
                            DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                        )
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } else {
                val dates = mBinding.calendarView.selectedDates

                PreferenceManager.putString(
                    PreferenceManager.NEW_JOB_PREF.JOB_START_DATE,
                    DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                )
                if (dates.size > 1) {
                    PreferenceManager.putString(
                        PreferenceManager.NEW_JOB_PREF.JOB_END_DATE,
                        DateUtils.getCurrentDateTime(
                            dates[dates.size - 1],
                            DateUtils.DATE_FORMATE_8
                        )
                    )

                } else {
                    PreferenceManager.putString(
                        PreferenceManager.NEW_JOB_PREF.JOB_END_DATE,
                        DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                    )

                }

                startActivity(
                    Intent(
                        this,
                        TemplateMilestoneActivity::class.java
                    ).apply {
                        if (this@DateActivity.data != null) {
                            putExtra("data", this@DateActivity.data)
                        }
                    })
              /*  if (dates.size > 1) {
                    if (data != null) {
                        startActivity(
                            Intent(
                                this,
                                TemplateMilestoneActivity::class.java
                            )
                                .putExtra(
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
                                ).putExtra(
                                    "start_date",
                                    DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                                )
                                .putExtra(
                                    "end_date",
                                    DateUtils.getCurrentDateTime(
                                        dates[dates.size - 1],
                                        DateUtils.DATE_FORMATE_8
                                    )
                                )
                                .putExtra("data", data)
                        )
                    } else {
                        startActivity(
                            Intent(
                                this,
                                TemplateMilestoneActivity::class.java
                            )
                                .putExtra(
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
                                ).putExtra(
                                    "start_date",
                                    DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                                )
                                .putExtra(
                                    "end_date",
                                    DateUtils.getCurrentDateTime(
                                        dates[dates.size - 1],
                                        DateUtils.DATE_FORMATE_8
                                    )
                                )
                        )
                    }
                } else {
                    if (data != null) {
                        startActivity(
                            Intent(
                                this,
                                TemplateMilestoneActivity::class.java
                            )
                                .putExtra(
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
                                ).putExtra(
                                    "start_date",
                                    DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                                )
                                .putExtra(
                                    "end_date",
                                    DateUtils.getCurrentDateTime(
                                        dates[0],
                                        DateUtils.DATE_FORMATE_8
                                    )
                                ).putExtra("data", data)
                        )
                    } else {
                        startActivity(
                            Intent(
                                this,
                                TemplateMilestoneActivity::class.java
                            )
                                .putExtra(
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
                                ).putExtra(
                                    "start_date",
                                    DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                                )
                                .putExtra(
                                    "end_date",
                                    DateUtils.getCurrentDateTime(
                                        dates[0],
                                        DateUtils.DATE_FORMATE_8
                                    )
                                )
                        )
                    }
                }*/
            }
        }
    }
}
