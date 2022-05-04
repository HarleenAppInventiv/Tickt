package com.example.ticktapp.mvvm.view.builder.categories

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.model.tradesmodel.Trade
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.TradeAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAlltradesBinding
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel

class AllTradesActivity : BaseActivity(), TradeAdapter.TradeAdapterListener {
    private lateinit var allTradesBinding: ActivityAlltradesBinding
    private lateinit var allTradesAdapter: TradeAdapter
    private val list by lazy { ArrayList<Trade?>() }
    private val allTradesViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allTradesBinding = DataBindingUtil.setContentView(this, R.layout.activity_alltrades)
        initRecyclerView()
        setObservers()
        allTradesViewModel.getTradeList(false)
    }

    override fun onPostResume() {
        super.onPostResume()
        allTradesBinding.searchToolbarBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setObservers() {
        setBaseViewModel(allTradesViewModel)
        allTradesViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        allTradesAdapter = TradeAdapter(this, list)
        val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        allTradesBinding.rvSearchResult.layoutManager = layoutManager
        allTradesBinding.rvSearchResult.adapter = allTradesAdapter
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when(apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                allTradesBinding.rvSearchResult.visibility = View.GONE
                allTradesBinding.tvResultTitleNoData.visibility = View.VISIBLE
            }
        }
        super.onException(exception, apiCode)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when(apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                list.clear()
                allTradesViewModel.mTradeListingResponseModel.trade?.let {
                    list.addAll(it)
                }
                if (list.size == 0) {
                    allTradesBinding.rvSearchResult.visibility = View.GONE
                    allTradesBinding.tvResultTitleNoData.visibility = View.VISIBLE
                }
                allTradesAdapter.notifyDataSetChanged()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onTradeClick(position: Int) {

    }

}