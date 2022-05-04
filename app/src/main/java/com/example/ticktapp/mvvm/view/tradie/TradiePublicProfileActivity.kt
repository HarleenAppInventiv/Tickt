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
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.TradeData
import com.app.core.model.tradie.BuilderModel
import com.app.core.model.tradie.PortFolio
import com.app.core.model.tradie.ReviewData
import com.app.core.model.tradie.VouchesData
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.IntentConstants
import com.bumptech.glide.Glide
import com.exampl.AddEditPortfolioActivity
import com.example.ticktapp.R
import com.example.ticktapp.adapters.*
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.ActivityTradiePublicProfileBinding
import com.example.ticktapp.dialog.GalleryCameraBottomSheet
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.TradeActivity
import com.example.ticktapp.mvvm.view.builder.ReviewListActivity
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.mvvm.viewmodel.TradieProfileViewModel
import com.example.ticktapp.permissionhelper.PermissionHelper
import com.example.ticktapp.util.FileUtils
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


@Suppress("DEPRECATION")
public class TradiePublicProfileActivity : BaseActivity(),
    OnClickListener, SpecializationAdapter.SpecListAdapterListener,
    JobsSmallAdapter.JobAdapterListener, PermissionHelper.IGetPermissionListener,
    GalleryCameraBottomSheet.CameraDialogCallBack, SwipeRefreshLayout.OnRefreshListener,
    SpecializationMixSmallAdapter.OnSpecializationClicked {
    private var shouldReturn: Boolean = false
    private var tradieImg: String? = null
    private lateinit var permissionHelper: PermissionHelper
    private var isEditableMode: Boolean = false
    private var forPublicInfo: Boolean = false
    private lateinit var data: JobRecModel
    private var tradieData: BuilderModel? = null
    private var uri: Uri? = null
    private var fPAth: String? = null
    private lateinit var mBinding: ActivityTradiePublicProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieProfileViewModel::class.java) }
    private val tradeData: ArrayList<TradeData> = ArrayList()
    private val specializatinData: ArrayList<SpecialisationData> = ArrayList()
    private val reviewData: ArrayList<ReviewData> = ArrayList()
    private val voucherData: ArrayList<VouchesData> = ArrayList()
    private val mUplodaViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private val TAG: String = "showMoreData"
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var vounchesAdapter: VounchesAdapter
    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )
    private lateinit var imageAdapter: PortfolioEditableAdapter
    private var photos: ArrayList<PortFolio>? = null
    private var photosHolderList: ArrayList<PortFolio>? = ArrayList()

    companion object {
        public var specializatinDataEditable: ArrayList<SpecialisationData> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_public_profile)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        setupView()
        setObservers()
        getIntentData()
        mBinding.linMain.visibility = View.GONE
        mViewModel.getTradiePublicProfile(true)
    }

    private fun getIntentData() {
        forPublicInfo = intent.getBooleanExtra("isEditable", false)


    }

    private fun setData() {
        if (tradieData != null) {
            mBinding.tvName.text = tradieData?.builderName
            mBinding.tvCompnayName.text = tradieData?.businessName
            mBinding.tvDetails.text = tradieData?.position
            if (tradieData?.areasOfSpecialization?.tradeData != null && tradieData?.areasOfSpecialization?.tradeData?.size!! > 0) {
                mBinding.tvDetails.text =
                    tradieData?.areasOfSpecialization?.tradeData?.get(0)?.tradeName
            }
            if (tradieData?.about != null && tradieData?.about!!.length > 0) {
                mBinding.tvDesc.text = tradieData?.about
                mBinding.linAbout.visibility = View.VISIBLE
            } else {
                mBinding.linAbout.visibility = View.GONE
                mBinding.tvDesc.text = ""
            }
            mBinding.tvJobCompleted.text = tradieData?.jobCompletedCount!!.toInt().toString()
            mBinding.tvRateCount.text = tradieData?.ratings.toString()
            if (tradieData?.reviewsCount!! > 1) {
                mBinding.tvReviewCount.text =
                    tradieData?.reviewsCount?.toInt().toString() + " " + getString(R.string.reviews)
            } else {
                mBinding.tvReviewCount.text =
                    tradieData?.reviewsCount?.toInt().toString() + " " + getString(R.string.review)
            }
            Glide.with(mBinding.root.context).load(tradieData?.builderImage)
                .placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile)
                .into(mBinding.ivUserProfile)
            tradieImg = tradieData?.builderImage

            tradeData.clear()
            specializatinData.clear()
            tradieData?.areasOfSpecialization?.tradeData?.let { tradeData.addAll(it) }
            tradieData?.areasOfSpecialization?.specializationData?.let {
                specializatinDataEditable.clear()
                specializatinDataEditable.addAll(it)
            }

            if (tradeData.size > 0 || specializatinData.size > 0)
                mBinding.llJobSpec.visibility = View.VISIBLE

            setUpListWithShowMore(
                specializatinData,
                specializatinDataEditable
            )

            setPortfolioAdapter(false)
            reviewData.clear()
            tradieData?.reviewData?.let { reviewData?.addAll(it) }
            if (tradieData?.reviewData?.size!! > 0) {
                mBinding.llReviewData.visibility = View.VISIBLE
            } else {
                mBinding.llReviewData.visibility = View.GONE
            }
            mBinding.tvReview.text =
                getString(R.string.reviews_) + " (" + reviewData?.size + ")"
            mBinding.rvReviews.adapter?.notifyDataSetChanged()
            voucherData.clear()
            tradieData?.vouchesData?.let { voucherData?.addAll(it) }
            if (tradieData?.vouchesData?.size!! > 0) {
                mBinding.llVouchData.visibility = View.VISIBLE
            } else {
                mBinding.llVouchData.visibility = View.GONE
            }
            mBinding.tvVouches.text =
                getString(R.string.voucher) + " (" + voucherData?.size + ")"
            mBinding.rvVouches.adapter?.notifyDataSetChanged()

            if (voucherData.size > 2) {
                mBinding.tvVounchList.visibility = View.VISIBLE
                mBinding.tvVounchList.text =
                    getString(R.string.show_all_vouchers, voucherData.size.toString())
            } else {
                mBinding.tvVounchList.visibility = View.GONE
            }
            if (reviewData.size > 3) {
                mBinding.tvReviewList.visibility = View.VISIBLE
                mBinding.tvReviewList.text =
                    getString(R.string.show_all_reviews, reviewData.size.toString())
            } else {
                mBinding.tvReviewList.visibility = View.GONE
            }
        }
    }

    private fun setPortfolioAdapter(isEditable: Boolean) {
        photos?.clear()
        tradieData?.portfolio?.let {
            for (item in tradieData?.portfolio!!) {
                if (!item.portfolioId.isNullOrEmpty())
                    item.isEditable = isEditable
            }

            photosHolderList?.clear()
            photosHolderList!!.addAll(it)

            if (photosHolderList!!.size > 3) {
                for (index in photosHolderList!!.indices) {
                    if (index < 3)
                        photos!!.add(photosHolderList!![index])
                    else
                        break
                }
                mBinding.tvPortfolioMore.visibility = View.VISIBLE
            } else {
                mBinding.tvPortfolioMore.visibility = View.GONE
                photos!!.addAll(photosHolderList!!)
            }
        }
        if (isEditable && getPortfolioCount() < 6)
            photos?.add(PortFolio())

        if (getPortfolioCount() > 0)
            mBinding.llPortfolioData.visibility = View.VISIBLE
        /*if (getPortfolioCount() > 1)
            mBinding.tvPhotos.text =
                getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(R.string.jobs) + ")"
        else
            mBinding.tvPhotos.text =
                getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(R.string.job) + ")"*/


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
        val layountManager = GridLayoutManager(this, 3)
        photos = ArrayList()
        imageAdapter = photos?.let { PortfolioEditableAdapter(this, false, it) }!!
        mBinding.rvPhotos.layoutManager = layountManager
        mBinding.rvPhotos.adapter = imageAdapter

        val reviewLayountManager = LinearLayoutManager(this)
        reviewAdapter = reviewData?.let { ReviewAdapter(it, false) }!!
        mBinding.rvReviews.layoutManager = reviewLayountManager
        mBinding.rvReviews.adapter = reviewAdapter

        val vounchLayountManager = LinearLayoutManager(this)
        vounchesAdapter = voucherData?.let { VounchesAdapter(it) }!!
        mBinding.rvVouches.layoutManager = vounchLayountManager
        mBinding.rvVouches.adapter = vounchesAdapter

        val mHomeAdapter = TradieMixSmallAdapter(tradeData)
        val jobLayoutManager = FlexboxLayoutManager(mBinding.root.context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }

        val mSpecAdapter = SpecializationMixSmallAdapter(specializatinData, true, this)
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)

        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobTypes, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvReviews, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvVouches, false)

    }


    private fun listener() {
        permissionHelper = PermissionHelper()
        permissionHelper.setListener(this)
        mBinding.ivCam.bringToFront()
        mBinding.tvReviewList.setOnClickListener(this)
        mBinding.tradieProfileIvBack.setOnClickListener { onBackPressed() }
        mBinding.srLayout.setOnRefreshListener(this)

        mBinding.tvDescTitleMore.setOnClickListener { manageMoreLessAbout() }
        mBinding.tvPortfolioMore.setOnClickListener { manageMoreLessPortfolio() }
        mBinding.ivEdit.setOnClickListener(this)
        mBinding.tvEdit.setOnClickListener(this)
        mBinding.ivCam.setOnClickListener(this)
        mBinding.tvEditAbout.setOnClickListener(this)
        mBinding.tvAddBio.setOnClickListener(this)
        mBinding.tvSave.setOnClickListener(this)
        mBinding.tvAddPortfolio.setOnClickListener(this)
        mBinding.tvEditBasic.setOnClickListener(this)
        mBinding.tvEditSpl.setOnClickListener(this)


    }

    var isPortfolioAllClicked: Boolean = false
    private fun manageMoreLessPortfolio() {
        if (!isPortfolioAllClicked) {
            isPortfolioAllClicked = true
            if (photosHolderList!!.size > 3) {
                photos!!.clear()
                photos!!.addAll(photosHolderList!!)
                mBinding.rvPhotos.adapter?.notifyDataSetChanged()
                mBinding.tvPortfolioMore.text = "Less"
            }
        } else {
            isPortfolioAllClicked = false
            photos!!.clear()
            for (index in photosHolderList!!.indices) {
                if (index < 3)
                    photos!!.add(photosHolderList!![index])
                else
                    break
            }
            mBinding.rvPhotos.adapter?.notifyDataSetChanged()
            mBinding.tvPortfolioMore.text = "All"
        }
    }

    var isAboutMoreClicked: Boolean = false
    private fun manageMoreLessAbout() {
        if (!isAboutMoreClicked) {
            isAboutMoreClicked = true
            mBinding.tvDescTitleMore.text = "Less"
            mBinding.tvDesc.maxLines = Integer.MAX_VALUE
            mBinding.tvDesc.ellipsize = null
        } else {
            isAboutMoreClicked = false
            mBinding.tvDescTitleMore.text = getString(R.string.more_)
            mBinding.tvDesc.maxLines = 3
            mBinding.tvDesc.ellipsize = TextUtils.TruncateAt.END
        }
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(mUplodaViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mUplodaViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.TRADIE_PROFILE_PUBLIC, ApiCodes.UPLOAD_FILE, ApiCodes.TRADIE_EDIT_PROFILE -> {
                mBinding.srLayout.isRefreshing = false
                showToastShort(exception.message)
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.TRADIE_PROFILE_PUBLIC -> {
                mBinding.srLayout.visibility = View.VISIBLE
                mBinding.linMain.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mViewModel.builderModel.let {
                    tradieData = it
                }
                mBinding.tvPortfolioMore.visibility = View.VISIBLE
                mBinding.tvDescTitleMore.visibility = View.VISIBLE
                setData()
                if (forPublicInfo)
                    mBinding.ivEdit.performClick()
            }
            ApiCodes.UPLOAD_FILE -> {
                mUplodaViewModel.imageUploadResponse.url?.let {
                    if (it.isNotEmpty()) {
                        tradieImg = it.get(0)
                        Glide.with(mBinding.root.context).load(fPAth)
                            .placeholder(R.drawable.placeholder_profile)
                            .error(R.drawable.placeholder_profile)
                            .into(mBinding.ivUserProfile)
                    }
                }
            }
            ApiCodes.TRADIE_EDIT_PROFILE -> {
                isEditableMode = true
                tradieData?.builderImage = tradieImg
                tradieData?.about = mBinding.tvDesc.text.toString()
                tradieData?.builderName = mBinding.tvName.text.toString()
                tradieData?.areasOfSpecialization?.tradeData?.clear()
                tradieData?.areasOfSpecialization?.specializationData?.clear()
                tradieData?.areasOfSpecialization?.tradeData?.addAll(tradeData)
                tradieData?.areasOfSpecialization?.specializationData?.addAll(specializatinData)
                shouldReturn = true
                onBackPressed()
            }

        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.tvReviewList -> {
                startActivityForResult(
                    Intent(this, ReviewListActivity::class.java).putExtra(
                        "data",
                        tradieData!!.reviewData
                    ).putExtra(
                        "title",
                        (tradieData!!.reviewsCount!!.toInt()).toString() + " review(s)"
                    ).putExtra("count", tradieData!!.reviewsCount!!.toInt()), 2610
                )
            }
            mBinding.tvEdit -> {
                mBinding.tvPortfolioMore.visibility = View.GONE
                photos!!.clear()
                photos!!.addAll(photosHolderList!!)
                mBinding.rvPhotos.adapter?.notifyDataSetChanged()

                mBinding.tvDescTitleMore.visibility = View.GONE
                mBinding.tvEditBasic.visibility = View.VISIBLE
                mBinding.tvEditSpl.visibility = View.VISIBLE
                mBinding.ivCam.visibility = View.VISIBLE
                mBinding.tvSave.visibility = View.VISIBLE
                mBinding.ivEdit.visibility = View.GONE
                mBinding.tvEdit.visibility = View.GONE
                mBinding.linReview.visibility = View.GONE
                mBinding.llVouchData.visibility = View.GONE
                mBinding.llReviewData.visibility = View.GONE
                mBinding.tvReviewList.visibility = View.GONE
                mBinding.tvDesc.maxLines = Integer.MAX_VALUE
                isEditableMode = true
                if (!tradieData?.about.isNullOrEmpty())
                    mBinding.tvEditAbout.visibility = View.VISIBLE
                else {
                    mBinding.linAbout.visibility = View.VISIBLE
                    mBinding.tvAddBio.visibility = View.VISIBLE
                    mBinding.tvEditAbout.visibility = View.GONE

                }
                if (tradieData?.portfolio.isNullOrEmpty()) {
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
            mBinding.tvEditSpl -> {
                startActivityForResult(
                    Intent(this, TradeActivity::class.java).putExtra("trade", tradeData)
                        .putExtra("specialization", specializatinData).putExtra("isForEdit", true),

                    PermissionConstants.TRADE_EDIT
                )
            }

            mBinding.tvAddPortfolio -> {
                startActivityForResult(
                    Intent(this, AddEditPortfolioActivity::class.java),
                    PermissionConstants.ADD_PORTFOLIO
                )
            }
            mBinding.ivEdit -> {
                mBinding.tvEdit.performClick()
            }
            mBinding.tvEditBasic -> {
                startActivityForResult(
                    Intent(this, EditBasicDetailsActivity::class.java),
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
                val tradeIdList = ArrayList<String>()
                val specialisationIdList = ArrayList<String>()
                for (data in tradeData) {
                    tradeIdList.add(data.tradeId!!)
                }
                for (data in specializatinData) {
                    specialisationIdList.add(data.specializationId!!)
                }

                val params = HashMap<String, Any>()
                params.put("fullName", tradieData?.builderName.toString())
                if (!tradieImg.isNullOrEmpty())
                    params.put("userImage", tradieImg!!)
                params.put("trade", tradeIdList)
                params.put("specialization", specialisationIdList)
                if (!mBinding.tvDesc.text.toString().trim().isNullOrEmpty())
                    params.put("about", mBinding.tvDesc.text.toString())
                mViewModel.editTradieProfile(params)

            }
            mBinding.tvAddBio -> {
                val intent = Intent(this, AboutusActivity::class.java)
                intent.putExtra("about_you", mBinding.tvDesc.text.toString())
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

    override fun onBackPressed() {
        if (isEditableMode && !forPublicInfo) {
            mBinding.tvEditBasic.visibility = View.GONE
            mBinding.tvEditAbout.visibility = View.GONE
            mBinding.tvEditSpl.visibility = View.GONE
            mBinding.ivCam.visibility = View.GONE
            mBinding.ivEdit.visibility = View.VISIBLE
            mBinding.tvEdit.visibility = View.VISIBLE
            mBinding.linReview.visibility = View.VISIBLE
            mBinding.tvSave.visibility = View.GONE
            mBinding.tvAddBio.visibility = View.GONE
            mBinding.llPortfolioAddData.visibility = View.GONE
            if (tradieData?.vouchesData?.size!! > 0) {
                mBinding.llVouchData.visibility = View.VISIBLE
            } else {
                mBinding.llVouchData.visibility = View.GONE
            }
            if (tradieData?.reviewData?.size!! > 0) {
                mBinding.llReviewData.visibility = View.VISIBLE
            } else {
                mBinding.llReviewData.visibility = View.GONE
            }
            if (voucherData.size > 2) {
                mBinding.tvVounchList.visibility = View.VISIBLE
                mBinding.tvVounchList.text =
                    getString(R.string.show_all_vouchers, voucherData.size.toString())
            } else {
                mBinding.tvVounchList.visibility = View.GONE
            }
            if (reviewData.size > 3) {
                mBinding.tvReviewList.visibility = View.VISIBLE
                mBinding.tvReviewList.text =
                    getString(R.string.show_all_reviews, reviewData.size.toString())
            } else {
                mBinding.tvReviewList.visibility = View.GONE
            }
            isEditableMode = false
            mBinding.srLayout.setOnRefreshListener(this)
            setData()
            return
        }
        if (shouldReturn)
            setResult(RESULT_OK)
        super.onBackPressed()
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

            2610-> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.hasExtra("data")) {
                        val reviewDatas = data.getSerializableExtra("data") as ArrayList<ReviewData>
                        val count = data.getIntExtra("count", 0)
                        if (reviewDatas.size > 3) {
                            mBinding.tvReviewList.visibility = View.VISIBLE
                            mBinding.tvReviewList.text =
                                getString(R.string.show_all_reviews, reviewData.size.toString())
                        } else {
                            mBinding.tvReviewList.visibility = View.GONE
                        }
                        reviewData.clear()
                        reviewData.addAll(reviewDatas)
                        tradieData?.reviewData = reviewData
                        reviewAdapter = reviewData?.let { ReviewAdapter(it, false) }!!
                        mBinding.rvReviews.adapter = reviewAdapter
                    }
                }
            }
            PermissionConstants.REQ_GALLERY -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    uri = data.data
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
                        UCrop.of(
                            Uri.fromFile(File(fPAth)), Uri.fromFile(
                                File(
                                    cacheDir, destinationFileName
                                )
                            )
                        ).withAspectRatio(1f, 1f)
                            .start(this)


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
                        UCrop.of(
                            Uri.fromFile(File(fPAth)), Uri.fromFile(
                                File(
                                    cacheDir, destinationFileName
                                )
                            )
                        ).withAspectRatio(1f, 1f)
                            .start(this)
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
                    tradieData?.portfolio?.add(portfolioData)
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
                                tradieData?.portfolio?.remove(item)
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
            PermissionConstants.TRADE_EDIT -> {
                data?.let {
                    val trade =
                        data.getSerializableExtra(IntentConstants.TRADE_LIST) as ArrayList<TradeData>
                    val specializatin =
                        data.getSerializableExtra(IntentConstants.SPEC_LIST) as ArrayList<SpecialisationData>
                    tradeData.clear()
                    specializatinData.clear()
                    tradeData.addAll(trade)
                    specializatinData.addAll(specializatin)
                    if (tradeData.size > 0 || specializatinData.size > 0)
                        mBinding.llJobSpec.visibility = View.VISIBLE
                    val mHomeAdapter = TradieMixSmallAdapter(tradeData)
                    val jobLayoutManager = FlexboxLayoutManager(mBinding.root.context).apply {
                        flexWrap = FlexWrap.WRAP
                        flexDirection = FlexDirection.ROW
                        alignItems = AlignItems.STRETCH
                    }

                    val mSpecAdapter = SpecializationMixSmallAdapter(specializatinData, true, this)
                    mBinding.rvJobTypes.layoutManager = jobLayoutManager
                    mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
                    mBinding.rvJobTypes.adapter?.notifyDataSetChanged()
                    if (tradeData != null && tradeData?.size!! > 0) {
                        mBinding.tvDetails.text = tradeData?.get(0)?.tradeName
                    }
                }
            }
            PermissionConstants.EDIT_BASIC_DETAILS -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val name = data.getStringExtra("name")
                    tradieData?.builderName = name
                    mBinding.tvName.setText(name)
                    shouldReturn = true
                }
            }


        }
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
                    getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(R.string.jobs) + ")"
            else
                mBinding.tvPhotos.text =
                    getString(R.string.portfolio) + " (" + getPortfolioCount() + " " + getString(R.string.job) + ")"
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
        if (isEditableMode)
            mBinding.srLayout.isRefreshing = false
        else
            mViewModel.getTradiePublicProfile(false)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.setPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onExpandClick(position: Int, specialisationData: SpecialisationData) {
        Log.i(TAG, "isShowMore $position")
        Log.i(TAG, "isShowMore ${specialisationData.specializationName}")
        var tagSpecName: String = specialisationData.specializationName!!.toLowerCase()
        if (tagSpecName == "show less") {
            setUpListWithShowMore(
                specializatinData,
                specializatinDataEditable
            )
        } else if (tagSpecName == "show more") {
            specializatinData.clear()
            specializatinData.addAll(specializatinDataEditable)
            specializatinData.add(SpecialisationData("SpecId Less", "Show Less"))
            val mHomeAdapter = TradieMixSmallAdapter(tradeData)
            val mSpecAdapter = SpecializationMixSmallAdapter(specializatinData, true, this)
            mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
            Log.i(TAG, "isShowMore ${specializatinData.size}")
        }
    }

    fun setUpListWithShowMore(
        listMain: ArrayList<SpecialisationData>,
        listEditable: ArrayList<SpecialisationData>
    ) {
        listMain.clear()
        if (listEditable.size > 5) {
            for (index in listEditable.indices) {
                if (index <= 4) {
                    listMain.add(listEditable[index])
                } else if (index == 5) {
                    listMain.add(SpecialisationData("SpecId", "Show More"))
                    break
                }
            }
        } else {
            listMain.addAll(listEditable)
        }
        val mHomeAdapter = TradieMixSmallAdapter(tradeData)
        val jobLayoutManager = FlexboxLayoutManager(mBinding.root.context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        val mSpecAdapter = SpecializationMixSmallAdapter(listMain, true, this)
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
        mBinding.rvJobTypes.adapter?.notifyDataSetChanged()
    }
}