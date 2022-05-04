package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradesmodel.Trade
import com.app.core.model.tradesmodel.TradeHome
import com.app.core.model.tradesmodel.TradeResult
import com.app.core.repo.TradeRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TradeViewModel : BaseViewModel() {
    private var mList: ArrayList<Trade>? = null
    public var mRecList: ArrayList<TradeHome>? = null
    lateinit var mTradeListingResponseModel: TradeResult

    private lateinit var mResetToken: String
    private val mRepo by lazy {
        TradeRepo()
    }


    /**
     * Api call to get Trade list.
     */
    fun getTradeList(swipe: Boolean) = CoroutinesBase.main {
        if (!swipe)
            setLoadingState(LoadingState.LOADING())
        updateView(mRepo.getTradeListing()) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradeResult::class.java)
                        mTradeListingResponseModel = responseModel
                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
        if (!swipe)
            setLoadingState(LoadingState.LOADED())

    }

    /**
     * Api call to get Trade list.
     */
    fun tradeSaveListing(page: Int) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING())
        updateView(mRepo.tradeSaveListing(page)) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                        val jobModel = object : TypeToken<List<TradeHome>>() {}
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson<ArrayList<TradeHome>>(gson.toJson(it.data), jobModel.type)
                        this.mRecList = responseModel

                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
        setLoadingState(LoadingState.LOADED())

    }
    /**
     * Api call to get Trade list.
     */
    fun tradeSaveListingWithoutLoader(page: Int) = CoroutinesBase.main {
        updateView(mRepo.tradeSaveListing(page)) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                        val jobModel = object : TypeToken<List<TradeHome>>() {}
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson<ArrayList<TradeHome>>(gson.toJson(it.data), jobModel.type)
                        this.mRecList = responseModel

                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
        setLoadingState(LoadingState.LOADED())

    }

    init {
        mList = ArrayList()
        mRecList= ArrayList()
    }

    fun getTradesListing() = mList

    fun getToken() = mResetToken
}