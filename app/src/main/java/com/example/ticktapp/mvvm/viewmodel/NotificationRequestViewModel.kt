package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.NotificationResponse
import com.app.core.repo.NotificationRequestRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class NotificationRequestViewModel : BaseViewModel() {

    private val mRepo by lazy { NotificationRequestRepo() }
    public var notificationModelList = NotificationResponse()


    /**
     * Api call to get notifications list.
     */
    fun notifications(page: Int, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.notifications(page)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                NotificationResponse::class.java
                            )
                        notificationModelList = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to Mark All notifications read.
     */
    fun markAllNotificationsRead(page: Int, markRead: Int, progrees: Boolean) =
        CoroutinesBase.main {
            if (progrees)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.markAllNotificationsRead(page, markRead)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                NotificationResponse::class.java
                            )
                        notificationModelList = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to read notifications.
     */
    fun readNotifications(mObjcet: HashMap<String, Any>) =
        CoroutinesBase.main {
            val resp = mRepo.readNotificaitons(mObjcet)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }

        }
}

