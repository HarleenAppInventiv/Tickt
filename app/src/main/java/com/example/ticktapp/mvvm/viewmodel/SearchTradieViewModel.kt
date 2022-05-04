package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradesmodel.TradeHome
import com.app.core.model.tradesmodel.TradieSearchResponse
import com.app.core.repo.TradieSearchRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class SearchTradieViewModel : BaseViewModel() {

    private val mRepo by lazy { TradieSearchRepo() }
    lateinit var tradeHome: List<TradeHome>


    fun search(request: Any, progress: Boolean = true) {
        CoroutinesBase.main {
            if (progress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.searchTradie(request)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val responseModel =
                                Gson().fromJson(
                                    Gson().toJson(it.data),
                                    TradieSearchResponse::class.java
                                )
                            this.tradeHome = responseModel.data
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}


