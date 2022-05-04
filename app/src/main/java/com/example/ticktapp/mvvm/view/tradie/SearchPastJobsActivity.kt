package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
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
import com.app.core.model.myrevenue.RevenueList
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.MyRevenueAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchSuggestionBinding
import com.example.ticktapp.mvvm.viewmodel.MyRevenueViewModel
import java.util.*

public class SearchPastJobsActivity : BaseActivity(), TextWatcher, OnClickListener {
    private lateinit var mBinding: ActivitySearchSuggestionBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MyRevenueViewModel::class.java) }
    private lateinit var mSearchAdapter: MyRevenueAdapter

    private val jobList by lazy { ArrayList<RevenueList>() }

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
        mBinding.edSearch.addTextChangedListener(this)
        mSearchAdapter = MyRevenueAdapter(jobList,true)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvSearchResult.layoutManager = layoutRecManager
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
        mViewModel.getSearchRevenue(searchText,page = 1)
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
                    if (mViewModel.getSavedJobListing()[0].revenue?.revenueList?.size == 0) {
                        mBinding.tvResultTitleNoData.visibility = VISIBLE
                    } else {
                        mBinding.tvResultTitleNoData.visibility = GONE
                    }
                    mSearchAdapter = mViewModel.getSavedJobListing()[0].revenue?.revenueList?.let {
                        MyRevenueAdapter(
                            it,true)
                    }!!
                    mBinding.rvSearchResult.adapter = mSearchAdapter;
                } else {
                    jobList.clear()
                    mSearchAdapter = MyRevenueAdapter(jobList,true)
                    mBinding.rvSearchResult.adapter = mSearchAdapter;
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
                mSearchAdapter = MyRevenueAdapter(jobList,true)
                mBinding.rvSearchResult.adapter = mSearchAdapter;
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
                //       mBinding.ivCloseSearch.visibility = INVISIBLE
            } else {
                mBinding.edSearch.setTypeface(
                    ResourcesCompat.getFont(
                        this,
                        R.font.neue_haas_display_bold
                    )
                )
                mBinding.edSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                callSearchAPI(p0.toString())
                //    mBinding.ivCloseSearch.visibility = VISIBLE
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun onClick(view: View?) {
        val pos = view?.tag.toString().toInt()
        startActivity(
            Intent(
                SearchDataActivity@ this,
                SearchAmountActivity::class.java
            ).putExtra("data", jobList[pos])
        )
    }
}