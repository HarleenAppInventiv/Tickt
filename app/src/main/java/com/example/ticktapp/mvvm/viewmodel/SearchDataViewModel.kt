package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.SearchDataRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobListModel
import com.app.core.model.jobmodel.JobModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job

class SearchDataViewModel : BaseViewModel() {

    private var corotine: Job? = null
    private val mRepo by lazy { SearchDataRepo() }
    lateinit var jobRectModelList: List<JobModel>
    lateinit var jobListModel: JobListModel


    fun searchData(searchText: String) {
        corotine?.cancel()
        corotine = CoroutinesBase.main {
            // setLoadingState(LoadingState.LOADING())
            val resp = mRepo.search(searchText)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val jobModel = object : TypeToken<List<JobModel>>() {}
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson<List<JobModel>>(gson.toJson(it.data), jobModel.type)
                        this.jobRectModelList = responseModel
                    }
                }
            }
            // setLoadingState(LoadingState.LOADED())
        }
    }

    fun getRearchData(boolean: Boolean) {
        CoroutinesBase.main {
            if (boolean)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getRecentSearchData()
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson(gson.toJson(it.data), JobListModel::class.java)
                        this.jobListModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun cancelRequest() {
        corotine?.cancel()
    }
}


