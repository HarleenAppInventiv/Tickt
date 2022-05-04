package com.example.ticktapp.mvvm.view.builder

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
import com.example.ticktapp.adapters.BuilderActiveJobsAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentBuilderActiveJobsBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.example.ticktapp.mvvm.viewmodel.BuilderJobDashboardViewModel
import com.example.ticktapp.mvvm.viewmodel.NewQuoteListRequestViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_toolbar.*

class ActiveBuilderJobFragment : BaseFragment(),
    View.OnClickListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var lastPost: Int = -1
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: FragmentBuilderActiveJobsBinding
    private lateinit var mRootView: View
    private val mViewModel by lazy { ViewModelProvider(this).get(BuilderJobDashboardViewModel::class.java) }
    private lateinit var mAdapter: BuilderActiveJobsAdapter
    private val list by lazy { ArrayList<JobDashboardModel>() }
    private var pageNumber = 1
    private val mViewModelQuote by lazy { ViewModelProvider(this).get(NewQuoteListRequestViewModel::class.java) }

    companion object {
        fun getInstance(): ActiveBuilderJobFragment {
            val fragment = ActiveBuilderJobFragment()
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
        setFragmentBaseViewModel(mViewModelQuote)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModelQuote.getResponseObserver().observe(this, this)
    }

    private fun getParamData(jobId: String): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        params.put("jobId", jobId)
        params.put("page", 1)
        params.put("sortBy", 1)
        return params
    }

    private fun initRecyclerView() {
        mBinding.tvResultTitleNoData.text = getString(R.string.no_active_jobs_found)
        mAdapter = BuilderActiveJobsAdapter(list) {
            val pos = it.tag as Int
            lastPost = pos
            list.get(pos).jobId?.let { it1 -> getParamData(it1) }?.let { it2 ->
                mViewModelQuote.getQuoteList(
                    it2, true
                )
            }
        }
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
            ApiCodes.ACTIVE_TRADIE_JOBS -> {
                showToastShort(exception.message)
                val parentFrag: JobDashboardBuilderFragment =
                    this@ActiveBuilderJobFragment.getParentFragment() as JobDashboardBuilderFragment
                parentFrag.updateView(null)
            }
            ApiCodes.GET_QUOTE -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {

        when (apiCode) {
            ApiCodes.ACTIVE_TRADIE_JOBS -> {
                val parentFrag: JobDashboardBuilderFragment =
                    this@ActiveBuilderJobFragment.getParentFragment() as JobDashboardBuilderFragment
                parentFrag.updateView(mViewModel.mTradieActiveJobResponse)
                mViewModel.mTradieActiveJobResponse.activeJobList?.let {
                    if (mAdapter.itemCount == 0 || pageNumber == 1)
                        mAdapter.setData(mViewModel.mTradieActiveJobResponse.activeJobList!!)
                    else
                        mAdapter.addData(mViewModel.mTradieActiveJobResponse.activeJobList!!)
                    hodeShowNoData()
                }
            }
            ApiCodes.GET_QUOTE -> {
                mViewModelQuote.getQuoteList().let {
                    if (it.size > 0 && it.get(0).quote_item != null) {
                        startActivityForResult(
                            Intent(
                                mBinding.root.context,
                                QuoteDetailsActivity::class.java
                            ).putExtra("data", list.get(lastPost))
                                .putExtra("mainData", it.get(0))
                                .putExtra("isBuilder", true).putExtra("isAction", false), 1310
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
        mViewModel.activeBuilderJobList(pageNumber, false)
    }

    fun hitApihere(progress: Boolean) {
        try {
            if (!childFragmentManager.isDestroyed) {
                pageNumber = 1
                mViewModel.activeBuilderJobList(pageNumber, progress)
            }
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
        }
    }
}
