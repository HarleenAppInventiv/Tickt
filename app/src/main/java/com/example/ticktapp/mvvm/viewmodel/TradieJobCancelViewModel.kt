package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.TradieCancelJobRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState

class TradieJobCancelViewModel : BaseViewModel() {


    private val mRepo by lazy { TradieCancelJobRepo() }


    fun cancelJobRequest(mObject: com.google.gson.JsonObject) =

        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            var resp: BaseResponse<Any>
            resp = mRepo.cancelJob(mObject)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun builderCancelJob(mObject: com.google.gson.JsonObject) =

        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            var resp: BaseResponse<Any>
            resp = mRepo.builderCancelJob(mObject)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
}





