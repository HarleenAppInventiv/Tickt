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
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity

class MyJobAdapter(var jobList: ArrayList<JobRecModel>) :
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
        if (jobList.size > 2)
            return 2
        return jobList.size
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
            binding.tvTitle.text = jobs.tradeName
            binding.tvDetails.text = jobs.jobName
            binding.tvTime.text = jobs.time
            binding.tvMoney.text = jobs.amount
            binding.tvPlace.text = jobs.locationName
            binding.tvDays.text = jobs.durations
            if (jobs.description.isNullOrEmpty() && jobs.details.isNullOrEmpty()) {
                binding.tvDesc.visibility = View.GONE
            } else {
                binding.tvDesc.visibility = View.VISIBLE
                if (!jobs.description.isNullOrEmpty())
                    binding.tvDesc.text = jobs.description
                if (!jobs.details.isNullOrEmpty())
                    binding.tvDesc.text = jobs.details
            }
            try {
                binding.tvSeen.text = jobs.questionsCount.toString().toDouble().toInt().toString()
                binding.tvMsg.text = jobs.viewersCount.toString().toDouble().toInt().toString()
            } catch (e: Exception) {
            }
            if (jobs.tradeSelectedUrl != null && jobs.tradeSelectedUrl!!.length > 0) {
                Glide.with(binding.root.context).load(jobs.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            } else {
                Glide.with(binding.root.context).load(jobs.userImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            }
            binding.cvMainRecordList.setOnClickListener {
                val context = binding.cvMainRecordList.context
                context.startActivity(
                    Intent(context, JobDetailsActivity::class.java)
                        .putExtra("data", jobs).putExtra("isBuilder", true).putExtra("isFullDate",true)
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