package com.example.ticktapp.mvvm.viewmodel


import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.amazons3.ImageUploadResponse
import com.app.core.model.amazons3.ImageUploadURIResponse
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.mvvm.repo.PostJobRepo
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PostJobViewModel : BaseViewModel() {
    lateinit var imageUploadResponse: ImageUploadResponse


    private val permissionHelper: PermissionHelper
    private val mRepo by lazy {
        PostJobRepo()
    }

    init {
        permissionHelper = PermissionHelper()
    }

    fun getPermissionHelper() = permissionHelper


    fun hitUploadFile(path: List<String>) {
        val body = ArrayList<MultipartBody.Part>()
        path.forEachIndexed { _, data ->
            if (!data.contains("http")) {
                if (data.lowercase().endsWith(".jpeg") || data.lowercase()
                        .endsWith(".jpg") || data.lowercase().endsWith(".png")
                ) {
                    val partData = createRequestBody(File(data))?.let {
                        MultipartBody.Part.createFormData(
                            "file", data,
                            it
                        )
                    }
                    partData?.let { it1 -> body.add(it1) }
                } else {
                    body.add(prepareFilePart(data))
                }
            }
        }
        val arr = arrayOfNulls<MultipartBody.Part>(body.size)
        body.toArray(arr)
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())

            val resp = body?.let { mRepo.uploadFile(arr) }
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

    fun hitUploadFileWithoutProgress(path: List<String>) {
        val body = ArrayList<MultipartBody.Part>()
        path.forEachIndexed { _, data ->
            if (!data.contains("http")) {
                if (data.lowercase().endsWith(".jpeg") || data.lowercase()
                        .endsWith(".jpg") || data.lowercase().endsWith(".png")
                ) {
                    val partData = createRequestBody(File(data))?.let {
                        MultipartBody.Part.createFormData(
                            "file", data,
                            it
                        )
                    }
                    partData?.let { it1 -> body.add(it1) }
                } else {
                    body.add(prepareFilePart(data))
                }
            }
        }
        val arr = arrayOfNulls<MultipartBody.Part>(body.size)
        body.toArray(arr)
        CoroutinesBase.main {
            val resp = body?.let { mRepo.uploadFile(arr) }
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
         }
    }

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

    fun createPost(params: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.createPost(params)
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
    fun createRepublishPost(params: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.createRepublishPost(params)
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
    fun createUpdatedPost(params: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.createUpdatedPost(params)
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