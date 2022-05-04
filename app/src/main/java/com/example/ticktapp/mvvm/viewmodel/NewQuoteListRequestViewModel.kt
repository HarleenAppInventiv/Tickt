package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradie.QuoteData
import com.app.core.model.tradie.QuoteItem
import com.app.core.model.tradie.QuoteList
import com.app.core.model.tradie.QuoteTradie
import com.app.core.repo.QuoteRequestRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class NewQuoteListRequestViewModel : BaseViewModel() {

    private val mRepo by lazy { QuoteRequestRepo() }
    private var mTradieRequestList = ArrayList<QuoteTradie>()
    private var mTradieRequest = QuoteItem()


    fun getQuoteList(): ArrayList<QuoteTradie> {
        return mTradieRequestList
    }

    fun getQuote(): QuoteItem {
        return mTradieRequest
    }

    /**
     * Api call to get New tradie list for applicant.
     */
    fun getQuoteList(params: HashMap<String, Any>, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getQuoteList(params)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), QuoteList::class.java)
                        mTradieRequestList = try {
                            responseModel.resultData as ArrayList
                        } catch (e: Exception) {
                            ArrayList(responseModel.resultData)
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun getQuoteListWithTraide(params: HashMap<String, Any>, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getQuoteListWithTraide(params)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), QuoteList::class.java)
                        mTradieRequestList = ArrayList(responseModel.resultData)
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun addItem(params: HashMap<String, Any>, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addItem(params)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), QuoteData::class.java)
                        mTradieRequest = responseModel.resultData!!
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


    fun addQuote(params: HashMap<String, Any>, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addQuote(params)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


    fun updateItem(params: HashMap<String, Any>, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.editItem(params)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        /*val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), QuoteList::class.java)
                        mTradieRequestList = ArrayList(responseModel.resultData)*/
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun deleteItem(params: HashMap<String, Any>, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.deleteItem(params)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        /*val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), QuoteList::class.java)
                        mTradieRequestList = ArrayList(responseModel.resultData)*/
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
}

