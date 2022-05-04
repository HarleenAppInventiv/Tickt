package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.tradie.ReviewData
import com.app.core.model.tradie.ReviewList
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.MoEngageConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ReviewFullAdapter
import com.example.ticktapp.adapters.ReviewFullBuilderAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieListBinding
import com.example.ticktapp.mvvm.viewmodel.JobDetailsViewModel
import com.example.ticktapp.util.MoEngageUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.moengage.core.Properties
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {
    private lateinit var mAdapter: ReviewFullAdapter
    private lateinit var mBinding: ActivityTradieListBinding
    private lateinit var title: String
    private var count = 0
    private var pos = -1
    private lateinit var list: ArrayList<ReviewData>
    private val mViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_list)
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setData()
        initRecyclerView()
//        tradieReview()
        setObservers()
    }


    private fun setData() {
        mBinding.tvTitle.text = title
        mBinding.llMainBg.setBackgroundColor(Color.WHITE)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    private fun getIntentData() {
        list = intent.getSerializableExtra("data") as ArrayList<ReviewData>
        title = intent.getStringExtra("title").toString()
        count = intent.getIntExtra("count", 0)
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        val data = Intent()
        data.putExtra("data", list)
        data.putExtra("count", count)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    fun tradieReview() {
        var reviewDataa: ArrayList<ReviewList> = ArrayList()

        var indx = 0
        for (index in list.indices) {
            var reviewList = ReviewList(list[index])
            reviewDataa.add(reviewList)
        }

        var mAdapter = ReviewFullBuilderAdapter(reviewDataa, true) {
            showAppPopupDialog(
                getString(R.string.are_you_want_to_delete_reply),
                getString(R.string.yes),
                getString(R.string.no),
                getString(R.string.delete),
                {
                    /*val pos = it?.tag as Int
                    list[pos].reviewData?.reviewId?.let { it2 ->
                        list[pos].reviewData?.replyData?.replyId?.let { it3 ->
                            mViewModel.deleteReviewReply(
                                it2,
                                it3, pos
                            )
                        }
                    }*/
                },
                {
                },
                true
            )
        }
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvTradie.layoutManager = layoutRecManager
        mBinding.rvTradie.adapter = mAdapter

    }

    private fun initRecyclerView() {
        mAdapter = ReviewFullAdapter(list) {
            showAppPopupDialog(
                getString(R.string.are_you_want_to_delete_review),
                getString(R.string.yes),
                getString(R.string.no),
                getString(R.string.delete),
                {
                    val pos = it.tag as Int
                    this.pos = pos
                    list[pos].reviewId?.let { it1 -> mViewModel.reviewRemoveTradie(it1) }

                },
                {
                },
                true
            )
        }
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvTradie.layoutManager = layoutRecManager
        mBinding.rvTradie.adapter = mAdapter

        viewedReviewsMoEngage()   //viewed reviews mo engage
        viewedReviewsMixPanel()
    }


    private fun hideShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.REVIEW_REMOVE_TRADIE -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.REVIEW_REMOVE_TRADIE -> {
                list.removeAt(pos)
                count -= 1
                mBinding.tvTitle.text = (count).toString() + " review(s)"
                mAdapter.notifyDataSetChanged()
                hideShowNoData()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(resources.getColor(R.color.color_f6f7f9))
        }
    }

    override fun onClick(p0: View?) {

    }

    override fun onRefresh() {
        mBinding.srLayout.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("id")) {
                try {
                    val msg = data.getStringExtra("msg")
                    val id = data.getStringExtra("id")
                    val rating = data.getDoubleExtra("rating", 0.0)

                    list.forEach {
                        if (it.reviewId == id) {
                            it.ratings = rating
                            it.review = msg
                        }
                    }
                    mBinding.rvTradie.adapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun viewedReviewsMoEngage() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())

        val signUpProperty = Properties()
        signUpProperty.addAttribute(MoEngageConstants.TIME_STAMP, timeStamp)

        MoEngageUtils.sendEvent(
            this,
            MoEngageConstants.MOENGAGE_EVENT_VIEWED_REVIEWS,
            signUpProperty
        )
    }

    private fun viewedReviewsMixPanel() {
        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        val mixpanel = MixpanelAPI.getInstance(
            this,
            getString(R.string.mix_panel_token)
        )

        val props = JSONObject()

        props.put(MoEngageConstants.TIME_STAMP, timeStamp)
        mixpanel.track(
            MoEngageConstants.MOENGAGE_EVENT_VIEWED_REVIEWS,
            props
        )
    }

}
