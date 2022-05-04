package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class SettingRepo : BaseRepo() {

    suspend fun getSetting() =
        apiRequest(ApiCodes.GET_SETTING) {
            unauthenticatedApiClient.getSetting()
        }

    suspend fun putSetting(data:HashMap<String,Any>) =
        apiRequest(ApiCodes.PUT_SETTING) {
            unauthenticatedApiClient.putSetting(data)
        }




}