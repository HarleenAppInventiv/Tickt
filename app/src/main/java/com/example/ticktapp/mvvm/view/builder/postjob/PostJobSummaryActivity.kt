package com.example.ticktapp.mvvm.view.builder.postjob

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.jobmodel.JobModel
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.model.jobmodel.PhotosThumb
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.model.tradesmodel.Trades
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.bumptech.glide.Glide
import com.exampl.VideoImageActivity
import com.example.ticktapp.R
import com.example.ticktapp.adapters.*
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityPostJobDetailsBinding
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.model.MilestoneRequestData
import com.example.ticktapp.mvvm.viewmodel.PostJobViewModel
import com.example.ticktapp.util.*
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PostJobSummaryActivity : BaseActivity(), View.OnClickListener,
    SpecializationAdapter.SpecListAdapterListener,
    JobsSmallAdapter.JobAdapterListener, JobsMoreSelectionAdapter.JobAdapterListener {
    private var isEdit: Boolean = false
    private var isRepublish: Boolean = false
    private lateinit var files: java.util.ArrayList<String>
    private lateinit var thumbs: java.util.ArrayList<String>
    private lateinit var uThumbs: java.util.ArrayList<String>

    private lateinit var thumbsUrl: HashMap<String, String>
    private lateinit var mData: ArrayList<MilestoneData>
    private var isSearchType: Int = 1
    private var isJobType: Int = 1
    private var end_date: String = ""
    private var start_date: String = ""
    private var amount: String = ""
    private var job_description: String = ""
    private var location_name: String = ""
    private var lng: String = ""
    private var lat: String = ""
    private var jobName: String = ""
    private lateinit var job_type: ArrayList<JobModel>
    private lateinit var categories: ArrayList<Trades>
    private lateinit var specialization: ArrayList<Specialisation>

    private lateinit var mBinding: ActivityPostJobDetailsBinding
    private lateinit var mHomeAdapter: JobsMoreSelectionAdapter
    private lateinit var mSpecAdapter: SpecializationAdapter
    private lateinit var milestoneAdapter: RowPostMilestoneAdapter
    private lateinit var imageAdapter: MediaPostAdapter
    private val mViewModel by lazy { ViewModelProvider(this).get(PostJobViewModel::class.java) }
    private var category: String = ""
    private var location: String = ""
    private var jobId: String = ""
    private var numberOfMileStone: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post_job_details)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
        setupData()
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun getIntentData() {
        thumbsUrl = HashMap()
        val postData = if (intent.hasExtra("rData")) {
            intent.getSerializableExtra("rData") as JobRecModelRepublish
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .isNullOrEmpty()
        ) {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DATA)
                .getPojoData(JobRecModelRepublish::class.java)
        } else {
            null
        }



        isEdit = if (intent.hasExtra("isEdit")) {
            intent.getBooleanExtra("isEdit", false)
        } else {
            postData?.isEdit ?: false
        }
        isRepublish = if (intent.hasExtra("isRepublish")) {
            intent.getBooleanExtra("isRepublish", false)
        } else {
            postData!=null
        }
        jobName = if (intent.hasExtra("jobName")) {
            intent.getStringExtra("jobName").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_NAME) ?: ""

        }
        jobId = if (intent.hasExtra("jobID")) {
            intent.getStringExtra("jobID").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_ID) ?: ""

        }
        categories =
            if (intent.hasExtra("categories")) {
                intent.getSerializableExtra("categories") as ArrayList<Trades>
            } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.CATEGORIES)
                    .isNullOrEmpty()
            ) {
                PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.CATEGORIES)
                    .getList<Trades>() ?: ArrayList()
            } else {
                ArrayList()
            }

        job_type = if (intent.hasExtra("job_type")) {
            intent.getSerializableExtra("job_type") as ArrayList<JobModel>
        } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_TYPE)
                .isNullOrEmpty()
        ) {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_TYPE)
                .getList<JobModel>() ?: ArrayList()
        } else {
            ArrayList()
        }
        if (intent.hasExtra("specialization"))
            specialization =
                intent.getSerializableExtra("specialization") as ArrayList<Specialisation>
        else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_SPECS)
                .isNullOrEmpty()
        ) {
            specialization = PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_SPECS)
                .getList<Specialisation>() ?: ArrayList()
        } else
            specialization = ArrayList()


        lat = if (intent.hasExtra("lat")) {
            intent.getStringExtra("lat").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_LAT) ?: ""

        }
        lng = if (intent.hasExtra("lng")) {
            intent.getStringExtra("lng").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_LONG) ?: ""

        }
        location_name = if (intent.hasExtra("location_name")) {
            intent.getStringExtra("location_name").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_LOCATION) ?: ""

        }
        job_description = if (intent.hasExtra("job_description")) {
            intent.getStringExtra("job_description").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_DESCRIPTION) ?: ""

        }

        amount = if (intent.hasExtra("amount")) {
            intent.getStringExtra("amount").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_AMOUNT) ?: ""

        }

        isSearchType = if (intent.hasExtra("isSearchType")) {
            intent.getIntExtra("isSearchType", -1)
        } else {
            PreferenceManager.getInt(PreferenceManager.NEW_JOB_PREF.JOB_BUDGET_TYPE, -1) ?: -1

        }
        isJobType = if (intent.hasExtra("isJobType")) {
            intent.getIntExtra("isJobType", -1)
        } else {
            PreferenceManager.getInt(PreferenceManager.NEW_JOB_PREF.JOB_PAY_TYPE, -1) ?: -1

        }
        start_date = if (intent.hasExtra("start_date")) {
            intent.getStringExtra("start_date").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_START_DATE) ?: ""

        }
        end_date = if (intent.hasExtra("end_date")) {
            intent.getStringExtra("end_date").toString()
        } else {
            PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_END_DATE) ?: ""

        }

        mData =
            if (intent.hasExtra("mData")) {
                intent.getSerializableExtra("mData") as ArrayList<MilestoneData>
            } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE)
                    .isNullOrEmpty()
            ) {
                PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_MILESTONE)
                    .getList<MilestoneData>() ?: ArrayList()
            } else {
                ArrayList()
            }
        files =
            if (intent.hasExtra("files")) {
                intent.getSerializableExtra("files") as ArrayList<String>
            } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES)
                    .isNullOrEmpty()
            ) {
                PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES)
                    .getDataList<String>() ?: ArrayList()
            } else {
                ArrayList()
            }
        thumbs =
            if (intent.hasExtra("thumbs")) {
                intent.getSerializableExtra("thumbs") as ArrayList<String>
            } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_THUMB)
                    .isNullOrEmpty()
            ) {
                PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_THUMB)
                    .getDataList<String>() ?: ArrayList()
            } else {
                ArrayList()
            }
        uThumbs =
            if (intent.hasExtra("uThumbs")) {
                intent.getSerializableExtra("uThumbs") as ArrayList<String>
            } else if (!PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_UTHUMB)
                    .isNullOrEmpty()
            ) {
                PreferenceManager.getString(PreferenceManager.NEW_JOB_PREF.JOB_IMAGES_UTHUMB)
                    .getDataList<String>() ?: ArrayList()
            } else {
                ArrayList()
            }

        Log.i("intentDates", "getIntentData:-start $start_date")
        Log.i("intentDates", "getIntentData:-end $end_date")
        files.forEachIndexed { _, files ->
            if (files.contains("http")) {
                uThumbs.forEachIndexed { _, thumb ->
                    thumbsUrl.put(files, thumb)
                }
            }
        }
        uploadThumb()
    }

    private fun uploadThumb() {
        thumbs.forEach {
            mViewModel.hitUploadFileWithoutProgress(thumbs)
        }
    }


    private fun setupData() {
        /*if (isEdit) {
            mBinding.tvPostApply.text = getString(R.string.edit_job)
        }*/
        mHomeAdapter = JobsMoreSelectionAdapter(this, job_type, false)
        val jobLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = mHomeAdapter


        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        specialization.forEach {
            it.isSelected = false
        }
        mSpecAdapter = specialization?.let { SpecializationAdapter(it, this, false) }!!
        mBinding.rvSpecialization.layoutManager = flexboxLayoutManager
        mBinding.rvSpecialization.adapter = mSpecAdapter

        val layoutManager = LinearLayoutManager(this)
        milestoneAdapter = mData?.let { RowPostMilestoneAdapter(it) }!!
        mBinding.rvJobMilestone.layoutManager = layoutManager
        mBinding.rvJobMilestone.adapter = milestoneAdapter

        val layoutManagerHor = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = files?.let { MediaPostAdapter(it) }!!
        mBinding.rvPhotos.layoutManager = layoutManagerHor
        mBinding.rvPhotos.adapter = imageAdapter

        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobMilestone, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvPhotos, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobTypes, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvSpecialization, false)


        if (isEdit) {
            mBinding.tvPostApply.text = getString(R.string.submit)
        }
        if (job_type.size == 0) {
            mBinding.llJobSpec.visibility = View.GONE
        } else {
            mBinding.llJobSpec.visibility = View.VISIBLE
        }
        if (specialization.size == 0) {
            mBinding.rvSpecialization.visibility = View.GONE
        } else {
            mBinding.rvSpecialization.visibility = View.VISIBLE
        }
        if (mData.size == 0) {
            mBinding.llMilestoneData.visibility = View.GONE
        } else {
            mBinding.llMilestoneData.visibility = View.VISIBLE
        }
        if (files.size == 0) {
            mBinding.rvPhotos.visibility = View.GONE
        } else {
            mBinding.rvPhotos.visibility = View.VISIBLE
        }

        if (categories.size > 0) {
            mBinding.llHeader.visibility = View.VISIBLE
            mBinding.tvTitle.text = categories.get(0).tradeName
            mBinding.tvDetails.text = jobName
            if (categories.get(0).selectedUrl != null && categories.get(0).selectedUrl!!.isNotEmpty()) {
                Glide.with(mBinding.root.context).load(categories.get(0).selectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            }
        } else {
            mBinding.llHeader.visibility = View.GONE
        }
        mBinding.tvTime.text = getString(R.string.today)
        if (isJobType != 2) {
            if (isSearchType == 1)
                mBinding.tvMoney.text = "$" + afterTextChanged(amount) + " p/h"
            else
                mBinding.tvMoney.text = "$" + afterTextChanged(amount) + " f/p"
        } else {
            mBinding.tvMoney.text = getString(R.string.for_quoting)
        }
        mBinding.tvPlace.text = location_name
        mBinding.tvDays.text = DateUtils.printDifference(
            DateUtils.getCurrentDateTime(DateUtils.DATE_FORMATE_8),
            start_date
        )
        mBinding.tvDesc.text = job_description
        if (job_description.isNullOrEmpty()) {
            mBinding.tvDesc.visibility = View.GONE
            mBinding.tvDescTitle.visibility = View.GONE
        } else {
            mBinding.tvDesc.visibility = View.VISIBLE
            mBinding.tvDescTitle.visibility = View.VISIBLE
        }
    }

    fun afterTextChanged(amount: String): String {
        val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###.##")
        return formatter.format(amount.toDouble())
    }

    private fun listener() {
        mBinding.srLayout.setOnRefreshListener {
            mBinding.srLayout.isRefreshing = false
        }
        mBinding.jobDetailsIvBack.setOnClickListener { onBackPressed() }
        mBinding.tvPostApply.setOnClickListener {
            val params = HashMap<String, Any>()
            params.put("jobName", jobName)

            category = ""
            val categoriesList = ArrayList<String>()
            categories.forEach {
                it.id?.let { it1 ->
                    categoriesList.add(it1)
                    category = categoriesList.toString()   //Mo Engage categories
                }
            }
            params.put("categories", categoriesList)

            val job_typeList = ArrayList<String>()
            job_type.forEach {
                it._id?.let { it1 -> job_typeList.add(it1) }
            }
            params.put("job_type", job_typeList)

            val specializationList = ArrayList<String>()
            specialization.forEach {
                it.id?.let { it1 -> specializationList.add(it1) }
            }
            params.put("specialization", specializationList)

            location = ""
            val coordinates = arrayListOf(lng.toDouble(), lat.toDouble())
            val location = HashMap<String, Any>()
            location.put("type", "Point")
            location.put("coordinates", coordinates)
            if (lat.length > 0)
                this.location = "$location_name ${location.toString()}"   //Mo Engage location
            params.put("location", location)
            params.put("location_name", location_name)
            params.put("job_description", job_description)
            if (isSearchType != 0) {
                if (isSearchType == 1)
                    params.put("pay_type", "Per hour")
                else
                    params.put("pay_type", "Fixed price")
            }

            params.put("amount", amount)
            params.put("from_date", start_date)
            params.put("to_date", end_date)
            if (isJobType == 2) {
                params.put("quoteJob", "1")
            } else {
                params.put("quoteJob", "0")
            }
            val milestones = ArrayList<MilestoneRequestData>()
            var order = 1
            mData.forEach {
                val milestoneData = MilestoneRequestData()
                milestoneData.to_date = it.end_date
                milestoneData.from_date = it.start_date
                milestoneData.isPhotoevidence = it.photoRequired
                milestoneData.recommended_hours = it.hours
                milestoneData.milestone_name = it.name
                milestoneData.order = order
                milestones.add(milestoneData)
                order++
            }
            params.put("milestones", milestones)

            numberOfMileStone = milestones.size  //Mo Engage Number of milestones
            val photosThumb = ArrayList<PhotosThumb>()
            files.forEach {
                var type = 1
                var thumb = ""
                if (it.lowercase().endsWith(".jpg") || it.lowercase()
                        .endsWith(".jpeg") || it.lowercase().endsWith(".png")
                ) {
                    type = 1
                    thumb = it
                } else if (it.lowercase().endsWith(".doc") || it.lowercase().endsWith(".docx")) {
                    type = 3
                    thumb = it
                } else if (it.lowercase().endsWith(".pdf")) {
                    type = 4
                    thumb = it
                } else {
                    type = 2
                    if (thumbsUrl.containsKey(it))
                        thumb = thumbsUrl[it].toString()
                    else
                        thumb = ""
                }
                photosThumb.add(PhotosThumb(type, thumb, it))
            }
            params.put("urls", photosThumb)
            if (isEdit) {
                params.put("jobId", jobId)
                mViewModel.createUpdatedPost(params)
            } else if (isRepublish) {
                params.put("jobId", jobId)
                mViewModel.createRepublishPost(params)
            } else {
                mViewModel.createPost(params)
            }
        }
        mBinding.tvDescTitleEdit.setOnClickListener {
            startActivityForResult(
                Intent(this, JobDescActivity::class.java)
                    .putExtra("isReturn", true)
                    .putExtra("job_description", job_description), 2610
            )
        }
        mBinding.tvTitleEdit.setOnClickListener {
            startActivityForResult(
                Intent(this, PostNewJobActivity::class.java)
                    .putExtra("isReturn", true)
                    .putExtra("jobName", jobName)
                    .putExtra("specialization", specialization)
                    .putExtra("categories", categories)
                    .putExtra("job_type", job_type), 1310
            )
        }
        mBinding.tvJobTypesEdit.setOnClickListener {
            startActivityForResult(
                Intent(this, PostNewJobActivity::class.java)
                    .putExtra("isReturn", true)
                    .putExtra("jobName", jobName)
                    .putExtra("specialization", specialization)
                    .putExtra("categories", categories)
                    .putExtra("job_type", job_type), 1310
            )
        }
        mBinding.tvSpecializationEdit.setOnClickListener {
            startActivityForResult(
                Intent(this, PostNewJobActivity::class.java)
                    .putExtra("isReturn", true)
                    .putExtra("jobName", jobName)
                    .putExtra("specialization", specialization)
                    .putExtra("categories", categories)
                    .putExtra("job_type", job_type), 1310
            )
        }
        mBinding.tvPhotosEdit.setOnClickListener {
            startActivityForResult(
                Intent(this, VideoImageActivity::class.java)
                    .putExtra("isReturn", true)
                    .putExtra("photos", files), 1326
            )

        }
        mBinding.tvJobMilestoneEdit.setOnClickListener {
            startActivityForResult(
                Intent(this, AllMilestoneActivity::class.java)
                    .putExtra("isReturn", true)
                    .putExtra("start_date", start_date)
                    .putExtra("end_date", end_date)
                    .putExtra("data", mData), 3910
            )
        }

    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    private fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onSpecCLick(position: Int) {

    }

    override fun onJobClick(position: Int) {
    }

    override fun onClick(p0: View?) {

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.CREATE_POST -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.CREATE_POST -> {
                if (PostJobData.postjobDataNullCheck()) {
                    PostJobData.clearMilestones()
                }
                postJobOnMoEngage(category, location, numberOfMileStone, start_date, end_date)
                postJobOnMixPanel(category, location, numberOfMileStone, start_date, end_date)
                startActivity(
                    Intent(
                        this,
                        PostedJobActivity::class.java
                    )
                )
            }
            ApiCodes.CREATE_REPUBLISH_POST -> {
                if (PostJobData.postjobDataNullCheck()) {
                    PostJobData.clearMilestones()
                }
                republishedJobMoEngage()
                rePublishJobMixPanel()
                startActivity(
                    Intent(
                        this,
                        PostedJobActivity::class.java
                    )
                )
            }
            ApiCodes.UPLOAD_FILE -> {
                mViewModel.imageUploadResponse.url.let {
                    it?.let { it1 ->
                        it1.forEach {
                            files.forEachIndexed { _, indexs ->
                                if (!(indexs.lowercase().endsWith(".jpg") || indexs.lowercase()
                                        .endsWith(".jpeg") || indexs.lowercase().endsWith(".png") ||
                                            indexs.lowercase().endsWith(".pdf") ||
                                            indexs.lowercase().endsWith(".doc") ||
                                            indexs.lowercase().endsWith(".docx")
                                            )
                                ) {
                                    thumbsUrl.put(indexs, it1.toString())
                                }

                            }
                        }

                    }
                }
            }
        }
    }

    private fun republishedJobMoEngage() {

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_REPUBLISHED_JOB,
            signUpProperty
        )
    }

    private fun rePublishJobMixPanel() {
        val mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mix_panel_token))
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_REPUBLISHED_JOB, props)
    }

    private fun postJobOnMixPanel(
        category: String,
        location: String,
        numberOfMileStone: Int,
        startDate: String,
        endDate: String
    ) {
        val mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mix_panel_token))
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)
        props.put(MoEngageConstants.LOCATION, location)
        props.put(MoEngageConstants.NUMBER_OF_MILESTONES, numberOfMileStone)
        props.put(MoEngageConstants.START_DATE, startDate)
        props.put(MoEngageConstants.END_DATE, endDate)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_POSTED_JOB, props)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1310 && data != null) {
            jobName = data?.getStringExtra("jobName").toString()
            categories = data?.getSerializableExtra("categories") as ArrayList<Trades>
            job_type = data.getSerializableExtra("job_type") as ArrayList<JobModel>
            if (data.hasExtra("specialization"))
                specialization =
                    data.getSerializableExtra("specialization") as ArrayList<Specialisation>
            else
                specialization = ArrayList()
            setupData()

        }
        if (resultCode == Activity.RESULT_OK && requestCode == 2610 && data != null) {
            job_description = data?.getStringExtra("job_description").toString()
            if (job_description.isNullOrEmpty()) {
                mBinding.tvDesc.visibility = View.GONE
                mBinding.tvDescTitle.visibility = View.GONE
            } else {
                mBinding.tvDesc.visibility = View.VISIBLE
                mBinding.tvDescTitle.visibility = View.VISIBLE
            }
            mBinding.tvDesc.text = job_description
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 3910 && data != null) {
            mData = data.getSerializableExtra("mData") as ArrayList<MilestoneData>
            milestoneAdapter = mData?.let { RowPostMilestoneAdapter(it) }!!
            mBinding.rvJobMilestone.adapter = milestoneAdapter
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 1326 && data != null) {
            if (data.hasExtra("files"))
                files = data.getSerializableExtra("files") as ArrayList<String>
            else
                files = ArrayList()
            imageAdapter = files?.let { MediaPostAdapter(it) }!!
            mBinding.rvPhotos.adapter = imageAdapter
            if (files.size == 0) {
                mBinding.rvPhotos.visibility = View.GONE
            } else {
                mBinding.rvPhotos.visibility = View.VISIBLE
            }
            if (data.hasExtra("thumbs"))
                thumbs = data.getSerializableExtra("thumbs") as ArrayList<String>
            else
                thumbs = ArrayList()
            uploadThumb()
        }

    }

    private fun postJobOnMoEngage(
        category: String,
        location: String,
        numberOfMileStone: Int,
        startDate: String,
        endDate: String
    ) {
        val signUpProperty = Properties()

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)
        signUpProperty.addAttribute(MoEngageConstants.LOCATION, location)
        signUpProperty.addAttribute(MoEngageConstants.NUMBER_OF_MILESTONES, numberOfMileStone)
        signUpProperty.addAttribute(MoEngageConstants.START_DATE, startDate)
        signUpProperty.addAttribute(MoEngageConstants.END_DATE, endDate)

        MoEngageUtils.sendEvent(this, MoEngageConstants.MOENGAGE_EVENT_POSTED_JOB, signUpProperty)
    }

}