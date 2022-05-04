package com.exampl


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.model.jobmodel.Photos
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*
import com.dropbox.core.android.Auth
import com.example.ticktapp.R
import com.example.ticktapp.adapters.UploadImagesAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityUploadMilestonePhotosBinding
import com.example.ticktapp.dialog.CameraVideBottomSheet
import com.example.ticktapp.dialog.dropBox.DropBoxDialog
import com.example.ticktapp.mvvm.view.tradie.completemilestone.MilestoneHoursActivity
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File


class UploadPhotosActivity : BaseActivity(), PermissionHelper.IGetPermissionListener,
    View.OnClickListener,
    UploadImagesAdapter.ClickListener, CameraVideBottomSheet.CameraDialogCallBack,
    DropBoxDialog.IDropBoxListener {
    private var jobId: String? = null
    private var jobName: String? = null
    private var milestoneId: String? = null
    private var jobCount: String? = null
    private var amount: String? = null
    private var payType: String? = null
    private var isJobCompleted: Boolean = false
    private lateinit var adapter: UploadImagesAdapter
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var mBinding: ActivityUploadMilestonePhotosBinding
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private var uri: Uri? = null
    private var fPAth: String? = null
    private val images by lazy { ArrayList<Photos>() }

    private val mViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_upload_milestone_photos)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        getIntentData()
        setObservers()
        setupView()
        enableDisableSubmit()
    }

    private fun getIntentData() {
        jobId = intent.getStringExtra(IntentConstants.JOB_ID)
        jobName = intent.getStringExtra(IntentConstants.JOB_NAME)
        milestoneId = intent.getStringExtra(IntentConstants.MILESTONE_ID)
        isJobCompleted = intent.getBooleanExtra(IntentConstants.IS_JOB_COMPLETED, false)
        jobCount = intent.getStringExtra(IntentConstants.JOB_COUNT)
        amount = intent.getStringExtra(IntentConstants.AMOUNT)
        payType = intent.getStringExtra(IntentConstants.PAY_TYPE)
        mBinding.ilHeader.tvTitle.text = jobName
    }


    private fun setupView() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
        val layountManager = GridLayoutManager(this, 3)
        adapter = UploadImagesAdapter(images, this, this)
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


    private fun listener() {
        mBinding.ivAddImageVideo.setOnClickListener {
            if (permissionHelper.hasPermission(
                    this,
                    permissions, PermissionConstants.PERMISSION
                )
            ) {

                CameraVideBottomSheet(this, this, true).show()
            }
        }
        mBinding.jobDescEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mBinding.jobDescTvCount.text = "${p0?.length}/1000"
                enableDisableSubmit()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        mBinding.btnSubmit.setOnClickListener {
            val image = ArrayList<String>()
            images.forEach {
                if (it.mediaType == 1)
                    image.add(it.link.toString())
            }
            if (image.size > 0) {
                mViewModel.hitUploadFile(image)
            }
        }
    }


    override fun onCameraClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            openCamera()

        }
    }

    override fun onGalleryClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            onGalleryJustImageChoose(false)
        }
    }

    override fun onVideoCapture() {

    }

    override fun onFileClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            getFileChooserIntent()
        }

    }

    private fun getFileChooserIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                val mimeTypes = arrayOf(
                    "application/pdf",
                    "image/*",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/doc",
                    "application/ms-doc"
                )
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
                "application/pdf",
                "image/*",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/doc",
                "application/ms-doc"
            )
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            startActivityForResult(intent, Constants.PICKFILE_REQUEST_CODE)
        }
    }

    var isBackFromDropBoxLogin: Boolean? = null

    override fun onDropBoxClicked() {
        val isPackageAdded = appInstalledOrNot("com.dropbox.android")
        if (isPackageAdded) {
            val dropboxIntent = Intent(Intent.ACTION_GET_CONTENT)
            dropboxIntent.setPackage("com.dropbox.android")

            val mimeTypes = arrayOf(
                "application/pdf",
                "image/*",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/doc",
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
                Auth.startOAuth2Authentication(
                    this,
                    DropBoxConstants.DROPBOX_APP_KEY_PERSONAL
                )
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
            DropBoxDialog(this, it, this, true).show(supportFragmentManager, "")
        } ?: kotlin.run {
            showToastLong("Unable to access")
        }
    }

    private fun enableDisableSubmit() {
        if (getMediaCount() > 0 && !mBinding.jobDescEd.text.toString().trim().isEmpty()) {
            mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_btn_yellow)
            mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_161d4a))
            mBinding.btnSubmit.isEnabled = true
            mBinding.btnSubmit.alpha = 1f
        } else {
            mBinding.btnSubmit.alpha = 0.4f
            mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_btn_yellow)
            mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_161d4a))
            mBinding.btnSubmit.isEnabled = false
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
                        intent = Intent(this, MilestoneHoursActivity::class.java)
                            .putExtra(IntentConstants.JOB_ID, jobId)
                            .putExtra(IntentConstants.JOB_NAME, jobName)
                            .putExtra(IntentConstants.MILESTONE_ID, milestoneId)
                            .putExtra(IntentConstants.IS_JOB_COMPLETED, isJobCompleted)
                            .putExtra(IntentConstants.JOB_COUNT, jobCount)
                            .putExtra(IntentConstants.AMOUNT, amount)
                            .putExtra(IntentConstants.PAY_TYPE, payType)
                            .putExtra(
                                IntentConstants.DESCRIPTION,
                                mBinding.jobDescEd.text.toString()
                            )
                            .putExtra(IntentConstants.IMAGES, it as ArrayList<String>)
                        resultLauncher.launch(intent)
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

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    setResult(RESULT_OK, data)
                    finish()
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
                    Log.d("FILE_PATH", "$filepath")
                    if (isValidFile(filepath)) {
                        fPAth = filepath
                        if (!fPAth.isNullOrEmpty() && File(fPAth).exists()) {
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
                            enableDisableSubmit()
                        } else {
                            showToastShort(getString(R.string.file_not_available))
                        }
                    } else {
                        showToastShort(getString(R.string.file_must_be_valid_format_or_size))
                    }
                }
            }
            PermissionConstants.REQ_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    uri = data.data
                    if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
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
                        enableDisableSubmit()

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
                        enableDisableSubmit()
                    }
                }
            }


            UCrop.REQUEST_CROP -> {
                if (data != null) {
                    val resultUri = UCrop.getOutput(data)
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
                    enableDisableSubmit()
                }
            }
        }
        if (getMediaCount() == 6) {
            images.removeAt(images.size - 1)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getMediaCount(): Int {
        var count = 0
        if (images != null) {

            images.forEach {
                if (it.mediaType == MediaType.IMAGE || it.mediaType == MediaType.PDF) {
                    count++
                }
            }
        }
        return count
    }

    private fun getImageUri(photo: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            contentResolver,
            photo,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    override fun onClick(p0: View?) {
        mBinding.ivAddImageVideo.performClick()
    }

    override fun onCancelCLick(pos: Int) {
        enableDisableSubmit()
        if (adapter.itemCount == 0)
            mBinding.ivAddImageVideo.visibility = View.VISIBLE

    }

    override fun onFileSelected(path: String?) {
        fPAth = path


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
        enableDisableSubmit()

    }
}