package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.MilestoneListRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.model.TemplateMilestoneData
import com.app.core.model.jobmodel.JobMilestoneListModel
import com.app.core.model.jobmodel.MilestoneDetails
import com.google.gson.Gson

class MilestoneListViewModel : BaseViewModel() {

    private val mRepo by lazy { MilestoneListRepo() }
    lateinit var mTradieJobMilestones: JobMilestoneListModel
    lateinit var milestoneDetails: MilestoneDetails
    lateinit var tempMilestoneDetails: TemplateMilestoneData


    /**
     * Api call to get Job milestones list
     */
    fun getJobMilestoneList(jobId: String) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getJobMilestonesList(jobId)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                JobMilestoneListModel::class.java
                            )
                        this.mTradieJobMilestones = responseModel


                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get Job milestones list
     */
    fun jobBuilderMilestonesList(jobId: String) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.jobBuilderMilestonesList(jobId)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                JobMilestoneListModel::class.java
                            )
                        this.mTradieJobMilestones = responseModel


                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     *
     * Api call to get Job milestones details
     */
    fun getMilestoneDetails(jobId: String, milestoneId: String) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getMilestoneDetails(jobId, milestoneId)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                MilestoneDetails::class.java
                            )
                        this.milestoneDetails = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get change milestones request
     */
    fun changeMilestoneRequest(data: HashMap<String, Any>) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.changeMilestoneRequest(data)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                JobMilestoneListModel::class.java
                            )
                        this.mTradieJobMilestones = responseModel


                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get change milestones request
     */
    fun editMilestoneRequest(data: HashMap<String, Any>) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.editMilestoneRequest(data)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                TemplateMilestoneData::class.java
                            )
                        this.tempMilestoneDetails = responseModel

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    fun declineMilestoneRequest(params: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.declineMilestoneRequest(params)
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
}



