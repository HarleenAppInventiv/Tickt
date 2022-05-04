package com.example.ticktapp.mvvm.view.builder;

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import android.view.Window
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.myrevenue.RevenueList
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.MyEarningAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchSuggestionBinding
import com.example.ticktapp.mvvm.viewmodel.MyRevenueViewModel
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import java.util.*

public class SearchBuilderPastJobsActivity : BaseActivity(), TextWatcher, OnClickListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private lateinit var mBinding: ActivitySearchSuggestionBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MyRevenueViewModel::class.java) }
    private lateinit var mSearchAdapter: MyEarningAdapter
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null

    private val jobList by lazy { ArrayList<RevenueList>() }
    private var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_suggestion)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        setupView()
        listener()
        setObservers()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
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
        mBinding.edSearch.hint = getString(R.string.search_your_transcation)
        mBinding.edSearch.addTextChangedListener(this)
        mSearchAdapter = MyEarningAdapter(jobList, true)
        val layoutRecManager = LinearLayoutManager(this)
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)

        mBinding.rvSearchResult.layoutManager = layoutRecManager
        mBinding.rvSearchResult.addOnScrollListener(endlessScrollListener!!)
        mBinding.rvSearchResult.adapter = mSearchAdapter;
    }

    private fun listener() {
        mBinding.ivCloseSearch.setOnClickListener {
            mBinding.edSearch.setText("")
        }
        mBinding.searchToolbarBack.setOnClickListener { onBackPressed() }
    }

    private fun callSearchAPI(searchText: String) {
        mBinding.pvSearchSuggestion.visibility = VISIBLE
        mViewModel.getBuilderSearchRevenue(searchText, page = pageNumber)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.MY_REVENUE_REQUEST -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.MY_REVENUE_REQUEST -> {
                mBinding.pvSearchSuggestion.visibility = GONE
                if (mBinding.edSearch.text?.length!! > 0) {
                    mBinding.tvResultTitle.visibility = GONE

                    mViewModel.getSavedJobListing()[0].revenue?.revenueList.let {
                        it?.let { it1 -> jobList.addAll(it1) }
                        mSearchAdapter.notifyDataSetChanged()
                    }
                    if (jobList.size == 0) {
                        mBinding.tvResultTitleNoData.visibility = VISIBLE
                    } else {
                        mBinding.tvResultTitleNoData.visibility = GONE
                    }
                } else {
                    jobList.clear()
                    mSearchAdapter.notifyDataSetChanged()
                }
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        mBinding.tvResultTitle.visibility = View.GONE
        if (p0 != null) {
            if (p0.isEmpty()) {
                mBinding.tvResultTitleNoData.visibility = GONE
                jobList.clear()
                mSearchAdapter.notifyDataSetChanged()
                mBinding.edSearch.setTypeface(
                    ResourcesCompat.getFont(
                        this,
                        R.font.neue_haas_display_roman
                    )
                )
                mBinding.edSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.search,
                    0,
                    0,
                    0
                )
            } else {
                mBinding.edSearch.setTypeface(
                    ResourcesCompat.getFont(
                        this,
                        R.font.neue_haas_display_bold
                    )
                )
                mBinding.edSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                jobList.clear()
                pageNumber = 1
                callSearchAPI(p0.toString())
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun onClick(view: View?) {
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        pageNumber = page + 1
        mViewModel.getBuilderSearchRevenue(mBinding.edSearch.toString().trim(), page = pageNumber)
    }
}
