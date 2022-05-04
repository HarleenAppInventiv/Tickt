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
import com.app.core.preferences.PreferenceManager
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAddMilestoneBinding
import com.example.ticktapp.dialog.SelectRecommandedHours
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.MoEngageUtils
import com.example.ticktapp.util.getList
import com.example.ticktapp.util.toJsonString
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import kotlinx.android.synthetic.main.activity_add_milestone.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddMilestoneActivity : BaseActivity() {

    private var isEdit: Boolean = false
    private var pos: Int = -1
    private lateinit var data: java.util.ArrayList<MilestoneData>
    private lateinit var end_date: String
    private lateinit var start_date: String

    /* private lateinit var end_date_choose: String
     private lateinit var start_date_choose: String*/
    private lateinit var mBinding: ActivityAddMilestoneBinding
    private var isReturn: Boolean = false
    private var isChecked: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_milestone)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        start_date = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE)?:""
        end_date = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_END_DATE)?:""
        /*end_date_choose = end_date
        start_date_choose = start_date*/
        isReturn = false
        isChecked = false
        listener()
        getIntentData()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun getIntentData() {
        setOptionalText()
        if (intent.hasExtra("allData")) {
            data = intent.getSerializableExtra("allData") as ArrayList<MilestoneData>
            isReturn = true
        }else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE).isNullOrEmpty()) {
            data = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE)?.getList<MilestoneData>()?: ArrayList()
            isReturn = true
        } else {
            data = ArrayList()
        }
        isEdit = intent.getBooleanExtra("isEdit", false)
        if (intent.hasExtra("data")) {
            isReturn = true
            pos = intent.getIntExtra("pos", -1)
            mBinding.tvMilestoneTitle.text = getString(R.string.edit_of_milestone) + " " + (pos + 1)
            val milestone = intent.getSerializableExtra("data") as MilestoneData
            mBinding.edMilestoneName.setText(milestone.name)
            mBinding.btnAddMilestoneTvHours.text = milestone.hours
            mBinding.cbPhotoRequired.isChecked = milestone.photoRequired
            isChecked = milestone.isChecked
            start_date = milestone.start_date
            end_date = milestone.end_date

            if (start_date == end_date) {
                mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    start_date
                ) + " - " + DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    start_date
                )
            } else if (end_date == null || end_date.equals("null") || end_date.equals("")) {
                mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    start_date
                )
                end_date = ""
            } else {
                if (end_date.split("-")[0] == start_date.split("-")[0]) {
                    mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        start_date
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        end_date
                    )
                } else {
                    mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        start_date
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        end_date
                    )
                }
            }
        } else {
            mBinding.tvMilestoneTitle.text = getString(R.string.milestone) + " " + (data.size + 1)
        }
        if (isEdit) {
            mBinding.jobMileAddMore.visibility = View.GONE
        }
    }

    private fun setOptionalText() {
        mBinding.cbPhotoRequired.text =
            getString(R.string.photo_evidence_required) + " " + "(Optional)"
        mBinding.tvRecommendedHours.text =
            getString(R.string.recommended_hours_) + " " + "(Optional)"
        mBinding.tvDurationOfMilestone.text =
            getString(R.string.duration_of_milestone) + " " + "(Optional)"
    }

    private fun openHourPicker() {
        SelectRecommandedHours(
            this,
            mBinding.btnAddMilestoneTvHours.text.toString(),
            object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    val date = p0?.getTag().toString()
                    mBinding.btnAddMilestoneTvHours.text = date
                }
            }).show()
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
        mBinding.tvAddMilestoneDone.setOnClickListener {
            if (isValidate()) {

                if (isEdit) {
                    editMilestoneMoEngage()
                    editMilestoneMixPanel()
                }

                val milestone = MilestoneData(
                    mBinding.edMilestoneName.text.toString(),
                    mBinding.cbPhotoRequired.isChecked,
                    isChecked,
                    start_date,
                    end_date,
                    btn_add_milestone_tv_hours.text.toString()
                )
                if (pos >= 0) {
                    data.set(pos, milestone)
                } else {
                    data.add(milestone)
                }
                if (isReturn) {
                    val intent = Intent()
                    if (isEdit && pos >= 0) {
                        intent.putExtra("newData", data.get(pos))
                        intent.putExtra("pos", pos)
                    } else {
                        intent.putExtra("newData", milestone)
                    }
                    intent.putExtra("allData", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE,data.toJsonString())
                    startActivity(
                        Intent(this, AllMilestoneActivity::class.java)/*.putExtra(
                            "data",
                            data
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
                            .putExtra(
                                "end_date", intent.getStringExtra("end_date")
                            )
                            .putExtra(
                                "rData", intent.getSerializableExtra("rData")
                            )*/
                    )
                    finish()
                }
            }
        }
        mBinding.jobMileAddMore.setOnClickListener {
            if (isValidate()) {
                val milestone = MilestoneData(
                    mBinding.edMilestoneName.text.toString(),
                    mBinding.cbPhotoRequired.isChecked,
                    isChecked,
                    start_date,
                    end_date,
                    btn_add_milestone_tv_hours.text.toString()
                )
                if (pos >= 0) {
                    data.set(pos, milestone)
                } else {
                    data.add(milestone)
                }
                mBinding.edMilestoneName.setText("")
                mBinding.cbPhotoRequired.isChecked = false
                mBinding.btnAddDate.text = ""
                mBinding.btnAddMilestoneTvHours.text = ""
                start_date = ""
                end_date = ""
                mBinding.tvMilestoneTitle.text =
                    getString(R.string.milestone) + " " + (data.size + 1)
                pos = -1
            }
        }
        mBinding.btnAddMilestoneTvHours.setOnClickListener { openHourPicker() }
        mBinding.btnAddDate.setOnClickListener {
            startActivityForResult(
                Intent(this, DateActivity::class.java).putExtra(
                    "isReturn",
                    true
                ).putExtra("start_date", start_date).putExtra("end_date", end_date),
                1310
            )
        }
    }

    private fun editMilestoneMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_EDIT_MILESTONES,
            signUpProperty
        )
    }

    private fun editMilestoneMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_EDIT_MILESTONES,
            props
        )
    }

    override fun onBackPressed() {
        if (isReturn) {
            if (data.size > 0) {
                val intent = Intent()
                if (isEdit && pos >= 0) {
                    intent.putExtra("newData", data.get(pos))
                    intent.putExtra("pos", pos)
                }
                intent.putExtra("allData", data)
                PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE,data.toJsonString())

                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                super.onBackPressed()
            }
        } else {

            if (data.size > 0) {
                PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE,data.toJsonString())

                startActivity(
                    Intent(this, AllMilestoneActivity::class.java)/*.putExtra(
                        "data",
                        data
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
                        .putExtra("end_date", intent.getStringExtra("end_date"))
                        .putExtra("rData", intent.getSerializableExtra("rData"))
              */  )
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }


    private fun isValidate(): Boolean {
        if (mBinding.edMilestoneName.text.toString().length == 0) {
            showToastShort(getString(R.string.enter_milestone_name))
            return false
        }
        /*if (mBinding.btnAddDate.text.toString().length == 0) {
            showToastShort(getString(R.string.select_milestone_duration))
            return false
        }
        if (mBinding.btnAddMilestoneTvHours.text.toString().length == 0) {
            showToastShort(getString(R.string.enter_recommended_hours))
            return false
        }
        if (mBinding.btnAddMilestoneTvHours.text.toString() == "00:00" || mBinding.btnAddMilestoneTvHours.text.toString() == "0:0") {
            showToastShort(getString(R.string.enter_valid_recommended_hours))
            return false
        }*/
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1310) {
            if (data != null && data.hasExtra("start_date")) {
                start_date = data!!.getStringExtra("start_date").toString()
                end_date = data!!.getStringExtra("end_date").toString()
                if (start_date == end_date) {
                    mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        start_date
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        start_date
                    )
                } else if (end_date == null || end_date.equals("null") || end_date.equals("")) {
                    mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        start_date
                    )
                    end_date = ""
                } else {
                    if (end_date.split("-")[0] == start_date.split("-")[0]) {
                        mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            start_date
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            end_date
                        )
                    } else {
                        mBinding.btnAddDate.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            start_date
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            end_date
                        )
                    }
                }
            }
        }

    }
}