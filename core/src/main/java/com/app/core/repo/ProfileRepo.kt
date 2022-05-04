package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes
import com.google.gson.JsonObject

class ProfileRepo : BaseRepo() {


    suspend fun getInitialProfileData() =
        apiRequest(ApiCodes.INIT_BASIC_PROFILE) {
            unauthenticatedApiClient.intialProfileData()
        }

    suspend fun getBasicProfileDetails() =
        apiRequest(ApiCodes.BASIC_PROFILE) {
            unauthenticatedApiClient.getBasicProfileDetails()
        }
    suspend fun getBasicBuilerProfileDetails() =
        apiRequest(ApiCodes.BASIC_PROFILE) {
            unauthenticatedApiClient.getBasicBuilerProfileDetails()
        }

    suspend fun uploadProfileImg(mObjct: JsonObject) =
        apiRequest(ApiCodes.BASIC_PROFILE_IMG) {
            unauthenticatedApiClient.uploadProfileImg(mObjct)
        }


    suspend fun builderEditProfile(mObjct: HashMap<String, Any>) =
        apiRequest(ApiCodes.BUILDER_EDIT_PROFILE) {
            unauthenticatedApiClient.builderEditProfile(mObjct)
        }

    suspend fun builderEditBasicProfile(mObjct: HashMap<String, Any>) =
        apiRequest(ApiCodes.BUILDER_EDIT_BASIC_PROFILE) {
            unauthenticatedApiClient.builderEditBasicProfile(mObjct)
        }
}
