package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.ChangePasswordRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.JsonObject

class ChangePasswordViewModel : BaseViewModel() {

    private val mRepo by lazy { ChangePasswordRepo() }


 /* change password api*/
    fun changePassword(mobject:JsonObject) =

        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp: BaseResponse<Any>
            resp = mRepo.changePassword(mobject)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }



}





