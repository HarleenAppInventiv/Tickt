package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.profile.InitalProfileModel
import com.app.core.model.tradie.BuilderModel
import com.app.core.repo.ProfileRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson
import com.google.gson.JsonObject

class ProfileViewModel : BaseViewModel() {

    lateinit var inItProfileModel: InitalProfileModel
    private val mRepo by lazy { ProfileRepo() }


 /* get inital profile details*/
    fun getInitialProfileData(progress:Boolean) =

        CoroutinesBase.main {
          if(progress)
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.getInitialProfileData()

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), InitalProfileModel::class.java)
                        inItProfileModel = responseModel
                    }
                    is API_VIEWMODEL_DATA.AUTORIZE_FAILED->{

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


    /* get basic profile details*/
    fun getBasicProfileDetails(progress: Boolean) =
        CoroutinesBase.main {
            if(progress)
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.getBasicProfileDetails()

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), InitalProfileModel::class.java)
                        inItProfileModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /* get basic profile details*/
    fun getBasicBuilerProfileDetails() =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.getBasicBuilerProfileDetails()

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), InitalProfileModel::class.java)
                        inItProfileModel = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


   /* save traie profile image*/
    fun uploadProfileImg(mObjcet:JsonObject) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.uploadProfileImg(mObjcet)

            updateView(resp) {
                when (it) {

                }
            }
            setLoadingState(LoadingState.LOADED())
        }



   /* edit tradie public details*/
    fun builderEditProfile(mObject:HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.builderEditProfile(mObject)
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
    /* edit tradie public details*/
    fun builderEditBasicProfile(mObject:HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.builderEditBasicProfile(mObject)
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

