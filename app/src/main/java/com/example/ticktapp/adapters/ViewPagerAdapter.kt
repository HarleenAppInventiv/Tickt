package com.example.ticktapp.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

/**
 * A [FragmentStatePagerAdapter] which adds fragment dynamically using [MyOrderViewPagerAdapter.addFragment]  method
 */
@SuppressLint("WrongConstant")
class ViewPagerAdapter(manager: FragmentManager?) :
    FragmentStatePagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun addFragments(fragment: List<Fragment>, title: List<String>) {
        mFragmentList.clear()
        mFragmentTitleList.clear()
        mFragmentList.addAll(fragment)
        mFragmentTitleList.addAll(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun getmFragmentList(): List<Fragment> {
        return mFragmentList
    }

    fun clearAll() {
        mFragmentList.clear()
        mFragmentTitleList.clear()
    }
}