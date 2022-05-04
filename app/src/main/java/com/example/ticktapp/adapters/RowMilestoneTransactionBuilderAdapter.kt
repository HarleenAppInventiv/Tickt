package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.myrevenue.MilestoneList
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestoneListTransacationBuilderBinding
import com.example.ticktapp.util.DateUtils

class RowMilestoneTransactionBuilderAdapter(
    val mItems: List<MilestoneList>?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_milestone_list_transacation_builder, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return mItems?.size!!
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                mItems?.let { holder.bind(it.get(position)) }
            }
        }
    }


    inner class ViewHolderAnswer(val binding: RowMilestoneListTransacationBuilderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(milestone: MilestoneList) {
            binding.tvMilestoneName.text = milestone.milestoneName
            binding.tvPrice.text = milestone.milestoneEarning
            if (milestone.fromDate == milestone.toDate) {
                binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    milestone.fromDate
                ) + " - " + DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    milestone.toDate
                )
            } else if (milestone.toDate == null || milestone.toDate.equals("null") || milestone.toDate.equals(
                    ""
                )
            ) {
                binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    milestone.fromDate
                )
                milestone.toDate = ""
            } else {
                if (milestone.toDate!!.split("-")[0] == milestone.fromDate!!.split("-")[0]) {
                    binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        milestone.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        milestone.toDate
                    )
                } else {
                    binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        milestone.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        milestone.toDate
                    )
                }
            }
            if (milestone.isPhotoevidence == true) {
                binding.tvMilestonePhotoRequired.visibility = View.VISIBLE
                binding.viewSmall.visibility = View.GONE
                binding.viewLong.visibility = View.VISIBLE
            } else {
                binding.tvMilestonePhotoRequired.visibility = View.GONE
                binding.viewSmall.visibility = View.VISIBLE
                binding.viewLong.visibility = View.GONE
            }
            binding.llRowMilestone.alpha = 1f
            binding.ivMilestoneCheck.alpha = 1f
            binding.rlLine.alpha = 1f
            if (milestone.status.equals("Approved")) {
                binding.llRowMilestone.alpha = 1f
                binding.ivMilestoneCheck.alpha = 1f
                binding.rlLine.alpha = 1f
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_check)
                binding.tvCheckApprove.visibility = View.GONE
            } else {
                binding.ivMilestoneCheck.alpha = 0.7f
                binding.rlLine.alpha = 0.7f
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
                binding.llRowMilestone.alpha = 0.5f
                binding.tvCheckApprove.visibility = View.GONE
            }
        }
    }

}