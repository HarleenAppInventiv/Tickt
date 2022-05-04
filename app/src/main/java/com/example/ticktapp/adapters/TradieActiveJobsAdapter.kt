package com.example.ticktapp.adapters

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
import com.example.ticktapp.util.DateUtils

class TradieActiveJobsAdapter(
    var jobList: ArrayList<JobRecModel>,
    val callback: onAdapterItemClick
) :
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
            binding.tvTitle.text = jobs.jobName
            binding.tvDetails.text = jobs.builderName
            binding.tvMoney.text = jobs.amount
            binding.tvPlace.text = jobs.locationName
            binding.tvDays.text = jobs.timeLeft
            if (jobs.toDate != null && jobs.toDate.toString().isNotEmpty()) {
                var outputFormatFrom = DateUtils.DATE_FORMATE_15
                var outputFormatTo = DateUtils.DATE_FORMATE_15
                if (DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        jobs.fromDate!!
                    ) && DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, jobs.toDate!!)
                ) {
                    outputFormatFrom = DateUtils.DATE_FORMATE_16
                    outputFormatTo = DateUtils.DATE_FORMATE_16
                } else if (DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        jobs.fromDate!!
                    ) && !DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, jobs.toDate!!)
                ) {
                    outputFormatFrom = DateUtils.DATE_FORMATE_16
                    outputFormatTo = DateUtils.DATE_FORMATE_15
                }

                binding.tvTime.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        outputFormatFrom,
                        jobs.fromDate
                    ) + " - " +
                            DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                outputFormatTo,
                                jobs.toDate
                            )
                )

            } else {
                var outputFormat = DateUtils.DATE_FORMATE_15
                if (DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, jobs.fromDate!!)) {
                    outputFormat = DateUtils.DATE_FORMATE_16
                }
                binding.tvTime.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        outputFormat,
                        jobs.fromDate
                    )
                )
            }
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
            binding.progressBarHor.max = jobs.totalMilestones
            binding.progressBarHor.progress = jobs.milestoneNumber
            binding.tvMilestoneStatus.text = jobs.status
            binding.cvMainRecordList.setOnClickListener {
                callback.onItemClick(adapterPosition)
            }
            binding.tvViewQuote.setOnClickListener {
                callback.onQuoteClick(adapterPosition)
            }

            if (jobs.milestoneNumber > 0 && jobs.status.toString().lowercase() == "declined"
            ) {
                binding.progressBarHor.progress = 0
            }

            if (jobs.quoteJob)
                binding.tvViewQuote.visibility = View.VISIBLE
            else
                binding.tvViewQuote.visibility = View.GONE
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

    interface onAdapterItemClick {
        fun onItemClick(pos: Int)
        fun onQuoteClick(pos: Int)
    }
}