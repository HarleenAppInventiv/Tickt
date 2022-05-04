package com.example.ticktapp.mvvm.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.model.tradesmodel.Qualification
import com.app.core.util.*
import com.example.ticktapp.R
import com.example.ticktapp.adapters.AddDocumentAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityAddDocumentBinding
import com.example.ticktapp.databinding.RowitemDocumentsBinding
import com.example.ticktapp.dialog.CameraBottomSheet
import com.app.core.model.jobmodel.QualifiedDoc
import com.example.ticktapp.mvvm.viewmodel.AddDocumentViewModel
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils.getPathFromUri
import kotlinx.android.synthetic.main.rowitem_documents.*
import kotlinx.android.synthetic.main.toolbar_onboarding.*
import java.io.ByteArrayOutputStream
import java.io.File


class AddDocumentActivity : BaseActivity(), View.OnClickListener,
    AddDocumentAdapter.DocListAdapterListener,
    PermissionHelper.IGetPermissionListener,
    CameraBottomSheet.CameraDialogCallBack {
    private lateinit var mBinding: ActivityAddDocumentBinding
    private lateinit var mAdapter: AddDocumentAdapter
    private var tadeList: ArrayList<String>? = null
    private var specList: ArrayList<String>? = null
    private var qualList: ArrayList<Qualification>? = null
    private val selectedQualification by lazy { ArrayList<com.app.core.model.registrationmodel.Qualification>() }
    private val selectedQualificationDoc by lazy { ArrayList<QualifiedDoc>() }
    private var email: String? = null
    private var name: String? = null
    private var phoneno: String? = null
    private var password: String? = null
    private var fPAth: String? = null
    private var hasUploadedAllDoc: Boolean? = null
    private var onBoardingData: OnBoardingData? = null
    private var selectedPosition = 0
    private var docUploadPosition = 0
    private var uri: Uri? = null
    private var isReturn = false
    private val mTradeViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }

    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private var permissionsNormal = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private val mViewModel by lazy { ViewModelProvider(this).get(AddDocumentViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_document)
        mBinding.model = mViewModel
        getIntentData()
        initView()
        setPermissionForR()
        if(!isReturn)
        setProgressDots()
        setObservers()
        setListeners()
        if (!isReturn)
            initRecyclerView()
    }

    private fun setPermissionForR() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        }
    }

    fun setProgressDots() {
        mBinding.rlToolbar.llProgressDots.visibility = View.VISIBLE
        mBinding.rlToolbar.v1.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v2.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v3.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v4.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v5.background =
            ContextCompat.getDrawable(this, R.drawable.bg_selected_progress_dot)
        mBinding.rlToolbar.v6.background =
            ContextCompat.getDrawable(this, R.drawable.bg_unselected_progress_dot)
    }


    private fun initRecyclerView() {
        mAdapter = qualList?.let { AddDocumentAdapter(it, this) }!!
        mBinding.rvDocument.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        mBinding.rvDocument.layoutManager = layoutManager
    }

    private fun getIntentData() {
        isReturn = intent.getBooleanExtra("isReturn", false)
        if (!isReturn) {
            email = intent.getStringExtra(IntentConstants.EMAIL)
            name = intent.getStringExtra(IntentConstants.FIRST_NAME)
            phoneno = intent.getStringExtra(IntentConstants.MOBILE_NUMBER)
            password = intent.getStringExtra(IntentConstants.PASSWORD)
            tadeList = intent.getStringArrayListExtra(IntentConstants.TRADE_LIST)
            specList = intent.getStringArrayListExtra(IntentConstants.SPEC_LIST)
            qualList = intent.getParcelableArrayListExtra<Qualification>(IntentConstants.QUAL_LIST)
        } else {
            mTradeViewModel.getTradeList(false)
        }

    }


    private fun setListeners() {
        mViewModel.getPermissionHelper().setListener(this)
        iv_back.setOnClickListener(this)
        mBinding.tvYellowBtn.setOnClickListener(this)
        mBinding.rlToolbar.tvSkip.setOnClickListener(this)
    }

    private fun initView() {
        if (!isReturn)
            mBinding.rlToolbar.tvSkip.visibility = View.VISIBLE
        mBinding.rlToolbar.tvTitle.setText(getString(R.string.add_qualifications))
        mBinding.tvYellowBtn.setText(getString(R.string.next))

    }

    /**
     * Setting up spannable string to show the "Register now in different font and color"
     */
    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(mTradeViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mTradeViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
        })
        mViewModel.getUploadMediaLiveData().observe(this, {
            if (it.mediaBean?.isSuccess.equals("1")) {
                val extension: String? =
                    it.mediaBean?.serverUrl?.substring(it.mediaBean?.serverUrl?.lastIndexOf(".")!!)
                qualList?.get(docUploadPosition)?.fileExt = extension
                qualList?.get(docUploadPosition)?.serverUrl = it.mediaBean?.serverUrl
                qualList?.get(docUploadPosition)?.isUploaded = true
            } else
                qualList?.get(docUploadPosition)?.isUploaded = false
            mAdapter.notifyDataSetChanged()
        })
    }


    override fun onClick(v: View?) {
        when (v) {
            mBinding.rlToolbar.tvSkip -> {
                startActivity(Intent(this, ABNActivity::class.java).apply {
                    onBoardingData = OnBoardingData(
                        firstName = name,
                        email = email,
                        mobileNumber = phoneno,
                        password = password,
                        trade = tadeList,
                        specialization = specList,
                    )
                    putExtra(IntentConstants.DATA, onBoardingData)
                })
            }
            iv_back -> {
                onBackPressed()
            }
            mBinding.tvYellowBtn -> {
                qualList?.forEachIndexed { index, element ->
                    if (qualList?.get(index)?.isSelected == true) {
                        if (qualList?.get(index)?.isUploaded == true) {
                            hasUploadedAllDoc = true
                        } else {
                            showToastShort(getString(R.string.upload_all_docs))
                            hasUploadedAllDoc = false
                            return
                        }
                    }
                }

                if (hasUploadedAllDoc == false)
                    showToastShort(getString(R.string.upload_all_docs))
                else {
                    if (!isReturn) {
                        startActivity(Intent(this, ABNActivity::class.java).apply {
                            onBoardingData = OnBoardingData(firstName = name,
                                email = email,
                                mobileNumber = phoneno,
                                password = password,
                                trade = tadeList,
                                specialization = specList,
                                qualification = qualList?.let {
                                    addQualificationData(it)
                                }
                            )
                            putExtra(IntentConstants.DATA, onBoardingData)
                        })

                    } else {
                        qualList?.let {
                            val intent=Intent()
                            intent.putExtra("doc", addQualificationDocData(it))
                            setResult(RESULT_OK,intent)
                            finish()

                        }
                    }
                }
            }
        }

    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.UPLOAD_FILE -> {
                mViewModel.imageUploadResponse.url?.let {
                    if (it.isNotEmpty()) {
                        qualList?.get(docUploadPosition)?.serverUrl = it[0]
                        qualList?.get(docUploadPosition)?.isUploaded = true
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }


            ApiCodes.GET_TRADE_LIST -> {
                mTradeViewModel.mTradeListingResponseModel.trade?.let {
                    qualList = ArrayList()
                    if (mTradeViewModel.mTradeListingResponseModel.trade!!.size > 0) {
                        mTradeViewModel.mTradeListingResponseModel.trade!!.get(0)!!.qualifications?.let {
                            qualList!!.addAll(
                                mTradeViewModel.mTradeListingResponseModel.trade!!.get(
                                    0
                                )!!.qualifications!!
                            )
                            initRecyclerView()
                        }
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun addQualificationData(data: ArrayList<Qualification>): ArrayList<com.app.core.model.registrationmodel.Qualification> {
        var qualification: com.app.core.model.registrationmodel.Qualification
        try {
            for (item in data) {
                if (item.isUploaded == true && !item.serverUrl.isNullOrBlank()) {
                    qualification = com.app.core.model.registrationmodel.Qualification(
                        qualification_id = item.id,
                        url = item.serverUrl
                    )
                    selectedQualification.add(qualification)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return selectedQualification
    }

    private fun addQualificationDocData(data: ArrayList<Qualification>): ArrayList<QualifiedDoc> {
        var qualificationDOc: QualifiedDoc
        try {
            for (item in data) {
                if (item.isUploaded == true && !item.serverUrl.isNullOrBlank()) {
                    qualificationDOc = QualifiedDoc(
                        qualification_id = item.id,
                        docName = item.name,
                        url = item.serverUrl
                    )
                    selectedQualificationDoc.add(qualificationDOc)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return selectedQualificationDoc
    }

    private fun getFileChooserIntent() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.PICKFILE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data?.data!!
                    fPAth = uri?.let { getPathFromUri(this, it) }
                    if (File(fPAth).exists()) {
                        fPAth?.let { mViewModel.hitUploadFile(it) }
                    } else {
                        showToastShort(getString(R.string.file_not_available))
                    }
                }
            }
            PermissionConstants.REQ_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    uri = data.data

                    if (uri != null) {
//                    val file = File(
//                        getCacheDir(),
//                        "IMG_" + System.currentTimeMillis() + ".jpeg"
//                    )
//                    if (!file.exists()) {
//                        file.createNewFile()
//                    }
//                    outputUri = Uri.fromFile(file)

                        fPAth = uri?.let { getPathFromUri(this, it) }
                        fPAth?.let { mViewModel.hitUploadFile(it) }
                        /*     UCrop.of(uri!!, outputUri!!)
                              .withAspectRatio(16f, 9f)
                              .start(this)

                     */
                    }
                }
            }
            PermissionConstants.REQ_CAMERA -> {
                if (resultCode == Activity.RESULT_OK ) {
                    uri = data?.data
                    if (uri == null) {
                        val photo = data?.extras?.get("data") as Bitmap
                        uri = getImageUri(photo = photo)
                    }
                    if (uri != null) {
                        fPAth = uri?.let { getPathFromUri(this, it) }
                        fPAth?.let { mViewModel.hitUploadFile(it) }
                    }
                }
            }
            /*     ImageCropper.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                       if (resultCode == Activity.RESULT_OK) {
                           fPAth = outputUri?.let { getPathFromUri(this, it) }
                           fPAth?.let { mViewModel.hitUploadFile(it) }
                       }
                   }

             */
            /*   UCrop.REQUEST_CROP -> {
                   if (resultCode == Activity.RESULT_OK) {
                       fPAth = outputUri?.let { getPathFromUri(this, it) }
                       fPAth?.let { mViewModel.hitUploadFile(it) }
                   }

               }

             */
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

    override fun onDocUpload(position: Int) {
        docUploadPosition = position
        if (qualList?.get(position)?.isSelected == true) {
            if (mViewModel.getPermissionHelper().hasPermission(
                    this,
                    permissions, PermissionConstants.PERMISSION
                )
            ) {
                CameraBottomSheet(this, this).show()
            }
        } else
            showToastShort("Please select the document first.")
    }


    override fun onCameraClicked() {
        if (mViewModel.getPermissionHelper()
                .hasPermission(this, permissionsNormal, PermissionConstants.REQ_CAMERA)
        ) {

            openCamera()

        }
    }

    /**
     * callback to observer when gallery clicked from [com.android.caretotalk.dialog.OptionsBottomSheet]
     */
    override fun onGalleryClicked() {
        if (mViewModel.getPermissionHelper()
                .hasPermission(this, permissionsNormal, PermissionConstants.REQ_GALLERY)
        ) {
            onGalleryChoose(false)
        }
    }

    override fun onFileClicked() {
        getFileChooserIntent()
    }

    override fun onDocSelected(position: Int, binding: RowitemDocumentsBinding) {
        if (qualList?.get(position)?.isSelected == true) {
            if (qualList?.get(position)?.isUploaded == true) {
                showAppPopupDialog(
                    getString(R.string.are_you_want_to_uncheck),
                    getString(R.string.yes),
                    getString(R.string.no), "",
                    {
                        qualList?.get(position)?.isSelected = false
                        qualList?.get(position)?.isUploaded = false
                        qualList?.get(position)?.serverUrl = null
                        qualList?.get(position)?.fileExt = null
                        mAdapter.notifyDataSetChanged()
                    },
                    {
                    },
                    true
                )
            } else
                qualList?.get(position)?.isSelected = false
        } else
            qualList?.get(position)?.isSelected = true
        mAdapter.notifyDataSetChanged()
    }

    private fun deleteDoc(binding: RowitemDocumentsBinding) {
        binding.cbQualification.background =
            ContextCompat.getDrawable(
                this, R.drawable.ic_checkbox_un_active
            )
        binding.tvFile.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.color_123f95
            )
        )
        binding.tvFile.background =
            ContextCompat.getDrawable(this, R.drawable.bg_round_corner_dfe5ef)
        binding.tvFile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.ivCancel.visibility = View.GONE
        binding.tvFile.setText(R.string.upload)
        binding.tvFile.alpha = 0.4f
    }


    override fun onDocCanceled(position: Int, binding: RowitemDocumentsBinding) {
        showAppPopupDialog(
            getString(R.string.are_you_want_to_delete),
            getString(R.string.yes),
            getString(R.string.no),
            getString(R.string.delete_document),
            {
                qualList?.get(position)?.isSelected = false
                qualList?.get(position)?.isUploaded = false
                qualList?.get(position)?.serverUrl = null
                qualList?.get(position)?.fileExt = null
                deleteDoc(binding)
            },
            {
            },
            true
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mViewModel.getPermissionHelper().setPermissionResult(requestCode, permissions, grantResults)
    }

    override fun permissionGiven(requestCode: Int) {
        when (requestCode) {
            PermissionConstants.PERMISSION ->
                CameraBottomSheet(this, this).show()
        }
    }


    override fun permissionCancel(requestCode: Int) {

    }

    private fun onGalleryCrop() {

        if (mViewModel.getPermissionHelper()
                .hasPermission(this, permissions, PermissionConstants.REQ_GALLERY)
        ) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val ur = intent.data

            val resInfoList = packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    ur,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            this.startActivityForResult(
                Intent.createChooser(
                    intent,
                    resources.getString(R.string.image_action)
                ), PermissionConstants.REQ_GALLERY
            )
        }

    }

}




