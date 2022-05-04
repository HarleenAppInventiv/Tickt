package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.JobSearchRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobRecModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchJobViewModel : BaseViewModel() {

    private val mRepo by lazy { JobSearchRepo() }
    lateinit var jobRectModelList: List<JobRecModel>


    fun search(jobSearchRequestModel: Any) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.search(jobSearchRequestModel)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val jobModel = object : TypeToken<List<JobRecModel>>() {}
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson<List<JobRecModel>>(gson.toJson(it.data), jobModel.type)
                        this.jobRectModelList = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}


