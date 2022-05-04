package com.example.ticktapp.mvvm.view.tradie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.jobmodel.JobModel
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.HomeAdapter
import com.example.ticktapp.adapters.JobsAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentHomeBinding
import com.example.ticktapp.mvvm.viewmodel.HomeViewModel
import com.example.ticktapp.util.BottomSheetPermissionFragment
import com.example.ticktapp.util.SingleShotLocationProvider
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_toolbar.*

class HomeFragment : BaseFragment(),
    View.OnClickListener, JobsAdapter.JobAdapterListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mRootView: View
    private lateinit var mAdapter: JobsAdapter
    private var mRecAdapter: HomeAdapter?=null
    private lateinit var mSaveAdapter: HomeAdapter
    private val list by lazy { ArrayList<JobModel>() }
    private val recList by lazy { ArrayList<JobRecModel>() }
    private val saveJobs by lazy { ArrayList<JobRecModel>() }
    private val mViewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private var isCalledAPI: Boolean = false;
    private var unReadCount: Int = 0

    companion object {
        fun getInstance(): HomeFragment {
            val fragment = HomeFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    override fun initialiseFragmentBaseViewModel() {
        initRecyclerView()
        setListerner()
        val token = PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN)
        Log.d("DEVICE_TOKEN", "${token}")

        setObservers()
        mViewModel.getJobTypeList()
        PreferenceManager.getString(PreferenceManager.LAT)?.let {
            PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                isCalledAPI = true
                Log.i("latlng", "initialiseFragmentBaseViewModel: $it $it1")
                if (it == "" || it1 == "")
                    mViewModel.getHomeListing(
                        "-37.8136", "144.9631", true
                    )
                else
                    mViewModel.getHomeListing(
                        it, it1, true
                    )
            }
        }
    }


    private fun setListerner() {
        mBinding.tvNearBy.setOnClickListener(this)
        mBinding.rlToolbar.homeToolbarTvSearch.setOnClickListener(this)
        mBinding.srLayout.setOnRefreshListener {
            mViewModel.getJobTypeList()
            PreferenceManager.getString(PreferenceManager.LAT)?.let {
                PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                    isCalledAPI = true
                    Log.i("latlng", "setListerner: $it $it1")
                    if (it == "" || it1 == "")
                        mViewModel.getHomeListing(
                            "-37.8136", "144.9631", true
                        )
                    else
                        mViewModel.getHomeListing(
                            it, it1, false,
                        )
                }
            }
        }
        mBinding.rlToolbar.ivNotification.setOnClickListener {
            startActivity(Intent(activity, NotificationsTradieActivity::class.java))
        }
    }

    private fun setObservers() {
        askPermission()
        setFragmentBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onResume() {
        super.onResume()
        if (ApplicationClass.isSaveRefresh) {
            PreferenceManager.getString(PreferenceManager.LAT)?.let {
                PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                    isCalledAPI = true
                    mViewModel.getHomeListing(
                        it, it1, true,
                    )
                }
            }
        }
        ApplicationClass.isSaveRefresh = false
    }

    fun askPermission() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_DENIED) {
            BottomSheetPermissionFragment(
                activity as AppCompatActivity,
                object : BottomSheetPermissionFragment.OnPermissionResult {
                    override fun onPermissionAllowed() {
                        context?.let {
                            SingleShotLocationProvider.requestSingleUpdate(false, it,
                                object : SingleShotLocationProvider.LocationCallback {
                                    override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates?) {
                                        Log.i(
                                            "latlng",
                                            "onNewLocationAvailable: ${location!!.latitude} :: ${location!!.longitude}"
                                        )
                                        PreferenceManager.putString(
                                            PreferenceManager.LAT,
                                            location!!.latitude.toString()
                                        )
                                        PreferenceManager.putString(
                                            PreferenceManager.LAN,
                                            location!!.longitude.toString()
                                        )
                                        mViewModel.getHomeListing(
                                            location!!.latitude.toString(),
                                            location!!.longitude.toString(),
                                            false,
                                        )
                                    }

                                    override fun onCurrentLocationNotFound() {
                                    }
                                })
                        }
                    }

                    override fun onPermissionDenied() {
                        PreferenceManager.putString(PreferenceManager.LAT, "-37.8136")
                        PreferenceManager.putString(PreferenceManager.LAN, "144.9631")
                        PreferenceManager.getString(PreferenceManager.LAT)?.let {
                            PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                                isCalledAPI = true
                                Log.i("latlng", "onPermissionDenied: $it $it1")
                                mViewModel.getHomeListing(
                                    it, it1, true,
                                )
                            }
                        }
                    }
                },
                arrayOf(
                    BottomSheetPermissionFragment.ACCESS_COARSE_LOCATION,
                    BottomSheetPermissionFragment.ACCESS_FINE_LOCATION
                )
            ).show(childFragmentManager, "")
        } else {
            PreferenceManager.putString(PreferenceManager.LAT, "-37.8136")
            PreferenceManager.putString(PreferenceManager.LAN, "144.9631")
            PreferenceManager.getString(PreferenceManager.LAT)?.let {
                PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                    isCalledAPI = true
                    Log.i("latlng", "askPermission: $it $it1")
                    mViewModel.getHomeListing(
                        it, it1, true,
                    )
                }
            }
        }
    }


    private fun initRecyclerView() {
        mAdapter = JobsAdapter(this, list)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        mSaveAdapter = HomeAdapter(saveJobs)

        val layoutSaveManager = LinearLayoutManager(activity)
setRecommendAdapter()
        mBinding.rvTrade.layoutManager = layoutManager
        mBinding.rvTrade.adapter = mAdapter

        mBinding.rvSaveJob.layoutManager = layoutSaveManager
        mBinding.rvSaveJob.adapter = mSaveAdapter

    }


    private fun setRecommendAdapter(){
        mRecAdapter?.notifyDataSetChanged()?: kotlin.run {
            mRecAdapter= HomeAdapter(recList)
            mBinding.rvRecommanded.adapter = mRecAdapter  }
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.JOB_TYPE_LIST -> {
                mBinding.tvRvH1.visibility = View.GONE
            }
            ApiCodes.HOME -> {
                mBinding.srLayout.isRefreshing = false
                mBinding.tvRvH2.visibility = View.GONE
                mBinding.tvRvH3.visibility = View.GONE
            }
        }
        super.onException(exception, apiCode)
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.JOB_TYPE_LIST -> {
                list.clear()
                mViewModel.mJsonResponseModel.jobModelList?.let {
                    list.addAll(it)
                }
                if (list.size == 0) {
                    mBinding.tvRvH1.visibility = View.GONE
                } else {
                    mBinding.tvRvH1.visibility = View.VISIBLE
                }
                mAdapter.notifyDataSetChanged()
            }
            ApiCodes.HOME -> {
                mBinding.srLayout.isRefreshing = false
                recList.clear()
                mViewModel.mHomeResponseModel.recJobList?.let {
                    recList.addAll(it)
                    mViewModel.mHomeResponseModel.unreadCount?.let {
                        unReadCount = mViewModel.mHomeResponseModel.unreadCount
                        unReadNotificationsCount(unReadCount)
                    }
                }
                if (recList.size == 0) {
                    mBinding.tvRvH2.visibility = View.GONE
                } else {
                    mBinding.tvRvH2.visibility = View.VISIBLE
                }
                mRecAdapter=null
              setRecommendAdapter()

                saveJobs.clear()
                mViewModel.mHomeResponseModel.saved_jobs?.let {
                    saveJobs.addAll(it)
                }
                if (saveJobs.size == 0) {
                    mBinding.tvRvH3.visibility = View.GONE
                } else {
                    mBinding.tvRvH3.visibility = View.VISIBLE
                }
                mSaveAdapter.notifyDataSetChanged()
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    private fun unReadNotificationsCount(unReadCount: Int) {
        if (unReadCount > 0) {
            mBinding.rlToolbar.rlUnreadCount.visibility = View.VISIBLE
            mBinding.rlToolbar.tvUnreadCount.text = unReadCount.toString()
        } else {
            mBinding.rlToolbar.rlUnreadCount.visibility = View.GONE
        }
    }


    override fun onClick(v: View?) {
        when (v) {
            tv_near_by -> {
                PreferenceManager.getString(PreferenceManager.LAT)?.let {
                    PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                        startActivity(
                            if (it.equals("")) {
                                PreferenceManager.putString(PreferenceManager.LAT, "-37.8136")
                                PreferenceManager.putString(PreferenceManager.LAN, "144.9631")
                                Intent(activity, SearchJobActivity::class.java)
                                    .putExtra("lat", "-37.8136".toDouble())
                                    .putExtra("lng", "144.9631".toDouble())
                                    .putExtra("isViewMore", true)

                            } else {
                                Intent(activity, SearchJobActivity::class.java)
                                    .putExtra("lat", it.toDouble())
                                    .putExtra("lng", it1.toDouble())
                                    .putExtra("isViewMore", true)
                            }
                        )
                    }
                }
            }
            home_toolbar_tv_search -> {
                startActivity(Intent(activity, SearchDataActivity::class.java))
            }

        }
    }

    override fun onRefresh() {
    }

    override fun onJobClick(position: Int) {
        startActivity(
            Intent(activity, SearchJobActivity::class.java)
                .putExtra("jobType", list[position]._id)
                .putExtra("jobName", list[position].name)
        )
    }


}