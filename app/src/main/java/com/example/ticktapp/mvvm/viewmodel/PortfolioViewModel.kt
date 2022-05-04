package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradie.PortFolio
import com.app.core.repo.TradieCancelJobRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.model.registration.RegistrationModel
import com.example.ticktapp.mvvm.repo.PortfolioRepo
import com.google.gson.Gson

class PortfolioViewModel : BaseViewModel() {


    private val mRepo by lazy { PortfolioRepo() }
    lateinit var portfolioModel:PortFolio

/*Api to add portfolio*/
    fun addPortfolio(param:HashMap<String,Any>) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            var resp: BaseResponse<Any>
            resp = mRepo.addPortfolio(param)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response = Gson().fromJson(
                            Gson().toJson(it.data),
                            PortFolio::class.java
                        )
                        this.portfolioModel = response
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /*Api to delete portfolio*/
    fun deletePortfolio(portfolioId:String) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.deletePortfolio(portfolioId)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /*Api to EDIT portfolio*/
    fun editPortfolio(param:HashMap<String,Any>) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.editPortfolio(param)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response = Gson().fromJson(
                            Gson().toJson(it.data),
                            PortFolio::class.java
                        )
                        this.portfolioModel = response
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

}





