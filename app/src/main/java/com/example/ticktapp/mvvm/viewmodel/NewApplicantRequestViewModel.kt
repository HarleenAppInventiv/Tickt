package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradie.BuilderModel
import com.app.core.repo.NewApplicantRequestRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class NewApplicantRequestViewModel : BaseViewModel() {

    private val mRepo by lazy { NewApplicantRequestRepo() }
    private var mTradieNewJobRequestList = ArrayList<JobRecModel>()
    private var mTradieRequestList = ArrayList<BuilderModel>()
    private var mTradieNeedApprovalRequestList = ArrayList<JobDashboardModel>()



    fun getNewJobListing(): ArrayList<JobRecModel> {
        return mTradieNewJobRequestList
    }
    fun getNeedApprovalJobs(): ArrayList<JobDashboardModel> {
        return mTradieNeedApprovalRequestList
    }

    fun getNewTradieListing(): ArrayList<BuilderModel> {
        return mTradieRequestList
    }
    /**
     * Api call to get New Job list.
     */
    fun getNewApplicationRequestListing(page: Int, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getNewApplicationRequestListing(page)
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

    /**
     * Api call to get Need Approval list.
     */
    fun needApproval(page: Int, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.needApproval(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<JobDashboardModel>::class.java)
                                .toList()
                        mTradieNeedApprovalRequestList = ArrayList(model)
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get New tradie list for applicant.
     */
    fun newApplicationRequestListing(params: HashMap<String, Any>, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.newApplicantLists(params)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val gson = GsonBuilder().create()
                        val model =
                            gson.fromJson(Gson().toJson(it.data), Array<BuilderModel>::class.java)
                                .toList()
                        mTradieRequestList = ArrayList(model)


                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

}

