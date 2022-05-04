package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.TradieActiveJobsAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentActiveJobsBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.tradie.completemilestone.MilestoneListingActivity
import com.example.ticktapp.mvvm.viewmodel.NewQuoteListRequestViewModel
import com.example.ticktapp.mvvm.viewmodel.TradieJobDashboardViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_toolbar.*

class ActiveJobFragment : BaseFragment(),
    View.OnClickListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener,
    TradieActiveJobsAdapter.onAdapterItemClick {
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: FragmentActiveJobsBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieJobDashboardViewModel::class.java) }
    private lateinit var mAdapter: TradieActiveJobsAdapter
    private val list by lazy { ArrayList<JobRecModel>() }
    private var pageNumber = 1
    private val mViewModelQuote by lazy { ViewModelProvider(this).get(NewQuoteListRequestViewModel::class.java) }
    private var lastPost: Int = -1

    companion object {
        fun getInstance(): ActiveJobFragment {
            val fragment = ActiveJobFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_active_jobs, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    override fun initialiseFragmentBaseViewModel() {
        setObservers()
        initRecyclerView()
        hitApihere(true)
    }


    private fun setObservers() {
        setFragmentBaseViewModel(mViewModel)
        setFragmentBaseViewModel(mViewModelQuote)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModelQuote.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = TradieActiveJobsAdapter(list, this)
        val layoutRecManager = LinearLayoutManager(activity)
        mBinding.rvActiveJobs.layoutManager = layoutRecManager
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)
        mBinding.rvActiveJobs.addOnScrollListener(endlessScrollListener!!)
        mBinding.rvActiveJobs.adapter = mAdapter

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ACTIVE_JOBS -> {
                showToastShort(exception.message)
                val parentFrag: JobDashboardFragment =
                    this@ActiveJobFragment.getParentFragment() as JobDashboardFragment
                parentFrag.updateView(null)
            }
            ApiCodes.GET_QUOTE -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.ACTIVE_JOBS -> {
                val parentFrag: JobDashboardFragment =
                    this@ActiveJobFragment.getParentFragment() as JobDashboardFragment
                parentFrag.updateView(mViewModel.mTradieActiveJobResponse)
                mViewModel.mTradieActiveJobResponse.activeJobsList?.let {
                    if (mAdapter.itemCount == 0 || pageNumber == 1)
                        mAdapter.setData(mViewModel.mTradieActiveJobResponse.activeJobsList!!)
                    else
                        mAdapter.addData(mViewModel.mTradieActiveJobResponse.activeJobsList!!)
                    hodeShowNoData()
                }
            }
            ApiCodes.GET_QUOTE -> {
                mViewModelQuote.getQuoteList().let {
                    if (it.size > 0 && it.get(0).quote_item != null) {
                        startActivityForResult(
                            Intent(
                                mBinding.root.context,
                                ViewQuoteActivity::class.java
                            ).putExtra("data", mAdapter.jobList.get(lastPost))
                                .putExtra("mainData", it.get(0)), 1310
                        )
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun hodeShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
    }

    override fun onClick(v: View?) {
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        pageNumber = page + 1
        mViewModel.getActiveJobList(pageNumber, false)
    }

    fun hitApihere(progress: Boolean) {
        try {
            if (!childFragmentManager.isDestroyed) {
                endlessScrollListener?.resetState()
                list.clear()
                pageNumber = 1
                mViewModel.getActiveJobList(pageNumber, progress)
            }
        } catch (e: IllegalStateException) {
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                hitApihere(true)
            }
        }

    override fun onItemClick(pos: Int) {
        resultLauncher.launch(
            Intent(context, MilestoneListingActivity::class.java)
                .putExtra("jobId", mAdapter.jobList.get(pos).jobId)
                .putExtra("jobName", mAdapter.jobList.get(pos).jobName)
                .putExtra("data", mAdapter.jobList.get(pos))
        )
    }

    override fun onQuoteClick(pos: Int) {
        lastPost = pos
        try {
            mViewModel.mTradieActiveJobResponse.activeJobsList?.get(pos)?.jobId?.let { it1 ->
                getParamData(
                    it1
                )
            }?.let { it2 ->
                mViewModelQuote.getQuoteList(
                    it2, true
                )
            }
        } catch (e: Exception) {
            Log.e("viewQuote", "onQuoteClick: ${e.message}")
        }
    }

    private fun getParamData(jobId: String): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        params.put("jobId", jobId)
        params.put("sortBy", 1)
        params.put("page", 1)
        return params
    }

}