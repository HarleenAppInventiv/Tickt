package com.exampl


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.model.jobmodel.Photos
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.app.core.util.DropBoxConstants.DROPBOX_APP_KEY_PERSONAL
import com.dreamg.videoTrimmer.LightVideoCompression
import com.dropbox.core.android.Auth
import com.dropbox.core.oauth.DbxCredential
import com.example.ticktapp.R
import com.example.ticktapp.adapters.AddMediaAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.base.LoaderType
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityUploadVideoPhotoBinding
import com.example.ticktapp.dialog.CameraVideBottomSheet
import com.example.ticktapp.dialog.dropBox.DropBoxDialog
import com.example.ticktapp.mvvm.view.builder.postjob.PostJobSummaryActivity
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.*
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLConnection
import java.util.*
import kotlin.collections.ArrayList


class VideoImageActivity : BaseActivity(), PermissionHelper.IGetPermissionListener,
    CameraVideBottomSheet.CameraDialogCallBack, View.OnClickListener,
    DropBoxDialog.IDropBoxListener {
    private var isReturn: Boolean = false
    private lateinit var adapter: AddMediaAdapter
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var mBinding: ActivityUploadVideoPhotoBinding
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private var localCredential: DbxCredential? = null
    private var uri: Uri? = null
    private var fPAth: String? = null
    private val images by lazy { ArrayList<Photos>() }
    private val thumbs by lazy { ArrayList<String>() }
    private var lastTime = 0L
    private var jobId = ""
    private val mViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private var rData: JobRecModelRepublish? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_upload_video_photo)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        getIntentData()
        setObservers()
        setupView()
        localCredential = Auth.getDbxCredential() //fetch the result from the AuthActivity

    }

    private fun getIntentData() {
        if (intent.hasExtra("isReturn")) {
            isReturn = intent.getBooleanExtra("isReturn", false)
            val photos = intent.getSerializableExtra("photos") as ArrayList<String>
            if (photos != null) {
                photos.forEach {
                    var type = 1
                    if (it.lowercase().endsWith(".jpg") || it.lowercase()
                            .endsWith(".jpeg") || it.lowercase().endsWith(".png")
                    ) {
                        type = 1
                    } else if (it.lowercase().endsWith(".doc") || it.lowercase()
                            .endsWith(".docx")
                    ) {
                        type = 3
                    } else if (it.lowercase().endsWith(".pdf")) {
                        type = 4
                    } else {
                        type = 2
                    }
                    images.add(Photos(type, it))
                }
            }
        }  else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES)
                .isNullOrEmpty()
        ) {
            val photos = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES)
                .getDataList<String>()
            if (photos != null) {
                photos.forEach {
                    var type = 1
                    if (it.lowercase().endsWith(".jpg") || it.lowercase()
                            .endsWith(".jpeg") || it.lowercase().endsWith(".png")
                    ) {
                        type = 1
                    } else if (it.lowercase().endsWith(".doc") || it.lowercase()
                            .endsWith(".docx")
                    ) {
                        type = 3
                    } else if (it.lowercase().endsWith(".pdf")) {
                        type = 4
                    } else {
                        type = 2
                    }
                    images.add(Photos(type, it))
                }
            }
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .isNullOrEmpty()
        ) {
            rData = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .getPojoData(JobRecModelRepublish::class.java)
            if (rData?.urls != null) {
                rData?.urls?.forEach {
                    images.add(Photos(it.mediaType, it.link))
                }
            }
            jobId = rData?.jobId.toString()
        }else if (intent.hasExtra("rData") && intent.getSerializableExtra("rData") != null) {
            rData = intent.getSerializableExtra("rData") as JobRecModelRepublish
            if (rData?.urls != null) {
                rData?.urls?.forEach {
                    images.add(Photos(it.mediaType, it.link))
                }
            }
            jobId = rData?.jobId.toString()
        }

        if (images.size > 0) {
            mBinding.ivAddImageVideo.visibility = View.GONE
            if (images.size != 6)
                images.add(Photos(0, ""))
        }
    }

    private fun setupView() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
        val layountManager = GridLayoutManager(this, 3)
        adapter = AddMediaAdapter(images, this)
        mBinding.rvMedia.layoutManager = layountManager
        mBinding.rvMedia.adapter = adapter

    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
    }

    private fun saveImage(photo: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            getContentResolver(),
            photo,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        return FileUtils.getPathFromUri(this, Uri.parse(path))
    }

    private fun listener() {
        mBinding.uploadImageBack.setOnClickListener { onBackPressed() }
        mBinding.ivAddImageVideo.setOnClickListener {
            if (permissionHelper.hasPermission(
                    this,
                    permissions, PermissionConstants.PERMISSION
                )
            ) {
                CameraVideBottomSheet(this, this).show()
            }
        }

        mBinding.uploadVideoAudioTvContinue.setOnClickListener {
            val image = ArrayList<String>()
            thumbs.clear()
            images.forEach {
                if (it.link?.length!! > 0 && !it.link!!.contains("http")) {
                    image.add(it.link.toString())
                    if (it.mediaType == 2) {
                        val bitmap = ThumbnailUtils.createVideoThumbnail(
                            it.link!!,
                            MediaStore.Video.Thumbnails.MICRO_KIND
                        )
                        val path = bitmap?.let { it1 -> saveImage(it1) }
                        path?.let { it1 -> thumbs.add(it1) }
                    }
                }
            }

            if (image.size > 0) {
                mViewModel.hitUploadFile(image)
            } else {
                if (isReturn) {

                    val image = ArrayList<String>()
                    images.forEach {
                        if (it.link!!.contains("http")) {
                            image.add(it.link.toString())
                        }
                    }
                    PreferenceManager.putString(
                        PreferenceManager.NEW_JOB_PREF.JOB_IMAGES,
                        image.toJsonString()
                    )

                    PreferenceManager.putString(
                        PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_THUMB,
                        thumbs.toJsonString()
                    )

                    setResult(
                        Activity.RESULT_OK,
                        Intent().putExtra("files", image).putExtra("thumbs", thumbs)
                    )
                    finish()
                } else {
                    PreferenceManager.putString(
                        PreferenceManager.NEW_JOB_PREF.JOB_ID,
                        jobId
                    )
                    if (lastTime + 2000 < System.currentTimeMillis()) {
                        lastTime = System.currentTimeMillis()
                        if (rData != null) {
                            val dataImage = ArrayList<String>()
                            rData?.urls?.forEachIndexed { _, newItem ->
                                if (newItem.link?.contains("http") == true) {
                                    if (adapter.isExits(newItem.link!!))
                                        dataImage.add(newItem.link!!)
                                }
                            }
                            val uThumbs = ArrayList<String>()
                            rData?.urls?.forEachIndexed { _, newItem ->
                                if (newItem.link?.contains("http") == true && newItem.mediaType == 2) {
                                    if (adapter.isExits(newItem.link!!))
                                        uThumbs.add(newItem.link!!)
                                }
                            }

                            PreferenceManager.putString(
                                PreferenceManager.NEW_JOB_PREF.JOB_IMAGES,
                                dataImage.toJsonString()
                            )

                            PreferenceManager.putString(
                                PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_UTHUMB,
                                uThumbs.toJsonString()
                            )

                            startActivity(
                                Intent(this, PostJobSummaryActivity::class.java)
                                /*   .putExtra(
                                       "jobName",
                                       intent.getStringExtra("jobName")
                                   ).putExtra(
                                       "categories",
                                       intent.getSerializableExtra("categories")
                                   ).putExtra(
                                       "job_type",
                                       intent.getSerializableExtra("job_type")
                                   ).putExtra(
                                       "specialization",
                                       intent.getSerializableExtra("specialization")
                                   ).putExtra(
                                       "lat",
                                       intent.getStringExtra("lat")
                                   ).putExtra(
                                       "lng",
                                       intent.getStringExtra("lng")
                                   )
                                   .putExtra(
                                       "location_name",
                                       intent.getStringExtra("location_name")
                                   )
                                   .putExtra(
                                       "job_description",
                                       intent.getStringExtra("job_description")
                                   )
                                   .putExtra(
                                       "amount",
                                       intent.getStringExtra("amount")
                                   ).putExtra(
                                       "isSearchType",
                                       intent.getIntExtra("isSearchType", 1)
                                   ).putExtra(
                                       "isJobType",
                                       intent.getIntExtra("isJobType", -1)
                                   )
                                   .putExtra("start_date", intent.getStringExtra("start_date"))
                                   .putExtra("end_date", intent.getStringExtra("end_date"))
                                   .putExtra("mData", intent.getSerializableExtra("mData"))
                                   .putExtra("files", dataImage)
                                   .putExtra("uThumbs", uThumbs)
                                   .putExtra("isEdit", rData?.isEdit)
                                   .putExtra("isRepublish", rData != null)
                                   .putExtra("jobID", jobId)*/
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this,
                                    PostJobSummaryActivity::class.java
                                )/*
                                    .putExtra(
                                        "jobName",
                                        intent.getStringExtra("jobName")
                                    ).putExtra(
                                        "categories",
                                        intent.getSerializableExtra("categories")
                                    ).putExtra(
                                        "job_type",
                                        intent.getSerializableExtra("job_type")
                                    ).putExtra(
                                        "specialization",
                                        intent.getSerializableExtra("specialization")
                                    ).putExtra(
                                        "lat",
                                        intent.getStringExtra("lat")
                                    ).putExtra(
                                        "lng",
                                        intent.getStringExtra("lng")
                                    )
                                    .putExtra(
                                        "location_name",
                                        intent.getStringExtra("location_name")
                                    )
                                    .putExtra(
                                        "job_description",
                                        intent.getStringExtra("job_description")
                                    )
                                    .putExtra(
                                        "amount",
                                        intent.getStringExtra("amount")
                                    ).putExtra(
                                        "isSearchType",
                                        intent.getIntExtra("isSearchType", 1)
                                    ).putExtra(
                                        "isJobType",
                                        intent.getIntExtra("isJobType", -1)
                                    )
                                    .putExtra("start_date", intent.getStringExtra("start_date"))
                                    .putExtra("end_date", intent.getStringExtra("end_date"))
                                    .putExtra("mData", intent.getSerializableExtra("mData"))
                                    .putExtra("isEdit", rData?.isEdit)
                                    .putExtra("isRepublish", rData != null)
                                    .putExtra("jobID", jobId)*/
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getVidoeCount(): Int {
        var count = 0
        if (images != null) {
            images.forEach {
                if (it.mediaType == 2) {
                    count++
                }
            }
        }
        return count
    }

    private fun getMediaCount(): Int {
        var count = 0
        if (images != null) {

            images.forEach {
                if (it.mediaType == 1 || it.mediaType == 3 || it.mediaType == 4) {
                    count++
                }
            }
        }
        return count
    }

    override fun onCameraClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            if (getMediaCount() < 6)
                openCamera()
            else
                showToastShort(getString(R.string.you_can_select_maximum_6_doc_or_image))

        }
    }

    override fun onDropBoxClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            if (getMediaCount() < 6)
                openDropBoxLogin()
            else
                showToastShort(getString(R.string.you_can_select_maximum_6_doc_or_image))
        }
    }

    var isBackFromDropBoxLogin: Boolean? = null
    private fun openDropBoxLogin() {
        val isPackageAdded = appInstalledOrNot("com.dropbox.android")
        if (isPackageAdded) {
            val dropboxIntent = Intent(Intent.ACTION_GET_CONTENT)
            dropboxIntent.setPackage("com.dropbox.android")

            val mimeTypes = arrayOf(
                "application/pdf", "image/*",
                "video/*",
               "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ,"application/doc" ,
                "application/ms-doc"
            )
            dropboxIntent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                dropboxIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            val chooserIntent = Intent.createChooser(dropboxIntent, "Select File")

            try {
                startActivityForResult(chooserIntent, Constants.PICKFILE_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                showToastLong("Activity not found")
            }
        } else {
            var token = PreferenceManager.getString(PreferenceManager.DROP_BOX_TOKEN)
            if (token.isNullOrEmpty()) {
                token = Auth.getOAuth2Token()
            }
            if (token.isNullOrEmpty()) {
                isBackFromDropBoxLogin = true
                Auth.startOAuth2Authentication(this@VideoImageActivity, DROPBOX_APP_KEY_PERSONAL)
            } else {
                getDropBoxData(token)
            }
        }


    }


    override fun onResume() {
        super.onResume()
        isBackFromDropBoxLogin?.let {
            if (it) {
                isBackFromDropBoxLogin = false
                val accessToken = Auth.getOAuth2Token()
                PreferenceManager.putString(PreferenceManager.DROP_BOX_TOKEN, accessToken)

                getDropBoxData(accessToken)
            } else {
                isBackFromDropBoxLogin = false
            }
        }
    }

    fun getDropBoxData(accessToken: String?) {
        accessToken?.let {
            DropBoxDialog(this, it, this).show(supportFragmentManager, "")
        } ?: kotlin.run {
            showToastLong("Unable to access")
        }
    }

    override fun onGalleryClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            if (getMediaCount() < 6 && getVidoeCount() < 2)
                onGalleryVideoChoose(true)
            else if (getMediaCount() < 6 && getVidoeCount() == 2)
                onGalleryJustImageChoose(true)
            else if (getVidoeCount() < 2)
                onGalleryJustVideoChoose(true)
            else
                showToastShort(getString(R.string.you_can_select_maximum_6_doc_or_image))
        }
    }

    override fun onVideoCapture() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            if (getVidoeCount() < 2)
                captureVideo()
            else
                showToastShort(getString(R.string.you_can_select_maximum_2_video))
        }
    }

    override fun onFileClicked() {
        if (getMediaCount() < 6)
            getFileChooserIntent()
        else
            showToastShort(getString(R.string.you_can_select_maximum_6_doc_or_image))

    }

    private fun getFileChooserIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                val mimeTypes = arrayOf(
                    "application/pdf", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
               ,"application/doc" ,
                            "application/ms-doc"  )
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
                if (mimeTypes.isNotEmpty()) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
                startActivityForResult(intent, Constants.PICKFILE_REQUEST_CODE)
            } else {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data =
                        Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivityForResult(intent, 2296)
                } catch (e: java.lang.Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityForResult(intent, 2296)
                }
            }
        } else {
            val mimeTypes = arrayOf(
                "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ,"application/doc" ,
                "application/ms-doc"  )
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            startActivityForResult(intent, Constants.PICKFILE_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.setPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.UPLOAD_FILE -> {
                mViewModel.imageUploadResponse.url?.let {
                    if (it.isNotEmpty()) {
                        if (isReturn) {
                            val dataImage = ArrayList<String>()
                            images.forEachIndexed { _, newItem ->
                                if (newItem.link?.contains("http") == true) {
                                    dataImage.add(newItem.link!!)
                                }
                            }
                            dataImage.addAll(it)
                            setResult(
                                Activity.RESULT_OK,
                                Intent().putExtra("files", dataImage).putExtra("thumbs", thumbs)
                            )
                            finish()
                        } else {
                            PreferenceManager.putString(
                                PreferenceManager.NEW_JOB_PREF.JOB_ID,
                                jobId
                            )
                            PreferenceManager.putString(
                                PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_THUMB,
                                thumbs.toJsonString()
                            )
                            if (rData != null) {
                                val dataImage = ArrayList<String>()
                                rData?.urls?.forEachIndexed { _, newItem ->
                                    if (newItem.link?.contains("http") == true) {
                                        if (adapter.isExits(newItem.link!!))
                                            dataImage.add(newItem.link!!)
                                    }
                                }
                                dataImage.addAll(it)
                                val uThumbs = ArrayList<String>()
                                rData?.urls?.forEachIndexed { _, newItem ->
                                    if (newItem.link?.contains("http") == true && newItem.mediaType == 2) {
                                        if (adapter.isExits(newItem.link!!))
                                            uThumbs.add(newItem.link!!)
                                    }
                                }

                                PreferenceManager.putString(
                                    PreferenceManager.NEW_JOB_PREF.JOB_IMAGES,
                                    dataImage.toJsonString()
                                )

                                PreferenceManager.putString(
                                    PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_UTHUMB,
                                    uThumbs.toJsonString()
                                )

                                startActivity(
                                    Intent(this, PostJobSummaryActivity::class.java)/*
                                        .putExtra(
                                            "jobName",
                                            intent.getStringExtra("jobName")
                                        ).putExtra(
                                            "categories",
                                            intent.getSerializableExtra("categories")
                                        ).putExtra(
                                            "job_type",
                                            intent.getSerializableExtra("job_type")
                                        ).putExtra(
                                            "specialization",
                                            intent.getSerializableExtra("specialization")
                                        ).putExtra(
                                            "lat",
                                            intent.getStringExtra("lat")
                                        ).putExtra(
                                            "lng",
                                            intent.getStringExtra("lng")
                                        )
                                        .putExtra(
                                            "location_name",
                                            intent.getStringExtra("location_name")
                                        )
                                        .putExtra(
                                            "job_description",
                                            intent.getStringExtra("job_description")
                                        )
                                        .putExtra(
                                            "amount",
                                            intent.getStringExtra("amount")
                                        ).putExtra(
                                            "isSearchType",
                                            intent.getIntExtra("isSearchType", 1)
                                        ).putExtra(
                                            "isJobType",
                                            intent.getIntExtra("isJobType", -1)
                                        ).putExtra(
                                            "start_date",
                                            intent.getStringExtra("start_date")
                                        )
                                        .putExtra("end_date", intent.getStringExtra("end_date"))
                                        .putExtra("mData", intent.getSerializableExtra("mData"))
                                        .putExtra("files", dataImage)
                                        .putExtra("thumbs", thumbs)
                                        .putExtra("uThumbs", uThumbs)
                                        .putExtra("isRepublish", rData != null)
                                        .putExtra("jobID", jobId)*/
                                )
                            } else {
                                val dataImage = ArrayList<String>()
                                images.forEachIndexed { _, newItem ->
                                    if (newItem.link?.contains("http") == true) {
                                        dataImage.add(newItem.link!!)
                                    }
                                }
                                dataImage.addAll(it)
                                PreferenceManager.putString(
                                    PreferenceManager.NEW_JOB_PREF.JOB_IMAGES,
                                    dataImage.toJsonString()
                                )

                                startActivity(
                                    Intent(this, PostJobSummaryActivity::class.java)/*
                                        .putExtra(
                                            "jobName",
                                            intent.getStringExtra("jobName")
                                        ).putExtra(
                                            "categories",
                                            intent.getSerializableExtra("categories")
                                        ).putExtra(
                                            "job_type",
                                            intent.getSerializableExtra("job_type")
                                        ).putExtra(
                                            "specialization",
                                            intent.getSerializableExtra("specialization")
                                        ).putExtra(
                                            "lat",
                                            intent.getStringExtra("lat")
                                        ).putExtra(
                                            "lng",
                                            intent.getStringExtra("lng")
                                        )
                                        .putExtra(
                                            "location_name",
                                            intent.getStringExtra("location_name")
                                        )
                                        .putExtra(
                                            "job_description",
                                            intent.getStringExtra("job_description")
                                        )
                                        .putExtra(
                                            "amount",
                                            intent.getStringExtra("amount")
                                        ).putExtra(
                                            "isSearchType",
                                            intent.getIntExtra("isSearchType", 1)
                                        ).putExtra(
                                            "isJobType",
                                            intent.getIntExtra("isJobType", -1)
                                        ).putExtra(
                                            "start_date",
                                            intent.getStringExtra("start_date")
                                        )
                                        .putExtra("end_date", intent.getStringExtra("end_date"))
                                        .putExtra("mData", intent.getSerializableExtra("mData"))
                                        .putExtra("files", it as ArrayList<String>)
                                        .putExtra("thumbs", thumbs)
                                        .putExtra("isRepublish", rData != null)
                                        .putExtra("jobID", jobId)*/
                                )
                            }

                        }
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.UPLOAD_FILE -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun permissionGiven(requestCode: Int) {
        when (requestCode) {
            PermissionConstants.PERMISSION ->
                CameraVideBottomSheet(this, this).show()
        }
    }

    override fun permissionCancel(requestCode: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.PICKFILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {

                        uri = data?.data
                     val filepath = uri?.let { FileUtils.getPathFromUri(this, it) }
                    Log.d("FILE_PATH","$filepath")
                    if (isValidFile(filepath)) {
                        fPAth=filepath
                        if (!fPAth.isNullOrEmpty() && File(fPAth?:"").exists()) {
                            if (images.size == 0) {
                                val photos = Photos(0, "")
                                images.add(photos)
                            }
                            if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                                mBinding.ivAddImageVideo.visibility = View.GONE
                            }

                            val photos = Photos(getFileType(fPAth), fPAth)
                            images.add(0, photos)
                            adapter.notifyDataSetChanged()
                        } else {
                            showToastShort(getString(R.string.file_not_available))
                        }
                    }
                    else{
                        showToastShort(getString(R.string.file_must_be_valid_format_or_size))
                    }
                }
            }
            Constants.DROPBOX_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                    if (File(fPAth).exists()) {
                        if (images.size == 0) {
                            val photos = Photos(0, "")
                            images.add(photos)
                        }
                        if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                            mBinding.ivAddImageVideo.visibility = View.GONE
                        }

                        val photos = Photos(getFileType(fPAth), fPAth)
                        images.add(0, photos)
                        adapter.notifyDataSetChanged()
                    } else {
                        showToastShort(getString(R.string.file_not_available))
                    }
                }
            }
            PermissionConstants.REQ_VIDEO -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                        if (images.size == 0) {
                            val photos = Photos(0, "")
                            images.add(photos)
                        }
                        if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                            mBinding.ivAddImageVideo.visibility = View.GONE
                        }
                        val photos = Photos(MediaType.VIDEO, fPAth)
                        images.add(0, photos)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            PermissionConstants.REQ_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    uri = data.data
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                        val isImage = isImageFile(fPAth)
//                        if (isImage) {
//
//                            var destinationFileName = System.currentTimeMillis().toString() + ""
//                            destinationFileName += ".jpeg"
//                            val file = File(cacheDir, destinationFileName)
//                            if (!file.exists()) {
//                                try {
//                                    file.createNewFile()
//                                } catch (e: IOException) {
//                                    e.printStackTrace()
//                                }
//                            }
//                            UCrop.of(
//                                Uri.fromFile(File(fPAth)), Uri.fromFile(
//                                    File(
//                                        cacheDir, destinationFileName
//                                    )
//                                )
//                            ).withAspectRatio(1f, 1f)
//                                .start(this)
//                        } else {
                            if (images.size == 0) {
                                val photos = Photos(0, "")
                                images.add(photos)
                            }
                            if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                                mBinding.ivAddImageVideo.visibility = View.GONE
                            }
                            val photos = Photos(getFileType(fPAth), fPAth)
                            images.add(0, photos)
                            adapter.notifyDataSetChanged()
