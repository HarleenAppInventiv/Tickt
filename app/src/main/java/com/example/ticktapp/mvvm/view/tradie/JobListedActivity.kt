package com.example.ticktapp.mvvm.view.tradie

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ticktapp.R
import com.example.ticktapp.adapters.NewJobRequestAdapter
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityNewJobsBinding
import com.app.core.model.jobmodel.JobRecModel

class JobListedActivity : BaseActivity() {
    private lateinit var mBinding: ActivityNewJobsBinding
    private lateinit var mAdapter: NewJobRequestAdapter
    private lateinit var list: ArrayList<JobRecModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_jobs)
        initRecyclerView()
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
    }

    private fun initRecyclerView() {
        list = intent.getSerializableExtra("data") as ArrayList<JobRecModel>
        mBinding.tvMilestoneTitle.text = list.size.toString() + " job(s)"
        mAdapter = NewJobRequestAdapter(list, true)
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