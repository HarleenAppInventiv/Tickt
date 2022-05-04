package com.app.core.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.model.profile.FirebaseModel
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.util.ApiCodes

class FirebaseRepo : BaseRepo() {
    suspend fun sendPushNotifications(firebaseMessage: FirebaseModel): BaseResponse<Any> {
        return apiRequest(ApiCodes.FIREBASE_MESSAGE) {
            unauthenticatedApiClient.sendPushNotifications(firebaseMessage)
        }
    }
}