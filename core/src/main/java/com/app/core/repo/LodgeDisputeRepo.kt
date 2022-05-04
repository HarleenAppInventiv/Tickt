package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class LodgeDisputeRepo : BaseRepo() {

    suspend fun lodgeDisputeBuidler(params: HashMap<String,Any>) =
        apiRequest(ApiCodes.LODGE_DISPUTE) {
            unauthenticatedApiClient.lodgeDisputeBuidler(params)
        }

    suspend fun lodgeDispute(params: HashMap<String,Any>) =
        apiRequest(ApiCodes.LODGE_DISPUTE) {
            unauthenticatedApiClient.lodgeDispute(params)
        }
}
