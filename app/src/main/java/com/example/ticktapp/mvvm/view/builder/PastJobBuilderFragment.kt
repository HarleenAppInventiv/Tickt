package com.example.ticktapp.mvvm.view.builder

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
import com.example.ticktapp.adapters.BuilderPastJobsAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentBuilderActiveJobsBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.example.ticktapp.mvvm.view.builder.postjob.PostNewJobActivity
import com.example.ticktapp.mvvm.viewmodel.BuilderJobDashboardViewModel
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_toolbar.*

class PastJobBuilderFragment : BaseFragment(),
    View.OnClickListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: FragmentBuilderActiveJobsBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(BuilderJobDashboardViewModel::class.java) }
    private lateinit var mAdapter: BuilderPastJobsAdapter
    private val list by lazy { ArrayList<JobDashboardModel>() }
    private var pageNumber = 1
    private val mViewModelJobs by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }

    companion object {
        fun getInstance(): PastJobBuilderFragment {
            val fragment = PastJobBuilderFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_builder_active_jobs,
                container,
                false
            )
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
        setFragmentBaseViewModel(mViewModelJobs)
        mViewModelJobs.getResponseObserver().observe(this, this)
        mViewModel.getResponseObserver().observe(this, this)

    }

    private fun initRecyclerView() {
        mAdapter = BuilderPastJobsAdapter(list) {
            val pos = it.tag as Int
            mViewModelJobs.jobDetailsFromBuilderRepublish(
                true,
                list.get(pos).jobId,
                list.get(pos).tradeId,
                list.get(pos).specializationId
            )
        }
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
        mBinding.tvResultTitleNoData.text = getString(R.string.completed_job_not_found)

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ACTIVE_TRADIE_JOBS -> {
                showToastShort(exception.message)
                val parentFrag: JobDashboardBuilderFragment =
                    this@PastJobBuilderFragment.getParentFragment() as JobDashboardBuilderFragment
                parentFrag.updateView(null)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {

        when (apiCode) {
            ApiCodes.ACTIVE_TRADIE_JOBS -> {
                val parentFrag: JobDashboardBuilderFragment =
                    this@PastJobBuilderFragment.getParentFragment() as JobDashboardBuilderFragment
                parentFrag.updateView(mViewModel.mTradieActiveJobResponse)
                mViewModel.mTradieActiveJobResponse.pastJobList?.let {
                    if (mAdapter.itemCount == 0 || pageNumber
                        == 1
                    )
                        mAdapter.setData(mViewModel.mTradieActiveJobResponse.pastJobList!!)
                    else
                        mAdapter.addData(mViewModel.mTradieActiveJobResponse.pastJobList!!)
                    hideShowNoData()
                }
            }
            ApiCodes.JOB_REPULISH_DETAILS -> {
                mViewModelJobs.mJsonResponseRepublishModel.let {
                    startActivity(
                        Intent(context, PostNewJobActivity::class.java)
                            .putExtra("data", mViewModelJobs.mJsonResponseRepublishModel)
                    )
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
        mViewModel.pastBuilderJobList(pageNumber, false)
    }

    fun hitApihere(progress: Boolean) {
        try {
            if (!childFragmentManager.isDestroyed) {
                list.clear()
                pageNumber = 1
                mViewModel.pastBuilderJobList(pageNumber, progress)
            }
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
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
