package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemBuilderActiveJobsBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.builder.NewApplicantListActivity
import com.example.ticktapp.mvvm.view.builder.QuoteListActivity
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.util.DateUtils

class NewApplicantRequestAdapter(var jobList: ArrayList<JobRecModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_builder_active_jobs, parent, false
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

    inner class ViewHolder(val binding: RowitemBuilderActiveJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobRecModel) {
            binding.tvTitle.text = jobs.tradeName
            binding.tvDetails.text = jobs.jobName
            binding.tvDaysLeft.text = jobs.timeLeft
            binding.tvMoney.text = jobs.amount
            binding.tvMoneyTotal.text = jobs.total
            binding.tvDays.text = jobs.durations

            if (jobs.duration == null || jobs.duration.equals("") || jobs.duration.equals("0")) {
                if (jobs.fromDate != null) {
                    if (jobs?.fromDate == jobs?.toDate) {
                        binding.tvDays.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            jobs?.fromDate + " - " + DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_14,
                                jobs?.fromDate
                            )
                        )
                    } else if (jobs?.toDate == null || jobs?.toDate.equals("null") || jobs?.toDate.equals(
                            ""
                        )
                    ) {
                        binding.tvDays.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            jobs?.fromDate
                        )
                        jobs?.toDate = ""
                    } else {
                        if (jobs?.toDate!!.split("-")[0] == jobs?.fromDate?.split("-")!![0]) {
                            binding.tvDays.text = DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_14,
                                jobs?.fromDate
                            ) + " - " + DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_14,
                                jobs?.toDate
                            )
                        } else {
                            binding.tvDays.text = DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_15,
                                jobs?.fromDate
                            ) + " - " + DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_15,
                                jobs?.toDate
                            )
                        }
                    }
                }
            }
            binding.tvMilestoneCount.text =
                itemView.context.getString(
                    R.string.remaining_milestone_count_,
                    jobs.milestoneNumber.toString()
                )
            binding.tvMilestoneCountTotal.text = itemView.context.getString(
                R.string.remaining_milestone_count_of,
                jobs.totalMilestones.toString()
            )
            if (jobs.status != null && jobs.status?.length!! > 0) {
                binding.tvMilestoneStatus.visibility = View.VISIBLE
                binding.tvMilestoneStatus.text = jobs.status
            } else {
                binding.tvMilestoneStatus.visibility = View.INVISIBLE
            }
            try {
                binding.llMilestone.visibility = View.GONE
                binding.tvMilestoneStatus.visibility = View.GONE
                binding.progressBarHor.visibility = View.GONE
                binding.progressBarHor.progress =
                    (((jobs.milestoneNumber.toDouble() / jobs.totalMilestones.toDouble()) * 100).toInt())
            } catch (e: Exception) {
            }
            if (jobs.tradeSelectedUrl != null && jobs.tradeSelectedUrl!!.length > 0) {
                Glide.with(binding.root.context).load(jobs.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            } else if (jobs.userImage != null && jobs.userImage!!.length > 0) {
                Glide.with(binding.root.context).load(jobs.userImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            } else {
                binding.ivUserProfile.setImageResource(R.drawable.bg_circle_grey)
            }
            if (jobs.milestoneNumber > 0) {
                binding.tvJobStatusAction.visibility = View.VISIBLE
                binding.tvJobStatusAction.text = binding.root.context.getString(R.string.approve)
            } else {
                if (jobs.isApplied) {
                    binding.tvJobStatusAction.visibility = View.VISIBLE
                    binding.tvJobStatusAction.text =
                        binding.root.context.getString(R.string.applications)
                } else {
                    binding.tvJobStatusAction.visibility = View.VISIBLE
                    binding.tvJobStatusAction.text =
                        binding.root.context.getString(R.string.applications)
                }
            }
            binding.ivGo.setOnClickListener {
                val context = binding.ivGo.context
                context.startActivity(
                    Intent(context, JobDetailsActivity::class.java)
                        .putExtra("data", jobs).putExtra("isBuilder", true)
                )
            }
            binding.tvJobStatusAction.setOnClickListener {
                val context = binding.cvMainRecordList.context
                if (context is AppCompatActivity) {
                    if (jobs.quoteJob) {
                        context.startActivityForResult(
                            Intent(
                                context,
                                QuoteListActivity::class.java
                            ).putExtra("data", jobs).putExtra("isAction", true), 1310
                        )
                    } else {
                        context.startActivityForResult(
                            Intent(context, NewApplicantListActivity::class.java)
                                .putExtra("data", jobs).putExtra("isBuilder", true), 1310
                        )
                    }
                }
            }
        }
    }

    fun setData(jobList: ArrayList<JobRecModel>) {
        this.jobList.clear()
        this.jobList.addAll(jobList)
        notifyDataSetChanged()
    }

    fun addData(jobList: ArrayList<JobRecModel>) {
        this.jobList.addAll(jobList)
        notifyDataSetChanged()
    }
}
