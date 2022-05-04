package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.profile.Settings
import com.app.core.repo.SettingRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class SettingViewModel : BaseViewModel() {

    private val mRepo by lazy { SettingRepo() }
    public lateinit var settings: Settings

    fun getSetting() {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getSetting()
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        try {
                            val responseModel =
                                Gson().fromJson(Gson().toJson(it.data), Settings::class.java)
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
            val resp = mRepo.putSetting(param)
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