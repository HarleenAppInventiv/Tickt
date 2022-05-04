package com.example.ticktapp.mvvm.view.builder.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.jobmodel.JobModel
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.model.tradesmodel.TradeHome
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.HomeTradieAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivitySearchTradieBinding
import com.example.ticktapp.model.registration.Location
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.builder.TradieProfileActivity
import com.example.ticktapp.mvvm.view.tradie.FilterActivity
import com.example.ticktapp.mvvm.view.tradie.SearchCalendarActivity
import com.example.ticktapp.mvvm.view.tradie.SearchLocationActivity
import com.example.ticktapp.mvvm.viewmodel.SearchTradieViewModel
import com.example.ticktapp.util.DateUtils
import com.example.ticktapp.util.MoEngageUtils
import com.google.android.gms.maps.model.*
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import org.json.JSONObject
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


public class SearchTradieActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySearchTradieBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(SearchTradieViewModel::class.java) }
    private lateinit var mRecAdapter: HomeTradieAdapter
    private val recList by lazy { ArrayList<TradeHome>() }
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var latSort: Double = 0.0
    private var lngSort: Double = 0.0
    private var isViewMore: Boolean = false
    private var isIgnoreLast: Boolean = false
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
    private var data: JobModel? = null
    private var page: Int = 1
    private var event_location = ""
    private var event_category = ""
    private var event_startDate = ""
    private var event_endDate = ""
    private var event_maxBudget = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_tradie)
        getIntentData()
        setupView()
        setListener()
        setObservers()
        page = 1
        callSearchAPI(true)
    }

    private fun getIntentData() {
        isViewMore = intent.getBooleanExtra("isViewMore", false)
        if (intent.hasExtra("data") && intent.getSerializableExtra("data") != null) {
            data = intent.getSerializableExtra("data") as JobModel
            if (data != null && data!!.name != null) {
                mBinding.rlToolbar.tvJobTitle.text = data!!.name
                //tradeId = data!!._id.toString()
                // data!!.specializationsId?.let { specailsations.add(it) }
            }
        } else {
            mBinding.rlToolbar.tvJobTitle.text = getString(R.string.all_around_me_)
        }
        if (intent.hasExtra("isLoad")) {
            isLoad = intent.getBooleanExtra("isLoad", false)
        }
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);
        if (intent.hasExtra("jobType")) {
            jobType = intent.getStringExtra("jobType").toString()
            jobName = intent.getStringExtra("jobName").toString()
        }

        if (intent.hasExtra("tradeID")) {
            tradeId = intent.getStringExtra("tradeID").toString()

            if (intent.hasExtra("specailsations")) {
                mBinding.rlToolbar.tvJobTitle.text = getString(R.string.all)


                //   specailsations.addAll(intent.getSerializableExtra("specailsations") as ArrayList<String>)
            }
            if (intent.hasExtra("tradeName")) {
                mBinding.rlToolbar.tvJobTitle.text = intent.getStringExtra("tradeName")


                //   specailsations.addAll(intent.getSerializableExtra("specailsations") as ArrayList<String>)
            }
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
        mBinding.rlSearchResult.setOnRefreshListener {
            if (it == SwipyRefreshLayoutDirection.TOP) {
                page = 1
                callSearchAPI(false)
            } else {
                callSearchAPI(false)
            }
        }
        mBinding.rlToolbar.tvJobTitle.setOnClickListener {
            startActivityForResult(Intent(this, OnlyCategorySearchActivity::class.java), 2021)
        }
        mBinding.rlToolbar.tvSearchPlace.setOnClickListener {
            startActivityForResult(
                Intent(
                    SearchJoActivyt@ this,
                    SearchLocationActivity::class.java
                ).putExtra("isTradie", true).putExtra("lat", lat).putExtra("lng", lng)
                    .putExtra("location", mBinding.rlToolbar.tvSearchPlace.text.toString()), 1310
            )
        }
        mBinding.rlToolbar.tvSearchDate.setOnClickListener {
            startActivityForResult(
                Intent(
                    SearchJoActivyt@ this,
                    SearchCalendarActivity::class.java
                ).putExtra("isTradie", true)
                    .putExtra("start_date", start_date).putExtra("end_date", end_date), 2610
            )
        }
        mBinding.rlToolbar.tvFilter.setOnClickListener {
            startActivityForResult(
                Intent(SearchJobActivity@ this, FilterActivity::class.java)
                    .putExtra("isTradie", true)
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
        mRecAdapter = HomeTradieAdapter(recList) {
            val pos = it?.tag as Int
            startActivity(
                Intent(
                    this,
                    TradieProfileActivity::class.java
                ).putExtra("data", recList[pos])
                    .putExtra("isBuilder", true)
            )
        }
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvSearchResult.layoutManager = layoutRecManager
        mBinding.rvSearchResult.adapter = mRecAdapter;
    }


    private fun callSearchAPI(progress: Boolean) {
        val coordicnates = arrayListOf(lng, lat)
        val location = Location(coordicnates)
        val params = HashMap<String, Any>()
        params.put("page", page)
        params.put("isFiltered", true)

        if (lat != 0.0) {
            params.put("location", location)
            event_location = "${location.location[0]} , ${location.location[1]}"
        } else if (latSort != 0.0) {
            val coordinates = arrayListOf(lngSort, latSort)
            val location = Location(coordinates)
            params.put("location", location)
            event_location = "$latSort , $lngSort"
        }

        if (jobTypes.size > 0)
            params.put("jobTypes", jobTypes)
        else if (jobType != null && jobType.length > 0) {
            params.put("jobTypes", arrayListOf(jobTypes))
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
        } else {
            if (!isIgnoreLast) {
                if (intent.hasExtra("tradeID")) {
                    val id = intent.getStringExtra("tradeID").toString()
                    val trades = arrayListOf<String>(id)
                    params.put("tradeId", trades)
                }
                if (intent.hasExtra("specailsations")) {
                    val sep = intent.getSerializableExtra("specailsations") as ArrayList<String>
                    params.put("specializationId", sep)
                }
            }
        }
        if (sortBy != null && !sortBy.equals("")) {
            params.put("sortBy", sortBy.toInt())
        } else {
            params.put("sortBy", 1)
        }
        if (!amount.equals("")) {
            params.put("max_budget", amount.toDouble())

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
        Log.i("callSearchAPI", "callSearchAPI: ${params.toString()}")

        mViewModel.search(params, progress)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.SEARCH -> {
                mBinding.rlSearchResult.isRefreshing = false
                mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                mBinding.tvResultTitle.visibility = View.GONE
                if (mRecAdapter != null) {
                    recList.clear()
                    mRecAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.SEARCH -> {
                try {
                    mBinding.rlSearchResult.isRefreshing = false
                    if (page == 1)
                        recList.clear()
                    mViewModel.tradeHome.let {
                        recList.addAll(it)
                    }

                    if (recList.size == 0) {

                        mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                        mBinding.tvResultTitle.visibility = View.GONE
                    } else {
                        mBinding.tvResultTitleNoData.visibility = View.GONE
                        mBinding.tvResultTitle.visibility = View.VISIBLE
                    }
                    mBinding.tvResultTitle.text =
                        recList.size.toString() + " " + getString(R.string.results)
                    mRecAdapter.notifyDataSetChanged()
                    page++

                    try {
                        event_category = recList[0]?.let {
                            it
                            recList[0]?.tradeData?.get(0)?.let {
                                it.tradeName!!
                            }.toString()
                        }
                    } catch (e: Exception) {
                        event_category = ""
                    }

                    searchedForTradiesMoEngage(
                        event_category,
                        event_location,
                        event_maxBudget,
                        event_startDate,
                        event_endDate
                    )
                    searchForTradieMixPanel(
                        event_category,
                        event_location,
                        event_maxBudget,
                        event_startDate,
                        event_endDate
                    )
                } catch (e: Exception) {
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    override fun onBackPressed() {
        if (isLoad) {
            startActivity(
                Intent(this, HomeBuilderActivity::class.java)
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
            page = 1
            isViewMore = false
            callSearchAPI(true)
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
                page = 1
                callSearchAPI(true)
            }
        } else if (requestCode == 1326 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                isIgnoreLast = true
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
                } else if (data.hasExtra("tradeName")) {
                    var tradeName = data.extras!!.getString("tradeName", "")
                    mBinding.rlToolbar.tvJobTitle.text = tradeName
                } else {
                    mBinding.rlToolbar.tvJobTitle.text = getString(R.string.all_around_me)
                }

                /*commented just to check above else if code that is written by me - Manav Mittal
                 else if (data.hasExtra("tradeName")) {
                     mBinding.rlToolbar.tvJobTitle.text = data.getStringExtra("tradeName")
                 } else {
                     mBinding.rlToolbar.tvJobTitle.text = getString(R.string.all_around_me_)
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
                if (data.hasExtra("lats")) {
                    latSort = data.getDoubleExtra("lats", 0.0)
                    lngSort = data.getDoubleExtra("lngs", 0.0)
                } else {
                    latSort = 0.0
                    lngSort = 0.0
                }
                page = 1
                callSearchAPI(true)
            }
        } else if (requestCode == 2021 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val jobData = data.getSerializableExtra("data") as JobModel
                specailsations.clear()
                if (jobData != null && jobData!!.name != null) {
                    mBinding.rlToolbar.tvJobTitle.text = jobData!!.name
                    tradeId = jobData!!._id.toString()
                    jobData!!.specializationsId?.let { specailsations.add(it) }
                }
                page = 1
                callSearchAPI(true)
            }
        }
    }


    private fun searchedForTradiesMoEngage(
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
            val difference: Long = abs(date1.time - date2.time)
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

    private fun searchForTradieMixPanel(
        category: String,
        location: String,
        max_budget: String = "0",
        startDate: String,
        endDate: String,
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
            val difference: Long = abs(date1.time - date2.time)
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
