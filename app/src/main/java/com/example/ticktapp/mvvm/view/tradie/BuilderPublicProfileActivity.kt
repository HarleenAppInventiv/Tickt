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
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.profile.InitalProfileModel
import com.app.core.model.tradie.PortFolio
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.bumptech.glide.Glide
import com.exampl.AddEditPortfolioActivity
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.JobsSmallAdapter
import com.example.ticktapp.adapters.PortfolioEditableAdapter
import com.example.ticktapp.adapters.SpecializationAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityBuilderPublicProfileBinding
import com.example.ticktapp.dialog.GalleryCameraBottomSheet
import com.example.ticktapp.mvvm.view.builder.profile.EditBuilderBasicDetailsActivity
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.mvvm.viewmodel.ProfileViewModel
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


@Suppress("DEPRECATION")
public class BuilderPublicProfileActivity : BaseActivity(),
    OnClickListener, SpecializationAdapter.SpecListAdapterListener,
    JobsSmallAdapter.JobAdapterListener, PermissionHelper.IGetPermissionListener,
    GalleryCameraBottomSheet.CameraDialogCallBack, SwipeRefreshLayout.OnRefreshListener {
    private var tradieImg: String? = null
    private lateinit var permissionHelper: PermissionHelper
    private var builderData: InitalProfileModel? = null
    private var uri: Uri? = null
    private var fPAth: String? = null
    private lateinit var mBinding: ActivityBuilderPublicProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }
    private val mUplodaViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private var shouldReturn: Boolean = false

    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private lateinit var imageAdapter: PortfolioEditableAdapter
    private var photos: ArrayList<PortFolio>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_builder_public_profile)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        setupView()
        setObservers()
        getIntentData()
    }

    private fun getIntentData() {
        builderData = intent.getSerializableExtra("data") as InitalProfileModel
        setData()
    }


    private fun setData() {
        if (builderData != null) {
            mBinding.srLayout.visibility = View.VISIBLE
            mBinding.tvName.text = builderData?.builderName
            mBinding.tvDetails.text = builderData?.position
            mBinding.tvCompnayName.text = builderData?.companyName
            if (builderData?.aboutCompany != null && builderData?.aboutCompany!!.isNotEmpty() && builderData?.aboutCompany != "null") {
                mBinding.tvDesc.text = builderData?.aboutCompany
                mBinding.linAbout.visibility = View.VISIBLE
                mBinding.tvDesc.visibility = View.VISIBLE
            } else {
                mBinding.linAbout.visibility = View.GONE
                mBinding.tvDesc.text = ""
                mBinding.tvDesc.visibility = View.GONE
            }
            if (builderData?.jobCompletedCount!! > 1) {
                mBinding.tvJobCompleted.text = getString(R.string.jobs_completed)
            } else {
                mBinding.tvJobCompleted.text = getString(R.string.job_completed)
            }
            mBinding.tvJobCompleted.text = builderData?.jobCompletedCount!!.toInt().toString()
            mBinding.tvRateCount.text = builderData?.ratings.toString()
            if (builderData?.reviewsCount!! > 1) {
                mBinding.tvReviewCount.text =
                    builderData?.reviewsCount?.toInt()
                        .toString() + " " + getString(R.string.reviews)
            } else {
                mBinding.tvReviewCount.text =
                    builderData?.reviewsCount?.toInt().toString() + " " + getString(R.string.review)
            }
            Glide.with(mBinding.root.context).load(builderData?.builderImage)
                .placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile)
                .into(mBinding.ivUserProfile)
            tradieImg = builderData?.builderImage


            setPortfolioAdapter(false)
            if (!builderData?.aboutCompany.isNullOrEmpty())
                mBinding.tvEditAbout.visibility = View.VISIBLE
            else {
                mBinding.linAbout.visibility = View.VISIBLE
                mBinding.tvAddBio.visibility = View.VISIBLE
                mBinding.tvEditAbout.visibility = View.GONE

            }
            if (builderData?.portfolio.isNullOrEmpty()) {
                mBinding.llPortfolioAddData.visibility = View.VISIBLE
            } else {
                for (data in imageAdapter.photos) {
                    data.isEditable = true

                }
                if (imageAdapter.itemCount < 6) {
                    val data = PortFolio()
                    data.isEditable = false
                    photos?.add(data)

                }
                imageAdapter.notifyDataSetChanged()

            }

        }
    }


    private fun setPortfolioAdapter(isEditable: Boolean) {
        photos?.clear()
        builderData?.portfolio?.let {
            for (item in builderData?.portfolio!!) {
                if (!item.portfolioId.isNullOrEmpty())
                    item.isEditable = isEditable
            }
            photos?.addAll(it)
        }
        if (isEditable && getPortfolioCount() < 6)
            photos?.add(PortFolio())

        if (getPortfolioCount() > 0)
            mBinding.llPortfolioData.visibility = View.VISIBLE
        if (getPortfolioCount() > 1)
            mBinding.tvPhotos.text =
                getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(R.string.jobs) + ")"
        else
            mBinding.tvPhotos.text =
                getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(R.string.job) + ")"


        mBinding.rvPhotos.adapter?.notifyDataSetChanged()
    }

    private fun getPortfolioCount(): Int {
        var count = 0
        if (photos != null) {

            photos?.forEach {
                if (!it.portfolioId.isNullOrEmpty()) {
                    count++
                }
            }
        }
        return count
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
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setupView() {
        mBinding.srLayout.visibility = View.GONE
        val layountManager = GridLayoutManager(this, 3)
        photos = ArrayList()
        imageAdapter = photos?.let { PortfolioEditableAdapter(this, true, it) }!!
        mBinding.rvPhotos.layoutManager = layountManager
        mBinding.rvPhotos.adapter = imageAdapter
        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
    }


    private fun listener() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
        mBinding.ivCam.bringToFront()
        mBinding.tradieProfileIvBack.setOnClickListener { onBackPressed() }
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.ivCam.setOnClickListener(this)
        mBinding.tvEditAbout.setOnClickListener(this)
        mBinding.tvAddBio.setOnClickListener(this)
        mBinding.tvSave.setOnClickListener(this)
        mBinding.tvAddPortfolio.setOnClickListener(this)
        mBinding.tvEditBasic.setOnClickListener(this)
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(mUplodaViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mUplodaViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.BASIC_PROFILE, ApiCodes.UPLOAD_FILE, ApiCodes.BUILDER_EDIT_PROFILE -> {
                mBinding.srLayout.isRefreshing = false
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.BASIC_PROFILE -> {
                mBinding.srLayout.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mViewModel.inItProfileModel.let {
                    builderData = it
                }
                setData()
            }
            ApiCodes.UPLOAD_FILE -> {
                mUplodaViewModel.imageUploadResponse.url?.let {
                    if (it.isNotEmpty()) {
                        tradieImg = it.get(0)
                        builderData?.builderImage = tradieImg
                        Glide.with(mBinding.root.context).load(fPAth)
                            .placeholder(R.drawable.placeholder_profile)
                            .error(R.drawable.placeholder_profile)
                            .into(mBinding.ivUserProfile)
                    }
                }
            }
            ApiCodes.BUILDER_EDIT_PROFILE -> {
                builderData?.builderImage = tradieImg
                builderData?.aboutCompany = mBinding.tvDesc.text.toString()
                builderData?.builderName = mBinding.tvName.text.toString()
                builderData?.areasOfSpecialization?.tradeData?.clear()
                builderData?.areasOfSpecialization?.specializationData?.clear()
                shouldReturn = true
                onBackPressed()
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.tvAddPortfolio -> {
                startActivityForResult(
                    Intent(this, AddEditPortfolioActivity::class.java).putExtra(
                        "isBuilder",
                        true
                    ),
                    PermissionConstants.ADD_PORTFOLIO
                )
            }

            mBinding.tvEditBasic -> {
                startActivityForResult(
                    Intent(this, EditBuilderBasicDetailsActivity::class.java),
                    PermissionConstants.EDIT_BASIC_DETAILS
                )
            }
            mBinding.ivCam -> {
                if (permissionHelper.hasPermission(
                        this,
                        permissions, PermissionConstants.PERMISSION
                    )
                ) {

                    GalleryCameraBottomSheet(this, this).show()
                }
            }
            mBinding.tvSave -> {

                var aboutYou = mBinding.tvDesc.text.toString()
                if (aboutYou != builderData?.aboutCompany) {
                    addedABoutCompanyMoEngage()
                    addedABoutCompanyMixPanel()
                }

                val params = HashMap<String, Any>()
                params.put("fullName", builderData?.builderName.toString())
                params.put("position", builderData?.position.toString())
                params.put("companyName", builderData?.companyName.toString())
                if (!builderData?.builderImage.isNullOrEmpty())
                    params.put("userImage", builderData?.builderImage.toString())
                if (!mBinding.tvDesc.text.toString().trim().isNullOrEmpty())
                    params.put("aboutCompany", mBinding.tvDesc.text.toString())
                mViewModel.builderEditProfile(params)
            }
            mBinding.tvAddBio -> {
                val intent = Intent(this, AboutusActivity::class.java)
                intent.putExtra("about_you", mBinding.tvDesc.text.toString())
                intent.putExtra("isBuilder", true)
                startActivityForResult(intent, PermissionConstants.ABOUT_YOU_CONTENT)
            }
            mBinding.tvEditAbout -> {
                mBinding.tvAddBio.performClick()
            }
        }
    }

    override fun onSpecCLick(position: Int) {
    }

    override fun onJobClick(position: Int) {
    }


    override fun permissionGiven(requestCode: Int) {
        when (requestCode) {
            PermissionConstants.PERMISSION ->
                GalleryCameraBottomSheet(this, this).show()
        }
    }

    override fun permissionCancel(requestCode: Int) {
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
                        fPAth?.let {
                            mUplodaViewModel.hitUploadFile(fPAth!!)
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
                        fPAth?.let {
                            mUplodaViewModel.hitUploadFile(fPAth!!)
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
                    fPAth?.let {
                        mUplodaViewModel.hitUploadFile(fPAth!!)
                    }

                }
            }
            PermissionConstants.ABOUT_YOU_CONTENT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val aboutYou = data.getStringExtra("about_you")
                    mBinding.linAbout.visibility = View.VISIBLE
                    mBinding.tvEditAbout.visibility = View.VISIBLE
                    mBinding.tvAddBio.visibility = View.GONE
                    mBinding.tvDesc.text = aboutYou
                    mBinding.tvDesc.visibility = View.VISIBLE


                }
            }
            PermissionConstants.ADD_PORTFOLIO -> {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    val jobName = data.getStringExtra("jobName")
                    val jobDesc = data.getStringExtra("jobDescription")
                    val portfolioUrl = data.getStringArrayListExtra("url")
                    val portfolioId = data.getStringExtra("portfolioId")
                    val portfolioData = PortFolio()
                    portfolioData.jobName = jobName
                    portfolioData.jobDescription = jobDesc
                    portfolioData.portfolioId = portfolioId
                    portfolioData.portfolioImage = portfolioUrl
                    portfolioData.isEditable = true
                    builderData?.portfolio?.add(portfolioData)
                    setPortfolioAdapter(true)
                    mBinding.llPortfolioAddData.visibility = View.GONE
                    shouldReturn = true
                }
            }

            PermissionConstants.EDIT_PORTFOLIO -> {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    val isDeleted = data.getBooleanExtra("isDeleted", false)
                    if (isDeleted) {
                        val portfolioId = data.getStringExtra("portfolioId")
                        for (item in imageAdapter.photos) {
                            if (item.portfolioId.equals(portfolioId)) {
                                imageAdapter.photos.remove(item)
                                builderData?.portfolio?.remove(item)
                                photos?.remove(item)
                                break
                            }
                        }
                        if (imageAdapter.itemCount == 1 && imageAdapter.photos.get(0).portfolioId.isNullOrEmpty()) {
                            imageAdapter.photos.removeAt(0)
                        } else {
                            if (!imageAdapter.photos.get(imageAdapter.itemCount - 1).portfolioId.isNullOrBlank()) {
                                setPortfolioAdapter(true)
                            }
                        }

                        imageAdapter.notifyDataSetChanged()
                        checkPortfolioCount()
                    } else {
                        val jobName = data.getStringExtra("jobName")
                        val jobDesc = data.getStringExtra("jobDescription")
                        val portfolioUrl = data.getStringArrayListExtra("url")
                        val portfolioId = data.getStringExtra("portfolioId")
                        for (item in imageAdapter.photos) {
                            if (item.portfolioId.equals(portfolioId)) {
                                item.portfolioImage = portfolioUrl
                                item.jobName = jobName
                                item.jobDescription = jobDesc
                                item.portfolioId = portfolioId
                                item.isEditable = true
                                break
                            }
                        }
                        imageAdapter.notifyDataSetChanged()
                    }
                    shouldReturn = true
                }
            }
            PermissionConstants.EDIT_BASIC_DETAILS -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val name = data.getStringExtra("name")
                    val companyName = data.getStringExtra("companyName")
                    val position = data.getStringExtra("position")

                    builderData?.builderName = name
                    builderData?.companyName = companyName
                    builderData?.position = position
                    mBinding.tvName.setText(name)
                    mBinding.tvCompnayName.setText(companyName)
                    mBinding.tvDetails.setText(position)
                    shouldReturn = true
                }
            }
        }
    }


    private fun addedABoutCompanyMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_ADDED_INFO_ABOUT_COMPANY,
            signUpProperty
        )
    }

    private fun addedABoutCompanyMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_ADDED_INFO_ABOUT_COMPANY,
            props
        )
    }

    override fun onBackPressed() {
        if (shouldReturn) {
            setResult(RESULT_OK)
            finish()
        } else
            super.onBackPressed()
    }

    private fun checkPortfolioCount() {
        if (imageAdapter.itemCount == 0) {
            mBinding.llPortfolioData.visibility = View.GONE
            mBinding.llPortfolioAddData.visibility = View.VISIBLE
        } else {
            if (getPortfolioCount() > 0)
                mBinding.llPortfolioData.visibility = View.VISIBLE
            if (getPortfolioCount() > 1)
                mBinding.tvPhotos.text =
                    getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(
                        R.string.jobs
                    ) + ")"
            else
                mBinding.tvPhotos.text =
                    getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(
                        R.string.job
                    ) + ")"
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

    override fun onRefresh() {
        mBinding.srLayout.isRefreshing = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.setPermissionResult(requestCode, permissions, grantResults)
    }
}