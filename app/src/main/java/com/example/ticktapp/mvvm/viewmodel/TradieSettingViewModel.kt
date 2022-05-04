package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.profile.Settings
import com.app.core.model.profile.TradieSettingsModel
import com.app.core.repo.TradieSettingRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class TradieSettingViewModel : BaseViewModel() {

    private val mRepo by lazy { TradieSettingRepo() }
    public var settings: TradieSettingsModel? = null

    fun getSetting() {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getTradieSetting()
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val responseModel =
                                Gson().fromJson(Gson().toJson(it.data), TradieSettingsModel::class.java)
                            settings = responseModel
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun putSetting(param: HashMap<String, Any>) {
        CoroutinesBase.main {
            val resp = mRepo.putTradieSetting(param)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
        }
    }

}



