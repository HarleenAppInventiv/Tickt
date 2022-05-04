package com.example.ticktapp.mvvm.view.tradie

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.model.tradie.ReviewData
import com.example.ticktapp.R
import com.example.ticktapp.adapters.ReviewAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityNewJobsBinding

class ReviewActivity : BaseActivity() {
    private lateinit var mBinding: ActivityNewJobsBinding
    private lateinit var mAdapter: ReviewAdapter
    private lateinit var list: ArrayList<ReviewData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_jobs)
        initRecyclerView()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
    }

    private fun initRecyclerView() {
        mBinding.llParent.setBackgroundColor(resources.getColor(R.color.white))
        list = intent.getSerializableExtra("data") as ArrayList<ReviewData>
        mBinding.tvMilestoneTitle.text =
            intent!!.extras!!.getString("title", list.size.toString() + " reviews")
        mAdapter = ReviewAdapter(list, true)
        val layoutRecManager = LinearLayoutManager(this)
        mBinding.rvNewJobs.layoutManager = layoutRecManager
        mBinding.rvNewJobs.adapter = mAdapter

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

}