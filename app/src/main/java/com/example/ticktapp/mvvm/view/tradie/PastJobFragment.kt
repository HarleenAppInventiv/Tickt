package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
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
import com.example.ticktapp.adapters.TradiePastJobsAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentPastJobBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.viewmodel.TradieJobDashboardViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation

class PastJobFragment : BaseFragment(),
    View.OnClickListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {

    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: FragmentPastJobBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieJobDashboardViewModel::class.java) }
    private lateinit var mAdapter: TradiePastJobsAdapter
    private val list by lazy { ArrayList<JobRecModel>() }
    private var pageNumber = 1

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
        mAdapter = TradiePastJobsAdapter(list)
        mAdapter.setFragmentContext(this)
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
            ApiCodes.APPLIED_JOBS -> {
                showToastShort(exception.message)
                val parentFrag: JobDashboardFragment =
                    this@PastJobFragment.getParentFragment() as JobDashboardFragment
                parentFrag.updateView(null)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {

        when (apiCode) {
            ApiCodes.PAST_JOBS -> {
                val parentFrag: JobDashboardFragment =
                    this@PastJobFragment.getParentFragment() as JobDashboardFragment
                parentFrag.updateView(mViewModel.mTradiePastJobResponse)
                mViewModel.mTradiePastJobResponse.pastJobList?.let {
                    if (mAdapter.itemCount == 0 || pageNumber
                        == 1
                    )
                        mAdapter.setData(mViewModel.mTradiePastJobResponse.pastJobList!!)
                    else
                        mAdapter.addData(mViewModel.mTradiePastJobResponse.pastJobList!!)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_past_job, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    companion object {
        fun getInstance(): PastJobFragment {
            return PastJobFragment()
        }
    }

    override fun onClick(view: View?) {
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        pageNumber = page + 1
        mViewModel.getPastJobList(pageNumber, false)
    }

    fun hitApihere(progress: Boolean) {
        try {
            if (!childFragmentManager.isDestroyed) {

                endlessScrollListener?.resetState()
                list.clear()
                pageNumber = 1
                mViewModel.getPastJobList(pageNumber, false)
            }
        } catch (e: IllegalStateException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            hitApihere(true)
            if (data?.hasExtra("id") == true) {
                val id = data.getStringExtra("id")
                list.forEach {
                    if (it.jobId == id) {
                        it.isRated = true
                    }
                }
                mBinding.rvActiveJobs.adapter?.notifyDataSetChanged()
            }
        }
    }

}