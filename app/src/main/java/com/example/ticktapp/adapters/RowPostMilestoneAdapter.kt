package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestonesBinding
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.util.DateUtils
import java.util.*

class RowPostMilestoneAdapter(var milestone: ArrayList<MilestoneData>) :
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
        fun bind(milestone: MilestoneData) {
            binding.tvMilestoneTitle.text =
                (adapterPosition + 1).toString() + ". " + milestone.name
            if (milestone.end_date != null && milestone.end_date.toString().isNotEmpty()) {
                binding.tvMilestoneDate.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        milestone.start_date
                    ) + " - " +
                            DateUtils.changeDateFormat(
                                DateUtils.DATE_FORMATE_8,
                                DateUtils.DATE_FORMATE_15,
                                milestone.end_date
                            )
                )
            } else {
                binding.tvMilestoneDate.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        milestone.start_date
                    )
                )
            }
        }
    }


}