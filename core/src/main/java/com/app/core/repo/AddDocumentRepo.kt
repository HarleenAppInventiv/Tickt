package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.util.ApiCodes
import okhttp3.MultipartBody

class AddDocumentRepo: BaseRepo() {
    suspend fun hitUploadFile(file: MultipartBody.Part): BaseResponse<Any> {
        return apiRequest(ApiCodes.UPLOAD_FILE) {
            unauthenticatedApiClient.uploadFile(file)
        }
    }
}