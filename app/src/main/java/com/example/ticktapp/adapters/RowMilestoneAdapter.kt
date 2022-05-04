package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.jobmodel.JobMilestone
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestonesBinding
import com.example.ticktapp.util.DateUtils
import java.util.*

class RowMilestoneAdapter(var milestone: ArrayList<JobMilestone>) :
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
            if (milestone.toDate != null && milestone.toDate.toString()
                    .isNotEmpty() && milestone.fromDate != null && milestone.fromDate.toString()
                    .isNotEmpty()
            ) {
                if (milestone.toDate!!.split("-")[0] == milestone.fromDate!!.split("-").get(0)) {
                    binding.tvMilestoneDate.setText(
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            milestone.fromDate
                        ) + " - " +
                                DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_14,
                                    milestone.toDate
                                )
                    )
                } else {
                    binding.tvMilestoneDate.setText(
                        DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_15,
                            milestone.fromDate
                        ) + " - " +
                                DateUtils.changeDateFormat(
                                    DateUtils.DATE_FORMATE_8,
                                    DateUtils.DATE_FORMATE_15,
                                    milestone.toDate
                                )
                    )
                }
            } else if (milestone.fromDate == milestone.toDate) {

                binding.tvMilestoneDate.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        milestone.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        milestone.toDate
                    )
                )
            } else {
                binding.tvMilestoneDate.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        milestone.fromDate
                    )
                )
            }
        }
    }


}