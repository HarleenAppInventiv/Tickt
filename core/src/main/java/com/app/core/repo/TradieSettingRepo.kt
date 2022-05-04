package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class TradieSettingRepo : BaseRepo() {

    suspend fun getTradieSetting() =
        apiRequest(ApiCodes.TRADIE_GET_SETTING) {
            unauthenticatedApiClient.getTradieSetting()
        }

    suspend fun putTradieSetting(data:HashMap<String,Any>) =
        apiRequest(ApiCodes.TRADIE_PUT_SETTING) {
            unauthenticatedApiClient.putTradieSetting(data)
        }




}