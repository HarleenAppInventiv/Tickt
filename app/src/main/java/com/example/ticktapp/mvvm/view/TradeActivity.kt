package com.example.ticktapp.mvvm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.Trade
import com.app.core.model.tradesmodel.TradeData
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.app.core.util.UserType
import com.example.ticktapp.R
import com.example.ticktapp.adapters.TradeAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityTradeBinding
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class TradeActivity : BaseActivity(),
    View.OnClickListener, TradeAdapter.TradeAdapterListener,
    SwipeRefreshLayout.OnRefreshListener {
    private var isForEdit: Boolean = false
    private lateinit var mBinding: ActivityTradeBinding
    private lateinit var mAdapter: TradeAdapter
    private val list by lazy { ArrayList<Trade?>() }
    private val selectedlist by lazy { ArrayList<String?>() }
    private var email: String? = null
    private var name: String? = null
    private var phoneno: String? = null
    private var password: String? = null
    private var selectedPosition = 0
    private var tradeData: ArrayList<TradeData> = ArrayList()
    private var tradeDataSelected: ArrayList<TradeData> = ArrayList()
    private var specializatinData: ArrayList<SpecialisationData> = ArrayList()

    private val mViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trade)
        mBinding.model = mViewModel
        getIntentData()
        initView()
        if(!isForEdit)
        setProgressDots()
        setObservers()
        setListeners()
        initRecyclerView()
        mViewModel.getTradeList(false)
    }


    fun setProgressDots() {
        mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
        mBinding.rlToolbar.v1.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v2.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v3.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v4.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v5.background =
            ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
        mBinding.rlToolbar.v6.background =
            ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
        mBinding.rlToolbar.v7.background =
            ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
        if (PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt() == 1) {
            mBinding.rlToolbar.v8.visibility = View.VISIBLE
            mBinding.rlToolbar.v8.background =
                ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
        }
    }

    private fun getIntentData() {
        isForEdit = intent.getBooleanExtra("isForEdit", false)
        if (!isForEdit) {
            email = intent.getStringExtra(IntentConstants.EMAIL)
            name = intent.getStringExtra(IntentConstants.FIRST_NAME)
            phoneno = intent.getStringExtra(IntentConstants.MOBILE_NUMBER)
            password = intent.getStringExtra(IntentConstants.PASSWORD)
        } else {
            tradeData = intent.getSerializableExtra("trade") as ArrayList<TradeData>
            specializatinData =
                intent.getSerializableExtra("specialization") as ArrayList<SpecialisationData>
        }
    }

    private fun initRecyclerView() {
        mAdapter = TradeAdapter(this, list)
        mBinding.rvTrade.adapter = mAdapter
        val layoutManager = GridLayoutManager(this, 3)
        mBinding.rvTrade.layoutManager = layoutManager
    }


    private fun setListeners() {
        iv_back.setOnClickListener(this)
        mBinding.refreshLayout.setOnRefreshListener(this)
        mBinding.tvYellowBtn.setOnClickListener(this)
    }

    private fun initView() {
        mBinding.tvYellowBtn.setText(getString(R.string.next))

        if (PreferenceManager.getString(PreferenceManager.USER_TYPE)
                ?.toInt() == UserType.BUILDER
        ) {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.your_trade_builder))
        } else {
            mBinding.rlToolbar.tvTitle.setText(getString(R.string.your_trade_tradie))
        }
    }

    /**
     * Setting up spannable string to show the "Register now in different font and color"
     *
     */
    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
        })
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                mBinding.refreshLayout.isRefreshing = false
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                mBinding.refreshLayout.isRefreshing = false
                mViewModel.mTradeListingResponseModel.trade?.let {
                    list.clear()
                    list.addAll(it)
                }
                if (list.size == 0)
                    mBinding.tvNoData.visibility = View.VISIBLE
                else
                    mBinding.tvNoData.visibility = View.GONE

                if (isForEdit) {
                    first@ for (item in tradeData) {
                        second@ for (data in list) {
                            if (item.tradeId.equals(data?.id)) {
                                data?.isSelected = true
                                break@first
                                break@second
                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    override fun onClick(v: View?) {
        when (v) {
            iv_back -> {
                onBackPressed()
            }
            mBinding.tvYellowBtn -> {
                selectedlist.clear()
                tradeDataSelected.clear()
                list.forEachIndexed { index, element ->
                    if (list.get(index)?.isSelected == true) {
                        val trade=TradeData()
                        trade.tradeId=list.get(index)!!.id
                        trade.tradeName=list.get(index)!!.tradeName
                        trade.tradeSelectedUrl=list.get(index)!!.selectedUrl
                        trade.tradeId=list.get(index)!!.id
                        selectedPosition = index
                        selectedlist.add(list.get(index)!!.id)
                        tradeDataSelected.add(trade)
                    }
                }
                if (selectedlist.size > 0) {
                    if (!isForEdit) {
                        startActivity(Intent(this, SpecializationActivity::class.java).apply {
                            putExtra(IntentConstants.MOBILE_NUMBER, phoneno)
                            putExtra(IntentConstants.FIRST_NAME, name)
                            putExtra(IntentConstants.EMAIL, email)
                            putExtra(IntentConstants.PASSWORD, password)
                            putExtra(IntentConstants.TRADE_LIST, selectedlist)
                            putParcelableArrayListExtra(
                                IntentConstants.SPEC_LIST,
                                list.get(selectedPosition)?.specialisations
                            )
                            putParcelableArrayListExtra(
                                IntentConstants.QUAL_LIST,
                                list.get(selectedPosition)?.qualifications
                            )
                        })
                    } else {
                        startActivityForResult(
                            Intent(this, SpecializationActivity::class.java).putExtra(
                                "trade",
                                tradeDataSelected
                            ).putExtra("specialization", specializatinData)
                                .putExtra("isForEdit", true).putExtra(
                                    IntentConstants.TRADE_LIST, selectedlist
                                )
                                .putParcelableArrayListExtra(
                                    IntentConstants.SPEC_LIST,
                                    list.get(selectedPosition)?.specialisations
                                ),
                            PermissionConstants.TRADE_EDIT
                        )
                    }
                } else
                    showToastShort(getString(R.string.please_select_trade))
            }
        }
    }

    override fun onRefresh() {
        mViewModel.getTradeList(true)
    }

    override fun onTradeClick(position: Int) {
        list.forEachIndexed { index, element ->
            if (index == position) {
                if (list.get(index)?.isSelected == true)
                    list.get(index)?.isSelected = false
                else
                    list.get(index)?.isSelected = true
            } else {
                list.get(index)?.isSelected = false
            }
        }

        mAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode==PermissionConstants.TRADE_EDIT)
        {
            setResult(RESULT_OK,data)
            finish()
        }
    }
}