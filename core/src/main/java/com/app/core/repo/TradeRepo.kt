package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.usermodel.UserDataModel
import com.app.core.util.ApiCodes
import com.google.gson.Gson

class TradeRepo : BaseRepo() {
    suspend fun getTradeListing() =
        apiRequest(ApiCodes.GET_TRADE_LIST) {
            unauthenticatedApiClient.tradeListing()
        }
    suspend fun tradeSaveListing(page:Int) =
        apiRequest(ApiCodes.GET_SAVED_TRADE_LIST) {
            unauthenticatedApiClient.tradeSaveListing(page)
        }
}