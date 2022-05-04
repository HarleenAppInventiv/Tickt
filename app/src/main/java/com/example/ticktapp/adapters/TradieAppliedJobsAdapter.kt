package com.example.ticktapp.adapters

import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemActiveJobsBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.tradie.TradieJobDetailActivity

class TradieAppliedJobsAdapter(var jobList: ArrayList<JobRecModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_active_jobs, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return jobList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                jobList[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: RowitemActiveJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobRecModel) {
            binding.tvMilestoneStatus.visibility = View.GONE
            binding.tvTitle.text = jobs.jobName
            binding.tvDetails.text = jobs.builderName
            binding.tvTime.text = jobs.time
            binding.tvMoney.text = jobs.amount
            binding.tvPlace.text = jobs.locationName
            binding.tvDays.text = jobs.durations

            if (Build.VERSION.SDK_INT >= 24) {
                binding.tvMilestoneCount.text = Html.fromHtml(
                    itemView.context.getString(
                        R.string.remaining_milestone_count,
                        jobs.milestoneNumber.toString(),
                        jobs.totalMilestones.toString()
                    ), Html.FROM_HTML_MODE_LEGACY
                )
            } else
                binding.tvMilestoneCount.text = Html.fromHtml(
                    itemView.context.getString(
                        R.string.remaining_milestone_count,
                        jobs.milestoneNumber.toString(),
                        jobs.totalMilestones.toString()
                    )
                )
//            if (jobs.builderImage != null && jobs.builderImage!!.length > 0) {
            if (jobs.tradeSelectedUrl != null && jobs.tradeSelectedUrl!!.isNotEmpty()) {
//                Glide.with(binding.root.context).load(jobs.builderImage)
                Glide.with(binding.root.context).load(jobs.tradeSelectedUrl)
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
                        .putExtra("data", jobs).putExtra("showMilestoneProgress", true)
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