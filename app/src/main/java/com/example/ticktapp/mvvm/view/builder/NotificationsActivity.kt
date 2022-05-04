package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.NotificationModel
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.NotificationsAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityNotificationListBinding
import com.example.ticktapp.mvvm.view.builder.postjob.CheckAndApproveMilestoneActivity
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.mvvm.viewmodel.*
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation

class NotificationsActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var isUpdate: Boolean = false
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityNotificationListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(NotificationRequestViewModel::class.java) }
    private val viewModel by lazy { ViewModelProvider(this).get(PhoneNumberViewModel::class.java) }
    private val mJobViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val mViewModelList by lazy { ViewModelProvider(this).get(ReviewListViewModel::class.java) }
    private val mMyProfileViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }

    private lateinit var mAdapter: NotificationsAdapter
    private val list by lazy { ArrayList<NotificationModel>() }
    private var pageNumber = 1
    private var lastType = -1
    private var otherData = ""
    private var isNotificationReadApi: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification_list)
        initRecyclerView()
        setObservers()
        setUpListeners()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        mViewModel.notifications(pageNumber, true)
    }

    private fun setUpListeners() {
        mBinding.tempMileBack.setOnClickListener { onBackPressed() }
        mBinding.tvMarkAllRead.setOnClickListener { markAllAsRead() }
    }

    private fun markAllAsRead() {
        mViewModel.markAllNotificationsRead(1, 1, true)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(viewModel)
        setBaseViewModel(mViewModelList)
        setBaseViewModel(mJobViewModel)
        setBaseViewModel(mMyProfileViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        viewModel.getResponseObserver().observe(this, this)
        mViewModelList.getResponseObserver().observe(this, this)
        mJobViewModel.getResponseObserver().observe(this, this)
        mMyProfileViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = NotificationsAdapter(list) {
            try {
                val pos = it.tag as Int
                if (mAdapter.getData().get(pos).notificationType != null) {
                    lastType = mAdapter.getData().get(pos).notificationType!!
                    mAdapter.getData().get(pos).senderId.let {
                        otherData = it.toString()
                    }
                    if (mAdapter.getData().get(pos).notificationType == 3 ||
                        mAdapter.getData().get(pos).notificationType == 9 ||
                        mAdapter.getData().get(pos).notificationType == 7 ||
                        mAdapter.getData().get(pos).notificationType == 14 ||
                        mAdapter.getData().get(pos).notificationType == 20 ||
                        mAdapter.getData().get(pos).notificationType == 21
                    ) {
                        mJobViewModel.jobDetailsFromBuilder(
                            true,
                            mAdapter.getData().get(pos).jobId,
                            "",
                            ""
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 16 ||
                        mAdapter.getData().get(pos).notificationType == 10
                    ) {
                        mViewModelList.getBuilderReviewList(
                            mAdapter.getData().get(pos).receiverId.toString(),
                            1
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 11) {
                        startActivity(
                            Intent(this, WebViewActivity::class.java)
                                .putExtra(IntentConstants.FROM, Constants.TERMS)
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 19) {
                        startActivity(
                            Intent(this, WebViewActivity::class.java)
                                .putExtra(IntentConstants.FROM, Constants.PRIVACY)
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 31 ||
                        mAdapter.getData().get(pos).notificationType == 12 ||
                        mAdapter.getData().get(pos).notificationType == 18
                    ) {
                        showAppPopupDialog(
                            intent.getStringExtra("body").toString(),
                            getString(R.string.ok),
                            "",
                            intent.getStringExtra("title").toString(),
                            {},
                            {},
                            false
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 1
                    ) {
                        startActivity(Intent(this, NewApplicantActivity::class.java))
                    }
                }
                val objects = HashMap<String, Any>()
                objects.put("notificationId", mAdapter.getData().get(pos)._id.toString())
                mAdapter.updateNotificationStatus(pos)
                mViewModel.readNotifications(objects)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvNotifications.layoutManager = layoutRecManager
        if (endlessScrollListener == null)
            endlessScrollListener =
                EndlessRecyclerViewScrollListenerImplementation(layoutRecManager, this)
        else
            endlessScrollListener?.setmLayoutManager(layoutRecManager)
        mBinding.rvNotifications.addOnScrollListener(endlessScrollListener!!)
        mBinding.rvNotifications.adapter = mAdapter

    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.NOTIFICATIONS -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.NOTIFICATIONS -> {
                if (isNotificationReadApi) {
                    mViewModel.notificationModelList.list.let {
                        for (index in mViewModel.notificationModelList.list!!.indices)
                            mAdapter.updateNotificationStatus(index)
                    }
                    isNotificationReadApi = false
                    return
                }
                if (mAdapter.itemCount == 0 || pageNumber == 1) {
                    mViewModel.notificationModelList.let {
                        it.list?.let { it1 -> mAdapter.setData(it1) }
                    }
                } else {
                    mViewModel.notificationModelList.let {
                        it.list?.let { it1 -> mAdapter.addData(it1) }
                    }
                }
                if (list.size == 0) {
                    mBinding.rlNoNotifications.visibility = View.VISIBLE
                    mBinding.rlRecycler.visibility = View.GONE
                    mBinding.tvMarkAllRead.visibility = View.GONE
                } else {
                    mBinding.rlNoNotifications.visibility = View.GONE
                    mBinding.rlRecycler.visibility = View.VISIBLE
                    mBinding.tvMarkAllRead.visibility = View.VISIBLE
                }
            }
            ApiCodes.JOB_DETAILS -> {
                mJobViewModel.mJsonResponseModel.let {
                    if (lastType == 3) {
                        startActivity(
                            Intent(this, JobDetailsActivity::class.java).putExtra("data", it)
                                .putExtra("isBuilder", true)
                        )
                    } else if (lastType == 7) {
                        startActivity(
                            Intent(this, TradieReviewJobActivity::class.java)
                                .putExtra("data", it).putExtra("isBuilder", true)
                                .putExtra("senderId", otherData)
                        )
                    } else if (lastType == 9) {
                        startActivity(
                            Intent(this, JobDetailsActivity::class.java).putExtra("data", it)
                                .putExtra("isBuilder", true).putExtra("isQuestionList", true)
                        )
                    } else if (lastType == 14) {
                        startActivity(
                            Intent(this, CheckAndApproveMilestoneActivity::class.java)
                                .putExtra("data", it).putExtra("isBuilder", true)
                        )
                    } else if (lastType == 20) {
                        startActivity(
                            Intent(
                                this,
                                QuoteListActivity::class.java
                            ).putExtra("data", it).putExtra("isAction", true)
                                .putExtra("fromHome", true)
                        )
                    } else if (lastType == 21) {
                        startActivity(
                            Intent(
                                this,
                                QuoteListActivity::class.java
                            ).putExtra("data", it).putExtra("isAction", false)
                                .putExtra("fromHome", true)
                        )
                    }
                }
            }
            ApiCodes.REVIEW_LIST -> {
                mViewModelList.reviewDataList.let {
                    startActivity(
                        Intent(this, ReviewListBuilderActivity::class.java).putExtra(
                            "data",
                            it
                        ).putExtra("title", (it.size).toString() + " review(s)")
                    )
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        pageNumber = page + 1
        mViewModel.notifications(pageNumber, false)
    }

    override fun onRefresh() {
        endlessScrollListener?.resetState()
        pageNumber = 1
        list.clear()
        mViewModel.notifications(pageNumber, false)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(resources.getColor(R.color.white))
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.setStatusBarColor(resources.getColor(R.color.white))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            endlessScrollListener?.resetState()
            pageNumber = 1
            list.clear()
            mViewModel.notifications(pageNumber, true)
            isUpdate = true
        }
    }

    override fun onBackPressed() {
        if (isUpdate) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

}
