package com.example.ticktapp.mvvm.view.tradie

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.CancelReason
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.AddMediaAdapter
import com.example.ticktapp.adapters.CancelAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityLodgeDisputeTradieBinding
import com.example.ticktapp.dialog.GalleryCameraBottomSheet
import com.app.core.model.jobmodel.Photos
import com.example.ticktapp.mvvm.view.builder.LodgeDisputedJobActivity
import com.example.ticktapp.mvvm.viewmodel.LodgeDisputeViewModel
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class LodgeDisputeTradieActivity : BaseActivity(), OnClickListener,
    PermissionHelper.IGetPermissionListener,
    GalleryCameraBottomSheet.CameraDialogCallBack {
    private lateinit var mAdapter: CancelAdapter
    private lateinit var cancelReasonList: ArrayList<CancelReason>
    private lateinit var mBinding: ActivityLodgeDisputeTradieBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(LodgeDisputeViewModel::class.java) }
    private val mViewModelImage by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private var jobId = ""
    private var jobName = ""
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lodge_dispute_tradie)
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setupView()
        setUpListeners()
        // enableDisableSubmit()
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

        cancelReasonList = ArrayList()
        cancelReasonList.add(
            CancelReason(
                1,
                getString(R.string.site_has_note_been_prepared),
                false
            )
        )
        cancelReasonList.add(CancelReason(2, getString(R.string.inadequate_access), false))
        cancelReasonList.add(CancelReason(3, getString(R.string.no_access_equip), false))
        cancelReasonList.add(
            CancelReason(
                4,
                getString(R.string.material_has_not_been_delivered),
                false
            )
        )
        cancelReasonList.add(CancelReason(5, getString(R.string.tradeperson_work_does_not), false))
        cancelReasonList.add(
            CancelReason(
                6,
                getString(R.string.tradeperson_has_not_supplied),
                false
            )
        )
        cancelReasonList.add(
            CancelReason(
                7,
                getString(R.string.tradeperson_work_not_standard),
                false
            )
        )

        mAdapter = CancelAdapter(cancelReasonList, object : CancelAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
                enableDisableSubmit()
            }
        })
        mBinding.rvCancel.layoutManager = LinearLayoutManager(this)
        mBinding.rvCancel.adapter = mAdapter

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
            ApiCodes.LODGE_DISPUTE -> {
                startActivity(
                    Intent(this, LodgeDisputedJobActivity::class.java)
                        .putExtra("data", "tradie")
                )
            }
            ApiCodes.UPLOAD_FILE -> {
                mViewModelImage.imageUploadResponse.url.let {
                    val photos = ArrayList<Photos>()
                    it?.forEach {
                        val photo = Photos(1, it)
                        photos.add(photo)
                    }
                    val mObject = HashMap<String, Any>()
                    mObject.put("jobId", jobId)
                    mObject.put("reason", mAdapter.getSelectedId())
                    if (!mBinding.edDetails.text.toString().trim().isEmpty())
                        mObject.put("details", mBinding.edDetails.text.toString())
                    mObject.put("photos", photos)
                    mViewModel.lodgeDispute(mObject)
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.LODGE_DISPUTE -> {
                showToastShort(exception.message)
            }
            ApiCodes.UPLOAD_FILE -> {
                showToastShort(exception.message)
            }


        }
        super.onException(exception, apiCode)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.btnSubmit -> {
                if (mAdapter.getSelectedId() == 0) {
                    showToastShort(getString(R.string.please_select_one_reason))
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
                        mObject.put("reason", mAdapter.getSelectedId())
                        if (!mBinding.edDetails.text.toString().trim().isEmpty())
                            mObject.put("details", mBinding.edDetails.text.toString())
                        mViewModel.lodgeDispute(mObject)
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

    private fun enableDisableSubmit() {
        if (mAdapter.getSelectedId() != 0) {
            mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_btn_yellow)
            mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_161d4a))
            mBinding.btnSubmit.isEnabled = true
        } else {
            mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_drawable_rect_dfe5ef)
            mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_99a4b6))
            mBinding.btnSubmit.isEnabled = false
        }
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
                        adapter.notifyDataSetChanged()
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