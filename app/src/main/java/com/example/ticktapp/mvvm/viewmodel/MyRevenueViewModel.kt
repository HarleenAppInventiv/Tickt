package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.myrevenue.MyRevenueModel
import com.app.core.model.myrevenue.RevenueList
import com.app.core.repo.MyRevenueRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Job

class MyRevenueViewModel : BaseViewModel() {

    private var job: Job? = null
    private val mRepo by lazy { MyRevenueRepo() }
    private var mTradieSavedJobRequestList = ArrayList<MyRevenueModel>()
    public var mRevenueList = RevenueList()


    fun getSavedJobListing(): ArrayList<MyRevenueModel> {
        return mTradieSavedJobRequestList
    }

    fun getMyRevenue(page: Int, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getMyRevenue(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<MyRevenueModel>::class.java)
                                .toList()
                        mTradieSavedJobRequestList = ArrayList(model)
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun myBuilderRevenueList(page: Int, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.myBuilderRevenueList(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<MyRevenueModel>::class.java)
                                .toList()
                        mTradieSavedJobRequestList = ArrayList(model)
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun myBuilderRevenueDetails(jobId: String, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.myBuilderRevenueDetails(jobId)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), RevenueList::class.java)
                        mRevenueList = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun getSearchRevenue(text: String, page: Int) =
        CoroutinesBase.main {
            val resp = mRepo.getSearchRevenue(text, page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<MyRevenueModel>::class.java)
                                .toList()
                        mTradieSavedJobRequestList = ArrayList(model)
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun getBuilderSearchRevenue(text: String, page: Int) {
        job?.cancel()
        job = CoroutinesBase.main {
            val resp = mRepo.getBuilderSearchRevenue(text, page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<MyRevenueModel>::class.java)
                                .toList()
                        mTradieSavedJobRequestList = ArrayList(model)
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}
