package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.profile.FirebaseModel
import com.app.core.repo.FirebaseRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel

class FirebaseViewModel : BaseViewModel() {

    private val mRepo by lazy { FirebaseRepo() }

    fun sendPushNotifications(mobject: FirebaseModel) =
        CoroutinesBase.main {
            val resp: BaseResponse<Any>
            resp = mRepo.sendPushNotifications(mobject)

            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
        }
}