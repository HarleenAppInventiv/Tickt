package com.example.ticktapp.adapters

import android.content.Intent
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseFragment
import com.example.ticktapp.databinding.RowitemPastJobsBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.tradie.RateBuilderActivityStar
import com.example.ticktapp.mvvm.view.tradie.TradieJobDetailActivity
import com.example.ticktapp.util.DateUtils

class TradiePastJobsAdapter(var jobList: ArrayList<JobRecModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var fragment: BaseFragment? = null
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_past_jobs, parent, false
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

    inner class ViewHolder(val binding: RowitemPastJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobRecModel) {
            binding.tvDays.text = jobs.status
            binding.tvTitle.text = jobs.jobName
            binding.tvDetails.text = jobs.builderName
            binding.tvTime.text = jobs.time
            binding.tvMoney.text = jobs.amount
            binding.tvPlace.text = jobs.locationName
            binding.progressBarHor.max = jobs.totalMilestones
            binding.progressBarHor.progress = jobs.milestoneNumber

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
//            if (jobs.builderImage != null && jobs.builderImage!!.isNotEmpty()) {
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
                        .putExtra("data", jobs).putExtra("showMilestoneProgress", false)
                        .putExtra("isQuestionAsked", false)
                )
            }
            Log.d("pastjobexception", "" + jobs.postedBy)

            binding.llAddBuilderReview.setOnClickListener {
                val context = binding.cvMainRecordList.context
                if (fragment != null) {
                    fragment!!.startActivityForResult(
                        Intent(context, RateBuilderActivityStar::class.java)
                            .putExtra("data", jobs).putExtra("showMilestoneProgress", true), 1310
                    )
                } else {
                    context.startActivity(
                        Intent(context, RateBuilderActivityStar::class.java)
                            .putExtra("data", jobs).putExtra("showMilestoneProgress", true)
                    )
                }
            }

            if (jobs.isRated)
                binding.llAddBuilderReview.visibility = View.GONE
            else {
                if (jobs.status.equals("COMPLETED"))
                    binding.llAddBuilderReview.visibility = View.VISIBLE
                else
                    binding.llAddBuilderReview.visibility = View.GONE
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

    fun setFragmentContext(fragment: BaseFragment) {
        this.fragment = fragment
    }
}