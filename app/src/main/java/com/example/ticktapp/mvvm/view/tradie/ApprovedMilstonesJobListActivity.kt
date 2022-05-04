package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.TradieActiveJobsAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityApprovedMilestonesBinding
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.mvvm.view.tradie.completemilestone.MilestoneListingActivity
import com.example.ticktapp.mvvm.viewmodel.ApprovedMilestonesListviewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ApprovedMilstonesJobListActivity:BaseActivity(),SwipeRefreshLayout.OnRefreshListener,EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener,
    TradieActiveJobsAdapter.onAdapterItemClick {
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityApprovedMilestonesBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(ApprovedMilestonesListviewModel::class.java) }
    private lateinit var mAdapter: TradieActiveJobsAdapter
    private val list by lazy { ArrayList<JobRecModel>() }
    private var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_approved_milestones)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        mViewModel.getApprovedMilestonesList(pageNumber,true)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }
    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
    }

    private fun initRecyclerView() {
        mAdapter = TradieActiveJobsAdapter( list,this)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvApprovedJobs.layoutManager = layoutRecManager
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)
        mBinding.rvApprovedJobs.addOnScrollListener(endlessScrollListener!!)
        mBinding.rvApprovedJobs.adapter = mAdapter

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        mBinding.srLayout.isRefreshing=false
        when (apiCode) {
            ApiCodes.APPROVED_MILESTONE_REQUEST -> {
                showToastShort(exception.message)

            }
        }
    }
    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        mBinding.srLayout.isRefreshing=false
        when (apiCode) {
            ApiCodes.APPROVED_MILESTONE_REQUEST -> {
                if(mAdapter.itemCount==0 || pageNumber==1)
                        mAdapter.setData( mViewModel.getApprovedMilestonesListing())
                    else
                        mAdapter.addData( mViewModel.getApprovedMilestonesListing())
                    hideShowNoData()

            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun hideShowNoData() {
        if(mAdapter.itemCount==0)
            mBinding.tvResultTitleNoData.visibility= View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility= View.GONE
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        pageNumber=page+1
        mViewModel.getApprovedMilestonesList(pageNumber,false)
    }


    override fun onRefresh() {
      hitApi(false)
    }

   fun  hitApi(progress:Boolean)
    {
        endlessScrollListener?.resetState()
        pageNumber=1
        list.clear()
        mViewModel.getApprovedMilestonesList(pageNumber,progress)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    hitApi(true)
                }
            }
        }

    override fun onItemClick(pos: Int) {
        viewAPPROVEDMilstoneMoEngage(mAdapter.jobList.get(pos).tradeName!!,mAdapter.jobList.get(pos).milestoneNumber)
        viewAPPROVEDMilstonePanel(mAdapter.jobList.get(pos).tradeName!!,mAdapter.jobList.get(pos).milestoneNumber)
        resultLauncher.launch(
            Intent(this, MilestoneListingActivity::class.java)
                .putExtra("jobId", mAdapter.jobList.get(pos).jobId).putExtra("jobName",mAdapter.jobList.get(pos).jobName)
                .putExtra("data",mAdapter.jobList.get(pos))
        )
    }

    override fun onQuoteClick(pos: Int) {

    }

    private fun viewAPPROVEDMilstoneMoEngage(category:String,MilestoneNumber:Int) {
        val signUpProperty = Properties()

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)
        signUpProperty.addAttribute(MoEngageConstants.MILESTONE_NUMBER, MilestoneNumber)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_VIEWED_APPROVED_MILESTONE,
            signUpProperty
        )
    }


    private fun viewAPPROVEDMilstonePanel(category:String,MilestoneNumber:Int){
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)
        props.put(MoEngageConstants.MILESTONE_NUMBER, MilestoneNumber)

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_VIEWED_APPROVED_MILESTONE,
            props)
    }
    }
