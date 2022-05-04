package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.TradieJobRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.TradieJobsDashboardModel
import com.google.gson.Gson

class TradieJobDashboardViewModel : BaseViewModel() {

    private val mRepo by lazy { TradieJobRepo() }
    lateinit var mTradieActiveJobResponse: TradieJobsDashboardModel
    lateinit var mTradiePastJobResponse: TradieJobsDashboardModel



    /**
     * Api call to get Active Job list.
     */
    fun getActiveJobList(page:Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getActiveJobListing(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradieJobsDashboardModel::class.java)
                        mTradieActiveJobResponse = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get Applied  Job list.
     */
    fun getAppliedJobList(page:Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getAppliedJobListing(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradieJobsDashboardModel::class.java)
                        mTradieActiveJobResponse = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get past  Job list.
     */
    fun getPastJobList(page:Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getPastJobListing(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradieJobsDashboardModel::class.java)
                        mTradiePastJobResponse = responseModel
                        Log.d("pastjobexception",it.data.toString().toString())
                    }
                    is API_VIEWMODEL_DATA.API_EXCEPTION->
                    {
                        Log.d("pastjobexception",it.toString())
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


}


