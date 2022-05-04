package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ViewPagerAdapter
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentJobsBinding
import com.app.core.model.jobmodel.TradieJobsDashboardModel


class JobDashboardFragment : BaseFragment(),
    SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, ViewPager.OnPageChangeListener {
    private lateinit var mBinding: FragmentJobsBinding
    private lateinit var mRootView: View
    private lateinit var adapter: ViewPagerAdapter


    companion object {
        fun getInstance(): JobDashboardFragment {
            val fragment = JobDashboardFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_jobs, container, false)
        mRootView = mBinding.root
        return mRootView
    }

    override fun initialiseFragmentBaseViewModel() {
        setupTabs()
        setListerner()
        onPageSelected(0)
    }

    private fun setupTabs() {
        adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(ActiveJobFragment.getInstance(), "Active")
        adapter.addFragment(AppliedJobFragment.getInstance(), "Applied")
        adapter.addFragment(PastJobFragment.getInstance(), "Past")
        mBinding.vpJobDashboard.adapter = adapter
        mBinding.vpJobDashboard.offscreenPageLimit = 3
        mBinding.tabsContainer.setupWithViewPager(mBinding.vpJobDashboard)
        mBinding.vpJobDashboard.addOnPageChangeListener(this)
    }

    private fun setListerner() {
        mBinding.srLayout.setOnRefreshListener(this)
        mBinding.linNewJob.setOnClickListener(this)
        mBinding.linMilestone.setOnClickListener(this)
    }

    override fun onRefresh() {
        try {
            if (!childFragmentManager.isDestroyed) {

                when (mBinding.vpJobDashboard.currentItem) {
                    0 -> {
                        val currentFragment =
                            adapter.getItem(mBinding.vpJobDashboard.currentItem) as ActiveJobFragment
                        currentFragment.hitApihere(false)

                    }
                    1 -> {
                        val currentFragment =
                            adapter.getItem(mBinding.vpJobDashboard.currentItem) as AppliedJobFragment
                        currentFragment.hitApihere(false)

                    }
                    2 -> {
                        val currentFragment =
                            adapter.getItem(mBinding.vpJobDashboard.currentItem) as PastJobFragment
                        currentFragment.hitApihere(false)

                    }
                    else -> mBinding.srLayout.isRefreshing = false
                }
            }
        } catch (e: IllegalStateException) {
        }
    }

    fun updateView(mTradieActiveJobResponse: TradieJobsDashboardModel?) {
        mTradieActiveJobResponse?.let {
            mBinding.tvNewJobsCount.text = it.newJobsCount.toString()
            mBinding.tvNewMilestoneCount.text = it.milestonesCount.toString()
        }

        mBinding.srLayout.isRefreshing = false
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.linNewJob -> {
                val intent = Intent(activity, NewJobsActivity::class.java)
                startActivity(intent)
            }
            mBinding.linMilestone -> {
                val intent = Intent(activity, ApprovedMilstonesJobListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        val tv1: TextView =
            ((mBinding.tabsContainer.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(
                1
            ) as TextView
        if (position == 0) {
            tv1.setTypeface(
                ResourcesCompat.getFont(
                    mBinding.root.context,
                    R.font.neue_haas_display_bold
                )
            )
        } else {
            tv1.setTypeface(
                ResourcesCompat.getFont(
                    mBinding.root.context,
                    R.font.neue_haas_display_roman
                )
            )
        }
        val tv2: TextView =
            ((mBinding.tabsContainer.getChildAt(0) as LinearLayout).getChildAt(1) as LinearLayout).getChildAt(
                1
            ) as TextView
        if (position == 1) {
            tv2.setTypeface(
                ResourcesCompat.getFont(
                    mBinding.root.context,
                    R.font.neue_haas_display_bold
                )
            )
        } else {
            tv2.setTypeface(
                ResourcesCompat.getFont(
                    mBinding.root.context,
                    R.font.neue_haas_display_roman
                )
            )
        }
        val tv3: TextView =
            ((mBinding.tabsContainer.getChildAt(0) as LinearLayout).getChildAt(2) as LinearLayout).getChildAt(
                1
            ) as TextView
        if (position == 2) {
            tv3.setTypeface(
                ResourcesCompat.getFont(
                    mBinding.root.context,
                    R.font.neue_haas_display_bold
                )
            )
        } else {
            tv3.setTypeface(
                ResourcesCompat.getFont(
                    mBinding.root.context,
                    R.font.neue_haas_display_roman
                )
            )
        }

    }

    override fun onPageScrollStateChanged(state: Int) {
    }


}