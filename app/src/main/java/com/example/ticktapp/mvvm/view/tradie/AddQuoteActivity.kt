package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.tradie.QuoteItem
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.ApiParams
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.adapters.QuoteItemTradieAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityAddQuoteBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.mvvm.viewmodel.NewQuoteListRequestViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddQuoteActivity : BaseActivity(), View.OnClickListener {

    private var quoteId: String = ""
    private val mViewModelQuote by lazy { ViewModelProvider(this).get(NewQuoteListRequestViewModel::class.java) }
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private lateinit var mBinding: ActivityAddQuoteBinding
    private lateinit var mAdapter: QuoteItemTradieAdapter
    private val list by lazy { ArrayList<QuoteItem>() }
    private var data: JobRecModel? = null
    private var jobDashBoard: JobDashboardModel? = null
    private var _ids = ""
    private var builderId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_quote)
        getIntentData()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        initRecyclerView()
        setUpListeners()
        setData()
        setObservers()
        mViewModelQuote.getQuoteListWithTraide(getParamData(), true)
    }

    private fun getParamData(): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        if (data != null)
            params.put("jobId", data?.jobId.toString())
        else
            params.put("jobId", jobDashBoard?.jobId.toString())
        params.put("tradieId", PreferenceManager.getString(PreferenceManager.USER_ID).toString())
        params.put("sortBy", 1)
        return params
    }

    private fun setObservers() {
        setBaseViewModel(mViewModelQuote)
        setBaseViewModel(mViewModel)
        mViewModelQuote.getResponseObserver().observe(this, this)
        mViewModel.getResponseObserver().observe(this, this)
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

    private fun initRecyclerView() {
        mAdapter = QuoteItemTradieAdapter(list, true) {
            val pos = it.tag as Int
            val data = mAdapter.getData()[pos]
            mBinding.edtItemNumber.setText(data.item_number.toString())
            mBinding.edtDescription.setText(data.description.toString())
            mBinding.edtPrice.setText(data.price.toString())
            mBinding.edtQty.setText(data.quantity.toString())
            mBinding.edtTotal.setText(data.totalAmount.toString())
            mBinding.tvAddItem.text = getString(R.string.save_item)
            mBinding.llDelete.visibility = View.VISIBLE
            _ids = data._id.toString()
        }
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvQuotes.layoutManager = layoutRecManager
        mBinding.rvQuotes.adapter = mAdapter
    }

    override fun onClick(p0: View?) {

    }

    private fun setUpListeners() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }

        mBinding.llDelete.setOnClickListener {
            showAppPopupDialog(
                getString(R.string.delete_quote_text),
                getString(R.string.yes),
                getString(R.string.no),
                getString(R.string.title_quote_job_delete),
                {
                    val params = HashMap<String, Any>()
                    params.put("itemId", _ids)
                    mViewModelQuote.deleteItem(params, false)

                },
                {},
                true
            )
        }

        mBinding.tvAddItem.setOnClickListener {
            if (mBinding.edtDescription.text.toString().length == 0) {
                showToastShort(getString(R.string.please_enter_desc))
            } else if (mBinding.edtPrice.text.toString().length == 0) {
                showToastShort(getString(R.string.please_enter_item_price))
            } else if (mBinding.edtQty.text.toString().length == 0) {
                showToastShort(getString(R.string.please_enter_item_qty))
            } else {
                if (mBinding.tvSubmitQuote.visibility == View.VISIBLE) {
                    val data = QuoteItem()
                    data.item_number = mBinding.edtItemNumber.text.toString().toInt()
                    data.description = mBinding.edtDescription.text.toString()
                    data.price = mBinding.edtPrice.text.toString().toDouble()
                    data.quantity = mBinding.edtQty.text.toString().toInt()
                    data.totalAmount = mBinding.edtTotal.text.toString().toDouble()
                    mAdapter.addData(data)
                    _ids = ""
                    mBinding.edtItemNumber.setText((mAdapter.getLastCount()).toString())
                    mBinding.edtDescription.setText("")
                    mBinding.edtPrice.setText("")
                    mBinding.edtTotal.setText("")
                    mBinding.edtQty.setText("")
                    mBinding.tvAddItem.text = getString(R.string.add_item)
                    mBinding.llRvData.visibility = View.VISIBLE
                    mBinding.llDelete.visibility = View.GONE
                } else {
                    showAppPopupDialog(
                        getString(R.string.update_quote_text),
                        getString(R.string.yes),
                        getString(R.string.no),
                        getString(R.string.title_quote_job),
                        {
                            val data = QuoteItem()
                            data.item_number = mBinding.edtItemNumber.text.toString().toInt()
                            data.description = mBinding.edtDescription.text.toString()
                            data.price = mBinding.edtPrice.text.toString().toDouble()
                            data.quantity = mBinding.edtQty.text.toString().toInt()
                            data.totalAmount = mBinding.edtTotal.text.toString().toDouble()
                            if (_ids.length > 0) {
                                data._id = _ids
                                val params = HashMap<String, Any>()
                                params.put("itemId", _ids)
                                params.put("item_number", mBinding.edtItemNumber.text.toString())
                                params.put("description", mBinding.edtDescription.text.toString())
                                params.put("price", mBinding.edtPrice.text.toString())
                                params.put("quantity", mBinding.edtQty.text.toString())
                                params.put("totalAmount", mBinding.edtTotal.text.toString())
                                params.put("quoteId", quoteId)
                                mViewModelQuote.updateItem(params, false)
                            } else {
                                val params = HashMap<String, Any>()
                                params.put("item_number", mBinding.edtItemNumber.text.toString())
                                params.put("description", mBinding.edtDescription.text.toString())
                                params.put("price", mBinding.edtPrice.text.toString())
                                params.put("quantity", mBinding.edtQty.text.toString())
                                params.put("totalAmount", mBinding.edtTotal.text.toString())
                                params.put("quoteId", quoteId)
                                mViewModelQuote.addItem(params, false)
                            }
                            mAdapter.addData(data)
                            _ids = ""
                            mBinding.edtItemNumber.setText((mAdapter.getLastCount()).toString())
                            mBinding.edtDescription.setText("")
                            mBinding.edtPrice.setText("")
                            mBinding.edtTotal.setText("")
                            mBinding.edtQty.setText("")
                            mBinding.llDelete.visibility = View.GONE
                            mBinding.tvAddItem.text = getString(R.string.add_item)
                        },
                        {},
                        true
                    )
                }
            }
        }
        mBinding.tvSubmitQuote.setOnClickListener {
            if (mAdapter.getData().size > 0) {
                val params = HashMap<String, Any>()
                if (data != null) {
                    params.put("jobId", data?.jobId.toString())
                    if (data?.builderId != null && data?.builderId!!.isNotEmpty())
                        params.put("builderId", data!!.builderId.toString())
                    else if (data?.postedBy?.builderId != null && data?.postedBy?.builderId!!.isNotEmpty())
                        params.put("builderId", data!!.postedBy.builderId.toString())
                } else {
                    params.put("jobId", jobDashBoard?.jobId.toString())
                    if (jobDashBoard?.builderId != null && jobDashBoard?.builderId!!.isNotEmpty())
                        params.put("builderId", jobDashBoard!!.builderId.toString())
                }
                params.put(
                    "userId",
                    PreferenceManager.getString(PreferenceManager.USER_ID).toString()
                )
                val jsonArray = ArrayList<HashMap<String, Any>>()
                var amount = 0.0
                mAdapter.getData().forEach {
                    val paramArray = HashMap<String, Any>()
                    paramArray.put("item_number", it.item_number.toString())
                    paramArray.put("description", it.description.toString())
                    paramArray.put("price", it.price.toString())
                    paramArray.put("quantity", it.quantity.toString())
                    paramArray.put("totalAmount", it.totalAmount.toString())
                    jsonArray.add(paramArray)
                    amount = it.totalAmount.toString().toDouble()
                }
                /*Log.i("builderId_submit_quote", "setUpListeners: $builderId")
                params.put("builderId", builderId!!)*/
                params.put("amount", amount)

                params.put("quote_item", jsonArray)
                mViewModelQuote.addQuote(params, true)
            } else {
                if (mBinding.edtDescription.text.toString().length == 0) {
                    showToastShort(getString(R.string.please_enter_desc))
                } else if (mBinding.edtPrice.text.toString().length == 0) {
                    showToastShort(getString(R.string.please_enter_item_price))
                } else if (mBinding.edtPrice.text.toString().length == 0) {
                    showToastShort(getString(R.string.please_enter_item_qty))
                } else {
                    val params = HashMap<String, Any>()
                    if (data != null)
                        params.put("jobId", data?.jobId.toString())
                    else
                        params.put("jobId", jobDashBoard?.jobId.toString())
                    params.put(
                        "userId",
                        PreferenceManager.getString(PreferenceManager.USER_ID).toString()
                    )
                    val paramArray = HashMap<String, Any>()
                    val jsonArray = ArrayList<HashMap<String, Any>>()
                    paramArray.put("item_number", mBinding.edtItemNumber.text.toString())

                    if (data?.builderId != null && data?.builderId!!.isNotEmpty())
                        paramArray.put("builderId", data!!.builderId.toString())
                    else if (data?.postedBy?.builderId != null && data?.postedBy?.builderId!!.isNotEmpty())
                        paramArray.put("builderId", data!!.postedBy.builderId.toString())

                    paramArray.put("description", mBinding.edtDescription.text.toString())
                    paramArray.put("price", mBinding.edtPrice.text.toString())
                    paramArray.put("quantity", mBinding.edtQty.text.toString())
                    paramArray.put("totalAmount", mBinding.edtTotal.text.toString())
                    jsonArray.add(paramArray)
                    params.put("amount", mBinding.edtTotal.text.toString())

                    /*Log.i("builderId_submit_quote", "setUpListeners: $builderId")
                    params.put("builderId", builderId!!)*/
                    params.put("quote_item", jsonArray)
                    mViewModelQuote.addQuote(params, true)
                }
            }
        }

        mBinding.edtPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (mBinding.edtQty.text.toString().length >= 0 && mBinding.edtPrice.text?.length!! >= 0) {
                        mBinding.edtTotal.text = (mBinding.edtQty.text.toString()
                            .toDouble() * mBinding.edtPrice.text.toString().toDouble()).toString()
                    }
                } catch (ex: Exception) {

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        mBinding.edtQty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (mBinding.edtQty.text.toString().length >= 0 && mBinding.edtPrice.text?.length!! >= 0) {
                        mBinding.edtTotal.text = (mBinding.edtQty.text.toString()
                            .toDouble() * mBinding.edtPrice.text.toString().toDouble()).toString()
                    }
                } catch (ex: Exception) {

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        super.onException(exception, apiCode)
        showToastShort(exception.message)
        mBinding.edtItemNumber.setText("1")
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        super.onResponseSuccess(statusCode, apiCode, msg)
        when (apiCode) {
            ApiCodes.GET_QUOTE -> {
                mBinding.edtItemNumber.setText("1")
                mViewModelQuote.getQuoteList().let {
                    if (it.size > 0 && it.get(0).quote_item != null) {
                        quoteId = it.get(0)._id.toString()
                        mBinding.llRvData.visibility = View.VISIBLE
                        mAdapter.setData(it.get(0).quote_item as ArrayList<QuoteItem>)
                        mBinding.tvSubmitQuote.visibility = View.GONE

                        if ((it.get(0).quote_item as ArrayList<QuoteItem>).size > 0) {
                            mBinding.edtItemNumber.setText((mAdapter.getLastCount()).toString())
                        } else
                            mBinding.edtItemNumber.setText("1")
                    }
                }
            }
            ApiCodes.ADD_ITEM -> {
                mViewModelQuote.getQuote().let {
                    mAdapter.lastDataUpdate(it)
                }
            }
            ApiCodes.DELETE_ITEM -> {
                runOnUiThread {
                    mAdapter.removeItem(_ids)
                    _ids = ""
                    mBinding.edtItemNumber.setText((mAdapter.getLastCount()).toString())
                    mBinding.edtDescription.setText("")
                    mBinding.edtPrice.setText("")
                    mBinding.edtTotal.setText("")
                    mBinding.edtQty.setText("")
                    mBinding.llDelete.visibility = View.GONE
                    mBinding.tvAddItem.text = getString(R.string.add_item)
                }
            }
            ApiCodes.ADD_QUOTE -> {
                if (data != null) {
                    val params = HashMap<String, Any>()
                    params[ApiParams.JOB_ID] = data?.jobId!!
                    params[ApiParams.TRADE_ID] = data?.tradeId!!
                    if (!data.specializationId.isNullOrEmpty()) params[ApiParams.SPECIALIZATION_ID] = data?.specializationId!!
                    if (data?.builderId != null && data?.builderId!!.isNotEmpty())
                        params[ApiParams.BUILDER_ID] = data?.builderId!!
                    else if (data?.postedBy?.builderId != null && data?.postedBy?.builderId!!.isNotEmpty())
                        params[ApiParams.BUILDER_ID] = data?.postedBy?.builderId!!
                    mViewModel.applyJob(params)
                    quoteJobMoEngage(
                        data?.tradeName!!,
                        data?.locationName!!,
                        data?.milestoneNumber!!,
                        data?.amount!!
                    )
                    quoteJobMixPanel(
                        data?.tradeName!!,
                        data?.locationName!!,
                        data?.milestoneNumber!!,
                        data?.amount!!
                    )
                } else {
                    val params = HashMap<String, Any>()
                    params[ApiParams.JOB_ID] = jobDashBoard?.jobId!!
                    params[ApiParams.TRADE_ID] = jobDashBoard?.tradeId!!
                    if (!data.specializationId.isNullOrEmpty())  params[ApiParams.SPECIALIZATION_ID] = jobDashBoard?.specializationId!!
                    if (jobDashBoard?.builderId != null && jobDashBoard?.builderId!!.isNotEmpty())
                        params[ApiParams.BUILDER_ID] = jobDashBoard?.builderId!!


                    mViewModel.applyJob(params)

                    try {
                        quoteJobMoEngage(
                            jobDashBoard?.tradeName!!,
                            jobDashBoard?.locationName!!,
                            jobDashBoard?.milestoneNumber!!,
                            jobDashBoard?.amount!!
                        )
                        quoteJobMixPanel(
                            jobDashBoard?.tradeName!!,
                            jobDashBoard?.locationName!!,
                            jobDashBoard?.milestoneNumber!!,
                            jobDashBoard?.amount!!
                        )
                    } catch (e: Exception) {

                    }
                }
            }
            ApiCodes.APPLY -> {
                jobAppliedMoEngage()
                jobAppliedMixPanel()
                startActivity(
                    Intent(this, QuoteAppliedActivity::class.java).putExtra(
                        "builderName",
                        data?.builderName ?: " builder"
                    )
                )
            }
        }
    }

    private fun jobAppliedMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)


        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_APPLIED_FOR_JOB,
            signUpProperty
        )
    }

    private fun jobAppliedMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)

        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_APPLIED_FOR_JOB, props
        )
    }

    private fun quoteJobMoEngage(
        category: String,
        location: String,
        numberOfMilestones: Int,
        amount: String
    ) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)
        signUpProperty.addAttribute(MoEngageConstants.CATEGORY, category)
        signUpProperty.addAttribute(MoEngageConstants.LOCATION, location)
        signUpProperty.addAttribute(MoEngageConstants.NUMBER_OF_MILESTONES, numberOfMilestones)
        signUpProperty.addAttribute(MoEngageConstants.AMOUNT, amount)


        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_QUOTED_A_JOB,
            signUpProperty
        )
    }

    private fun quoteJobMixPanel(
        category: String,
        location: String,
        numberOfMilestones: Int,
        amount: String
    ) {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mix_panel_token))

        val props = JSONObject()
        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        props.put(MoEngageConstants.CATEGORY, category)
        props.put(MoEngageConstants.LOCATION, location)
        props.put(MoEngageConstants.NUMBER_OF_MILESTONES, numberOfMilestones)
        props.put(MoEngageConstants.AMOUNT, amount)
        mixpanel.track(MoEngageConstants.MOENGAGE_EVENT_QUOTED_A_JOB, props)
    }


    private fun getIntentData() {
        if (intent.getSerializableExtra("data") is JobDashboardModel) {
            jobDashBoard = intent.getSerializableExtra("data") as JobDashboardModel
            /*jobDashBoard!!.builderId.let {
                Log.i("builderId_submit_quote", "setUpListeners: $it")
                builderId = it!!
            }*/
        } else {
            data = intent.getSerializableExtra("data") as JobRecModel
            /*  if (data != null) {
                  if (data!!.postedBy != null) {
                      if (data!!.postedBy!!.builderId != null) {
                          builderId = data!!.postedBy.builderId.toString()
                      }
                  }
              }*/
        }
    }

    private fun setData() {
        /*mBinding.tvQuoteTotalAmount.text =
            getString(R.string.total) + ": $" + mainData.totalQuoteAmount*/
        if (data != null) {
            mBinding.tvTitle.text = data?.tradeName
            mBinding.tvDetails.text = data?.jobName
            if (data?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(data?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            }
        } else {
            mBinding.tvTitle.text = jobDashBoard?.tradeName
            mBinding.tvDetails.text = jobDashBoard?.jobName
            if (jobDashBoard?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(jobDashBoard?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            } else if (jobDashBoard?.jobData != null && jobDashBoard?.jobData?.tradeSelectedUrl != null) {
                Glide.with(mBinding.root.context).load(jobDashBoard?.jobData?.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(mBinding.ivUserProfile)
            }
        }
        ViewCompat.setNestedScrollingEnabled(mBinding.rvQuotes, false)
    }

}