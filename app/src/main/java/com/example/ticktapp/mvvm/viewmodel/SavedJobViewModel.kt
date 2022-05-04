package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.SavedJobRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobRecModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class SavedJobViewModel : BaseViewModel() {

    private val mRepo by lazy { SavedJobRepo() }
    private var mTradieSavedJobRequestList = ArrayList<JobRecModel>()


    fun getSavedJobListing(): ArrayList<JobRecModel> {
        return mTradieSavedJobRequestList
    }

    /**
     * Api call to get New Job list.
     */
    fun getSavedJobRequestList(page: Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getSavedJobListing(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<JobRecModel>::class.java)
                                .toList()
                        mTradieSavedJobRequestList = ArrayList(model)
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

}