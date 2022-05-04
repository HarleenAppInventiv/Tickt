package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.builderreview.BuilderProfileModel
import com.app.core.repo.BuilderProfileRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class BuilderProfileViewModel : BaseViewModel() {

    private val mRepo by lazy { BuilderProfileRepo() }
    lateinit var mJsonResponseModel: BuilderProfileModel

    fun builderProfile(isProgress: Boolean, builderId: String?) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.builderProfile(builderId)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), BuilderProfileModel::class.java)

                        this.mJsonResponseModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}