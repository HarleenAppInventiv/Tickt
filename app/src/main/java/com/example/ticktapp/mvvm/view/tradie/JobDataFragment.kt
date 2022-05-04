package com.example.ticktapp.mvvm.view.tradie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.FragmentJobDataBinding
import com.app.core.model.jobmodel.JobRecModel

class JobDataFragment : BaseFragment(),
    View.OnClickListener {
    private lateinit var mBinding: FragmentJobDataBinding
    private lateinit var mRootView: View
    private lateinit var data: JobRecModel

    companion object {
        fun getInstance(jobRecModel: JobRecModel): JobDataFragment {
            val fragment = JobDataFragment()
            val data = Bundle()
            data.putSerializable("data", jobRecModel);
            fragment.arguments = data;
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_job_data, container, false)
        mRootView = mBinding.root
        if (arguments != null) {
            data = arguments?.getSerializable("data") as JobRecModel
        }
        return mRootView
    }

    override fun initialiseFragmentBaseViewModel() {
        setJobData(data)
        mBinding.jobDataCv.setOnClickListener {
            startActivity(
                Intent(activity, JobDetailsActivity::class.java)
                    .putExtra("data", data)
            )
        }
    }

    private fun setJobData(jobs: JobRecModel) {
        mBinding.tvTitle.text = jobs.jobName
        mBinding.tvDetails.text = jobs.builderName
        mBinding.tvTime.text = jobs.time
        mBinding.tvMoney.text = jobs.amount
        mBinding.tvPlace.text = jobs.locationName
        mBinding.tvDays.text = jobs.durations
        if (jobs.builderImage != null && jobs.builderImage!!.length > 0) {
            Glide.with(mBinding.root.context).load(jobs.builderImage)
                .placeholder(R.drawable.placeholder_profile)
                .into(mBinding.ivUserProfile)
        } else {
            Glide.with(mBinding.root.context).load(jobs.userImage)
                .placeholder(R.drawable.placeholder_profile)
                .into(mBinding.ivUserProfile)
        }
    }

    override fun onClick(p0: View?) {
    }
}