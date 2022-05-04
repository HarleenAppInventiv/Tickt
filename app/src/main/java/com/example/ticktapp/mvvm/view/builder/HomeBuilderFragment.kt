package com.example.ticktapp.mvvm.view.builder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.core.model.tradesmodel.Trade
import com.app.core.model.tradesmodel.TradeHome
import com.app.core.preferences.PreferenceManager
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.adapters.HomeTradieAdapter
import com.example.ticktapp.adapters.HomeTradieHorizontalAdapter
import com.example.ticktapp.adapters.TradeAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentHomeBuilderBinding
import com.example.ticktapp.mvvm.view.builder.search.CategorySearchActivity
import com.example.ticktapp.mvvm.view.builder.search.SearchTradieActivity
import com.example.ticktapp.mvvm.viewmodel.HomeViewModel
import com.example.ticktapp.mvvm.viewmodel.TradeViewModel
import com.example.ticktapp.util.BottomSheetPermissionFragment
import com.example.ticktapp.util.SingleShotLocationProvider

class HomeBuilderFragment : BaseFragment(),
    View.OnClickListener, TradeAdapter.TradeAdapterListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mBinding: FragmentHomeBuilderBinding
    private lateinit var mRootView: View
    private lateinit var mAdapter: TradeAdapter
    private lateinit var mHomeAdapter: HomeTradieHorizontalAdapter
    private lateinit var mHomeRecAdapter: HomeTradieAdapter
    private val list by lazy { ArrayList<Trade?>() }
    private val mViewModel by lazy { ViewModelProvider(this).get(TradeViewModel::class.java) }
    private val mHomeViewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private var isCalledAPI: Boolean = false;
    private val tradeHome by lazy { ArrayList<TradeHome>() }
    private val tradeRecHome by lazy { ArrayList<TradeHome>() }
    private var unReadCount: Int = 0

    companion object {
        fun getInstance(): HomeBuilderFragment {
            val fragment = HomeBuilderFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home_builder, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    override fun initialiseFragmentBaseViewModel() {
        initRecyclerView();
        setListerner();
        setObservers()
        mViewModel.getTradeList(false)
        PreferenceManager.getString(PreferenceManager.LAT)?.let {
            PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                isCalledAPI = true
                if (it == "" || it1 == "")
                    mHomeViewModel.getHomeListing(
                        "-37.8136", "144.9631", true
                    )
                else
                    mHomeViewModel.getHomeListing(
                        it, it1, true
                    )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ApplicationClass.isSaveRefresh) {
            mViewModel.tradeSaveListingWithoutLoader(1)
        }
        if (mHomeViewModel.getResponseObserver().hasObservers()) {
            PreferenceManager.getString(PreferenceManager.LAT)?.let {
                PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                    isCalledAPI = true
                    Log.i("latlng", "setListerner: $it $it1")
                    if (it == "" || it1 == "")
                        mHomeViewModel.getHomeListing(
                            "-37.8136", "144.9631", true
                        )
                    else
                        mHomeViewModel.getHomeListing(
                            it, it1, true,
                        )
                }
            }
        }
    }

    private fun setListerner() {
        mBinding.tvNearBy.setOnClickListener(this)
        mBinding.rlToolbar.homeToolbarTvSearch.setOnClickListener(this)
        mBinding.srLayout.setOnRefreshListener {
            mViewModel.getTradeList(true)
            PreferenceManager.getString(PreferenceManager.LAT)?.let {
                PreferenceManager.getString(PreferenceManager.LAN)?.let { it1 ->
                    isCalledAPI = true
                    if (it == "" || it1 == "")
                        mHomeViewModel.getHomeListing(
                            "-37.8136", "144.9631", true
                        )
                    else
                        mHomeViewModel.getHomeListing(
                            it, it1, false
                        )
                }
            }
        }
        mBinding.rlToolbar.homeToolbarTvSearch.setOnClickListener {
            startActivity(Intent(activity, CategorySearchActivity::class.java))
        }
        mBinding.tvAllCategory.setOnClickListener {
            startActivity(Intent(activity, CategorySearchActivity::class.java))
        }

        mBinding.tvAllSaveTradie.setOnClickListener {
            startActivityForResult(
                Intent(activity, TradieListActivity::class.java).putExtra(
                    "data",
                    tradeHome
                ).putExtra("title", getString(R.string.saved_tradie_)), 1310
            )
        }

        mBinding.tvNearBy.setOnClickListener {
            PreferenceManager.getString(PreferenceManager.LAT)
                ?.let {
                    PreferenceManager.getString(PreferenceManager.LAN)
                        ?.let { it1 ->
                            startActivity(
                                if (it.equals("")) {
                                    PreferenceManager.putString(
                                        PreferenceManager.LAT,
                                        "-37.8136"
                                    )
                                    PreferenceManager.putString(
                                        PreferenceManager.LAN,
                                        "144.9631"
                                    )
                                    Intent(
                                        activity,
                                        SearchTradieActivity::class.java
                                    )
                                        .putExtra("lat", "-37.8136".toDouble())
                                        .putExtra("lng", "144.9631".toDouble())
                                        .putExtra("isViewMore", true)
                                } else {
                                    Intent(
                                        activity,
                                        SearchTradieActivity::class.java
                                    )
                                        .putExtra("lat", it.toDouble())
                                        .putExtra("lng", it1.toDouble())
                                        .putExtra("isViewMore", true)
                                }
                            )
                        }
                }
        }

        mBinding.rlToolbar.ivNotification.setOnClickListener {
            startActivity(Intent(activity, NotificationsActivity::class.java))
        }
    }

    private fun setObservers() {
        askPermission()
        setFragmentBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        mHomeViewModel.getResponseObserver().observe(this, this)
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
                                        mHomeViewModel.getHomeListing(
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
                                mHomeViewModel.getHomeListing(
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
                    mHomeViewModel.getHomeListing(
                        it, it1, true,
                    )
                }
            }
        }
    }


    private fun initRecyclerView() {
        mAdapter = TradeAdapter(this, list)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCategories.layoutManager = layoutManager
        mBinding.rvCategories.adapter = mAdapter

        mHomeAdapter = HomeTradieHorizontalAdapter(tradeHome) {
            val pos = it?.tag as Int
            startActivity(
                Intent(
                    activity,
                    TradieProfileActivity::class.java
                ).putExtra("data", tradeHome[pos])
                    .putExtra("isBuilder", true)
            )
        }
        val layoutManagerTradie =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvSaveTradie.layoutManager = layoutManagerTradie
        mBinding.rvSaveTradie.adapter = mHomeAdapter

        mHomeRecAdapter = HomeTradieAdapter(tradeRecHome) {
            val pos = it?.tag as Int
            startActivity(
                Intent(
                    activity,
                    TradieProfileActivity::class.java
                ).putExtra("data", tradeRecHome[pos])
                    .putExtra("isBuilder", true)
            )
        }
        val layoutManagerRecTradie =
            LinearLayoutManager(activity)
        mBinding.rvTradePeople.layoutManager = layoutManagerRecTradie
        mBinding.rvTradePeople.adapter = mHomeRecAdapter

        ViewCompat.setNestedScrollingEnabled(mBinding.rvCategories, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvTradePeople, false)
        ViewCompat.setNestedScrollingEnabled(mBinding.rvSaveTradie, false)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.GET_TRADE_LIST -> {
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
            ApiCodes.GET_TRADE_LIST -> {
                list.clear()
                mViewModel.mTradeListingResponseModel.trade?.let {
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
                tradeHome.clear()
                mHomeViewModel.mHomeResponseModel.saved_tradespeople?.let {
                    tradeHome.addAll(it)
                    mHomeViewModel.mHomeResponseModel.unreadCount?.let {
                        unReadCount = mHomeViewModel.mHomeResponseModel.unreadCount
                        unReadNotificationsCount(unReadCount)
                    }
                }
                if (tradeHome.size == 0) {
                    mBinding.tvRvH2.visibility = View.GONE
                } else {
                    mBinding.tvRvH2.visibility = View.VISIBLE
                }
                mHomeAdapter.notifyDataSetChanged()

                tradeRecHome.clear()
                mHomeViewModel.mHomeResponseModel.recomended_tradespeople?.let {
                    tradeRecHome.addAll(it)
                }
                if (tradeRecHome.size == 0) {
                    mBinding.tvRvH3.visibility = View.GONE
                } else {
                    mBinding.tvRvH3.visibility = View.VISIBLE
                }
                mHomeRecAdapter.notifyDataSetChanged()
            }
            ApiCodes.GET_SAVED_TRADE_LIST -> {
                tradeHome.clear()
                mViewModel.mRecList?.let {
                    tradeHome.addAll(it)
                }
                if (tradeHome.size == 0) {
                    mBinding.tvRvH2.visibility = View.GONE
                } else {
                    mBinding.tvRvH2.visibility = View.VISIBLE
                }
                mHomeAdapter.notifyDataSetChanged()
                ApplicationClass.isSaveRefresh = false
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
    }

    override fun onRefresh() {
    }

    override fun onTradeClick(position: Int) {
        val spec = ArrayList<String>()
        list.get(position)?.specialisations?.forEach {
            it.id?.let { it1 -> spec.add(it1) }
        }
        startActivity(
            Intent(activity, SearchTradieActivity::class.java)
                .putExtra("tradeID", list.get(position)?.id)
                .putExtra("tradeName", list.get(position)?.tradeName)
                .putExtra("specailsations", spec)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                tradeHome.clear()
                tradeHome.addAll(data.getSerializableExtra("data") as List<TradeHome>)
                mHomeAdapter.notifyDataSetChanged()
                if (tradeHome.size == 0) {
                    mBinding.tvRvH2.visibility = View.GONE
                } else {
                    mBinding.tvRvH2.visibility = View.VISIBLE
                }
            }
        }
    }
}