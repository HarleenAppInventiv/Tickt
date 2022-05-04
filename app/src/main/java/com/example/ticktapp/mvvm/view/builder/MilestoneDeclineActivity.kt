package com.example.ticktapp.mvvm.view.builder

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.AddMediaAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityDeclineMilestoneBinding
import com.example.ticktapp.dialog.GalleryCameraBottomSheet
import com.app.core.model.jobmodel.Photos
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.mvvm.viewmodel.MilestoneListViewModel
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import com.yalantis.ucrop.UCrop
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MilestoneDeclineActivity : BaseActivity(), OnClickListener,
    PermissionHelper.IGetPermissionListener,
    GalleryCameraBottomSheet.CameraDialogCallBack {

    private lateinit var mBinding: ActivityDeclineMilestoneBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(MilestoneListViewModel::class.java) }
    private val mViewModelImage by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private var jobId = ""
    private var jobName = ""
    private var milestoneId = ""
    private lateinit var adapter: AddMediaAdapter
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private val images by lazy { ArrayList<Photos>() }
    private var uri: Uri? = null
    private var fPAth: String? = null
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_decline_milestone)
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setupView()
        setUpListeners()
    }

    private fun setupView() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
        val layountManager = GridLayoutManager(this, 3)
        AddMediaAdapter(images) {
            mBinding.ivAddImageVideo.performClick()
        }.also { adapter = it }
        mBinding.rvMedia.layoutManager = layountManager
        mBinding.rvMedia.adapter = adapter

    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    private fun getIntentData() {
        jobId = intent.getStringExtra(IntentConstants.JOB_ID).toString()
        jobName = intent.getStringExtra(IntentConstants.JOB_NAME).toString()
        milestoneId = intent.getStringExtra(IntentConstants.MILESTONE_ID).toString()
        mBinding.tvTitle.text = jobName
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(Color.WHITE)
        }
    }

    override fun onCameraClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            if (getMediaCount() < 6)
                openCamera()
            else
                showToastShort(getString(R.string.you_can_select_maximum_6_image))

        }
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

    override fun onGalleryClicked() {
        if (permissionHelper.hasPermission(this, permissions, PermissionConstants.REQ_CAMERA)
        ) {
            if (getMediaCount() < 6)
                onGalleryJustImageChoose(true)
            else
                showToastShort(getString(R.string.you_can_select_maximum_6_image))
        }
    }

    private fun setUpListeners() {
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.ivAddImageVideo.setOnClickListener {
            if (permissionHelper.hasPermission(
                    this,
                    permissions, PermissionConstants.PERMISSION
                )
            ) {

                GalleryCameraBottomSheet(this, this).show()
            }
        }
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        setBaseViewModel(mViewModelImage)
        mViewModelImage.getResponseObserver().observe(this, this)

    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.DECLINE_MILESTONE -> {
                milestoneDeclinedMoEngage()  //milestone declined mo engage
                milestoneDeclinedMixPanel()
                startActivityForResult(Intent(this, MilestoneDeclinedJobActivity::class.java), 1992)
            }
            ApiCodes.UPLOAD_FILE -> {
                mViewModelImage.imageUploadResponse.url.let {
                    val mObject = HashMap<String, Any>()
                    mObject.put("jobId", jobId)
                    mObject.put("status", 2)
                    mObject.put("milestoneId", milestoneId)
                    mObject.put("reason", mBinding.edDetails.text.toString())
                    it?.let { it1 -> mObject.put("url", it1) }
                    mViewModel.declineMilestoneRequest(mObject)
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.DECLINE_MILESTONE -> {
                showToastShort(exception.message)
            }
            ApiCodes.UPLOAD_FILE -> {
                showToastShort(exception.message)
            }


        }
        super.onException(exception, apiCode)
    }

    private fun milestoneDeclinedMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_MILESTONE_DECLINED,
            signUpProperty
        )
    }

    private fun milestoneDeclinedMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_MILESTONE_DECLINED,
            props
        )
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.btnSubmit -> {
                if (mBinding.edDetails.text.toString().isEmpty()) {
                    showToastShort(getString(R.string.please_enter_reason))
                } else {
                    val image = ArrayList<String>()
                    images.forEach {
                        if (it.link.toString().length > 0)
                            image.add(it.link.toString())
                    }
                    if (image.size > 0) {
                        mViewModelImage.hitUploadFile(image)
                    } else {
                        val mObject = HashMap<String, Any>()
                        mObject.put("jobId", jobId)
                        mObject.put("status", 2)
                        mObject.put("milestoneId", milestoneId)
                        mObject.put("reason", mBinding.edDetails.text.toString())
                        mViewModel.declineMilestoneRequest(mObject)
                    }
                }
            }


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


    override fun permissionGiven(requestCode: Int) {
        when (requestCode) {
            PermissionConstants.PERMISSION ->
                GalleryCameraBottomSheet(this, this).show()
        }
    }

    override fun permissionCancel(requestCode: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1992 -> {
                setResult(Activity.RESULT_OK)
                finish()
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
                        val photos = Photos(1, fPAth)
                        images.add(0, photos)
                        adapter.notifyDataSetChanged()

                    }
                } else if (resultCode == Activity.RESULT_OK && data != null && data.clipData != null) {
                    val mClipData: ClipData = data.clipData!!
                    if (images.size + data.clipData!!.itemCount > 7) {
                        showToastShort(getString(R.string.you_can_select_maximum_6_image))
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
                            val photos = Photos(1, fPAth)
                            images.add(0, photos)
                            adapter.notifyDataSetChanged()
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
                        val photos = Photos(1, fPAth)
                        images.add(0, photos)
                        adapter.notifyDataSetChanged() }
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
                    val photos = Photos(1, fPAth)
                    images.add(0, photos)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        if (getMediaCount() == 6 && images[images.size - 1].mediaType == 0) {
            images.removeAt(images.size - 1)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getImageUri(photo: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            getContentResolver(),
            photo,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}
