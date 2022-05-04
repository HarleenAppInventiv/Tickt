package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.LodgeDisputeRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobAppliedModel
import com.app.core.model.jobmodel.JobRecModel

class LodgeDisputeViewModel : BaseViewModel() {

    private val mRepo by lazy { LodgeDisputeRepo() }
    lateinit var mJsonResponseModel: JobRecModel
    lateinit var jobAppliedModel: JobAppliedModel

    fun lodgeDisputeBuidler(params: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.lodgeDisputeBuidler(params)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun lodgeDispute(params: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.lodgeDispute(params)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        /*val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), JobRecModel::class.java)

                        this.mJsonResponseModel = responseModel*/
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}

