package com.example.ticktapp.mvvm.view.builder.postjob

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.jobmodel.JobModel
import com.app.core.model.jobmodel.JobRecModelRepublish
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.model.tradesmodel.Trade
import com.app.core.model.tradesmodel.Trades
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.JobsMoreSelectionAdapter
import com.example.ticktapp.adapters.SpecializationAdapter
import com.example.ticktapp.adapters.TradeAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityPostNewJobBinding
import com.example.ticktapp.mvvm.viewmodel.HomeViewModel
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel
import com.example.ticktapp.util.PostJobData
import com.example.ticktapp.util.toJsonString
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class PostNewJobActivity : BaseActivity(), TradeAdapter.TradeAdapterListener,
    SpecializationAdapter.SpecListAdapterListener, JobsMoreSelectionAdapter.JobAdapterListener {
    private var data: JobRecModelRepublish? = null
    private var isReturn: Boolean = false
    private val mViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }
    private val mHomeViewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private lateinit var mAdapter: TradeAdapter
    private lateinit var mSpecAdapter: SpecializationAdapter
    private lateinit var mHomeAdapter: JobsMoreSelectionAdapter
    private var specList: ArrayList<Specialisation>? = null
    private lateinit var job_type: ArrayList<JobModel>
    private lateinit var categories: ArrayList<Trades>
    private lateinit var specialization: ArrayList<Specialisation>

    companion object {
        var noSpecSelected: Boolean = false
    }

    private val list by lazy { ArrayList<Trade?>() }
    private val jobList by lazy { ArrayList<JobModel>() }
    private lateinit var mBinding: ActivityPostNewJobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.clearNewJobPrefs()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_post_new_job)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setupView()
        listener()
        setObservers()
        mViewModel.getTradeList(false)
        mHomeViewModel.getJobTypeList()
    }

    private fun getIntentData() {
        if (intent.hasExtra("isReturn")) {
            isReturn = intent.getBooleanExtra("isReturn", false)
            categories = intent.getSerializableExtra("categories") as ArrayList<Trades>
            job_type = intent.getSerializableExtra("job_type") as ArrayList<JobModel>
            if (intent.hasExtra("specialization"))
                specialization =
                    intent.getSerializableExtra("specialization") as ArrayList<Specialisation>
            else
                specialization = ArrayList()
            val jobName = intent.getStringExtra("jobName")
            mBinding.postEdJobName.setText(jobName)
        } else if (intent.hasExtra("data")) {
            mBinding.tvTitle.text = getString(R.string.edit_job)
            try {
                data = intent.getSerializableExtra("data") as JobRecModelRepublish
                val trades = Trades(
                    true,
                    data?.categories?.get(0)?.id,
                    0,
                    data?.categories?.get(0)?.selectedUrl,
                    data?.categories?.get(0)?.tradeName,
                    "",
                    "",
                    "",
                    0,
                    "",
                    "",
                )
                val tradesArrayList = ArrayList<Trades>()
                tradesArrayList.add(trades)
                categories = tradesArrayList
                val jobModel = JobModel(
                    data?.jobType?.get(0)?.jobTypeId,
                    data?.jobType?.get(0)?.jobTypeName,
                    data?.jobType?.get(0)?.jobTypeImage,
                    "",
                    "",
                    "",
                    true,
                )
                val jobArrayList = ArrayList<JobModel>()
                jobArrayList.add(jobModel)
                job_type = jobArrayList
                if (data?.specializationData != null) {
                    val spesData = ArrayList<Specialisation>()
                    data?.specializationData!!.forEach {
                        spesData.add(
                            Specialisation(
                                true,
                                it.specializationId,
                                1,
                                "",
                                "",
                                "",
                                it.specializationName,
                                0
                            )
                        )
                    }
                    specialization = spesData
                } else
                    specialization = ArrayList()
                val jobName = data?.jobName
                mBinding.postEdJobName.setText(jobName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            categories = ArrayList()
            job_type = ArrayList()
            specialization = ArrayList()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (PostJobData.postjobDataNullCheck()) {
            PostJobData.clearMilestones()
        }
    }

    private fun listener() {
        mBinding.postIvBack.setOnClickListener { onBackPressed() }
        mBinding.tvPostSubmit.setOnClickListener {
            if (isValid()) {
                val cateList = ArrayList<Trades>()
                list.forEach {
                    if (it?.isSelected == true) {
                        val trad = Trades(
                            id = it.id,
                            tradeId = it.tradeId,
                            name = it.name,
                            tradeName = it.tradeName,
                            tradeImg = it.tradeImg,
                            selectedUrl = it.selectedUrl,
                        )
                        cateList.add(trad)
                    }
                }
                val jobLists = ArrayList<JobModel>()
                jobList.forEach {
                    if (it.isSelected == true) {
                        jobLists.add(it)
                    }
                }
                val specLists = ArrayList<Specialisation>()

                if (specList != null) {
                    specList?.forEach {
                        if (it.isSelected == true) {
                            if (it.id != null && !it.id.equals(""))
                                specLists.add(it)
                        }
                    }
                }
                /*if (specLists.size == 0) {
                    showToastShort("Please select specialization")
                    return@setOnClickListener
                }*/
                if (specLists.size == 0) {
                    noSpecSelected = true
                    specList?.forEach {
                        if (it.id != null && !it.id.equals("")) {
                            specLists.add(it)
                            it.isSelected = true
                        }
                    }
//                    mSpecAdapter.notifyDataSetChanged()
                } else {
                    noSpecSelected = false
                }
                PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_NAME,mBinding.postEdJobName.text.toString())
                PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.CATEGORIES,cateList.toJsonString())
                PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_TYPE,jobLists.toJsonString())
                PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_SPECS,specLists.toJsonString())
                if (isReturn) {
                    setResult(
                        Activity.RESULT_OK,
                        Intent().putExtra("jobName", mBinding.postEdJobName.text.toString())
                            .putExtra("categories", cateList)
                            .putExtra("job_type", jobLists)
                            .putExtra("specialization", specLists)
                    )
                    finish()
                } else {


                    if (data != null) {
                        startActivity(
                            Intent(
                                this,
                                LocationActivity::class.java
                            )/*.putExtra("jobName", mBinding.postEdJobName.text.toString())
                                .putExtra("categories", cateList)
                                .putExtra("job_type", jobLists)
                                .putExtra("specialization", specLists).putExtra("data", data)*/
                        )
                        PreferenceManager.putString(PreferenceManager.NEW_JOB_PREF.JOB_DATA,data.toJsonString())

                    } else {
                        startActivity(
                            Intent(
                                this,
                                LocationActivity::class.java
                            )/*.putExtra("jobName", mBinding.postEdJobName.text.toString())
                                .putExtra("categories", cateList)
                                .putExtra("job_type", jobLists)
                                .putExtra("specialization", specLists)*/
                        )
                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        if (mBinding.postEdJobName.text.toString().length == 0) {
            showToastShort(getString(R.string.please_enter_job_name))
            return false
        }
        var isCategorySelected = false
        list.forEach {
            if (it?.isSelected == true) {
                isCategorySelected = true
            }
        }
        if (!isCategorySelected) {
            showToastShort(getString(R.string.please_select_category))
            return false
        }
        isCategorySelected = false
        jobList.forEach {
            if (it?.isSelected == true) {
                isCategorySelected = true
            }
        }
        if (!isCategorySelected) {
            showToastShort(getString(R.string.please_select_job_type))
            return false
        }
        return true
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

    private fun setupView() {
        mAdapter = TradeAdapter(this, list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCategories.layoutManager = layoutManager
        mBinding.rvCategories.adapter = mAdapter
        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        specList = ArrayList()
        mSpecAdapter = specList?.let {
            SpecializationAdapter(it, this, fromPostJob = true)
        }!!
        mBinding.rvSpecialization.layoutManager = flexboxLayoutManager
        mBinding.rvSpecialization.adapter = mSpecAdapter

        mHomeAdapter = JobsMoreSelectionAdapter(this, jobList, true)
        val jobLayoutManager = GridLayoutManager(this, 3)

        mBinding.rvJobList.layoutManager = jobLayoutManager
        mBinding.rvJobList.adapter = mHomeAdapter

        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobList, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvCategories, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvSpecialization, false)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mHomeViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                mBinding.tvRvH1Category.visibility = View.GONE
            }
            ApiCodes.JOB_TYPE_LIST -> {
                mBinding.tvRvH2JobTypes.visibility = View.GONE
                mBinding.rvJobList.visibility = View.GONE
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
                list.clear()
                mViewModel.mTradeListingResponseModel.trade?.let {
                    list.addAll(it)
                }
                if (list.size == 0) {
                    mBinding.tvRvH1Category.visibility = View.GONE
                } else {
                    mBinding.tvRvH1Category.visibility = View.VISIBLE
                }
                if (list.size > 0 && categories != null && categories.size > 0) {
                    list.forEachIndexed { _, elements1 ->
                        categories.forEachIndexed { _, elements2 ->
                            if (elements1?.id == elements2.id) {
                                elements1?.isSelected = true
                                if (specialization != null && specialization.size > 0 && elements1?.specialisations != null && elements1.specialisations!!.size > 0) {
                                    elements1.specialisations!!.forEachIndexed { _, spIndex ->
                                        specList?.add(spIndex)

                                        run lit@{
                                            specialization.forEachIndexed { _, spIndex1 ->
                                                if (specList!![0].name == "All" && specList!![0].isSelected!!) {
                                                    return@forEachIndexed
                                                } else if (specialization.size == elements1.specialisations!!.size) {
                                                    addAllInSpecList(true)
                                                    return@lit
                                                } else if (spIndex.id == spIndex1.id) {
                                                    spIndex.isSelected = true
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (specList!![0].name != "All") {
                        addAllInSpecList(false)
                    }
                }
                mAdapter.notifyDataSetChanged()
                if (specialization != null && specialization.size > 0) {
                    mBinding.tvRvH3Specailatisation.visibility = View.VISIBLE
                    mBinding.rvSpecialization.visibility = View.VISIBLE
                    mSpecAdapter.notifyDataSetChanged()
                }
            }
            ApiCodes.JOB_TYPE_LIST -> {
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        jobList.clear()
                        mHomeViewModel.mJsonResponseModel.jobModelList?.let {
                            jobList.addAll(it)
                        }
                        if (jobList.size == 0) {
                            mBinding.rvJobList.visibility = View.GONE
                            mBinding.tvRvH2JobTypes.visibility = View.GONE
                        } else {
                            mBinding.rvJobList.visibility = View.VISIBLE
                            mBinding.tvRvH2JobTypes.visibility = View.VISIBLE
                        }
                        if (jobList.size > 0 && job_type != null && job_type.size > 0) {
                            jobList.forEachIndexed { _, elements1 ->
                                job_type.forEachIndexed { _, elements2 ->
                                    if (elements1._id == elements2._id) {
                                        elements1.isSelected = true
                                    }
                                }
                            }
                        }
                        mHomeAdapter.notifyDataSetChanged()
                    }
                }, 1000)

            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun addAllInSpecList(b: Boolean = true) {
        specList!!.add(
            0, Specialisation(
                b,
                "",
                1,
                "",
                "",
                "",
                "All",
                0
            )
        )
    }

    override fun onTradeClick(position: Int) {
        if (list[position]?.isSelected == false) {
            list?.forEachIndexed { index, element ->
                list[index]?.isSelected = false
            }
            list[position]?.isSelected = true
            mAdapter.notifyDataSetChanged()
            if (list[position]?.specialisations != null)
                specList?.clear()
            list.get(position)?.specialisations?.let { specList?.addAll(it) }
            specList?.forEachIndexed { index, elements ->
                specList!![index].isSelected = false
            }
            specList!!.add(
                0, Specialisation(
                    true,
                    "",
                    1,
                    "",
                    "",
                    "",
                    "All",
                    0
                )
            )
            if (specList?.size!! > 0) {
                mBinding.tvRvH3Specailatisation.visibility = View.VISIBLE
                mBinding.rvSpecialization.visibility = View.VISIBLE
                mSpecAdapter.notifyDataSetChanged()
            } else {
                mBinding.rvSpecialization.visibility = View.GONE
                mBinding.tvRvH3Specailatisation.visibility = View.GONE
            }
        } else {
            list[position]?.isSelected = false
            mAdapter.notifyDataSetChanged()
            specList?.clear()
            mSpecAdapter.notifyDataSetChanged()
            mBinding.tvRvH3Specailatisation.visibility = View.GONE
            mBinding.rvSpecialization.visibility = View.GONE
        }
    }

    override fun onSpecCLick(position: Int) {
    }

    override fun onJobClick(position: Int) {
    }

}