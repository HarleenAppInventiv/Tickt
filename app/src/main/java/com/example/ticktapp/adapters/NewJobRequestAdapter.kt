package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemRecommendedJobsBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.tradie.TradieJobDetailActivity

class NewJobRequestAdapter(var jobList: ArrayList<JobRecModel>, var isCompleteList: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_recommended_jobs, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        if (isCompleteList) {
            return jobList.size
        } else {
            if (jobList.size > 2)
                return 2
            return jobList.size
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                jobList[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: RowitemRecommendedJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobRecModel) {
            binding.tvTitle.text = jobs.jobName
            binding.tvDetails.text = jobs.builderName
            binding.tvTime.text = jobs.time
            binding.tvMoney.text = jobs.amount
            binding.tvPlace.text = jobs.locationName
            binding.tvDays.text = jobs.durations
            binding.tvDesc.text = jobs.description
            if (jobs.description.isNullOrEmpty()) {
                binding.tvDesc.visibility = View.GONE
            } else {
                binding.tvDesc.visibility = View.VISIBLE
            }
            try {
                binding.tvSeen.text = jobs.questionsCount.toString().toDouble().toInt().toString()
                binding.tvMsg.text = jobs.viewersCount.toString().toDouble().toInt().toString()
            } catch (e: Exception) {
            }
            if (jobs.builderImage != null && jobs.builderImage!!.length > 0) {
                Glide.with(binding.root.context).load(jobs.builderImage)
                    .placeholder(R.drawable.placeholder_profile)
                    .into(binding.ivUserProfile)
            } else {
                Glide.with(binding.root.context).load(jobs.userImage)
                    .placeholder(R.drawable.placeholder_profile)
                    .into(binding.ivUserProfile)
            }
            binding.cvMainRecordList.setOnClickListener {
                val context = binding.cvMainRecordList.context
                context.startActivity(
                    Intent(context, TradieJobDetailActivity::class.java)
                        .putExtra("data", jobs).putExtra("showMilestoneProgress", false)
                        .putExtra("forNewJob", true)
                )
            }
        }
    }

    fun setData(jobList: ArrayList<JobRecModel>) {
        this.jobList.clear()
        this.jobList = jobList
        notifyDataSetChanged()
    }

    fun addData(jobList: ArrayList<JobRecModel>) {
        this.jobList.addAll(jobList)
        notifyDataSetChanged()
    }
}