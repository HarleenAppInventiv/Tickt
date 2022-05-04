package com.example.ticktapp.mvvm.view.builder

import android.app.Activity
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
import com.example.ticktapp.databinding.FragmentDashboardBinding
import com.app.core.model.jobmodel.TradieJobDashboardModel


class JobDashboardBuilderFragment : BaseFragment(),
    SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, ViewPager.OnPageChangeListener {
    private lateinit var mBinding: FragmentDashboardBinding
    private lateinit var mRootView: View
    private lateinit var adapter: ViewPagerAdapter


    companion object {
        fun getInstance(): JobDashboardBuilderFragment {
            val fragment = JobDashboardBuilderFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
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
        adapter.addFragment(ActiveBuilderJobFragment.getInstance(), getString(R.string.active))
        adapter.addFragment(OpenJobBuilderFragment.getInstance(), getString(R.string.open))
        adapter.addFragment(PastJobBuilderFragment.getInstance(), getString(R.string.past))
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

    fun refreshAll() {
        try {
            if (!childFragmentManager.isDestroyed) {
                val currentFragment1 =
                    adapter.getItem(0) as ActiveBuilderJobFragment
                if (currentFragment1.isAdded) {
                    currentFragment1.hitApihere(false)
                }
                val currentFragment2 =
                    adapter.getItem(1) as OpenJobBuilderFragment
                if (currentFragment2.isAdded) {
                    currentFragment2.hitApihere(false)
                }
                val currentFragment3 =
                    adapter.getItem(2) as PastJobBuilderFragment
                if (currentFragment3.isAdded) {
                    currentFragment3.hitApihere(false)
                }

            }
        } catch (exp: IllegalStateException) {
            exp.printStackTrace()
        }
    }

    override fun onRefresh() {
        try {
            if (!childFragmentManager.isDestroyed) {
                when (mBinding.vpJobDashboard.currentItem) {
                    0 -> {
                        val currentFragment =
                            adapter.getItem(mBinding.vpJobDashboard.currentItem) as ActiveBuilderJobFragment
                        if (currentFragment.isAdded) {
                            currentFragment.hitApihere(false)
                        } else {
                            mBinding.srLayout.isRefreshing = false
                        }
                    }
                    1 -> {
                        val currentFragment =
                            adapter.getItem(mBinding.vpJobDashboard.currentItem) as OpenJobBuilderFragment
                        if (currentFragment.isAdded) {
                            currentFragment.hitApihere(false)
                        } else {
                            mBinding.srLayout.isRefreshing = false
                        }
                    }
                    2 -> {
                        val currentFragment =
                            adapter.getItem(mBinding.vpJobDashboard.currentItem) as PastJobBuilderFragment
                        if (currentFragment.isAdded) {
                            currentFragment.hitApihere(false)
                        } else {
                            mBinding.srLayout.isRefreshing = false
                        }
                    }

                }
            }
        } catch (exp: IllegalStateException) {
            exp.printStackTrace()
        }
    }

    fun updateView(mTradieActiveJobResponse: TradieJobDashboardModel?) {
        mTradieActiveJobResponse?.let {
            if (it.needApprovalCount.toString().equals("0")) {
                mBinding.tvNewMilestoneCount.visibility = View.GONE
            } else {
                mBinding.tvNewMilestoneCount.visibility = View.VISIBLE
            }
            if (it.newApplicantsCount.toString().equals("0")) {
                mBinding.tvNewJobsCount.visibility = View.GONE
            } else {
                mBinding.tvNewJobsCount.visibility = View.VISIBLE
            }
            mBinding.tvNewJobsCount.text = it.newApplicantsCount.toString()
            mBinding.tvNewMilestoneCount.text = it.needApprovalCount.toString()
        }

        mBinding.srLayout.isRefreshing = false
    }

    override fun onClick(p0: View?) {
        when (p0) {
            mBinding.linNewJob -> {
                startActivityForResult(Intent(activity, NewApplicantActivity::class.java), 1310)
            }
            mBinding.linMilestone -> {
                startActivityForResult(
                    Intent(activity, NeedApprovalBuilderActivity::class.java),
                    1310
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            onRefresh()
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
