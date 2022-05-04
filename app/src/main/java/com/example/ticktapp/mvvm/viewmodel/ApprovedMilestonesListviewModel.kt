package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.ApprovedMilestoneListRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobRecModel

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ApprovedMilestonesListviewModel : BaseViewModel() {

    private val mRepo by lazy { ApprovedMilestoneListRepo() }
    private var mTradieApprovedMilestonesJobRequestList = ArrayList<JobRecModel>()


    fun getApprovedMilestonesListing(): ArrayList<JobRecModel> {
        return mTradieApprovedMilestonesJobRequestList
    }

    /**
     * Api call to get Approved Milestones Job list.
     */
    fun getApprovedMilestonesList(page: Int, progress: Boolean) =
        CoroutinesBase.main {
            if (progress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getApprovedMilestonesList(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<JobRecModel>::class.java)
                                .toList()
                        mTradieApprovedMilestonesJobRequestList = ArrayList(model)

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


}


