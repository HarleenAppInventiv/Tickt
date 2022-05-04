package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.TemplateRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.model.TemplateData
import com.example.ticktapp.model.TemplateMilestoneData
import com.example.ticktapp.model.registration.TokenModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TemplateViewModel : BaseViewModel() {
    private var registrationModel: TokenModel? = null
    lateinit var templateData: List<TemplateData>
    lateinit var templateMilestoneData: TemplateMilestoneData


    private val mRepo by lazy { TemplateRepo() }

    fun createTemplate(data: HashMap<String, Any>) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING())
        updateView(mRepo.createTemplate(data)) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), TokenModel::class.java)
                        registrationModel = responseModel
                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
        setLoadingState(LoadingState.LOADED())

    }

    fun getTemplateList() = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING())
        updateView(mRepo.getTemplateList()) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                        val jobModel = object : TypeToken<List<TemplateData>>() {}
                        val gson = Gson();
                        val responseModel =
                            gson.fromJson<List<TemplateData>>(gson.toJson(it.data), jobModel.type)
                        templateData = responseModel
                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
        setLoadingState(LoadingState.LOADED())

    }

    fun getTemplateMilestoneList(tmpID: String) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING())
        updateView(mRepo.getTemplateMilestoneList(tmpID)) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                TemplateMilestoneData::class.java
                            )
                        templateMilestoneData = responseModel
                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
        setLoadingState(LoadingState.LOADED())

    }


    fun deleteTemplateMilestoneList(data: HashMap<String, Any>) = CoroutinesBase.main {
        setLoadingState(LoadingState.LOADING())
        updateView(mRepo.deleteTemplateMilestoneList(data)) {
            when (it) {
                is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    try {
                    } catch (e: Exception) {
                        Log.d("trade_error", e.message.toString())
                    }
                }
            }
        }
        setLoadingState(LoadingState.LOADED())
    }
}