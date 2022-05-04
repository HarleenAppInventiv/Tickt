package com.example.ticktapp.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.app.core.model.jobmodel.JobDashboardModel
import com.example.ticktapp.databinding.RowitemBuilderActiveJobsBinding
import com.example.ticktapp.mvvm.view.builder.postjob.CheckAndApproveMilestoneActivity
import com.example.ticktapp.util.DateUtils

class BuilderActiveJobsAdapter(
    var jobList: ArrayList<JobDashboardModel>,
    val listener: View.OnClickListener
) :
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
                holder.binding.tvMoneyTotal.tag = position
                holder.binding.tvMoneyTotal.setOnClickListener {
                    if (jobList[position]?.quoteJob != null && jobList[position]?.quoteJob == true && jobList[position]?.quote != null && jobList[position]?.quote.size > 0) {
                        listener.onClick(it)
                    }
                }
            }
        }
    }

    inner class ViewHolder(val binding: RowitemBuilderActiveJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobDashboardModel) {
            binding.tvTitle.text = jobs.tradeName
            binding.tvDetails.text = jobs.jobName
            binding.tvDaysLeft.text = jobs.timeLeft
            binding.tvMoney.text = jobs.amount
            binding.tvMoneyTotal.text = jobs.total
            if (jobs.fromDate != null) {
                if (jobs?.fromDate == jobs?.toDate) {
                    binding.tvDays.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobs?.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobs?.fromDate
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
            binding.tvMilestoneCount.text =
                itemView.context.getString(
                    R.string.remaining_milestone_count_,
                    jobs.milestoneNumber.toString()
                )
            binding.tvMilestoneCountTotal.text = itemView.context.getString(
                R.string.remaining_milestone_count_of,
                jobs.totalMilestones.toString()
            )
            if (jobs.status?.length!! > 0) {
                binding.tvMilestoneStatus.visibility = View.VISIBLE
                if (jobs.status != null && jobs.status?.lowercase() == "needs approval") {
                    binding.tvMilestoneStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_status_update,
                        0,
                        0,
                        0
                    )
                } else {
                    binding.tvMilestoneStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
                binding.tvMilestoneStatus.text = jobs.status
            } else {
                binding.tvMilestoneStatus.visibility = View.INVISIBLE
            }
            try {
                binding.progressBarHor.progress =
                    (((jobs.milestoneNumber.toDouble() / jobs.totalMilestones.toDouble()) * 100).toInt())
            } catch (e: Exception) {
            }
            if (jobs.tradeSelectedUrl != null && jobs.tradeSelectedUrl!!.isNotEmpty()) {
                Glide.with(binding.root.context).load(jobs.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            } else if (jobs.tradieImage != null && jobs.tradieImage!!.length > 0) {
                Glide.with(binding.root.context).load(jobs.tradieImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            } else {
                binding.ivUserProfile.setImageResource(R.drawable.bg_circle_grey)
            }
            if (jobs.milestoneNumber > 0 && jobs.status.toString().lowercase() == "needs approval"
            ) {
                binding.tvJobStatusAction.visibility = View.VISIBLE
                binding.tvJobStatusAction.text = binding.root.context.getString(R.string.approve)
            } else if (jobs.milestoneNumber > 0 && jobs.needApproval) {
                binding.tvJobStatusAction.visibility = View.VISIBLE
                binding.tvJobStatusAction.text = binding.root.context.getString(R.string.approve)
            } else {
                if (jobs.isApplied) {
                    binding.tvJobStatusAction.visibility = View.VISIBLE
                    binding.tvJobStatusAction.text =
                        binding.root.context.getString(R.string.applications)
                } else {
                    binding.tvJobStatusAction.visibility = View.GONE
                }
            }
            if (jobs.milestoneNumber > 0 && jobs.status.toString().lowercase() == "declined"
            ) {
                binding.progressBarHor.progress = 0
            }
            binding.cvMainRecordList.setOnClickListener {
                Log.i("tradieID", "bind: ${jobs.tradieId}")
                val context = binding.cvMainRecordList.context
                context.startActivity(
                    Intent(context, CheckAndApproveMilestoneActivity::class.java)
                        .putExtra("data", jobs).putExtra("isBuilder", true).putExtra("isChat", true)
                )
            }

        }
    }

    fun setData(jobList: ArrayList<JobDashboardModel>) {
        this.jobList.clear()
        this.jobList.addAll(jobList)
        notifyDataSetChanged()
    }

    fun addData(jobList: ArrayList<JobDashboardModel>) {
        this.jobList.addAll(jobList)
        notifyDataSetChanged()
    }
}
