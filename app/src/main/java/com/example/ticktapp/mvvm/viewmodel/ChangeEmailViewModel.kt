package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.ChangeEmailRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.JsonObject

class ChangeEmailViewModel : BaseViewModel() {

    private val mRepo by lazy { ChangeEmailRepo() }


 /* change email  api*/
    fun changeEmail(mobject:JsonObject) =

        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.changeEmail(mobject)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /* verify email  api*/
    fun verifyEmail(mobject:JsonObject) =

        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.verifyEmail(mobject)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

}





