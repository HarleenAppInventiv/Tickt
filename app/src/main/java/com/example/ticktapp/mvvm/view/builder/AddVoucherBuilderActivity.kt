package com.example.ticktapp.mvvm.view.builder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.Constants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ViewMediaAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityAddVouchBinding
import com.example.ticktapp.dialog.FileBottomSheet
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.jobmodel.Photos
import com.app.core.preferences.PreferenceManager
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.mvvm.viewmodel.VoucherViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddVoucherBuilderActivity : BaseActivity(), OnClickListener,
    PermissionHelper.IGetPermissionListener,
    FileBottomSheet.CameraDialogCallBack {
    private lateinit var mBinding: ActivityAddVouchBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(VoucherViewModel::class.java) }
    private val mViewModelImage by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private var jobId = ""
    private var jobName = ""
    private var tradieId = ""
    private lateinit var adapter: ViewMediaAdapter
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_vouch)
        setObservers()
        getIntentData()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        setupView()
        setUpListeners()
    }

    private fun getIntentData() {
        if (intent.hasExtra("id")) {
            tradieId = intent.getStringExtra("id").toString()
        }
    }

    private fun setupView() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
        val layountManager = GridLayoutManager(this, 3)
        ViewMediaAdapter(images) {
            if (images.size == 0)
                mBinding.ivAddImageVideo.visibility = View.VISIBLE
        }.also { adapter = it }
        mBinding.rvMedia.layoutManager = layountManager
        mBinding.rvMedia.adapter = adapter

        ViewCompat.setNestedScrollingEnabled(mBinding.rvMedia, false)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }


    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(Color.WHITE)
        }
    }

    override fun onFileClicked() {
        getFileChooserIntent()
    }

    private fun getFileChooserIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                val mimeTypes = arrayOf(
                    "application/pdf", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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
                "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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


    private fun setUpListeners() {
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.ivAddImageVideo.setOnClickListener {
            if (permissionHelper.hasPermission(
                    this,
                    permissions, PermissionConstants.PERMISSION
                )
            ) {

                FileBottomSheet(this, this).show()
            }
        }
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.postEdJobName.setOnClickListener {
            startActivityForResult(
                Intent(this, ChooseJobActivity::class.java).putExtra(
                    "isReturn",
                    true
                ).putExtra("tradieId", tradieId), 1310
            )
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        setBaseViewModel(mViewModelImage)
        mViewModelImage.getResponseObserver().observe(this, this)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.ADD_VOUCH -> {
                leftVouchMoEngage()   // left vouch Mo Engage
                leftVouchMixPanel()
                mViewModel.vouchData.let {
                    val vData = Intent()
                    vData.putExtra("data", it)
                    setResult(Activity.RESULT_OK, vData)
                    finish()
                }
            }
            ApiCodes.UPLOAD_FILE -> {
                mViewModelImage.imageUploadResponse.url.let {
                    val mObject = HashMap<String, Any>()
                    mObject.put("jobId", jobId)
                    mObject.put("jobName", jobName)
                    mObject.put("tradieId", tradieId)
                    if (!mBinding.edDetails.text.toString().trim().isEmpty())
                        mObject.put("vouchDescription", mBinding.edDetails.text.toString())
                    mObject.put("photos", it as ArrayList<String>)
                    mObject.put("recommendation", it.get(0))
                    mViewModel.addVoucher(mObject)
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ADD_VOUCH -> {
                showToastShort(exception.message)
            }
            ApiCodes.UPLOAD_FILE -> {
                showToastShort(exception.message)
            }
        }
        super.onException(exception, apiCode)
    }

    private fun leftVouchMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_LEFT_VOUCHER,
            signUpProperty
        )
    }

    private fun leftVouchMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_LEFT_VOUCHER,
            props
        )
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.btnSubmit -> {
                val any = if (jobId.isEmpty()) {
                    showToastShort(getString(R.string.please_choose_job))
                } else if (images.size == 0) {
                    showToastShort(getString(R.string.please_upload_document))
                } else if (mBinding.edDetails.text.toString().isEmpty()) {
                    showToastShort(getString(R.string.please_enter_vouch_desc))
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
                        mObject.put("jobName", jobName)
                        mObject.put("tradieId", tradieId)
                        if (!mBinding.edDetails.text.toString().trim().isEmpty())
                            mObject.put("vouchDescription", mBinding.edDetails.text.toString())
                        mViewModel.addVoucher(mObject)
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
                FileBottomSheet(this, this).show()
        }
    }

    override fun permissionCancel(requestCode: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.PICKFILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        uri = data?.data!!
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                        if (File(fPAth).exists()) {
                            if (fPAth?.endsWith(".doc") == true || fPAth?.endsWith(".docx") == true || fPAth?.endsWith(
                                    ".docs"
                                ) == true
                            ) {
                                images.add(Photos(3, fPAth))
                            } else {
                                images.add(Photos(4, fPAth))
                            }
                            adapter.notifyDataSetChanged()
                            mBinding.ivAddImageVideo.visibility = View.GONE
                        } else {
                            showToastShort(getString(R.string.file_not_available))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            1310 -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.hasExtra("data")) {
                        val jobData = data.getSerializableExtra("data") as JobRecModel
                        jobId = jobData.jobId.toString()
                        jobName = jobData.jobName.toString()
                        if (jobData.tradieId != null && jobData.tradieId!!.length > 0)
                            tradieId = jobData.tradieId.toString()
                        mBinding.postEdJobName.text = jobName
                    }
                }
            }
        }
    }

}
