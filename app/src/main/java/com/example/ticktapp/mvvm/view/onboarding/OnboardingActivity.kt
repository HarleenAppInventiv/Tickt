package com.example.ticktapp.mvvm.view.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.app.core.preferences.PreferenceManager
import com.app.core.util.UserType
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityOnboardingBinding
import com.example.ticktapp.mvvm.view.LoginActivity
import com.example.ticktapp.mvvm.view.SignupProcessActivity
import com.example.ticktapp.mvvm.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.toolbar_onboarding.*

class OnboardingActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityOnboardingBinding
    private var fragmentArrayList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        initListener()
        setUpViewPager()
        setFullScreen()
    }

    override fun onClick(v: View?) {
        when (v) {
            mBinding.tvYellowBtn -> {
                startActivity(
                    Intent(
                        this,
                        SignupProcessActivity::class.java
                    )
                )
            }
            mBinding.tvWhiteBtn -> {
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
            }

            mBinding.ivBack->
            {
                onBackPressed()
            }


        }
    }


    private fun initListener() {
        mBinding.tvYellowBtn.setOnClickListener(this)
        mBinding.tvWhiteBtn.setOnClickListener(this)
        mBinding.ivBack.setOnClickListener(this)

    }

    private fun setUpViewPager() {
        val tutorialPagerAdapter = TutorialPagerAdapter(supportFragmentManager, fragmentArrayList)
        tutorialPagerAdapter.addFragment(OnboardingFragment.getInstance(1))
        tutorialPagerAdapter.addFragment(OnboardingFragment.getInstance(2))
        tutorialPagerAdapter.addFragment(OnboardingFragment.getInstance(3))
        mBinding.vpTutorials.offscreenPageLimit = 3
        mBinding.vpTutorials.adapter = tutorialPagerAdapter
        mBinding.vpTutorials.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {
            }

            override fun onPageSelected(position: Int) {
                handleIndicatorView(
                    position == 0,
                    position == 1,
                    position == 2
                )
                setTutorialTitle(
                    position
                )
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        handleIndicatorView(p1 = true, p2 = false, p3 = false)
        setTutorialTitle(0)
    }


    @SuppressLint("WrongConstant")
    inner class TutorialPagerAdapter(
        fm: FragmentManager,
        private val fragmentArrayList: ArrayList<Fragment>
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): Fragment {
            return fragmentArrayList[position]
        }

        fun addFragment(fragment: Fragment) {
            fragmentArrayList.add(fragment)
        }
    }

    private fun setTutorialTitle(position: Int) {
        try {
            if (PreferenceManager.getString(PreferenceManager.USER_TYPE)
                    ?.toInt() == UserType.TRAIDIE
            ) {
                when (position) {
                    0 -> {
                        mBinding.tvTitle.text =
                            getString(R.string.Find_quality_work_that_grows_your_business)
                    }
                    1 -> {
                        mBinding.tvTitle.text =
                            getString(R.string.Choose_work_that_suits_your_location_price_and_schedule)
                    }
                    2 -> {
                        mBinding.tvTitle.text =
                            getString(R.string.Job_milestones_and_scheduling_ensures_you_are_paid_on_time)
                    }
                }
            } else if (PreferenceManager.getString(PreferenceManager.USER_TYPE)
                    ?.toInt() == UserType.BUILDER
            ) {
                when (position) {
                    0 -> {
                        mBinding.tvTitle.text =
                            getString(R.string.Find_quality_trades_people)
                    }
                    1 -> {
                        mBinding.tvTitle.text =
                            getString(R.string.manage_jobs_milestone)
                    }
                    2 -> {
                        mBinding.tvTitle.text =
                            getString(R.string.seamless_payments_and_get_your_project)
                    }
                }
            }
        }
        catch (e : Exception){

        }
    }

    private fun handleIndicatorView(
        p1: Boolean,
        p2: Boolean,
        p3: Boolean,
    ) {
        mBinding.vIndicatorOne.isSelected = p1
        mBinding.vIndicatorTwo.isSelected = p2
        mBinding.vIndicatorThree.isSelected = p3
    }


}