package com.example.ticktapp.mvvm.view.tradie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.TradieAppliedJobsAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentActiveJobsBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.viewmodel.TradieJobDashboardViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_toolbar.*

class AppliedJobFragment : BaseFragment(),
    View.OnClickListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: FragmentActiveJobsBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieJobDashboardViewModel::class.java) }
    private lateinit var mAdapter: TradieAppliedJobsAdapter
    private val list by lazy { ArrayList<JobRecModel>() }
    private var pageNumber = 1

    companion object {
        fun getInstance(): AppliedJobFragment {
            val fragment = AppliedJobFragment()
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
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = TradieAppliedJobsAdapter(list)
        val layoutRecManager = LinearLayoutManager(activity)
        mBinding.rvActiveJobs.layoutManager = layoutRecManager
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)
        mBinding.rvActiveJobs.addOnScrollListener(endlessScrollListener!!)
        mBinding.rvActiveJobs.adapter = mAdapter
        mBinding.tvResultTitleNoData.text = getString(R.string.not_applied_any_job)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.APPLIED_JOBS -> {
                showToastShort(exception.message)
                val parentFrag: JobDashboardFragment =
                    this@AppliedJobFragment.getParentFragment() as JobDashboardFragment
                parentFrag.updateView(null)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {

        when (apiCode) {
            ApiCodes.APPLIED_JOBS -> {
                val parentFrag: JobDashboardFragment =
                    this@AppliedJobFragment.getParentFragment() as JobDashboardFragment
                parentFrag.updateView(mViewModel.mTradieActiveJobResponse)
                mViewModel.mTradieActiveJobResponse.appliedJobList?.let {
                    if (mAdapter.itemCount == 0 || pageNumber
                        == 1
                    )
                        mAdapter.setData(mViewModel.mTradieActiveJobResponse.appliedJobList!!)
                    else
                        mAdapter.addData(mViewModel.mTradieActiveJobResponse.appliedJobList!!)
                    hideShowNoData()
                }
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun hideShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
    }


    override fun onClick(v: View?) {
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        pageNumber = page + 1
        mViewModel.getAppliedJobList(pageNumber, false)
    }

    fun hitApihere(progress: Boolean) {
        try {
            if (!childFragmentManager.isDestroyed) {
                endlessScrollListener?.resetState()
                list.clear()
                pageNumber = 1
                mViewModel.getAppliedJobList(pageNumber, progress)
            }
        } catch (e: IllegalStateException) {
        }
    }

}