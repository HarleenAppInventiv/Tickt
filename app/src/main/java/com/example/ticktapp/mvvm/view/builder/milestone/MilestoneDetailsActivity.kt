package com.example.ticktapp.mvvm.view.builder.milestone

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ImageAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMilestoneDetailsBinding
import com.app.core.model.jobmodel.JobMilestStonRespnse
import com.app.core.model.jobmodel.MilestoneDetails
import com.app.core.model.jobmodel.Photos
import com.app.core.preferences.PreferenceManager
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.mvvm.view.builder.MilestoneDeclineActivity
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.mvvm.viewmodel.MilestoneListViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
public class MilestoneDetailsActivity : BaseActivity(),
    OnClickListener {
    private var pos: Int = -1
    private lateinit var data: JobMilestStonRespnse
    private var milestoneDetails: MilestoneDetails? = null
    private var mileStoneNumber: Int = 0

    private lateinit var mBinding: ActivityMilestoneDetailsBinding
    private val mJobViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val mViewModel by lazy { ViewModelProvider(this).get(MilestoneListViewModel::class.java) }
    private lateinit var imageAdapter: ImageAdapter
    private var photos: ArrayList<Photos>? = null
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_milestone_details)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setupView()
        setObservers()
    }

    private fun getIntentData() {
        if (intent.getSerializableExtra("data") is JobMilestStonRespnse) {
            mBinding.srLayout.visibility = View.GONE
            data = intent.getSerializableExtra("data") as JobMilestStonRespnse
            pos = intent.getIntExtra("pos", 0)
            mileStoneNumber = pos + 1
            if (!data.categories.isNullOrEmpty()) {
                if (data.categories!![0].trade_name != null) {
                    category = data.categories?.get(0)?.trade_name
                }
            } else {
                category = ""
            }
            Log.i("categorycategory", "getIntentData: $category")
            mViewModel.getMilestoneDetails(
                data?.jobId.toString(),
                data.milestones?.get(pos)?.milestoneId.toString(),
            )
        }
    }

    private fun setData() {
        if (data != null && milestoneDetails != null) {
            mBinding.tvJobNameTitle.text = data?.jobName
            mBinding.tvDescHours.text =
                milestoneDetails?.hoursWorked + " " + getString(R.string.hours)
            mBinding.tvPhotosText.text =
                data.milestones?.get(pos)?.milestoneName
            if (milestoneDetails?.description != null && milestoneDetails?.description?.length!! > 0) {
                mBinding.tvDescTitle.visibility = View.VISIBLE
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDesc.text = milestoneDetails?.description
            } else {
                mBinding.tvDescTitle.visibility = View.GONE
                mBinding.tvDesc.visibility = View.GONE
            }
            photos?.clear()
            milestoneDetails?.images?.let { photos?.addAll(it) }
            mBinding.rvPhotos.adapter?.notifyDataSetChanged()

            if (data.milestones?.get(pos)?.status == 2 || data.milestones?.get(pos)?.status == 3) {
                mBinding.llOk.visibility = View.VISIBLE
                mBinding.llApprove.visibility = View.GONE
                mBinding.tvDecline.visibility = View.GONE
            } else {
                mBinding.llOk.visibility = View.GONE
                mBinding.llApprove.visibility = View.VISIBLE
                mBinding.tvDecline.visibility = View.VISIBLE
            }
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

    private fun setupView() {
        val layountManager = GridLayoutManager(this, 3)
        photos = ArrayList()
        imageAdapter = photos?.let { ImageAdapter(it) }!!
        mBinding.rvPhotos.layoutManager = layountManager
        mBinding.rvPhotos.adapter = imageAdapter
        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
    }


    private fun listener() {
        mBinding.jobDetailsIvBack.setOnClickListener { onBackPressed() }
        mBinding.srLayout.setOnRefreshListener {
            mViewModel.getMilestoneDetails(
                data?.jobId.toString(),
                data.milestones?.get(pos)?.milestoneId.toString(),
            )
        }

        mBinding.tvDecline.setOnClickListener {
            startActivityForResult(
                Intent(this, MilestoneDeclineActivity::class.java).putExtra(
                    IntentConstants.JOB_ID, data?.jobId
                )
                    .putExtra(IntentConstants.JOB_NAME, data?.jobName)
                    .putExtra(IntentConstants.MILESTONE_ID, data.milestones?.get(pos)?.milestoneId),
                1310
            )
        }
        mBinding.llOk.setOnClickListener { onBackPressed() }
        mBinding.tvPostApply.setOnClickListener {
            milestoneCheckApprovedMoengage(
                category!!,
                mileStoneNumber
            )

            milestoneCheckedMixPanel(
                category!!,
                mileStoneNumber
            )   //MileStone Check and Approved
            startActivity(
                Intent(
                    this,
                    MilestoneConfirmPayActivity::class.java
                ).putExtra("data", data).putExtra("pos", pos)
            )
        }
    }

    private fun milestoneCheckApprovedMoengage(category: String, mileStoneNumber: Int) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)
        signUpProperty.addAttribute(MoEngageConstants.MILESTONE_NUMBER, mileStoneNumber)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_MILESTONE_CHECK_APPROVED,
            signUpProperty
        )
    }

    private fun milestoneCheckedMixPanel(category: String, mileStoneNumber: Int) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)
        props.put(MoEngageConstants.MILESTONE_NUMBER, mileStoneNumber)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_MILESTONE_CHECK_APPROVED,
            props
        )
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(mJobViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.JOB_MILESTONE_DETAILS -> {
                mBinding.srLayout.isRefreshing = false
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.JOB_MILESTONE_DETAILS -> {
                mBinding.srLayout.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mViewModel.milestoneDetails.let {
                    if (it != null) {
                        milestoneDetails = it
                        setData()
                    }

                }
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}