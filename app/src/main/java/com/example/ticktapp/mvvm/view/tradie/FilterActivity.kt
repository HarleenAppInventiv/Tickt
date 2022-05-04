package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.tradesmodel.Specialisation
import com.app.core.model.tradesmodel.Trade
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.JobsSelectionAdapter
import com.example.ticktapp.adapters.SpecializationAdapter
import com.example.ticktapp.adapters.TradeAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityFilterBinding
import com.app.core.model.jobmodel.JobModel
import com.example.ticktapp.mvvm.viewmodel.HomeViewModel
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class FilterActivity : BaseActivity(), TradeAdapter.TradeAdapterListener,
    SpecializationAdapter.SpecListAdapterListener, JobsSelectionAdapter.JobAdapterListener,
    OnClickListener {
    private var isTradie: Boolean = false
    private var amount: String? = "0"
    private var tradeID: String? = ""
    private var sortBy: String? = ""
    private var selectionPos: Int? = 1
    private lateinit var jobTypes: ArrayList<String>
    private lateinit var specialisation: ArrayList<String>
    private lateinit var mBinding: ActivityFilterBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }
    private val mHomeViewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    private lateinit var mAdapter: TradeAdapter
    private lateinit var mSpecAdapter: SpecializationAdapter
    private lateinit var mHomeAdapter: JobsSelectionAdapter
    private var specList: ArrayList<Specialisation>? = null

    private val list by lazy { ArrayList<Trade?>() }
    private val jobList by lazy { ArrayList<JobModel>() }
    private var isSearchType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setupView()
        setSeekBarBudget(0)
        setData()
        setObservers()
        mViewModel.getTradeList(false)
        if (!isTradie)
            mHomeViewModel.getJobTypeList()
        else {
            mBinding.llFilterBudget.visibility = View.GONE
        }
    }

    private fun getIntentData() {
        if (intent.hasExtra("isTradie")) {
            isTradie = intent.getBooleanExtra("isTradie", false)
        }
        jobTypes = intent.getSerializableExtra("jobType") as ArrayList<String>
        amount = intent.extras!!.getString("amount", "0")
        isSearchType = intent.getIntExtra("isSearchType", 0)
        specialisation = intent.getSerializableExtra("specailsations") as ArrayList<String>
        tradeID = intent.getStringExtra("tradeID")
        sortBy = intent.getStringExtra("sortBy")
    }

    private fun setData() {
        if (isSearchType != 0) {
            if (isSearchType == 1) {
                setMinimumBudgetSelection(1)
            } else {
                setMinimumBudgetSelection(2)
            }
        }
        if (sortBy != null && !sortBy.equals("")) {
            when {
                sortBy.equals("1") -> {
                    setSelected(1)
                }
                sortBy.equals("2") -> {
                    setSelected(2)
                }
                sortBy.equals("3") -> {
                    setSelected(3)
                }
                else -> {
                    setSelected(1)
                }
            }
        } else {
            setSelected(1)
        }
        if (amount.isNullOrEmpty()) {
            amount = "0"
        }
        mBinding.tvAmount.text = amount
        setSeekBarBudget(Integer.parseInt(amount))
    }

    private fun setSelected(pos: Int) {
        selectionPos = pos
        if (pos == 0) {
            mBinding.cbIvHigestRated.setImageResource(R.drawable.radio_selected)
            mBinding.cbIvClosedToMe.setImageResource(R.drawable.radio_unselected)
            mBinding.cbIvMostJobCompleted.setImageResource(R.drawable.radio_unselected)
        } else if (pos == 1) {
            mBinding.cbIvHigestRated.setImageResource(R.drawable.radio_selected)
            mBinding.cbIvClosedToMe.setImageResource(R.drawable.radio_unselected)
            mBinding.cbIvMostJobCompleted.setImageResource(R.drawable.radio_unselected)
        } else if (pos == 2) {
            mBinding.cbIvHigestRated.setImageResource(R.drawable.radio_unselected)
            mBinding.cbIvClosedToMe.setImageResource(R.drawable.radio_selected)
            mBinding.cbIvMostJobCompleted.setImageResource(R.drawable.radio_unselected)
        } else {
            mBinding.cbIvHigestRated.setImageResource(R.drawable.radio_unselected)
            mBinding.cbIvClosedToMe.setImageResource(R.drawable.radio_unselected)
            mBinding.cbIvMostJobCompleted.setImageResource(R.drawable.radio_selected)
        }
    }

    private fun setMinimumBudgetSelection(pos: Int) {
        if (pos == 1) {
            isSearchType = 1
            mBinding.cbIvPerHour.setImageResource(R.drawable.radio_selected)
            mBinding.cbIvFixedPrice.setImageResource(R.drawable.radio_unselected)
        } else if (pos == 2) {
            isSearchType = 2
            mBinding.cbIvPerHour.setImageResource(R.drawable.radio_unselected)
            mBinding.cbIvFixedPrice.setImageResource(R.drawable.radio_selected)
        }
    }


    private fun setSeekBarBudget(value: Int) {
        mBinding.seekBudget.progress = value
        mBinding.seekBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.tvAmount.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
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
        mSpecAdapter = specList?.let { SpecializationAdapter(it, this) }!!
        mBinding.rvSpecialization.layoutManager = flexboxLayoutManager
        mBinding.rvSpecialization.adapter = mSpecAdapter

        mHomeAdapter = JobsSelectionAdapter(this, jobList)
        val jobLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        mBinding.rvJobTypes.layoutManager = jobLayoutManager
        mBinding.rvJobTypes.adapter = mHomeAdapter

        ViewCompat.setNestedScrollingEnabled(mBinding.rvJobTypes, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvCategories, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvSpecialization, false)
    }

    private fun listener() {
        mBinding.llFixedPrice.setOnClickListener { setMinimumBudgetSelection(2) }
        mBinding.llPerHour.setOnClickListener { setMinimumBudgetSelection(1) }
        mBinding.searchToolbarBack.setOnClickListener { onBackPressed() }
        mBinding.llHigestRated.setOnClickListener { setSelected(1) }
        mBinding.llClosedToMe.setOnClickListener { setSelected(2) }
        mBinding.llMostJobCompleted.setOnClickListener { setSelected(3) }
        mBinding.tvClear.setOnClickListener {
            mBinding.tvAmount.text = "0"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBinding.seekBudget.setProgress(0, true)
            } else {
                mBinding.seekBudget.progress = 0
            }
            list.forEachIndexed { index, element ->
                list[index]?.isSelected = false
            }
            jobList.forEachIndexed { index, element ->
                jobList[index].isSelected = false
            }
            setSelected(0)
            mAdapter.notifyDataSetChanged()
            mHomeAdapter.notifyDataSetChanged()
            specList?.clear()
            mSpecAdapter.notifyDataSetChanged()
            mBinding.tvRvH3Specailatisation.visibility = View.GONE
            mBinding.rvSpecialization.visibility = View.GONE
        }
        mBinding.tvFilterSubmit.setOnClickListener {
            try {
                var filterCount = 0
                var isValid = -1
                val data = Intent()
                var filterTradeType = ""
                var filterTradeName = ""

                list.forEachIndexed { index, elements ->
                    if (elements?.isSelected == true) {
                        filterTradeType = elements.id.toString()
                        filterTradeName = elements.tradeName.toString()
                    }
                }
                if (filterTradeType.isNotEmpty()) {
                    isValid = 0
                    data.putExtra("tradeID", filterTradeType)
                    data.putExtra("tradeName", filterTradeName)
                    filterCount++
                    val filterSpecialisation = ArrayList<Specialisation>()

                    run checkIfAll@{
                        specList?.forEachIndexed { index, elements ->
                            if (elements.name == "All" && elements.isSelected!!) {
                                specList!!.removeAt(0)
                                filterSpecialisation.addAll(specList!!)
                                return@checkIfAll
                            } else if (elements.isSelected == true) {
                                if (elements.id != null && !elements.id.equals(""))
                                    filterSpecialisation.add(elements)
                            }
                        }
                    }

                    if (specList!!.size == list[tradePosition]!!.specialisations!!.size) {
                        data.putExtra("isAllSelected", true)
//                        if (!tradeName.isNullOrEmpty())
//                            data.putExtra("tradeName", tradeName)
                    }
                    data.putExtra(
                        "specCount",
                        (specList?.size?.minus(1)) == filterSpecialisation.size
                    )
                    if (filterSpecialisation != null && filterSpecialisation.size > 0) {
                        filterCount++
                        data.putExtra("specialisation", filterSpecialisation)
                        isValid = 1   }

                } else {
                    isValid = 2
                }

                val filterJobType = ArrayList<JobModel>()
                jobList.forEachIndexed { index, elements ->
                    if (elements.isSelected == true) {
                        filterJobType.add(elements)
                    }
                }
                if (filterJobType.size > 0) {
                    data.putExtra("jobType", filterJobType)
                    filterCount++
                }

                if (mBinding.tvAmount.text.toString()
                        .isNotEmpty() && mBinding.seekBudget.progress > 0
                ) {
                    data.putExtra("amount", mBinding.tvAmount.text.toString())
                    filterCount++
                }
                if (selectionPos == 0) {
                    data.putExtra("sortBy", "1")
                } else if (selectionPos == 1) {
                    data.putExtra("sortBy", "1")
                    filterCount++
                } else if (selectionPos == 2) {
                    PreferenceManager.getString(PreferenceManager.LAT)
                        ?.let {
                            PreferenceManager.getString(PreferenceManager.LAN)
                                ?.let { it1 ->
                                    if (it.equals("")) {
                                        PreferenceManager.putString(
                                            PreferenceManager.LAT,
                                            "-37.8136"
                                        )
                                        PreferenceManager.putString(
                                            PreferenceManager.LAN,
                                            "144.9631"
                                        )
                                        data.putExtra("lats", "-37.8136".toDouble())
                                        data.putExtra("lngs", "144.9631".toDouble())
                                    } else {
                                        data.putExtra("lats", it.toDouble())
                                        data.putExtra("lngs", it1.toDouble())
                                    }
                                }
                        }
                    data.putExtra("sortBy", "2")
                    filterCount++
                } else if (selectionPos == 3) {
                    data.putExtra("sortBy", "3")
                    filterCount++
                }
                data.putExtra("isSearchType", isSearchType)
                /*if (mBinding.tvAmount.text.toString()
                        .isNotEmpty() && mBinding.tvAmount.text.toString()
                        .toDouble() == 0.0
                ) {
                    showToastShort(getString(R.string.valid_amount))
                }
                else {*/
                data.putExtra("filterCount", filterCount.toString())
                setResult(Activity.RESULT_OK, data)
                finish()
//                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
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
                mBinding.rvJobTypes.visibility = View.GONE
            }
        }
    }

    var listSize: Int = 0
    var tradePosition: Int = 0
    var tradeName: String = ""
    var TAG: String = "filterTag"
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
                var isAllSelected: Boolean = false
                list.forEachIndexed { index, element ->
                    if (list[index]?.id == tradeID) {
                        tradePosition=index
                        list[index]?.isSelected = true
                        if (list[index]?.specialisations != null)
                            specList?.clear()

                        list.get(index)?.specialisations?.let { specList?.addAll(it) }
                        var isAll = false
                        if (specList!!.size == specialisation.size|| (!tradeID.isNullOrEmpty() && specialisation.isNullOrEmpty())) {
                            isAllSelected = true
                        }else {
                            specList?.forEachIndexed { index, elements ->
                                specList!![index].isSelected =
                                    specialisation.contains(specList!![index].id)
                            }
                        }
                        if (specList?.size!! > 0) {
                            val sep = Specialisation(
                                isAllSelected,
                                "",
                                1,
                                "",
                                "",
                                "",
                                "All",
                                0
                            )
                            specList?.add(0, sep)
                            mBinding.tvRvH3Specailatisation.visibility = View.VISIBLE
                            mBinding.rvSpecialization.visibility = View.VISIBLE
                            mSpecAdapter.notifyDataSetChanged()
                        } else {
                            if (!isTradie)
                                mBinding.rvSpecialization.visibility = View.GONE
                            mBinding.tvRvH3Specailatisation.visibility = View.GONE
                        }

                    }
                }
                mAdapter.notifyDataSetChanged()
            }
            ApiCodes.JOB_TYPE_LIST -> {
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        jobList.clear()
                        mHomeViewModel.mJsonResponseModel.jobModelList?.let {
                            jobList.addAll(it)
                        }
                        jobList.forEachIndexed { index, element ->
                            if (jobTypes.contains(jobList[index]._id)) {
                                jobList[index].isSelected = true
                            }
                        }
                        if (jobList.size == 0) {
                            mBinding.rvJobTypes.visibility = View.GONE
                            mBinding.tvRvH2JobTypes.visibility = View.GONE
                        } else {
                            mBinding.rvJobTypes.visibility = View.VISIBLE
                            mBinding.tvRvH2JobTypes.visibility = View.VISIBLE
                        }
                        mHomeAdapter.notifyDataSetChanged()
                    }
                }, 1000)

            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    override fun onClick(p0: View?) {
    }

    override fun onTradeClick(position: Int) {
        tradePosition = position
        if (!list[position]!!.isSelected!!) {
            tradeName = list[position]!!.tradeName!!
            Log.i(TAG, "onTradeClick: trade name= $tradeName")
            Log.i(TAG, "onTradeClick: selection ${list[position]!!.isSelected}")
        } else {
            tradeName = ""
        }

        if (list[position]?.isSelected == false) {
            list.forEachIndexed { index, element ->
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
            if (specList?.size!! > 0) {
                val sep = Specialisation(name = "All")
                specList?.add(0, sep)
                specList!![0].isSelected = true
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