//                        }

                    }
                } else if (resultCode == Activity.RESULT_OK && data != null && data.clipData != null) {
                    val mClipData: ClipData = data.clipData!!
                    if (images.size + data.clipData!!.itemCount > 7) {
                        showToastShort(getString(R.string.you_can_select_maximum_6_doc_or_image))
                        return
                    }
                    for (i in 0 until mClipData.getItemCount()) {
                        val item: ClipData.Item = mClipData.getItemAt(i)
                        uri = item.getUri()
                        if (uri != null) {
                            fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                            if (images.size == 0) {
                                val photos = Photos(0, "")
                                images.add(photos)
                            }
                            if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                                mBinding.ivAddImageVideo.visibility = View.GONE
                            }
                            val photos = Photos(getFileType(fPAth), fPAth)
                            images.add(0, photos)
                            adapter.notifyDataSetChanged()

                            /*   val isImage = isImageFile(fPAth)
                               if (isImage) {
                                   if (images.size == 0) {
                                       val photos = Photos(0, "")
                                       images.add(photos)
                                   }
                                   if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                                       mBinding.ivAddImageVideo.visibility = View.GONE
                                   }
                                   val photos = Photos(1, fPAth)
                                   images.add(0, photos)
                                   adapter.notifyDataSetChanged()
                               } else {
                                   if (images.size == 0) {
                                       val photos = Photos(0, "")
                                       images.add(photos)
                                   }
                                   if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                                       mBinding.ivAddImageVideo.visibility = View.GONE
                                   }
                                   val photos = Photos(getFileType(fPAth), fPAth)
                                   images.add(0, photos)
                                   adapter.notifyDataSetChanged()
                               }*/
                        }
                    }
                }
            }
            PermissionConstants.REQ_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                        if (images.size == 0) {
                            val photos = Photos(0, "")
                            images.add(photos)
                        }
                        if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                            mBinding.ivAddImageVideo.visibility = View.GONE
                        }
                        val photos = Photos(getFileType(fPAth), fPAth)
                        images.add(0, photos)
                        adapter.notifyDataSetChanged()

