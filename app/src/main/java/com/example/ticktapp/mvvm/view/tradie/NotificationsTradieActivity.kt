package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.NotificationModel
import com.app.core.model.NotificationResponse
import com.app.core.model.tradie.BuilderModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.app.core.util.Constants
import com.app.core.util.IntentConstants
import com.example.ticktapp.R
import com.example.ticktapp.adapters.NotificationsAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityNotificationsTradieBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.WelcomeActivity
import com.example.ticktapp.mvvm.view.builder.*
import com.example.ticktapp.mvvm.viewmodel.*
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListener
import com.example.ticktapp.paging.EndlessRecyclerViewScrollListenerImplementation
import java.io.Serializable

class NotificationsTradieActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener,
    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private var isUpdate: Boolean = false
    private var endlessScrollListener: EndlessRecyclerViewScrollListener? = null
    private lateinit var mBinding: ActivityNotificationsTradieBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(NotificationRequestViewModel::class.java) }
    private val viewModel by lazy { ViewModelProvider(this).get(PhoneNumberViewModel::class.java) }
    private val mJobViewModel by lazy { ViewModelProvider(this).get(JobDetailsViewModel::class.java) }
    private val mViewModelList by lazy { ViewModelProvider(this).get(ReviewListViewModel::class.java) }
    private val mMyProfileViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }
    private val mProfileViewModel by lazy { ViewModelProvider(this).get(TradieProfileViewModel::class.java) }
    private lateinit var mAdapter: NotificationsAdapter
    private val list by lazy { ArrayList<NotificationModel>() }
    private var tradieData: BuilderModel? = null
    private var pageNumber = 1
    private var lastType = -1
    private var otherData = ""
    private var isNotificationReadApi: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifications_tradie)
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
        isNotificationReadApi = true
        mViewModel.markAllNotificationsRead(1, 1, true)
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        setBaseViewModel(viewModel)
        setBaseViewModel(mViewModelList)
        setBaseViewModel(mJobViewModel)
        setBaseViewModel(mMyProfileViewModel)
        setBaseViewModel(mProfileViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        viewModel.getResponseObserver().observe(this, this)
        mViewModelList.getResponseObserver().observe(this, this)
        mJobViewModel.getResponseObserver().observe(this, this)
        mMyProfileViewModel.getResponseObserver().observe(this, this)
        mProfileViewModel.getResponseObserver().observe(this, this)
    }

    private fun initRecyclerView() {
        mAdapter = NotificationsAdapter(list) { it ->
            try {
                val pos = it.tag as Int
                if (mAdapter.getData().get(pos).notificationType != null) {
                    lastType = mAdapter.getData().get(pos).notificationType!!
                    Log.i("notificationType", lastType.toString())
                    mAdapter.getData().get(pos).jobId.let {
                        Log.i("jobId", mAdapter.getData().get(pos).jobId.toString())
                    }
                    mAdapter.getData().get(pos).senderId.let {
                        otherData = it.toString()
                    }
                    if (mAdapter.getData().get(pos).notificationType == 3 ||
                        mAdapter.getData().get(pos).notificationType == 9 ||
                        mAdapter.getData().get(pos).notificationType == 14 ||
                        mAdapter.getData().get(pos).notificationType == 20 ||
                        mAdapter.getData().get(pos).notificationType == 21 ||
                        mAdapter.getData().get(pos).notificationType == 8
                    ) {
                        mJobViewModel.getTradieJobsDetails(
                            true,
                            mAdapter.getData().get(pos).jobId
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 12) {
                        val intent = Intent(this, NewJobsActivity::class.java)
                        startActivity(intent)
                    } else if (mAdapter.getData().get(pos).notificationType == 16 ||
                        mAdapter.getData().get(pos).notificationType == 10
                    ) {
                        mProfileViewModel.getTradiePublicProfile(true)
                    } else if (mAdapter.getData().get(pos).notificationType == 11) {  // done
                        startActivity(
                            Intent(this, WebViewActivity::class.java)
                                .putExtra(IntentConstants.FROM, Constants.TERMS)
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 19) {  //done
                        startActivity(
                            Intent(this, WebViewActivity::class.java)
                                .putExtra(IntentConstants.FROM, Constants.PRIVACY)
                        )
                    } else if (mAdapter.getData()
                            .get(pos).notificationType == 17
                    ) {
                        mProfileViewModel.getTradiePublicProfile(true)
                    } else if (mAdapter.getData().get(pos).notificationType == 31 ||
                        mAdapter.getData().get(pos).notificationType == 18 || mAdapter.getData()
                            .get(pos).notificationType == 4
                    ) {
                        showAppPopupDialog(
                            mAdapter.getData().get(pos).notificationText!!,
                            getString(R.string.ok),
                            "",
                            mAdapter.getData().get(pos).title!!,
                            {},
                            {},
                            false
                        )
                    } else if (mAdapter.getData().get(pos).notificationType == 13) {
                        showAppPopupDialog(
                            mAdapter.getData().get(pos).notificationText!!,
                            getString(R.string.ok),
                            "",
                            mAdapter.getData().get(pos).title!!,
                            {
                                //do logout
                                mViewModel.doLogout()
                            },
                            {},
                            false
                        )
                    }
                }
                Log.i(
                    "notificationType",
                    "initRecyclerView: - read status== ${mAdapter.getData().get(pos).read!!}"
                )
                if (mAdapter.getData().get(pos).read == 0) {
                    Log.i("notificationType", "initRecyclerView: - entered if condition")
                    val objects = HashMap<String, Any>()
                    objects.put("notificationId", mAdapter.getData().get(pos)._id.toString())
                    mAdapter.updateNotificationStatus(pos)
                    mViewModel.readNotifications(objects)
                }
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
                    isNotificationReadApi=false
                    return
                }
                Log.i("notificatinStyayus", "onResponseSuccess: ${list.size}")
                if (mAdapter.itemCount == 0 || pageNumber == 1) {
                    mViewModel.notificationModelList.let {
                        it.list?.let { it1 -> mAdapter.setData(it1) }
                    }
                } else {
                    mViewModel.notificationModelList.let {
                        it.list?.let { it1 -> mAdapter.addData(it1) }
                    }
                    if(list.size==0){
                        mBinding.rlNoNotifications.visibility=View.VISIBLE
                        mBinding.rlRecycler.visibility=View.GONE
                        mBinding.tvMarkAllRead.visibility = View.GONE
                    }else{
                        mBinding.rlNoNotifications.visibility=View.GONE
                        mBinding.rlRecycler.visibility=View.VISIBLE
                        mBinding.tvMarkAllRead.visibility = View.VISIBLE
                    }
                }
            }
            ApiCodes.TRADIE_PROFILE_PUBLIC -> {
                mProfileViewModel.builderModel.let {
                    tradieData = it

                    if (lastType == 17) {
                        startActivity(
                            Intent(this, VounchListActivity::class.java).putExtra(
                                "data",
                                tradieData!!.vouchesData
                            ).putExtra("title", tradieData?.voucherCount.toString() + " voucher(s)")
                                .putExtra("id", tradieData?.builderId).putExtra("isMyVouch", true)
                        )
                    } else {

                        startActivity(
                            Intent(this, ReviewActivity::class.java).putExtra(
                                "data",
                                tradieData!!.reviewData
                            ).putExtra(
                                "title",
                                (tradieData!!.reviewsCount!!.toInt()).toString() + " review(s)"
                            ).putExtra("count", tradieData!!.reviewsCount!!.toInt())
                        )
                    }
                }
            }
            ApiCodes.LOGOUT -> {
                val type = PreferenceManager.getInt(PreferenceManager.SOCIAL_TYPE)
                val uType = PreferenceManager.getString(PreferenceManager.USER_TYPE)
                PreferenceManager.clearAllPrefs()
                PreferenceManager.putInt(PreferenceManager.SOCIAL_TYPE, type)
                PreferenceManager.putString(PreferenceManager.USER_TYPE, uType)
                startActivity(
                    Intent(this, WelcomeActivity::class.java).putExtra(
                        "isBlock",
                        true
                    )
                )
                finishAffinity()
            }
            ApiCodes.JOB_DETAILS -> {
                mJobViewModel.mJsonResponseModel.let {
                    if (lastType == 3 || lastType == 14) {
                        Log.i("onResponseSuccess: ", it.details!!)
                        Log.i("onResponseSuccess: ", (it != null).toString())
                        Log.i("onResponseSuccess: ", (it is JobRecModel).toString())
                        startActivity(
                            Intent(this, TradieJobDetailActivity::class.java).putExtra(
                                "data",
                                it as Serializable
                            )
                                .putExtra("isBuilder", false)
                        )
                    } else if (lastType == 8) {
                        startActivity(
                            Intent(this, RateBuilderActivityStar::class.java)
                                .putExtra("data", it as Serializable)
                                .putExtra("showMilestoneProgress", true)
                        )
                    } else if (lastType == 9) {
                        startActivity(
                            Intent(this, TradieQuestionListActivity::class.java).putExtra(
                                "data",
                                it as Serializable
                            )
                                .putExtra("isBuilder", false).putExtra("isQuestionList", true)
                        )
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun markReadStatus(notificationModelList: NotificationResponse) {
        notificationModelList.let {
            it.list?.let { it1 ->
                for (index in it1.indices) {
                    Log.i("notificationStatus", "markReadStatus: $index ${list[index].read}")
                    if (it1[index].read == 1) {
                        mAdapter.updateNotificationStatus(index)
                    }
                }
            }
        }
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

//type - job details activity
