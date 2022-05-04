package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.HomeAdapter
import com.example.ticktapp.adapters.ViewPagerAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchJobBinding
import com.app.core.model.jobmodel.JobModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.model.registration.Location
import com.example.ticktapp.mvvm.viewmodel.SearchJobViewModel
import com.example.ticktapp.util.BottomSheetPermissionFragment
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.MoEngageUtils
import com.example.ticktapp.util.SingleShotLocationGetProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.mixpanel.android.mpmetrics.MixpanelAPI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.moengage.core.Properties
import org.json.JSONObject
import com.google.android.gms.maps.MapsInitializer





public class SearchJobActivity : BaseActivity(), OnMapReadyCallback,
    ViewPager.OnPageChangeListener {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var mBinding: ActivitySearchJobBinding
    private lateinit var map: SupportMapFragment
    private val mViewModel by lazy { ViewModelProvider(this).get(SearchJobViewModel::class.java) }
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var mRecAdapter: HomeAdapter
    private var googleMap: GoogleMap? = null
    private val recList by lazy { ArrayList<JobRecModel>() }
    private val allMarker by lazy { ArrayList<Marker>() }
    private val recFragList by lazy { ArrayList<Fragment>() }
    private val recTitles by lazy { ArrayList<String>() }
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var isViewMore: Boolean = false
    private var isLoad: Boolean = false
    private var isSearchType: Int = 0;
    private var amount: String = ""
    private var sortBy: String = ""
    private var start_date: String = ""
    private var end_date: String = ""
    private var jobType: String = ""
    private val jobTypes by lazy { ArrayList<String>() }
    private val specailsations by lazy { ArrayList<String>() }
    private var jobName: String = ""
    private var tradeId: String = ""
    private var oldMarker: Marker? = null
    private var data: JobModel? = null
    private var event_location = ""
    private var event_category = ""
    private var event_startDate = ""
    private var event_endDate = ""
    private var event_maxBudget = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_job)
        try {
            MapsInitializer.initialize(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        map = supportFragmentManager.findFragmentById(mBinding.map.id) as SupportMapFragment;
        map.getMapAsync(this);
        getIntentData()
        setBottomSheet()
        setupView()
        setListener()
        setObservers()
        callSearchAPI(false)
    }

    private fun getIntentData() {
        isViewMore = intent.getBooleanExtra("isViewMore", false)
        if (intent.hasExtra("data") && intent.getSerializableExtra("data") != null) {
            data = intent.getSerializableExtra("data") as JobModel
            if (data != null && data!!.name != null) {
                mBinding.rlToolbar.tvJobTitle.text = data!!.name
            }
        }
        if (intent.hasExtra("isLoad")) {
            isLoad = intent.getBooleanExtra("isLoad", false)
        }
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);
        if (intent.hasExtra("jobType")) {
            jobType = intent.getStringExtra("jobType").toString()
            jobName = intent.getStringExtra("jobName").toString()
            mBinding.rlToolbar.tvJobTitle.text = getString(R.string.all_around_me)
        }
        isSearchType = intent.getIntExtra("isSearchType", 0)
        if (intent.hasExtra("amount") && intent.getStringExtra("amount") != null)
            amount = intent.getStringExtra("amount").toString()
        if (intent.hasExtra("start_date") &&
            intent.getStringExtra("start_date") != null &&
            intent.getStringExtra("start_date")?.length!! > 0
        ) {
            start_date = intent.getStringExtra("start_date").toString()
            if (intent.hasExtra("end_date"))
                end_date = intent.getStringExtra("end_date").toString()
            if (start_date == end_date) {
                mBinding.rlToolbar.tvSearchDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    start_date
                )
            } else {
                if (end_date.split("-")[0] == start_date.split("-")[0]) {
                    mBinding.rlToolbar.tvSearchDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        start_date
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        end_date
                    )
                } else {
                    mBinding.rlToolbar.tvSearchDate.text =
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            start_date
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            end_date
                        )
                }
            }
        }
        if (intent.hasExtra("location") && intent.getStringExtra("location") != null && intent.getStringExtra(
                "location"
            )?.length!! > 0
        ) {
            mBinding.rlToolbar.tvSearchPlace.text = intent.getStringExtra("location")
        }
    }

    private fun setListener() {
        mBinding.rlToolbar.searchToolbarBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.rlToolbar.tvSearchPlace.setOnClickListener {
            startActivityForResult(
                Intent(
                    SearchJoActivyt@ this,
                    SearchLocationActivity::class.java
                ).putExtra("lat", lat).putExtra("lng", lng)
                    .putExtra("location", mBinding.rlToolbar.tvSearchPlace.text.toString()), 1310
            )
        }
        mBinding.rlToolbar.tvSearchDate.setOnClickListener {
            startActivityForResult(
                Intent(
                    SearchJoActivyt@ this,
                    SearchCalendarActivity::class.java
                ).putExtra("start_date", start_date).putExtra("end_date", end_date), 2610
            )
        }
        mBinding.rlToolbar.tvFilter.setOnClickListener {
            startActivityForResult(
                Intent(SearchJobActivity@ this, FilterActivity::class.java)
                    .putExtra("jobType", jobTypes)
                    .putExtra("amount", amount)
                    .putExtra("sortBy", sortBy)
                    .putExtra("isSearchType", isSearchType)
                    .putExtra("specailsations", specailsations)
                    .putExtra("tradeID", tradeId),
                1326
            )
        }
    }

    private fun setupView() {
        adapter = ViewPagerAdapter(supportFragmentManager)
        mBinding.vpJobData.adapter = adapter
        mRecAdapter = HomeAdapter(recList)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvSearchResult.layoutManager = layoutRecManager
        mBinding.rvSearchResult.adapter = mRecAdapter;
        mBinding.vpJobData.clipToPadding = false
        mBinding.vpJobData.setPadding(60, 0, 60, 0);
        mBinding.vpJobData.pageMargin = 40
        mBinding.vpJobData.addOnPageChangeListener(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
    }

    private fun callSearchAPI(isFilter: Boolean) {
        val coordinates = arrayListOf(lng, lat)
        val coordinates1 = arrayListOf(0.0, 0.0)
        val location = Location(coordinates)
        val location1 = Location(coordinates1)
        val params = HashMap<String, Any>()
        params.put("page", 1)
        params.put("isFiltered", isFilter)

        if (lat != 0.0) {
            params.put("location", location)
            event_location = "$lat , $lng"
        } else {
            params.put("location", location1)
            event_location = "${location1.location[0]} , ${location1.location[1]}"
        }

        if (jobTypes.size > 0)
            params.put("jobTypes", jobTypes)
        else if (jobType != null && jobType.length > 0) {
            params.put("jobTypes", arrayListOf(jobType))
        }
        if (!start_date.equals("")) {
            params.put("from_date", start_date)
            event_startDate = start_date
            if (!start_date.equals(end_date)) {
                params.put("to_date", end_date)
                event_endDate = end_date
            } else {
                event_endDate = ""
            }
        } else {
            event_startDate = ""
        }
        if (tradeId != null && !tradeId.equals("")) {
            val trades = arrayListOf<String>(tradeId)
            params.put("tradeId", trades)
            if (specailsations.size > 0)
                params.put("specializationId", specailsations)
        } else if (data != null && data?._id != null && data?._id.toString() != "") {
            val trades = arrayListOf<String>(data?._id!!)
            params.put("tradeId", trades)
            val spellArr = arrayListOf<String>(data!!.specializationsId.toString())
            if (spellArr.size > 0)
                params.put("specializationId", spellArr)
        }
        if (sortBy != null && !sortBy.equals("")) {
            params.put("sortBy", sortBy.toInt())
        } else {
            params.put("sortBy", 1)
        }
        if (!amount.equals("")) {
            params.put("max_budget", amount.toInt())
            event_maxBudget = amount
        } else {
            event_maxBudget = "0"
        }
        if (isSearchType != 0) {
            if (isSearchType == 1)
                params.put("pay_type", "Per hour")
            else
                params.put("pay_type", "Fixed price")
        }
        if (oldMarker != null) {
            oldMarker?.zIndex = 50f
        }
        oldMarker = null
        mViewModel.search(params)

    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.SEARCH -> {
                mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                mBinding.tvResultTitle.visibility = View.GONE
                if (mRecAdapter != null) {
                    recList.clear()
                    mRecAdapter.notifyDataSetChanged()
                }
                if (adapter != null) {
                    adapter.clearAll()
                    adapter.notifyDataSetChanged()
                }
                if (googleMap != null)
                    googleMap!!.clear()
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.SEARCH -> {
                recList.clear()
                mViewModel.jobRectModelList.let {
                    recList.addAll(it)
                }
                recFragList.clear()
                recTitles.clear()
                recList.forEach {
                    recFragList.add(JobDataFragment.getInstance(it))
                    recTitles.add("")
                }
                if (recTitles.size == 0) {
                    mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                    mBinding.tvResultTitle.visibility = View.GONE
                } else {
                    mBinding.tvResultTitleNoData.visibility = View.GONE
                    mBinding.tvResultTitle.visibility = View.VISIBLE
                }
                adapter.addFragments(recFragList, recTitles)
                mBinding.vpJobData.adapter = adapter
                mBinding.tvResultTitle.text =
                    recList.size.toString() + " " + getString(R.string.results)
                mRecAdapter.notifyDataSetChanged()
                addMapMarkers()

                try {
                    event_category = recList[0]?.let {
                        it
                        recList[0]?.tradeName?.let {
                            it!!
                        }.toString()
                    }
                } catch (e: Exception) {
                    event_category = ""
                }

                searchJobsMoEngage(
                    event_category,
                    event_location,
                    event_maxBudget,
                    event_startDate,
                    event_endDate
                )
                searchJobsMixPanel(
                    event_category,
                    event_location,
                    event_maxBudget,
                    event_startDate,
                    event_endDate
                )
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun addMapMarkers() {
        if (googleMap != null) {
            googleMap!!.clear()
        }
        if (allMarker != null) {
            allMarker.clear()
        }
        recList.forEach {
            val location = LatLng(it.location.location[1], it.location.location[0])
            val marker =
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.status_unselected))
                    .position(location).snippet(it.jobId).zIndex(50f)
            allMarker.add(googleMap!!.addMarker(marker)!!)
        }
        if (allMarker.size > 0) {
            oldMarker = allMarker[0]
            oldMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.status_selected))
            oldMarker?.zIndex = 60f
        }
        if (lat != 0.0 && !isViewMore) {
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 14f))
        } else {
            if (PreferenceManager.getString(PreferenceManager.LAT) != null &&
                !PreferenceManager.getString(PreferenceManager.LAT).equals("")
            ) {
                PreferenceManager.getString(PreferenceManager.LAT)?.let {
                    PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                        if (it.toDouble() == -37.8136) {
                            BottomSheetPermissionFragment(
                                this,
                                object : BottomSheetPermissionFragment.OnPermissionResult {
                                    override fun onPermissionAllowed() {
                                        SingleShotLocationGetProvider.requestSingleUpdate(false,
                                            this@SearchJobActivity,
                                            object :
                                                SingleShotLocationGetProvider.LocationCallback {
                                                override fun onNewLocationAvailable(location: SingleShotLocationGetProvider.GPSCoordinates?) {
                                                    googleMap!!.animateCamera(
                                                        CameraUpdateFactory.newLatLngZoom(
                                                            LatLng(
                                                                location?.latitude?.toDouble()!!,
                                                                location?.longitude?.toDouble()
                                                            ), 14f
                                                        )
                                                    )
                                                }

                                                override fun onCurrentLocationNotFound() {
                                                    googleMap!!.animateCamera(
                                                        CameraUpdateFactory.newLatLngZoom(
                                                            LatLng(
                                                                it.toDouble(), it1.toDouble()
                                                            ), 14f
                                                        )
                                                    )
                                                }
                                            })

                                    }

                                    override fun onPermissionDenied() {
                                        googleMap!!.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(
                                                    it.toDouble(), it1.toDouble()
                                                ), 14f
                                            )
                                        )
                                    }
                                },
                                arrayOf(
                                    BottomSheetPermissionFragment.ACCESS_COARSE_LOCATION,
                                    BottomSheetPermissionFragment.ACCESS_FINE_LOCATION
                                )
                            ).show(supportFragmentManager, "")

                        } else {
                            googleMap!!.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        it.toDouble(), it1.toDouble()
                                    ), 14f
                                )
                            )
                        }
                    }
                }
            } else {
                googleMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            -37.8136,
                            144.9631
                        ), 14f
                    )
                )
            }
        }


        googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                bottomSheetBehavior.peekHeight = 210
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val id = marker.snippet;
                if (oldMarker != null) {
                    oldMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.status_unselected))
                    oldMarker?.zIndex = 50f
                }
                oldMarker = marker
                recList.forEachIndexed { index, item ->
                    run {
                        if (item.jobId.equals(id)) {
                            mBinding.vpJobData.currentItem = index
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.status_selected))
                            marker.zIndex = 60f
                        }
                    }
                }
                return true;
            }
        })
    }

    private fun setBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from<LinearLayout>(mBinding.llMainBottom)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.e("collop", "collop")
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        if (bottomSheetBehavior.peekHeight != 165)
                            bottomSheetBehavior.peekHeight = 165
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehavior.setState(
                        BottomSheetBehavior.STATE_COLLAPSED
                    )
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    else -> {
                    }
                }
                Log.e("collop", "collop" + newState)


            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        bottomSheetBehavior.peekHeight = 850
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (oldMarker != null) {
            oldMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.status_unselected))
            oldMarker?.zIndex = 50f

            allMarker.forEach {
                if (it.snippet == recList[position].jobId) {
                    oldMarker = allMarker[position]
                    oldMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.status_selected))
                    oldMarker?.zIndex = 60f
                }
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onBackPressed() {
        if (isLoad) {
            startActivity(
                Intent(this, HomeActivity::class.java)
            )
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            val location = data?.getStringExtra("location")
            mBinding.rlToolbar.tvSearchPlace.text = location
            lat = data!!.getDoubleExtra("lat", 0.0)
            lng = data!!.getDoubleExtra("lng", 0.0)
            isViewMore = false
            callSearchAPI(false)
        } else if (requestCode == 2610 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("start_date")) {
                start_date = data!!.getStringExtra("start_date").toString()
                end_date = data!!.getStringExtra("end_date").toString()
                if (start_date == end_date) {
                    mBinding.rlToolbar.tvSearchDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        start_date
                    )
                } else if (end_date == null || end_date.equals("null") || end_date.equals("")) {
                    mBinding.rlToolbar.tvSearchDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        start_date
                    )
                    end_date = ""
                } else {
                    if (end_date.split("-")[0] == start_date.split("-")[0]) {
                        mBinding.rlToolbar.tvSearchDate.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            start_date
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            end_date
                        )
                    } else {
                        mBinding.rlToolbar.tvSearchDate.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            start_date
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            end_date
                        )
                    }
                }
                callSearchAPI(false)
            }
        } else if (requestCode == 1326 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (isViewMore) {
                    lat = 0.0
                    lng = 0.0
                }
                if (data.hasExtra("tradeID") && data.getStringExtra("tradeID") != null) {
                    tradeId = data.getStringExtra("tradeID").toString()
                } else {
                    tradeId = ""
                }
                jobTypes.clear()
                jobType = ""
                if (data.hasExtra("jobType") && data.getSerializableExtra("jobType") != null) {
                    var jobType = data.getSerializableExtra("jobType") as List<JobModel>
                    jobType.forEachIndexed { index, element ->
                        jobTypes.add(element._id.toString())
                    }
                }
                specailsations.clear()
                this.data = null
                val specName = ArrayList<String>()
                if (data.hasExtra("specialisation") && data.getSerializableExtra("specialisation") != null) {
                    val specialisation =
                        data.getSerializableExtra("specialisation") as List<Specialisation>
                    specialisation.forEachIndexed { index, element ->
                        specailsations.add(element.id.toString())
                        specName.add(element.name.toString())
                    }
                }

                if (specailsations.size == 1)
                    mBinding.rlToolbar.tvJobTitle.text = specName[0]
                else if (specailsations.size > 1) {
                    if (data.hasExtra("isAllSelected")) {
                        if (data.hasExtra("tradeName")) {
                            var tradeName = data.extras!!.getString("tradeName", "")
                            mBinding.rlToolbar.tvJobTitle.text = tradeName
                        }
                    } else {
                        mBinding.rlToolbar.tvJobTitle.text =
                            specName[0] + " +" + (specName.size - 1) + " " + getString(R.string.more)
                    }
                } else {
                    mBinding.rlToolbar.tvJobTitle.text = getString(R.string.all_around_me)
                }


                /* if (specailsations.size == 1)
                     mBinding.rlToolbar.tvJobTitle.text = specName[0]
                 else if (specailsations.size > 1) {
                     mBinding.rlToolbar.tvJobTitle.text =
                         specName[0] + " +" + (specName.size - 1) + " " + getString(R.string.more)
                 } else if (data.hasExtra("tradeName")) {
                     mBinding.rlToolbar.tvJobTitle.text = data.getStringExtra("tradeName")
                 } else {
                     mBinding.rlToolbar.tvJobTitle.text = getString(R.string.all_around_me)
                 }*/
                amount = ""
                sortBy = ""
                if (data.hasExtra("amount") && data.getStringExtra("amount") != null) {
                    amount = data.getStringExtra("amount").toString()
                }
                if (data.hasExtra("sortBy") && data.getStringExtra("sortBy") != null) {
                    sortBy = data.getStringExtra("sortBy").toString()
                }
                if (data.hasExtra("isSearchType")) {
                    isSearchType = data.getIntExtra("isSearchType", 0)
                }
                if (data.hasExtra("filterCount") && data.getStringExtra("filterCount") != null && data.getStringExtra(
                        "filterCount"
                    ) != "0"
                ) {
                    mBinding.rlToolbar.tvFilterCount.visibility = View.VISIBLE
                    mBinding.rlToolbar.tvFilterCount.text = data.getStringExtra("filterCount")
                } else {
                    mBinding.rlToolbar.tvFilterCount.visibility = View.GONE
                }
                callSearchAPI(true)
            }
        }
    }

    private fun searchJobsMoEngage(
        category: String,
        location: String,
        max_budget: String = "0",
        startDate: String,
        endDate: String
    ) {

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()

        var differenceInDays = if (isDate(startDate) && isDate(endDate)) {
            val date1: Date
            val date2: Date
            val dates = SimpleDateFormat("MM/dd/yyyy")
            date1 = dates.parse(startDate)
            date2 = dates.parse(endDate)
            val difference: Long = Math.abs(date1.time - date2.time)
            val differenceDates = difference / (24 * 60 * 60 * 1000)
            val dayDifference = differenceDates.toString()
            dayDifference.toString()
        } else {
            "0"
        }
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)
        signUpProperty.addAttribute(MoEngageConstants.LOCATION, location)
        signUpProperty.addAttribute(MoEngageConstants.MAX_BUDGET, max_budget)
        signUpProperty.addAttribute(MoEngageConstants.LENGTH_OF_HIRE, differenceInDays)
        signUpProperty.addAttribute(MoEngageConstants.START_DATE, startDate)
        signUpProperty.addAttribute(MoEngageConstants.END_DATE, endDate)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_SEARCHED_FOR_TRADIE,
            signUpProperty
        )
    }

    private fun searchJobsMixPanel(
        category: String,
        location: String,
        max_budget: String = "0",
        startDate: String,
        endDate: String
    ) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        var differenceInDays = if (isDate(startDate) && isDate(endDate)) {
            val date1: Date
            val date2: Date
            val dates = SimpleDateFormat("MM/dd/yyyy")
            date1 = dates.parse(startDate)
            date2 = dates.parse(endDate)
            val difference: Long = Math.abs(date1.time - date2.time)
            val differenceDates = difference / (24 * 60 * 60 * 1000)
            val dayDifference = differenceDates.toString()
            dayDifference.toString()
        } else {
            "0"
        }
        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)
        props.put(MoEngageConstants.LOCATION, location)
        props.put(MoEngageConstants.MAX_BUDGET, max_budget)
        props.put(MoEngageConstants.LENGTH_OF_HIRE, differenceInDays)
        props.put(MoEngageConstants.START_DATE, startDate)
        props.put(MoEngageConstants.END_DATE, endDate)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_SEARCHED_FOR_TRADIE,
            props
        )
    }

    fun isDate(dateStr: String): Boolean {
        var isValidDate: Boolean = false
        try {
            var formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val date = formatter.parse(dateStr)
            isValidDate = true
            println(date)
        } catch (e: Exception) {
            isValidDate = false
        }
        return isValidDate
    }

}