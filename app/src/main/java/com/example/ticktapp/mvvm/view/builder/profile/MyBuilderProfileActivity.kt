package com.example.ticktapp.mvvm.view.builder.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.profile.InitalProfileModel
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.TradeData
import com.app.core.model.tradie.PortFolio
import com.app.core.model.tradie.ReviewData
import com.app.core.model.tradie.ReviewList
import com.app.core.model.tradie.VouchesData
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.*
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityMyBuilderProfileBinding
import com.example.ticktapp.mvvm.view.builder.JobsListActivity
import com.example.ticktapp.mvvm.view.builder.ReviewListBuilderActivity
import com.example.ticktapp.mvvm.view.builder.VounchListActivity
import com.example.ticktapp.mvvm.view.tradie.BuilderPublicProfileActivity
import com.example.ticktapp.mvvm.viewmodel.ProfileViewModel
import com.example.ticktapp.mvvm.viewmodel.ReviewListViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager


@Suppress("DEPRECATION")
public class MyBuilderProfileActivity : BaseActivity(),
    OnClickListener, SpecializationAdapter.SpecListAdapterListener,
    JobsSmallAdapter.JobAdapterListener, SpecializationMixSmallAdapter.OnSpecializationClicked {
    private var tradieData: InitalProfileModel? = null
    private var reviewDataList = ArrayList<ReviewList>()


    private lateinit var mBinding: ActivityMyBuilderProfileBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }
    private val tradeData: ArrayList<TradeData> = ArrayList()
    private val specializatinData: ArrayList<SpecialisationData> = ArrayList()
    private val reviewData: ArrayList<ReviewData> = ArrayList()
    private val voucherData: ArrayList<VouchesData> = ArrayList()
    private val jobPostedData: ArrayList<JobRecModel> = ArrayList()

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var vounchesAdapter: VounchesAdapter
    private lateinit var jobPostedAdapter: MyJobAdapter

    private lateinit var imageAdapter: PortfolioAdapter
    private var photos: ArrayList<PortFolio>? = null
    private var photosHolderList: ArrayList<PortFolio>? = ArrayList()
    private var isShouldReturn = false
    private val mViewModelList by lazy { ViewModelProvider(this).get(ReviewListViewModel::class.java) }

    companion object {
        public var specializatinDataEditable: ArrayList<SpecialisationData> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_builder_profile)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        listener()
        setupView()
        setObservers()
        mViewModel.getBasicProfileDetails(true)
    }


    private fun setData() {
        if (tradieData != null) {
            mBinding.tvName.text = tradieData?.builderName
            mBinding.tvPosition.text = tradieData?.position
            mBinding.tvCompanyName.text = tradieData?.companyName
            if (tradieData?.aboutCompany != null && tradieData?.aboutCompany?.length!! > 0 && tradieData?.aboutCompany != "null") {
                mBinding.tvDescTitle.visibility = View.VISIBLE
                mBinding.tvDescTitleMore.visibility = View.VISIBLE
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDesc.text = tradieData?.aboutCompany
            } else {
                mBinding.tvDescTitle.visibility = View.GONE
                mBinding.tvDescTitleMore.visibility = View.GONE
                mBinding.tvDesc.visibility = View.GONE
            }
            if (tradieData?.jobCompletedCount!! > 1) {
                mBinding.tvJobCompletedText.text = getString(R.string.jobs_completed)
            } else {
                mBinding.tvJobCompletedText.text = getString(R.string.job_completed)
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
                .placeholder(R.drawable.placeholder_profile)
                .error(R.drawable.placeholder_profile)
                .into(mBinding.ivUserProfile)
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


            photos?.clear()
            photosHolderList?.clear()
            tradieData?.portfolio?.let {
                photosHolderList!!.addAll(it)
//                photos?.addAll(it)
            }
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
            if (photos?.size!! > 0)
                mBinding.llPortfolioData.visibility = View.VISIBLE
           /* mBinding.tvPhotos.text =
                getString(R.string.portfolio) + " (" + photos?.size + " " + getString(R.string.jobs) + ")"*/

            mBinding.rvPhotos.adapter?.notifyDataSetChanged()
            reviewData.clear()
            tradieData?.reviewData?.let { reviewData?.addAll(it) }
            if (tradieData?.reviewData != null && tradieData?.reviewData?.size!! > 0) {
                mBinding.llReviewData.visibility = View.VISIBLE
            } else {
                mBinding.llReviewData.visibility = View.GONE
            }
            mBinding.tvReview.text =
                getString(R.string.reviews_) + " (" + reviewData?.size + ")"
            mBinding.rvReviews.adapter?.notifyDataSetChanged()
            voucherData.clear()
            tradieData?.vouchesData?.let { voucherData?.addAll(it) }
            if (tradieData?.vouchesData != null && tradieData?.vouchesData?.size!! > 0) {
                mBinding.llVouchData.visibility = View.VISIBLE
            } else {
                mBinding.llVouchData.visibility = View.GONE
            }
            mBinding.tvVouches.text =
                getString(R.string.voucher) + " (" + voucherData?.size + ")"
            mBinding.rvVouches.adapter?.notifyDataSetChanged()

            mBinding.tvVounchList.visibility = View.VISIBLE
            mBinding.tvVounchList.text =
                getString(R.string.show_all_vouchers, voucherData.size.toString())

            jobPostedData.clear()
            tradieData?.jobPostedData?.let { jobPostedData?.addAll(it) }
            if (tradieData?.jobPostedData != null && tradieData?.jobPostedData?.size!! > 0) {
                mBinding.lljobPostedData.visibility = View.VISIBLE
            } else {
                mBinding.lljobPostedData.visibility = View.GONE
            }
            mBinding.rvJobPosted.adapter?.notifyDataSetChanged()
            mBinding.tvJobList.visibility = View.VISIBLE
            mBinding.tvJobList.text =
                getString(R.string.show_all_jobs, tradieData?.totalJobPostedCount!!.toString())

            mBinding.tvReviewList.visibility = View.VISIBLE
            mBinding.tvReviewList.text =
                getString(R.string.show_all_reviews, tradieData?.reviewsCount.toString())
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
            window.statusBarColor = Color.WHITE
        }
    }

    private fun setupView() {
        val layountManager = GridLayoutManager(this, 3)
        photos = ArrayList()
        imageAdapter = photos?.let { PortfolioAdapter(it) }!!
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

        val jobPostedLayountManager = LinearLayoutManager(this)
        jobPostedAdapter = jobPostedData?.let { MyJobAdapter(it) }!!
        mBinding.rvJobPosted.layoutManager = jobPostedLayountManager
        mBinding.rvJobPosted.adapter = jobPostedAdapter


        val mHomeAdapter = TradieMixSmallAdapter(tradeData)
        val jobLayoutManager = FlexboxLayoutManager(mBinding.root.context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }

        val mSpecAdapter = SpecializationMixSmallAdapter(specializatinData, true, this)
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
//        mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
        mBinding.rvJobTypes.adapter = mSpecAdapter

        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobTypes, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvReviews, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvVouches, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobPosted, false)

    }


    private fun listener() {
        mBinding.tvDescTitleMore.setOnClickListener { manageMoreLessAbout() }
        mBinding.tvPortfolioMore.setOnClickListener { manageMoreLessPortfolio() }
        mBinding.tradieProfileIvBack.setOnClickListener { onBackPressed() }
        mBinding.srLayout.setOnRefreshListener {
            mViewModel.getBasicProfileDetails(false)

        }
        mBinding.ivEdit.setOnClickListener {
            if (tradieData != null) {
                builderProfile.launch(
                    Intent(this, BuilderPublicProfileActivity::class.java).putExtra(
                        "data",
                        tradieData
                    )
                )
            }
        }
        mBinding.tvEdit.setOnClickListener {
            if (tradieData != null) {
                builderProfile.launch(
                    Intent(this, BuilderPublicProfileActivity::class.java).putExtra(
                        "data",
                        tradieData
                    )
                )
            }
        }
        mBinding.tvReviewList.setOnClickListener {
            startActivityForResult(
                Intent(this, ReviewListBuilderActivity::class.java).putExtra(
                    "data",
                    reviewDataList
                ).putExtra("title", (tradieData!!.reviewData?.size).toString() + " review(s)"), 1310
            )
        }
        mBinding.tvVounchList.setOnClickListener {
            startActivity(
                Intent(this, VounchListActivity::class.java).putExtra(
                    "data",
                    tradieData!!.vouchesData
                ).putExtra("count", tradieData?.vouchesData?.size.toString())
                    .putExtra("title", (tradieData!!.vouchesData?.size).toString() + " vouche(s)")
                    .putExtra("isMyVouch", true)
            )
        }

        mBinding.tvJobList.setOnClickListener {
            startActivity(
                Intent(this, JobsListActivity::class.java).putExtra(
                    "data",
                    tradieData!!.jobPostedData
                ).putExtra("title", (tradieData!!.totalJobPostedCount).toString() + " job(s)")
            )
        }
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
                if(index<3)
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

    private var builderProfile = registerForActivityResult(
        StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                isShouldReturn = true
                mViewModel.getBasicProfileDetails(true)
            }
        }
    )

    override fun onBackPressed() {
        if (isShouldReturn) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(mViewModelList)
        mViewModel.getResponseObserver().observe(this, this)
        mViewModelList.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.BASIC_PROFILE -> {
                mBinding.srLayout.isRefreshing = false
            }

        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.BASIC_PROFILE -> {
                mBinding.srLayout.visibility = View.VISIBLE
                mBinding.srLayout.isRefreshing = false
                mViewModel.inItProfileModel.let {
                    tradieData = it
                    mViewModelList.getBuilderReviewList(it.builderId.toString(), 1)
                }
                setData()
            }

            ApiCodes.REVIEW_LIST -> {
                mViewModelList.reviewDataList.let {
                    reviewDataList = it
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

    override fun onSpecCLick(position: Int) {
    }

    override fun onJobClick(position: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                reviewDataList = data.getSerializableExtra("data") as ArrayList<ReviewList>
            }
        }
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
//            mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
            mBinding.rvJobTypes.adapter = mSpecAdapter
            Log.i(TAG, "isShowMore ${specializatinData.size}")
        }
    }

    private val TAG: String = "showMoreData"

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
//        mBinding.rvJobTypes.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
        mBinding.rvJobTypes.adapter = mSpecAdapter
        mBinding.rvJobTypes.adapter?.notifyDataSetChanged()
    }
}