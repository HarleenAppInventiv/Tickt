package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.NewJobRequestRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobRecModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class NewJobRequestViewModel : BaseViewModel() {

    private val mRepo by lazy { NewJobRequestRepo() }
    private var mTradieNewJobRequestList = ArrayList<JobRecModel>()


    fun getNewJobListing(): ArrayList<JobRecModel> {
        return mTradieNewJobRequestList
    }

    /**
     * Api call to get New Job list.
     */
    fun getNewJobRequestList(page: Int,progrees:Boolean) =
        CoroutinesBase.main {
            if(progrees)
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getNewJobRequestListing(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<JobRecModel>::class.java)
                                .toList()
                        mTradieNewJobRequestList = ArrayList(model)


                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


}


