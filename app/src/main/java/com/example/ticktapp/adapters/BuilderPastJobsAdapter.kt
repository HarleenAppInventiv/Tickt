package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.RowitemBuilderPastJobsBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.example.ticktapp.mvvm.view.builder.TradieReviewActivity
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.util.DateUtils

class BuilderPastJobsAdapter(
    var jobList: ArrayList<JobDashboardModel>,
    val listener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var fragment: BaseFragment? = null
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_builder_past_jobs, parent, false
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

    inner class ViewHolder(val binding: RowitemBuilderPastJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobDashboardModel) {
            binding.tvTitle.text = jobs.tradeName
            binding.tvDetails.text = jobs.jobName
            binding.tvMoney.text = jobs.amount
            binding.tvPlace.text = jobs.locationName
            if (jobs.jobData != null && jobs.jobData.fromDate != null) {
                if (jobs.jobData?.fromDate == jobs.jobData?.toDate) {
                    binding.tvTime.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobs.jobData?.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobs.jobData?.fromDate
                    )
                } else if (jobs.jobData?.toDate == null || jobs.jobData?.toDate.equals("null") || jobs.jobData?.toDate.equals(
                        ""
                    )
                ) {
                    binding.tvTime.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobs.jobData?.fromDate
                    )
                    jobs.jobData?.toDate = ""
                } else {
                    if (jobs.jobData?.toDate!!.split("-")[0] == jobs.jobData?.fromDate?.split("-")!![0]) {
                        binding.tvTime.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            jobs.jobData?.fromDate
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            jobs.jobData?.toDate
                        )
                    } else {
                        binding.tvTime.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            jobs.jobData?.fromDate
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            jobs.jobData?.toDate
                        )
                    }
                }
            } else {
                binding.tvTime.text = jobs.timeLeft
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
            binding.tvStatus.text = jobs.status
            try {
                binding.progressBarHor.progress =
                    (((jobs.milestoneNumber.toDouble() / jobs.totalMilestones.toDouble()) * 100).toInt())
            } catch (e: Exception) {
            }
            if (jobs.jobData != null && jobs.jobData.tradeSelectedUrl != null) {
                Glide.with(binding.root.context).load(jobs.jobData.tradeSelectedUrl)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            } else if (jobs.tradieImage != null && jobs.tradieImage!!.length > 0) {
                Glide.with(binding.root.context).load(jobs.tradieImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            } else {
                binding.ivUserProfile.setImageResource(R.drawable.bg_circle_grey)
            }
            if (jobs.status.equals("EXPIRED")) {
                binding.llJobStatusRateJob.visibility = View.GONE
                binding.tvJobStatusAction.visibility = View.VISIBLE
                binding.tvJobStatusAction.text =
                    binding.root.context.getString(R.string.publish_again)
            } else {
                binding.tvJobStatusAction.visibility = View.GONE
                if (jobs.status.equals("COMPLETED") && !jobs.isRated) {
                    binding.llJobStatusRateJob.visibility = View.VISIBLE
                } else {
                    binding.llJobStatusRateJob.visibility = View.GONE
                }
            }
            binding.cvMainRecordList.setOnClickListener {
                val context = binding.cvMainRecordList.context
                context.startActivity(
                    Intent(context, JobDetailsActivity::class.java)
                        .putExtra("data", jobs).putExtra("isBuilder", true).putExtra("isPast",true)
                )
            }
            binding.llJobStatusRateJob.setOnClickListener {
                val context = binding.llJobStatusRateJob.context
                if (fragment != null) {
                    fragment!!.startActivityForResult(
                        Intent(context, TradieReviewActivity::class.java)
                            .putExtra("data", jobs).putExtra("isBuilder", true), 1310
                    )
                } else {
                    context.startActivity(
                        Intent(context, TradieReviewActivity::class.java)
                            .putExtra("data", jobs).putExtra("isBuilder", true)
                    )
                }

            }
            binding.tvJobStatusAction.setOnClickListener {
                it.tag = absoluteAdapterPosition
                listener.onClick(it)
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

    fun setFragmentContext(fragment: BaseFragment) {
        this.fragment = fragment
    }
}
