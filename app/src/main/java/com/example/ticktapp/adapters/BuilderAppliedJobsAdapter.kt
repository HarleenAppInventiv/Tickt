package com.example.ticktapp.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemBuilderActiveJobsBinding
import com.app.core.model.jobmodel.JobDashboardModel
import com.example.ticktapp.mvvm.view.builder.NewApplicantListActivity
import com.example.ticktapp.mvvm.view.builder.QuoteListActivity
import com.example.ticktapp.mvvm.view.tradie.JobDetailsActivity
import com.example.ticktapp.util.DateUtils

class BuilderAppliedJobsAdapter(var jobList: ArrayList<JobDashboardModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context: Fragment? = null
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_builder_active_jobs, parent, false
            )
        )
    }

    public fun setFragmentContect(context: Fragment) {
        this.context = context
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
        fun bind(jobs: JobDashboardModel) {
            binding.tvMilestoneStatus.visibility = View.GONE
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

            if (jobs.status != null && jobs.status?.length!! > 0) {
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
            if (jobs.quoteJob) {
                binding.tvJobStatusAction.visibility = View.VISIBLE
                binding.tvJobStatusAction.text =
                    jobs.quoteCount.toString() + " " + binding.root.context.getString(R.string.quotes)
            } else {
                if (jobs.milestoneNumber > 0) {
                    binding.tvJobStatusAction.visibility = View.VISIBLE
                    binding.tvJobStatusAction.text =
                        binding.root.context.getString(R.string.approve)
                } else {
                    if (jobs.isApplied) {
                        binding.tvJobStatusAction.visibility = View.VISIBLE
                        binding.tvJobStatusAction.text =
                            binding.root.context.getString(R.string.applications)
                    } else {
                        binding.tvJobStatusAction.visibility = View.GONE
                    }
                }
            }
            binding.cvMainRecordList.setOnClickListener {
                if (context != null) {
                    context!!.startActivityForResult(
                        Intent(context!!.context, JobDetailsActivity::class.java)
                            .putExtra("data", jobs).putExtra("isOpen", true), 2610
                    )
                } else {
                    val context = binding.cvMainRecordList.context
                    if (context is AppCompatActivity) {
                        context.startActivityForResult(
                            Intent(context, JobDetailsActivity::class.java)
                                .putExtra("data", jobs).putExtra("isOpen", true), 2610
                        )
                    }
                }
            }
            binding.tvJobStatusAction.setOnClickListener {
                if (jobs.quoteJob) {
                    if (jobs.quoteCount > 0) {
                        val context = binding.cvMainRecordList.context
                        if (context is AppCompatActivity) {
                            context.startActivityForResult(
                                Intent(
                                    context,
                                    QuoteListActivity::class.java
                                ).putExtra("data", jobs).putExtra("isAction", true), 1310
                            )
                        }
                    }
                } else if (jobs.isApplied) {
                    val context = binding.cvMainRecordList.context
                    context.startActivity(
                        Intent(context, NewApplicantListActivity::class.java)
                            .putExtra("data", jobs).putExtra("isBuilder", true)
                    )
                }
            }

            binding.ivGo.setOnClickListener {
                if (jobs.quoteJob) {
                    if (jobs.quoteCount > 0) {
                        val context = binding.ivGo.context
                        if (context is AppCompatActivity) {
                            context.startActivityForResult(
                                Intent(
                                    context,
                                    QuoteListActivity::class.java
                                ).putExtra("data", jobs).putExtra("isAction", true), 1310
                            )
                        }
                    } else {
                        Toast.makeText(
                            context!!.requireActivity(),
                            "Np quote applied yet.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (jobs.isApplied) {
                    val context = binding.ivGo.context
                    context.startActivity(
                        Intent(context, NewApplicantListActivity::class.java)
                            .putExtra("data", jobs).putExtra("isBuilder", true)
                    )
                }
            }
        }
    }

    fun setData(jobList: ArrayList<JobDashboardModel>) {
        this.jobList.clear()
        this.jobList = jobList
        notifyDataSetChanged()
    }

    fun addData(jobList: ArrayList<JobDashboardModel>) {
        this.jobList.addAll(jobList)
        notifyDataSetChanged()
    }
}
