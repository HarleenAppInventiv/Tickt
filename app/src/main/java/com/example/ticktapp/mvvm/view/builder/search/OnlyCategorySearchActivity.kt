package com.example.ticktapp.mvvm.view.builder.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.SearchCircleDataAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityOnlySearchCategoryBinding
import com.app.core.model.jobmodel.JobModel
import com.example.ticktapp.mvvm.viewmodel.SearchDataViewModel

class OnlyCategorySearchActivity : BaseActivity(), View.OnClickListener, TextWatcher {
    private lateinit var mBinding: ActivityOnlySearchCategoryBinding
    private lateinit var mSearchAdapter: SearchCircleDataAdapter

    private val jobList by lazy { java.util.ArrayList<JobModel>() }
    private val mSearchViewModel by lazy { ViewModelProvider(this).get(SearchDataViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_only_search_category)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        setObservers()
        setListeners()
        initRecyclerView()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
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

    private fun initRecyclerView() {
        mBinding.edSearch.addTextChangedListener(this)
        mSearchAdapter = SearchCircleDataAdapter(this, jobList)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvSearchResult.layoutManager = layoutRecManager
        mBinding.rvSearchResult.adapter = mSearchAdapter;
    }


    private fun setListeners() {
        mBinding.ivCloseSearch.setOnClickListener {
            mBinding.edSearch.setText("")
        }
        mBinding.searchToolbarBack.setOnClickListener { onBackPressed() }
        mBinding.edSearch.setOnFocusChangeListener { _, b ->
            if (b) {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                    mBinding.edSearch,
                    InputMethodManager.SHOW_FORCED
                )
                mBinding.edSearch.isSingleLine = true
                mBinding.edSearch.hint = ""
                mBinding.edSearch.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    mBinding.edSearch.getWindowToken(),
                    0
                )
                if (mBinding.edSearch.text.toString().isEmpty()) {
                    mBinding.edSearch.isSingleLine = false
                    mBinding.edSearch.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    mBinding.edSearch.hint =
                        getString(R.string.what_tradeperson_are_you_searching_for)
                }
            }
        }
    }

    private fun callSearchAPI(searchText: String) {
        mBinding.pvSearchSuggestion.visibility = VISIBLE
        mSearchViewModel.searchData(searchText)
    }

    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mSearchViewModel)
        mSearchViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {

            ApiCodes.SEARCH_DATA -> {
                if (!(exception.message.equals("cancelled") || exception.message.equals("Request failed. Please retry"))) {
                    mBinding.pvSearchSuggestion.visibility = View.GONE
                    jobList.clear()
                    mSearchAdapter.notifyDataSetChanged()
                    mBinding.tvResultTitleNoData.visibility = VISIBLE
                }
                if (mBinding.edSearch.text.toString().isEmpty()) {
                    mBinding.pvSearchSuggestion.visibility = View.GONE
                    jobList.clear()
                    mSearchAdapter.notifyDataSetChanged()
                    mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {

            ApiCodes.SEARCH_DATA -> {
                mBinding.pvSearchSuggestion.visibility = View.GONE
                if (mBinding.edSearch.text?.length!! > 0) {
                    jobList.clear()
                    mSearchViewModel.jobRectModelList.let {
                        jobList.addAll(it)
                    }
                    if (jobList.size == 0) {
                        mBinding.tvResultTitleNoData.visibility = VISIBLE
                    } else {
                        mBinding.tvResultTitleNoData.visibility = View.GONE
                    }
                    mSearchAdapter = SearchCircleDataAdapter(this, jobList)
                    mBinding.rvSearchResult.adapter = mSearchAdapter;
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
        if (p0 != null) {
            if (p0.isEmpty()) {
                mBinding.tvResultTitleNoData.visibility = View.GONE
                jobList.clear()
                mSearchAdapter.notifyDataSetChanged()
                mBinding.edSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.search,
                    0,
                    0,
                    0
                )
                mSearchViewModel.cancelRequest()
                mBinding.ivCloseSearch.visibility = INVISIBLE
            } else {
                mBinding.edSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                callSearchAPI(p0.toString())
                mBinding.ivCloseSearch.visibility = VISIBLE
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun onClick(p0: View?) {
        val pos = p0?.tag.toString().toInt()
        setResult(
            Activity.RESULT_OK, Intent().putExtra("data", jobList[pos])
        )
        finish()
    }
}