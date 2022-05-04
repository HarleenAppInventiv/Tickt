package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchDatesBinding
import com.app.core.model.jobmodel.JobModel
import com.example.ticktapp.mvvm.view.builder.search.SearchTradieActivity
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.preventTwoClick
import com.savvi.rangedatepicker.CalendarPickerView
import java.util.*
import kotlin.collections.ArrayList


public class SearchCalendarActivity : BaseActivity() {
    private var isTradie: Boolean = false
    private lateinit var mBinding: ActivitySearchDatesBinding
    private var data: JobModel? = null
    private var start_date: String = ""
    private var end_date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_dates)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setupView()
        setupCalendarView()
        listener()
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
            val dates = ArrayList<Date>()
            val startData = DateUtils.getDateFromString(start_date, DateUtils.DATE_FORMATE_8)
            dates.add(startData)
            mBinding.calendarView.selectDate(dates.get(0))
            if (end_date.isNotEmpty()) {
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


    fun getIntentData() {
        if (intent.hasExtra("isTradie")) {
            isTradie = intent.getBooleanExtra("isTradie", false)
        }
        if (intent.hasExtra("data"))
            data = intent.getSerializableExtra("data") as JobModel

        if (intent.hasExtra("start_date")) {
            start_date = intent.getStringExtra("start_date").toString()
            end_date = intent.getStringExtra("end_date").toString()
        }
        if (isTradie) {
            mBinding.tvSearchTitle.text = getString(R.string.when_is_your_job_)
            mBinding.tvSearchTitle.setTextColor(ContextCompat.getColor(this, R.color.color_161d4a))
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

    private fun setupView() {
        if (data != null) {
            mBinding.llHeader.visibility = View.VISIBLE
            mBinding.tvSkip.visibility = View.VISIBLE
            mBinding.tvTitle.text = data?.name
            mBinding.tvDetails.text = data?.trade_name
            Glide.with(mBinding.root.context).load(data?.image)
                .into(mBinding.ivUserProfile)
        } else {
            mBinding.llHeader.visibility = View.GONE
            mBinding.tvSkip.visibility = View.GONE
        }
    }

    private fun listener() {
        mBinding.searchToolbarBack.setOnClickListener { onBackPressed() }
        mBinding.tvSkip.setOnClickListener {
            preventTwoClick(mBinding.tvSkip)
            if (isTradie) {
                startActivity(
                    Intent(this, SearchTradieActivity::class.java)
                        .putExtra("data", data)
                        .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                        .putExtra("isLoad", true)
                        .putExtra("amount", intent.getStringExtra("amount"))
                        .putExtra("lat", intent.getDoubleExtra("lat", 0.0))
                        .putExtra("lng", intent.getDoubleExtra("lng", 0.0))
                        .putExtra("location", intent.getStringExtra("location"))
                )
                ActivityCompat.finishAffinity(this)
            } else {
                startActivity(
                    Intent(this, SearchJobActivity::class.java)
                        .putExtra("data", data)
                        .putExtra("isLoad", true)
                        .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                        .putExtra("amount", intent.getStringExtra("amount"))
                        .putExtra("lat", intent.getDoubleExtra("lat", 0.0))
                        .putExtra("lng", intent.getDoubleExtra("lng", 0.0))
                        .putExtra("location", intent.getStringExtra("location"))
                )
                ActivityCompat.finishAffinity(this)
            }
        }
        mBinding.tvCalendarContinue.setOnClickListener {
            val dates = mBinding.calendarView.selectedDates
            if (dates.size > 1) {
                if (data != null) {
                    if (isTradie) {
                        startActivity(
                            Intent(this, SearchTradieActivity::class.java)
                                .putExtra("data", data)
                                .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                                .putExtra("isLoad", true)
                                .putExtra("amount", intent.getStringExtra("amount"))
                                .putExtra("lat", intent.getDoubleExtra("lat", 0.0))
                                .putExtra("lng", intent.getDoubleExtra("lng", 0.0))
                                .putExtra("location", intent.getStringExtra("location"))
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
                        )
                        ActivityCompat.finishAffinity(this)
                    } else {
                        startActivity(
                            Intent(this, SearchJobActivity::class.java)
                                .putExtra("data", data)
                                .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                                .putExtra("isLoad", true)
                                .putExtra("amount", intent.getStringExtra("amount"))
                                .putExtra("lat", intent.getDoubleExtra("lat", 0.0))
                                .putExtra("lng", intent.getDoubleExtra("lng", 0.0))
                                .putExtra("location", intent.getStringExtra("location"))
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
                        )
                        ActivityCompat.finishAffinity(this)
                    }
                } else {
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
                }
            } else if (dates.size == 1) {
                if (data != null) {
                    if (isTradie) {
                        startActivity(
                            Intent(this, SearchTradieActivity::class.java)
                                .putExtra("data", data)
                                .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                                .putExtra("isLoad", true)
                                .putExtra("amount", intent.getStringExtra("amount"))
                                .putExtra("lat", intent.getDoubleExtra("lat", 0.0))
                                .putExtra("lng", intent.getDoubleExtra("lng", 0.0))
                                .putExtra("location", intent.getStringExtra("location"))
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
                        )
                        ActivityCompat.finishAffinity(this)
                    } else {
                        startActivity(
                            Intent(this, SearchJobActivity::class.java)
                                .putExtra("data", data)
                                .putExtra("isLoad", true)
                                .putExtra("isSearchType", intent.getIntExtra("isSearchType", 0))
                                .putExtra("amount", intent.getStringExtra("amount"))
                                .putExtra("lat", intent.getDoubleExtra("lat", 0.0))
                                .putExtra("lng", intent.getDoubleExtra("lng", 0.0))
                                .putExtra("location", intent.getStringExtra("location"))
                                .putExtra(
                                    "start_date",
                                    DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                                )
                                .putExtra(
                                    "end_date",
                                    DateUtils.getCurrentDateTime(dates[0], DateUtils.DATE_FORMATE_8)
                                )
                        )
                        ActivityCompat.finishAffinity(this);
                    }
                } else {
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
                }
            } else {
                showToastShort(getString(R.string.please_select_date))
            }
        }
    }
}