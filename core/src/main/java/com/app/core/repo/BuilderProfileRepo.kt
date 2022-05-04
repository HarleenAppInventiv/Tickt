package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.util.ApiCodes

class BuilderProfileRepo : BaseRepo() {

    suspend fun builderProfile(builderId:String?) =
        apiRequest(ApiCodes.BUILDER_PROFILE) {
            unauthenticatedApiClient.builderProfile(builderId)
        }
}