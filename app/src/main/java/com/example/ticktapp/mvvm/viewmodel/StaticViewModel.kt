package com.example.ticktapp.mvvm.viewmodel
import androidx.databinding.ObservableField
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.StaticUrlModel
import com.app.core.repo.StaticDataRepo

import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.mvvm.repo.SignupRepo
import com.google.gson.Gson

class StaticViewModel : BaseViewModel() {
    private lateinit var mResetToken: String
    private var staticUrlModel: StaticUrlModel? = null
    private val mRepo by lazy { StaticDataRepo() }

    fun getPrivacyPolicy(){
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.hitPrivacyData()
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response = Gson().fromJson(Gson().toJson(it.data), StaticUrlModel::class.java)
                        this.staticUrlModel = response
                    } } }
            setLoadingState(LoadingState.LOADED())
        }
    }


    fun getTermsAndCondition(){
        CoroutinesBase.main{
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.hitTncData()
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response = Gson().fromJson(Gson().toJson(it.data), StaticUrlModel::class.java)
                        this.staticUrlModel = response
                    } } }
            setLoadingState(LoadingState.LOADED())
        }
    }



    fun getStaticUrl() = staticUrlModel

    fun getToken() = mResetToken
}