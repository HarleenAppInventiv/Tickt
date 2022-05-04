package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.jobmodel.*
import com.app.core.model.tradie.BuilderModel
import com.app.core.repo.JobRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class JobDetailsViewModel : BaseViewModel() {

    private val mRepo by lazy { JobRepo() }
    lateinit var mJsonResponseModel: JobRecModel
    lateinit var mJsonResponseRepublishModel: JobRecModelRepublish
    lateinit var mJsonResponseModelList: List<JobRecModel>
    lateinit var jobAppliedModel: JobAppliedModel
    lateinit var jobMilestStonRespnse: JobMilestStonRespnse
    lateinit var builderModel: BuilderModel

    var status: Int = 0


    fun getJobsDetails(isProgress: Boolean, jobId: String?, tradID: String?, specID: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getJobTypeListing(jobId, tradID, specID)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), JobRecModel::class.java)
                        this.mJsonResponseModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun getTradieJobsDetails(isProgress: Boolean, jobId: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getTradieJobDetails(jobId)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), JobRecModel::class.java)
                        this.mJsonResponseModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun jobDetailsFromBuilder(
        isProgress: Boolean,
        jobId: String?,
        tradID: String?,
        specID: String?
    ) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.jobDetailsFromBuilder(jobId, tradID, specID)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), JobRecModel::class.java)
                        this.mJsonResponseModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun jobDetailsFromBuilderRepublish(
        isProgress: Boolean,
        jobId: String?,
        tradID: String?,
        specID: String?,
    ) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.jobDetailsFromBuilderRepublish(jobId, tradID, specID)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                JobRecModelRepublish::class.java
                            )
                        this.mJsonResponseRepublishModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }


    fun jobDetailsForEditJob(
        isProgress: Boolean,
        jobId: String?,
        isRespJob:Boolean
    ) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.jobDetailsForEditBuilderJob(jobId,isRespJob)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                JobRecModelRepublish::class.java
                            )
                        this.mJsonResponseRepublishModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun jobDetailsFromBuilderOpen(
        isProgress: Boolean,
        jobId: String?,
        tradID: String?,
        specID: String?
    ) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.jobDetailsFromBuilderOpen(jobId, tradID, specID)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                JobEditData::class.java
                            )
                        this.mJsonResponseRepublishModel = responseModel.data!!
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
    fun jobDetailsRemove(
        isProgress: Boolean,
        jobId: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.jobDetailsRemove(jobId)
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
    fun closeQuoteJob(
        isProgress: Boolean,
        jobId: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val data=HashMap<String,Any>()
            jobId?.let { data.put("jobId", it) }
            val resp = mRepo.closeQuoteJob(data)
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
    fun closeOpenJob(
        isProgress: Boolean,
        jobId: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val data=HashMap<String,Any>()
            jobId?.let { data.put("jobId", it) }
            val resp = mRepo.closeOpenJob(data)
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
    fun getTradieProfile(isProgress: Boolean, tradID: String?, jobId: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getTradieProfile(tradID, jobId)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), BuilderModel::class.java)
                        this.builderModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun getTradieProfile(isProgress: Boolean, tradID: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getTradieProfile(tradID)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), BuilderModel::class.java)
                        this.builderModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun saveJob(jobId: String?, tradID: String?, specID: String?, isSave: Boolean?) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.saveJob(jobId, tradID, specID, isSave)
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

    fun saveTradie(tradID: String?, isSave: Boolean?) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.saveTradie(tradID, isSave)
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

    fun cancelInvite(ivId: String?, tradID: String?, jobId: String?) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.cancelInvite(ivId, tradID, jobId)
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

    fun replyCancellation(mObject: JsonObject) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.replyCancellationRequest(mObject)
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

    fun replyChangeRequest(params: JsonObject) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.replyChangeRequest(params)
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

    fun replyCancellationRequestBuilder(mObject: JsonObject) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.replyCancellationRequestBuilder(mObject)
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

    fun reviewTradie(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.reviewTradie(data)
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

    fun reviewUpdateTradie(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.reviewUpdateTradie(data)
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

    fun reviewRemoveTradie(reviewId: String) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.reviewRemoveTradie(reviewId)
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


    fun applyJob(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.applyJob(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), JobAppliedModel::class.java)
                        this.jobAppliedModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun milestoneLists(data: String) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.milestoneLists(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                JobMilestStonRespnse::class.java
                            )
                        this.jobMilestStonRespnse = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun jobsList(page: Int, isProgress: Boolean?) {
        CoroutinesBase.main {
            if (isProgress == true)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.jobsList(page)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val jobModel = object : TypeToken<List<JobRecModel>>() {}
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson<List<JobRecModel>>(gson.toJson(it.data), jobModel.type)
                        this.mJsonResponseModelList = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun vouchJobsList(page: Int, tradeID: String?, isProgress: Boolean?) {
        CoroutinesBase.main {
            if (isProgress == true)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.vouchJobsList(page, tradeID)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val jobModel = object : TypeToken<List<JobRecModel>>() {}
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson<List<JobRecModel>>(gson.toJson(it.data), jobModel.type)
                        this.mJsonResponseModelList = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun inviteForJob(tradID: String?, jobId: String?) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = jobId?.let { mRepo.inviteForJob(tradID.toString(), it) }
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

    fun acceptDeclineRequest(jobId: String?, tradID: String?, status: Int?) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.acceptDeclineRequest(jobId, tradID, status)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        if (status != null) {
                            this.status = status
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
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


