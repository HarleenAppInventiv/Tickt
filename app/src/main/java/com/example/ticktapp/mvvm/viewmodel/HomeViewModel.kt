package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.HomeRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.HomeResponseModel
import com.app.core.model.jobmodel.JobResponseModel
import com.google.gson.Gson

class HomeViewModel : BaseViewModel() {

    private val mRepo by lazy { HomeRepo() }
    lateinit var mJsonResponseModel: JobResponseModel
    lateinit var mHomeResponseModel: HomeResponseModel


    /**
     * Api call to get Job Type list.
     */
    fun getJobTypeList() = CoroutinesBase.main {
        updateView(mRepo.getJobTypeListing()) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), JobResponseModel::class.java)
                        mJsonResponseModel = responseModel
                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
    }


    fun getHomeListing(lat: String, lng: String, progress: Boolean) {
        CoroutinesBase.main {
            if (progress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getHomeListing(lat, lng)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), HomeResponseModel::class.java)
                        this.mHomeResponseModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}


