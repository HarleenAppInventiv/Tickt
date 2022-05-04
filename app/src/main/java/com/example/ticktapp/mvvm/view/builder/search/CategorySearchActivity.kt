package com.example.ticktapp.mvvm.view.builder.search

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.tradesmodel.Trade
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.SearchCircleDataAdapter
import com.example.ticktapp.adapters.SearchDataAdapter
import com.example.ticktapp.adapters.TradeAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchCategoryBinding
import com.app.core.model.jobmodel.JobModel
import com.example.ticktapp.mvvm.view.tradie.SearchLocationActivity
import com.example.ticktapp.mvvm.viewmodel.SearchDataViewModel
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel

class CategorySearchActivity : BaseActivity(), View.OnClickListener,
    TradeAdapter.TradeAdapterListener, TextWatcher {
    private lateinit var mBinding: ActivitySearchCategoryBinding
    private lateinit var mAdapter: TradeAdapter
    private val list by lazy { ArrayList<Trade?>() }
    private lateinit var mSearchAdapter: SearchCircleDataAdapter
    private lateinit var mSearchDataAdapter: SearchDataAdapter

    private val jobList by lazy { java.util.ArrayList<JobModel>() }
    private val tmpList by lazy { java.util.ArrayList<JobModel>() }

    private val mViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }
    private val mSearchViewModel by lazy { ViewModelProvider(this).get(SearchDataViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_category)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        setObservers()
        setListeners()
        initRecyclerView()
        mViewModel.getTradeList(false)
        mSearchViewModel.getRearchData(false)
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
        mAdapter = TradeAdapter(this, list)
        mBinding.rvTrade.adapter = mAdapter
        val layoutManager = GridLayoutManager(this, 3)
        mBinding.rvTrade.layoutManager = layoutManager

        mBinding.edSearch.addTextChangedListener(this)
        mSearchAdapter = SearchCircleDataAdapter(this, jobList)
        mSearchDataAdapter = SearchDataAdapter(this, tmpList)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvSearchResult.layoutManager = layoutRecManager
        mBinding.rvSearchResult.adapter = mSearchDataAdapter;
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
//                mBinding.edSearch.hint = ""
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
                   /* mBinding.edSearch.hint =
                        getString(R.string.what_tradeperson_are_you_searching_for)*/
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
        setBaseViewModel(mViewModel)
        setBaseViewModel(mSearchViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mSearchViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
        })
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                mBinding.tvNoData.visibility = VISIBLE
            }
            ApiCodes.RECENT_SEARCH -> {
                mBinding.tvSearchTitle.visibility = View.GONE
            }
            ApiCodes.SEARCH_DATA -> {
                if (!(exception.message.equals("cancelled") || exception.message.equals("Request failed. Please retry"))) {
                    mBinding.tvSearchTitle.visibility = View.GONE
                    mBinding.pvSearchSuggestion.visibility = View.GONE
                    jobList.clear()
                    mBinding.rvSearchResult.adapter = mSearchAdapter;
                    mSearchAdapter.notifyDataSetChanged()
                    mBinding.tvResultTitleNoData.visibility = VISIBLE
                }
                if (mBinding.edSearch.text.toString().isEmpty()) {
                    mBinding.tvSearchTitle.visibility = View.VISIBLE
                    mBinding.pvSearchSuggestion.visibility = View.GONE
                    jobList.clear()
                    mSearchDataAdapter = SearchDataAdapter(this, tmpList)
                    mBinding.rvSearchResult.adapter = mSearchDataAdapter;
                    mBinding.tvResultTitleNoData.visibility = View.GONE
                }
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                mViewModel.mTradeListingResponseModel.trade?.let {
                    list.addAll(it)
                }
                if (list.size == 0)
                    mBinding.tvNoData.visibility = VISIBLE
                else
                    mBinding.tvNoData.visibility = View.GONE

                mAdapter.notifyDataSetChanged()
            }
            ApiCodes.RECENT_SEARCH -> {
                tmpList.clear()
                mSearchViewModel.jobListModel.let {
                    it.resultData?.let { it1 -> tmpList.addAll(it1) }
                }
                if (tmpList.size > 0) {
                    mBinding.rvSearchResult.visibility = VISIBLE
                    mBinding.tvSearchTitle.visibility = VISIBLE
                } else {
                    mBinding.rvSearchResult.visibility = View.GONE
                    mBinding.tvSearchTitle.visibility = View.GONE
                }
                mSearchDataAdapter.notifyDataSetChanged()
            }
            ApiCodes.SEARCH_DATA -> {
                mBinding.tvSearchTitle.visibility = View.GONE
                mBinding.pvSearchSuggestion.visibility = View.GONE
                if (mBinding.edSearch.text?.length!! > 0) {
                    mBinding.rvTrade.visibility = View.GONE
                    mBinding.tvDescTitle.visibility = View.GONE
                    mBinding.tvDescTitle.visibility = View.GONE
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
                    mBinding.rvTrade.visibility = VISIBLE
                    mBinding.tvDescTitle.visibility = VISIBLE
                    jobList.clear()
                    mSearchAdapter.notifyDataSetChanged()
                    mSearchDataAdapter = SearchDataAdapter(this, tmpList)
                    mBinding.rvSearchResult.adapter = mSearchDataAdapter;
                    mSearchDataAdapter.notifyDataSetChanged()
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    override fun onTradeClick(position: Int) {
        val spec = ArrayList<String>()
        list.get(position)?.specialisations?.forEach {
            it.id?.let { it1 -> spec.add(it1) }
        }
        startActivity(
            Intent(this, SearchTradieActivity::class.java)
                .putExtra("tradeID", list.get(position)?.id)
                .putExtra("specailsations", spec)
                .putExtra("tradeName",list.get(position)?.tradeName)
        )
        finish()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        mBinding.tvDescTitle.visibility = View.GONE
        mBinding.tvSearchTitle.visibility = View.GONE
        if (p0 != null) {
            if (p0.isEmpty()) {
                mBinding.rvTrade.visibility = VISIBLE
                mBinding.tvDescTitle.visibility = VISIBLE
                mBinding.tvSearchTitle.visibility = VISIBLE
                mBinding.tvResultTitleNoData.visibility = View.GONE
                jobList.clear()
                mSearchAdapter.notifyDataSetChanged()
                mSearchDataAdapter = SearchDataAdapter(this, tmpList)
                mBinding.rvSearchResult.adapter = mSearchDataAdapter;
                mSearchDataAdapter.notifyDataSetChanged()
                mBinding.edSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.search,
                    0,
                    0,
                    0
                )
                mSearchViewModel.cancelRequest()
                mBinding.ivCloseSearch.visibility = INVISIBLE
            } else {
                mBinding.rvTrade.visibility = View.GONE
                mBinding.tvDescTitle.visibility = View.GONE
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
        if (mBinding.tvSearchTitle.visibility == VISIBLE && tmpList.size > 0) {
            startActivity(
                Intent(
                    CategorySearchActivity@ this,
                    SearchLocationActivity::class.java
                ).putExtra("data", tmpList[pos]).putExtra("isTradie", true)
            )
        } else {
            startActivity(
                Intent(
                    CategorySearchActivity@ this,
                    SearchLocationActivity::class.java
                ).putExtra("data", jobList[pos]).putExtra("isTradie", true)
            )
        }
    }
}