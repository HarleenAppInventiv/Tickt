package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradie.BuilderModel
import com.app.core.repo.TradieProfileRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.TradieInitalProfileModel
import com.google.gson.Gson
import com.google.gson.JsonObject

class TradieProfileViewModel : BaseViewModel() {

    lateinit var mTradieInitalProfileData: TradieInitalProfileModel
    private val mRepo by lazy { TradieProfileRepo() }
    lateinit var builderModel: BuilderModel


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
                            Gson().fromJson(Gson().toJson(it.data), TradieInitalProfileModel::class.java)
                            mTradieInitalProfileData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


    /* get basic profile details*/
    fun getTradieBasicProfileDetails() =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.getTradieBasicProfileDetails()

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TradieInitalProfileModel::class.java)
                        mTradieInitalProfileData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }


   /* save traie profile image*/
    fun uploadProfilePic(mObjcet:JsonObject) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.uploadTradieProfileImg(mObjcet)

            updateView(resp) {
                when (it) {

                }
            }
            setLoadingState(LoadingState.LOADED())
        }


  /* get tradie public profile details*/
    fun getTradiePublicProfile(isProgress: Boolean) {
        CoroutinesBase.main {
            if (isProgress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getTradiePublicProfile()
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



   /* edit tradie public details*/
    fun editTradieProfile(mObject:HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.tradieEditProfile(mObject)
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
    fun tradieEditBasicProfile(mObject:HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.tradieEditBasicProfile(mObject)
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





