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
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ReviewFullBuilderAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieListBinding
import com.example.ticktapp.mvvm.viewmodel.ReviewListViewModel

class ReviewListBuilderActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {
    private lateinit var mAdapter: ReviewFullBuilderAdapter
    private lateinit var mBinding: ActivityTradieListBinding
    private lateinit var title: String
    private lateinit var list: ArrayList<ReviewList>
    private val mViewModel by lazy { ViewModelProvider(this).get(ReviewListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_list)
        setUpListeners()
        setObservers()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        setData()
        initRecyclerView()
    }

    private fun setData() {
        mBinding.tvTitle.text = title
        mBinding.llMainBg.setBackgroundColor(Color.WHITE)
    }


    private fun getIntentData() {
        list = intent.getSerializableExtra("data") as ArrayList<ReviewList>
        title = intent.getStringExtra("title").toString()
    }

    private fun setUpListeners() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onBackPressed() {
        val data = Intent()
        data.putExtra("data", list)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun initRecyclerView() {
        mAdapter = ReviewFullBuilderAdapter(list) {
            showAppPopupDialog(
                getString(R.string.are_you_want_to_delete_reply),
                getString(R.string.yes),
                getString(R.string.no),
                getString(R.string.delete),
                {
                    val pos = it?.tag as Int
                    list[pos].reviewData?.reviewId?.let { it2 ->
                        list[pos].reviewData?.replyData?.replyId?.let { it3 ->
                            mViewModel.deleteReviewReply(
                                it2,
                                it3, pos
                            )
                        }
                    }
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

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.DELETE_REVIEW_REPLY -> {
                showToastShort(exception.message)
            }
        }
    }


    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.DELETE_REVIEW_REPLY -> {
                mViewModel.pos.let {
                    list.get(it).reviewData?.replyData = ReviewData()
                    mBinding.rvTradie.adapter?.notifyDataSetChanged()
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun hideShowNoData() {
        if (mAdapter.itemCount == 0)
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        else
            mBinding.tvResultTitleNoData.visibility = View.GONE
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
            if (data != null && data.hasExtra("data")) {
                try {
                    val reviewData = data.getSerializableExtra("data") as ReviewData
                    val msg = data.getStringExtra("msg")
                    val id = data.getStringExtra("id")
                    list.forEach {
                        if (it.reviewData?.reviewId == id) {
                            if (msg == null || msg.length == 0) {
                                it.reviewData?.replyData = reviewData
                            } else {
                                it.reviewData?.replyData?.reply = msg
                            }
                        }
                    }
                    mBinding.rvTradie.adapter?.notifyDataSetChanged()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

}
