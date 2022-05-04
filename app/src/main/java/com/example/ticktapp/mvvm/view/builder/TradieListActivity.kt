package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.tradesmodel.TradeHome
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.HomeTradieAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieListBinding
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel

class TradieListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {
    private lateinit var mAdapter: HomeTradieAdapter
    private lateinit var mBinding: ActivityTradieListBinding
    private lateinit var title: String
    private lateinit var list: ArrayList<TradeHome>
    private val mViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_list)
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setObservers()
        setData()
        initRecyclerView()
        mViewModel.tradeSaveListing(1)
    }

    private fun setData() {
        mBinding.tvTitle.text = title
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun getIntentData() {
        if (intent.hasExtra("data")) {
            list = intent.getSerializableExtra("data") as ArrayList<TradeHome>
        } else {
            list = ArrayList()
        }
        title = intent.getStringExtra("title").toString()
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        val data = Intent()
        data.putExtra("data", list)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun initRecyclerView() {
        mAdapter = HomeTradieAdapter(list) {
            val pos = it?.tag as Int
            startActivityForResult(
                Intent(
                    this,
                    TradieProfileActivity::class.java
                ).putExtra("data", list[pos])
                    .putExtra("isBuilder", true), 1310
            )
        }
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvTradie.layoutManager = layoutRecManager
        mBinding.rvTradie.adapter = mAdapter
    }


    private fun hideShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
    }


    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    override fun onClick(p0: View?) {

    }

    override fun onRefresh() {
        mBinding.srLayout.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            mViewModel.tradeSaveListing(1)
        }
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.GET_SAVED_TRADE_LIST -> {
                hideShowNoData()
            }
        }
        super.onException(exception, apiCode)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.GET_SAVED_TRADE_LIST -> {
                list.clear()
                mViewModel.mRecList?.let {
                    list.addAll(it)
                }
                mAdapter.notifyDataSetChanged()
                hideShowNoData()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

}
