package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.google.gson.JsonObject

class TradieProfileRepo : BaseRepo() {


    suspend fun getInitialProfileData() =
        apiRequest(ApiCodes.TRADIE_BASIC_PROFILE) {
            unauthenticatedApiClient.tradieIntialProfileData()
        }

    suspend fun getTradieBasicProfileDetails() =
        apiRequest(ApiCodes.TRADIE_BASIC_PROFILE) {
            unauthenticatedApiClient.getTradieBasicProfileDetails()
        }



    suspend fun uploadTradieProfileImg(mObjct:JsonObject) =
        apiRequest(ApiCodes.TRADIE_PROFILE_IMG) {
            unauthenticatedApiClient.uploadTradieProfileImg(mObjct)
        }

    suspend fun getTradiePublicProfile() =
        apiRequest(ApiCodes.TRADIE_PROFILE_PUBLIC) {
            unauthenticatedApiClient.getTradiePublicProfile()
        }
    suspend fun tradieEditProfile(mObjct:HashMap<String, Any>) =
        apiRequest(ApiCodes.TRADIE_EDIT_PROFILE) {
            unauthenticatedApiClient.tradieEditProfile(mObjct)
        }

    suspend fun tradieEditBasicProfile(mObjct:HashMap<String, Any>) =
        apiRequest(ApiCodes.TRADIE_EDIT_PROFILE) {
            unauthenticatedApiClient.tradieEditBasicProfile(mObjct)
        }
}