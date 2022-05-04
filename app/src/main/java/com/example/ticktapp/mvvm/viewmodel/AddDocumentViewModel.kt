package com.example.ticktapp.mvvm.viewmodel


import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.amazons3.ImageUploadResponse
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.mvvm.repo.AddDocumentRepo
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddDocumentViewModel : BaseViewModel() {
    lateinit var imageUploadResponse: ImageUploadResponse

    private val permissionHelper: PermissionHelper
    private val mRepo by lazy {
        AddDocumentRepo()
    }

    init {
        permissionHelper = PermissionHelper()
    }

    fun getPermissionHelper() = permissionHelper


    fun hitUploadFile(path: String) {
        var body: MultipartBody.Part? = null
        if (path.endsWith(".jpeg") || path.endsWith(".jpg") || path.endsWith(".png")) {
            body = createRequestBody(File(path))?.let {
                MultipartBody.Part.createFormData(
                    "file", path,
                    it
                )
            }
        } else {
            body = prepareFilePart(path)
        }
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = body?.let { mRepo.hitUploadFile(it) }
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response =
                            Gson().fromJson(Gson().toJson(it.data), ImageUploadResponse::class.java)
                        imageUploadResponse = response;
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    private fun createRequestBody(file: File): RequestBody? {
        return RequestBody.create("image/*".toMediaTypeOrNull(), file)
    }

    fun prepareFilePart(path: String): MultipartBody.Part {
        val file = File(path)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", path, requestFile);
    }


}