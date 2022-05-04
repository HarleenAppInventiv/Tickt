package com.example.ticktapp.mvvm.view.tradie

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.core.preferences.PreferenceManager
import com.app.core.util.*

import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityTradieBankDetailsBinding
import com.example.ticktapp.dialog.GalleryCameraBottomSheet
import com.app.core.model.jobmodel.Photos
import com.app.core.model.jobmodel.TradieBankDetails
import com.example.ticktapp.mvvm.view.DoneActivity
import com.example.ticktapp.mvvm.viewmodel.BankDetailsViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.example.ticktapp.util.MoEngageUtils
import com.moengage.core.Properties
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_tradie_bank_details.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TradieBankDetailsActivity : BaseActivity(),PermissionHelper.IGetPermissionListener,
    GalleryCameraBottomSheet.CameraDialogCallBack {
    private var isForResult: Boolean = false
    private var shouldSubmit: Boolean = false
    private var bankDetails: TradieBankDetails? = null
    private lateinit var files: java.util.ArrayList<String>
    private lateinit var mBinding: ActivityTradieBankDetailsBinding
    private var jobId: String = ""
    private var desc: String? = null
    private var jobName: String = ""
    private var milestoneId: String = ""
    private var jobCount: String = ""
    private var amount: String = ""
    private var payType: String = ""
    private var hours: String = ""
    private var isJobCompleted: Boolean = false
    private val mViewModel by lazy { ViewModelProvider(this).get(BankDetailsViewModel::class.java) }
    private lateinit var permissionHelper: PermissionHelper
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private var uri: Uri? = null
    private var fPAth: String? = null
    private val images by lazy { ArrayList<Photos>() }
    private val imageUrl by lazy { ArrayList<String>() }
    private var isEditableDetails:Boolean=true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_bank_details)
        setUpListeners()
        getIntentData()
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        mViewModel.getBankDetails()
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModel.getValidationLiveData().observe(this, {
            mBinding.tvNameError.visibility = View.GONE
            mBinding.tvAccntNoError.visibility = View.GONE
            mBinding.tvBsbError.visibility = View.GONE
            when (it.type) {
                ValidationsConstants.ACCOUNT_NAME_EMPTY -> {
                    mBinding.tvNameError.visibility = View.VISIBLE
                    mBinding.tvNameError.text = it.message
                }
                ValidationsConstants.ACCOUNT_NUMBER_EMPTY, ValidationsConstants.ACCOUNT_NUMBER_LENGTH -> {
                    mBinding.tvAccntNoError.visibility = View.VISIBLE
                    mBinding.tvAccntNoError.text = it.message
                }
                ValidationsConstants.BSB_EMPTY, ValidationsConstants.BSB_NUMBER_LENGTH -> {
                    mBinding.tvBsbError.visibility = View.VISIBLE
                    mBinding.tvBsbError.text = it.message
                }
            }
        })
    }

    private fun getIntentData() {
        desc = intent.getStringExtra(IntentConstants.DESCRIPTION)
        jobId = intent.getStringExtra(IntentConstants.JOB_ID).toString()
        jobName = intent.getStringExtra(IntentConstants.JOB_NAME).toString()
        milestoneId = intent.getStringExtra(IntentConstants.MILESTONE_ID).toString()
        isJobCompleted = intent.getBooleanExtra(IntentConstants.IS_JOB_COMPLETED, false)
        jobCount = intent.getStringExtra(IntentConstants.JOB_COUNT).toString()
        amount = intent.getStringExtra(IntentConstants.AMOUNT).toString()
        payType = intent.getStringExtra(IntentConstants.PAY_TYPE).toString()
        hours = intent.getStringExtra("hours").toString()
        if (intent.hasExtra(IntentConstants.IMAGES))
            files = intent.getSerializableExtra(IntentConstants.IMAGES) as ArrayList<String>
        else
            files = ArrayList<String>()
        if (intent.hasExtra("bankDetails")) {
            bankDetails = intent.getSerializableExtra("bankDetails") as TradieBankDetails
            bankDetails?.let {
                setData(it)
                mBinding.ilHeader.ivEdit.visibility = View.VISIBLE
                mBinding.btnBnkAccnt.text = getString(R.string.submit)
                enableDisable(false)

            }
            shouldSubmit = true
        } else {
            mBinding.ilHeader.ivEdit.visibility = View.GONE
            mBinding.btnBnkAccnt.text = getString(R.string.save)

        }
        mBinding.ilHeader.tvTitle.visibility = View.GONE
        mBinding.ilHeader.ivBack.visibility = View.INVISIBLE
        mBinding.ilHeader.ivBack1.visibility = View.VISIBLE
    }

    private fun setData(tradieBankDetails: TradieBankDetails) {
        mBinding.edtActName.setText(tradieBankDetails.account_name)
        mBinding.edtActNo.setText(tradieBankDetails.account_number)
        mBinding.edtBsbNo.setText(tradieBankDetails.bsb_number)
    }

    private fun enableDisable(isEnable: Boolean) {
        isEditableDetails=isEnable
        mBinding.edtBsbNo.isEnabled = isEnable
        mBinding.edtActName.isEnabled = isEnable
        mBinding.edtActNo.isEnabled = isEnable

    }

    private fun setUpListeners() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)

        mBinding.btnBnkAccnt.setOnClickListener {
            val jsonObject = com.google.gson.JsonObject()
            jsonObject.addProperty("account_name", mBinding.edtActName.text.toString())
            jsonObject.addProperty("account_number", mBinding.edtActNo.text.toString())
            jsonObject.addProperty("bsb_number", mBinding.edtBsbNo.text.toString())
            jsonObject.addProperty("userId", PreferenceManager.getString(PreferenceManager.USER_ID))
            mViewModel.addBankDetails(jsonObject, !isEditableDetails)
        }
        mBinding.ilHeader.ivBack1.setOnClickListener {
            onBackPressed()
        }
        mBinding.edtBsbNo.addTextChangedListener(GenricWatcher(mBinding.edtBsbNo))
        mBinding.edtActNo.addTextChangedListener(GenricWatcher(mBinding.edtActNo))
        mBinding.edtActName.addTextChangedListener(GenricWatcher(mBinding.edtActName))
        mBinding.tvIdVerification.setOnClickListener {
            startActivity(Intent(this,VerificationProcessActivity::class.java))
        }
        mBinding.btnAddId.setOnClickListener {
            if(mViewModel.mTradieBankModel?.accountVerified == false){
                if (permissionHelper.hasPermission(
                        this,
                        permissions, PermissionConstants.PERMISSION
                    )
                ) {

                    GalleryCameraBottomSheet(this, this).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isForResult) {
            val intent = Intent()
            intent.putExtra("isForFinish", false)
            setResult(RESULT_OK, intent)
            finish()
        } else
            super.onBackPressed()

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.ADD_BANK_DETAILS, ApiCodes.REMOVE_BANK_DETAILS, ApiCodes.MARK_JOB_COMPLETE -> {
                showToastShort(exception.message)
            }

        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {

        when (apiCode) {
            ApiCodes.GET_BANK_DETAILS -> {
                if (mViewModel.mTradieBankModel != null) {
                    mBinding.edtActName.setText(mViewModel.mTradieBankModel!!.account_name)
                    mBinding.edtActNo.setText(mViewModel.mTradieBankModel!!.account_number)
                    mBinding.edtBsbNo.setText(mViewModel.mTradieBankModel!!.bsb_number)
                    if(mViewModel.mTradieBankModel!!.accountVerified == true){
                        mBinding.btnAddId.text = getString(R.string.id_verified)
                        mBinding.btnAddId.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check,0,0,0)
                    }
                }
            }

            ApiCodes.ADD_BANK_DETAILS -> {

                if (mViewModel.mTradieBankModel != null) {
                    addedBankDetailsEngage()
                    mBinding.btnBnkAccnt.text = getString(R.string.submit)
                    mBinding.ilHeader.ivEdit.visibility = View.VISIBLE
                    showToastShort(msg)
                    bankDetails = mViewModel.mTradieBankModel
                    isForResult = true
                    shouldSubmit = true
                }
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun addedBankDetailsEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_TRADIE_ADDED_BANK_DETAILS,
            signUpProperty
        )

    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.let {
                    data.putExtra("isForFinish", true)
                    setResult(RESULT_OK, data)
                    finish()
                }
            }
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

    inner class GenricWatcher(var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }


        override fun afterTextChanged(p0: Editable?) {
            when (view.id) {
                R.id.edt_ActName -> {
                    mViewModel.mAccountNameEditText = p0.toString()
                    mBinding.tvNameError.visibility = View.GONE
                }
                R.id.edt_ActNo -> {
                    mViewModel.mAccountNumberEditText = p0.toString()
                    mBinding.tvAccntNoError.visibility = View.GONE
                }
                R.id.edt_BsbNo -> {
                    mViewModel.mBsbNumberEditText = p0.toString()
                    val sb = StringBuilder(p0)
                    mBinding.tvBsbError.visibility = View.GONE
                    if (mBinding.edtBsbNo.text.toString().length == 4) {
                        if (mBinding.edtBsbNo.text.toString().contains("-"))
                            return
                        sb.insert(3, "-")
                        mBinding.edtBsbNo.setText(sb.toString())
                        mBinding.edtBsbNo.edt_BsbNo.setSelection(mBinding.edtBsbNo.text.toString().length)
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
                        val photos = Photos(1, fPAth)
                        images.add(0, photos)
                        startActivity(Intent(this, DoneActivity::class.java)
                            .apply {
                                putExtra(IntentConstants.FROM, Constants.BANK)
                            })
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
                        val photos = Photos(1, fPAth)
                        images.add(0, photos)
                        startActivity(Intent(this, DoneActivity::class.java)
                            .apply {
                                putExtra(IntentConstants.FROM, Constants.BANK)
                            })
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
                    val photos = Photos(1, fPAth)
                    images.add(0, photos)
                    startActivity(Intent(this, DoneActivity::class.java)
                        .apply {
                            putExtra(IntentConstants.FROM, Constants.BANK)
                        })
                }
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