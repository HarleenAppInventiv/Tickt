package com.example.ticktapp.mvvm.repo

import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.util.ApiCodes
import okhttp3.MultipartBody

class PostJobRepo : BaseRepo() {
    suspend fun uploadFile(file: Array<MultipartBody.Part?>): BaseResponse<Any> {
        return apiRequest(ApiCodes.UPLOAD_FILE) {
            unauthenticatedApiClient.uploadFile(file)
        }
    }
    suspend fun hitUploadFile(file: MultipartBody.Part): BaseResponse<Any> {
        return apiRequest(ApiCodes.UPLOAD_FILE) {
            unauthenticatedApiClient.uploadFile(file)
        }
    }
    suspend fun createPost(params: HashMap<String, Any>): BaseResponse<Any> {
        return apiRequest(ApiCodes.CREATE_POST) {
            unauthenticatedApiClient.createPost(params)
        }
    }
    suspend fun createRepublishPost(params: HashMap<String, Any>): BaseResponse<Any> {
        return apiRequest(ApiCodes.CREATE_REPUBLISH_POST) {
            unauthenticatedApiClient.createRepublishPost(params)
        }
    }
    suspend fun createUpdatedPost(params: HashMap<String, Any>): BaseResponse<Any> {
        return apiRequest(ApiCodes.CREATE_REPUBLISH_POST) {
            unauthenticatedApiClient.createUpdatedPost(params)
        }
    }
}