//                        var destinationFileName = System.currentTimeMillis().toString() + ""
//                        destinationFileName += ".jpeg"
//                        val file = File(cacheDir, destinationFileName)
//                        if (!file.exists()) {
//                            try {
//                                file.createNewFile()
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                        }
//                        UCrop.of(
//                            Uri.fromFile(File(fPAth)), Uri.fromFile(
//                                File(
//                                    cacheDir, destinationFileName
//                                )
//                            )
//                        ).withAspectRatio(1f, 1f)
//                            .start(this)
                    }
                }
            }

            PermissionConstants.CAPTURE_VIDEO -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }

                        if (!isFileLessThan30MB(File(fPAth))) {
                            fPAth?.let {
                                val ImageName =
                                    "Video_" + Calendar.getInstance().getTimeInMillis()
                                        .toString() + ".mp4"
                                val file = File(
                                    Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOCUMENTS
                                    ), ImageName
                                )
                                if (!file.exists()) {
                                    file.createNewFile()
                                }
                                showProgressDialog(LoaderType.NORMAL, "")
                                LightVideoCompression(this, it,
                                    file, object :
                                        LightVideoCompression.CompressListener {
                                        override fun onCompressedVideo(destPath: String) {
                                            hideProgressDialog(LoaderType.NORMAL, "")
                                            if (images.size == 0) {
                                                val photos = Photos(0, "")
                                                images.add(photos)
                                            }
                                            if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                                                mBinding.ivAddImageVideo.visibility = View.GONE
                                            }
                                            fPAth = destPath
                                            val photos = Photos(MediaType.VIDEO, fPAth)
                                            images.add(0, photos)
                                            adapter.notifyDataSetChanged()
                                        }

                                        override fun onCompressFail() {
                                            hideProgressDialog(LoaderType.NORMAL, "")
                                            showToastShort(getString(R.string.error_while_video_compressing))
                                        }
                                    })
                            }
                        } else {
                            showToastShort(getString(R.string.large_file_size))
                        }
                    }
                }
            }
            UCrop.REQUEST_CROP -> {
                if (data != null) {
                    val resultUri = UCrop.getOutput(data!!)
                    if (resultUri != null) fPAth = resultUri.path
                    else
                        return
                    if (images.size == 0) {
                        val photos = Photos(0, "")
                        images.add(photos)
                    }
                    if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                        mBinding.ivAddImageVideo.visibility = View.GONE
                    }
                    val photos = Photos(MediaType.IMAGE, fPAth)
                    images.add(0, photos)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        if (getMediaCount() == 6 && getVidoeCount() == 2 && images[images.size - 1].mediaType == 0) {
            images.removeAt(images.size - 1)
            adapter.notifyDataSetChanged()
        }
    }

    fun isImageFile(path: String?): Boolean {
        val mimeType: String = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }



    private fun isFileLessThan30MB(file: File): Boolean {
        val maxFileSize = 30 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }

    private fun getImageUri(photo: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            getContentResolver(),
            photo,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

    override fun onClick(p0: View?) {
        mBinding.ivAddImageVideo.performClick()
    }


    override fun onFileSelected(path: String?) {
        fPAth = path

        if (!isFileLessThan30MB(File(fPAth))) {
            if (images.size == 0) {
                val photos = Photos(0, "")
                images.add(photos)
            }
            if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                mBinding.ivAddImageVideo.visibility = View.GONE
            }
            val photos = Photos(getFileType(fPAth), fPAth)
            images.add(0, photos)
            adapter.notifyDataSetChanged()
        } else {
            showToastLong("File should be less than 30MB")
        }
    }
}