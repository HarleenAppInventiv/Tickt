package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class VouchRepo : BaseRepo() {

    suspend fun addVoucher(data: HashMap<String, Any>) =
        apiRequest(ApiCodes.ADD_VOUCH) {
            unauthenticatedApiClient.addVoucher(data)
        }
}