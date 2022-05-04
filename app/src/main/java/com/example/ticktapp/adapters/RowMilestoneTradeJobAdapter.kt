package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestonesBinding
import com.app.core.model.jobmodel.JobMilestone
import com.example.ticktapp.util.DateUtils
import java.util.*

class RowMilestoneTradeJobAdapter(var milestone: ArrayList<JobMilestone>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_milestones, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return milestone.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                milestone[position]?.let { holder.bind(it) }

            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowMilestonesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(milestone: JobMilestone) {
            binding.tvMilestoneTitle.text =
                (adapterPosition + 1).toString() + ". " + milestone.milestoneName
            if (milestone.toDate != null && milestone.toDate.toString().isNotEmpty()) {
                var outputFormatFrom = DateUtils.DATE_FORMATE_15
                var outputFormatTo = DateUtils.DATE_FORMATE_15
                if (DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        milestone.fromDate!!
                    ) && DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, milestone.toDate!!)
                ) {
                    outputFormatFrom = DateUtils.DATE_FORMATE_16
                    outputFormatTo = DateUtils.DATE_FORMATE_16
                } else if (DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        milestone.fromDate!!
                    ) && !DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        milestone.toDate!!
                    )
                ) {
                    outputFormatFrom = DateUtils.DATE_FORMATE_16
                    outputFormatTo = DateUtils.DATE_FORMATE_15
                }

                binding.tvMilestoneDate.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        outputFormatFrom,
                        milestone.fromDate
                    ) + " - " +
                            DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                outputFormatTo,
                                milestone.toDate
                            )
                )

            } else {
                var outputFormat = DateUtils.DATE_FORMATE_15
                if (milestone.fromDate != null) {
                    if (DateUtils.checkForCurrentYear(
                            DateUtils.DATE_FORMATE_8,
                            milestone.fromDate!!
                        )
                    ) {
                        outputFormat = DateUtils.DATE_FORMATE_16
                    }
                }
                binding.tvMilestoneDate.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        outputFormat,
                        milestone.fromDate
                    )
                )
            }
        }
    }


}