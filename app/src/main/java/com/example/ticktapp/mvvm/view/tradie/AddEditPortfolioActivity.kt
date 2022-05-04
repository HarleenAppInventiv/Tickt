package com.exampl


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
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.model.jobmodel.Photos
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.UploadImagesAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityAddPortfolioBinding
import com.example.ticktapp.dialog.GalleryCameraBottomSheet
import com.example.ticktapp.mvvm.viewmodel.PortfolioViewModel
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

class AddEditPortfolioActivity : BaseActivity(), PermissionHelper.IGetPermissionListener,
    GalleryCameraBottomSheet.CameraDialogCallBack, View.OnClickListener,
    UploadImagesAdapter.ClickListener {
    private var isReturn: Boolean = false
    private var jobName: String = ""
    private var jobDesc: String = ""
    private var portfolioId: String = ""
    private lateinit var adapter: UploadImagesAdapter
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var mBinding: ActivityAddPortfolioBinding
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private var uri: Uri? = null
    private var fPAth: String? = null
    private val images by lazy { ArrayList<Photos>() }
    private val imageUrl by lazy { ArrayList<String>() }

    private val mViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private val mPortfolioViewModel by lazy { ViewModelProvider(this).get(PortfolioViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_portfolio)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        getIntentData()
        setObservers()
        setupView()
    }

    private fun getIntentData() {
        if (intent.hasExtra("isBuilder") && intent.getBooleanExtra("isBuilder", false)) {
            mBinding.tvDesc.text = getString(R.string.builder_portfolio_text)
        }
        if (intent.hasExtra("isReturn")) {
            isReturn = intent.getBooleanExtra("isReturn", false)
            jobName = intent.getStringExtra("jobName").toString()
            jobDesc = intent.getStringExtra("jobDescription").toString()
            portfolioId = intent.getStringExtra("portfolioId").toString()
            val photos = intent.getSerializableExtra("photos") as ArrayList<String>
            if (photos != null) {
                photos.forEach {
                    images.add(Photos(1, it))
                }
            }
            mBinding.jobNameDesc.setText(jobName)
            mBinding.jobDescEd.setText(jobDesc)
        }
        if (images.size > 0) {
            mBinding.ivAddImageVideo.visibility = View.GONE
            mBinding.tvAddPhoto.visibility = View.GONE
            if (images.size != 6)
                images.add(Photos(0, ""))
        }
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
        setBaseViewModel(mPortfolioViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mPortfolioViewModel.getResponseObserver().observe(this, this)
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

                GalleryCameraBottomSheet(this, this).show()
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
            if (!valid())
                return@setOnClickListener
            val image = ArrayList<String>()
            images.forEach {
                if (it.link?.length!! > 0 && !it.link!!.contains("http")) {
                    image.add(it.link.toString())
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
                    val params = HashMap<String, Any>()
                    params.put("jobName", mBinding.jobNameDesc.text.toString())
                    params.put("jobDescription", mBinding.jobDescEd.text.toString())
                    params.put("url", image)
                    params.put("portfolioId", portfolioId)
                    mPortfolioViewModel.editPortfolio(params)
                    imageUrl.clear()
                    imageUrl.addAll(image)

                }
            }


        }

        mBinding.jobDescEd.addTextChangedListener(GenricWatcher(mBinding.jobDescEd))
        mBinding.jobNameDesc.addTextChangedListener(GenricWatcher(mBinding.jobNameDesc))
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
            onGalleryJustImageChoose(true)
        }
    }

    override fun onVideoCapture() {

    }

    override fun onFileClicked() {


    }

    inner class GenricWatcher(var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }


        override fun afterTextChanged(p0: Editable?) {
            when (view) {
                mBinding.jobNameDesc -> {
                    mBinding.tvJobnameerror.visibility = View.INVISIBLE
                }
                mBinding.jobDescEd -> {
                    mBinding.tvJobDescError.visibility = View.INVISIBLE
                }


            }
        }

    }

    private fun enableDisableSubmit() {
        /* if (getMediaCount() > 0 && !mBinding.jobDescEd.text.toString().trim()
                 .isEmpty() && !mBinding.jobNameDesc.text.toString().trim().isEmpty()
         ) {
             mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_btn_yellow)
             mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_161d4a))
             mBinding.btnSubmit.isEnabled = true
         } else {
             mBinding.btnSubmit.setBackgroundResource(R.drawable.bg_drawable_rect_dfe5ef)
             mBinding.btnSubmit.setTextColor(resources.getColor(R.color.color_99a4b6))
             mBinding.btnSubmit.isEnabled = false
         }*/
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
                        val dataImage = ArrayList<String>()
                        if (isReturn) {
                            images.forEachIndexed { _, newItem ->
                                if (newItem.link?.contains("http") == true) {
                                    dataImage.add(newItem.link!!)
                                }
                            }


                        }
                        dataImage.addAll(it)
                        val params = HashMap<String, Any>()
                        params.put("jobName", mBinding.jobNameDesc.text.toString())
                        params.put("jobDescription", mBinding.jobDescEd.text.toString())
                        params.put("url", dataImage)
                        imageUrl.clear()
                        imageUrl.addAll(dataImage)
                        if (!isReturn)
                            mPortfolioViewModel.addPortfolio(params)
                        else {
                            params.put("portfolioId", portfolioId)
                            mPortfolioViewModel.editPortfolio(params)
                        }

                    }
                }
            }
            ApiCodes.ADD_PORTFOLIO -> {
                mPortfolioViewModel.portfolioModel.let {

                    addPortfolioMoEngage()   //added portfolio mo engage
                    addPortfolioMixPanel()

                    val intent = Intent()
                    intent.putExtra("jobName", mBinding.jobNameDesc.text.toString())
                    intent.putExtra("jobDescription", mBinding.jobDescEd.text.toString())
                    intent.putExtra("url", it.portfolioImage as ArrayList<String>)
                    intent.putExtra("portfolioId", it.portfolioId)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
            ApiCodes.EDIT_PORTFOLIO -> {
                val intent = Intent()
                intent.putExtra("jobName", mBinding.jobNameDesc.text.toString())
                intent.putExtra("jobDescription", mBinding.jobDescEd.text.toString())
                intent.putExtra("url", imageUrl)
                intent.putExtra("portfolioId", portfolioId)
                setResult(RESULT_OK, intent)
                finish()
            }


        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.UPLOAD_FILE, ApiCodes.ADD_PORTFOLIO, ApiCodes.EDIT_PORTFOLIO -> {
                showToastShort(exception.message)
            }
        }
    }

    private fun addPortfolioMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_ADDED_PORTFOLIO,
            signUpProperty
        )
    }

    private fun addPortfolioMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_ADDED_PORTFOLIO,
            props
        )
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
                GalleryCameraBottomSheet(this, this).show()
        }
    }

    override fun permissionCancel(requestCode: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            PermissionConstants.REQ_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    uri = data.data
                    try {
                        multipleImages(data)
                    } catch (e: Exception) {

                    }
/*
                if (uri != null) {
                        fPAth = uri?.let { FileUtils.getPathFromUri(this, it) }
                        var destinationFileName = System.currentTimeMillis().toString() + ""
                        destinationFileName += ".jpeg"
                        val file = File(cacheDir, destinationFileName)
                        if (!file.exists()) {
                            try {
                                file.createNewFile()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        if (images.size == 0) {
                            val photos = Photos(0, "")
                            images.add(photos)
                        }
                        if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                            mBinding.ivAddImageVideo.visibility = View.GONE
                            mBinding.tvAddPhoto.visibility = View.GONE
                        }
                        val photos = Photos(1, fPAth)
                        images.add(0, photos)
                        adapter.notifyDataSetChanged()
                        enableDisableSubmit()

                        if (getMediaCount() == 6) {
                            images.removeAt(images.size - 1)
                            adapter.notifyDataSetChanged()
                        }
                        UCrop.of(
                            Uri.fromFile(File(fPAth)), Uri.fromFile(
                                File(
                                    cacheDir, destinationFileName
                                )
                            )
                        ).withAspectRatio(1f, 1f)
                            .start(this)
                }*/
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
                        var destinationFileName = System.currentTimeMillis().toString() + ""
                        destinationFileName += ".jpeg"
                        val file = File(cacheDir, destinationFileName)
                        if (!file.exists()) {
                            try {
                                file.createNewFile()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        if (images.size == 0) {
                            val photos = Photos(0, "")
                            images.add(photos)
                        }
                        if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                            mBinding.ivAddImageVideo.visibility = View.GONE
                            mBinding.tvAddPhoto.visibility = View.GONE
                        }
                        val photos = Photos(1, fPAth)
                        images.add(0, photos)
                        adapter.notifyDataSetChanged()
                        enableDisableSubmit()

                        if (getMediaCount() == 6) {
                            images.removeAt(images.size - 1)
                            adapter.notifyDataSetChanged()
                        }
                        /* UCrop.of(
                             Uri.fromFile(File(fPAth)), Uri.fromFile(
                                 File(
                                     cacheDir, destinationFileName
                                 )
                             )
                         ).withAspectRatio(1f, 1f)
                             .start(this)*/
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
                        mBinding.tvAddPhoto.visibility = View.GONE
                    }
                    val photos = Photos(1, fPAth)
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

    private fun multipleImages(data: Intent) {
        if (data.getClipData() != null) {
            var count = data.clipData!!.itemCount
            for (i in 0..count - 1) {
                var imageUri: Uri = data.clipData!!.getItemAt(i).uri
                var fPAth = imageUri?.let { FileUtils.getPathFromUri(this, it) }

                var destinationFileName = System.currentTimeMillis().toString() + ""
                destinationFileName += ".jpeg"
                val file = File(cacheDir, destinationFileName)
                if (!file.exists()) {
                    try {
                        file.createNewFile()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (images.size == 0) {
                    val photos = Photos(0, "")
                    images.add(photos)
                }
                if (mBinding.ivAddImageVideo.visibility == View.VISIBLE) {
                    mBinding.ivAddImageVideo.visibility = View.GONE
                    mBinding.tvAddPhoto.visibility = View.GONE
                }
                val photos = Photos(1, fPAth)
                images.add(0, photos)
                adapter.notifyDataSetChanged()
                enableDisableSubmit()

                if (getMediaCount() == 6) {
                    images.removeAt(images.size - 1)
                    adapter.notifyDataSetChanged()
                }
            }
        } else if (data.getData() != null) {
            var imagePath: String = data.data!!.path.toString()
            val e = Log.e("imagePath", imagePath);
        }
    }

    private fun getMediaCount(): Int {
        var count = 0
        if (images != null) {

            images.forEach {
                if (it.mediaType == 1) {
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
            getContentResolver(),
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
        if (adapter.itemCount == 0) {
            mBinding
                .ivAddImageVideo.visibility = View.VISIBLE
            mBinding
                .tvAddPhoto.visibility = View.VISIBLE
        }

    }

    private fun valid(): Boolean {

        mBinding.tvJobDescError.visibility = View.INVISIBLE
        mBinding.tvJobnameerror.visibility = View.INVISIBLE

        if (getMediaCount() == 0) {
            showToastShort("Please choose image")
        } else if (mBinding.jobNameDesc.text.toString().trim().isEmpty()) {
            mBinding.tvJobnameerror.visibility = View.VISIBLE
            mBinding.tvJobnameerror.setText(getString(R.string.please_enter_job_name))
            return false
        }
//        else if (mBinding.jobDescEd.text.toString().trim().isEmpty()) {
//            mBinding.tvJobDescError.visibility = View.VISIBLE
//            mBinding.tvJobDescError.setText(getString(R.string.please_enter_job_desc))
//            return false
//        }
        return true
    }
}

