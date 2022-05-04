package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.TradieJobRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.TradieJobDashboardModel
import com.google.gson.Gson

class BuilderJobDashboardViewModel : BaseViewModel() {

    private val mRepo by lazy { TradieJobRepo() }
    lateinit var mTradieActiveJobResponse: TradieJobDashboardModel


    /**
     * Api call to get Active Job list.
     */
    fun activeBuilderJobList(page:Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.activeBuilderJobList(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradieJobDashboardModel::class.java)
                        mTradieActiveJobResponse = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get Applied  Job list.
     */
    fun openBuilderJobList(page:Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.openBuilderJobList(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradieJobDashboardModel::class.java)
                        mTradieActiveJobResponse = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get Applied  Job list.
     */
    fun pastBuilderJobList(page:Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.pastBuilderJobList(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradieJobDashboardModel::class.java)
                        mTradieActiveJobResponse = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
}
