package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.builderreview.BuilderReviewModel
import com.app.core.repo.BuilderRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.JobRecModel
import com.google.gson.Gson

class BuilderRatingViewModel: BaseViewModel() {
    private val mRepo by lazy { BuilderRepo() }
    fun getJobsDetails(isProgress:Boolean,rating: BuilderReviewModel) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.postRating(rating)
            updateView(
                resp
            ) {
                Log.d("responsedata", ""+resp.apiError?.message)
                when (it) {

                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), JobRecModel::class.java)
                       // this.mJsonResponseModel = responseModel
                        Log.d("responsedata", Gson().toJson(it.data))
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}