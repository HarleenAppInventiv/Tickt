package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestoneListBinding
import com.example.ticktapp.model.MilestoneData
import com.example.ticktapp.mvvm.view.builder.postjob.AllMilestoneActivity
import com.example.ticktapp.util.DateUtils
import java.util.*

class RowAddMilestoneAdapter(
    val mItems: ArrayList<MilestoneData>,
    val listener: OnMilestoneDataListner
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AllMilestoneActivity.ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_milestone_list, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return mItems.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                holder.bind(mItems.get(holder.absoluteAdapterPosition))
                holder.binding.ivMilestoneCheck.setOnClickListener {
                    mItems.get(holder.absoluteAdapterPosition).isChecked =
                        !mItems.get(holder.absoluteAdapterPosition).isChecked
                    notifyItemChanged(holder.absoluteAdapterPosition)
                }
                holder.binding.ivMilestoneEdit.setOnClickListener {
                    listener.onEdit(holder.absoluteAdapterPosition)
                }
                holder.binding.llDelete.setOnClickListener {
                    holder.binding.srLayout.close(true)
                    listener.onDelete(holder.absoluteAdapterPosition)
                }
            }
        }
    }


    inner class ViewHolderAnswer(val binding: RowMilestoneListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(milestone: MilestoneData) {
            binding.tvMilestoneName.text = milestone.name
            if (milestone.start_date == milestone.end_date) {
                binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    milestone.start_date
                ) + " - " + DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    milestone.start_date
                )
            } else if (milestone.end_date == null || milestone.end_date.equals("null") || milestone.end_date.equals(
                    ""
                )
            ) {
                binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    milestone.start_date
                )
                milestone.end_date = ""
            } else {
                if (milestone.end_date.split("-")[0] == milestone.start_date.split("-")[0]) {
                    binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        milestone.start_date
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        milestone.end_date
                    )
                } else {
                    binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        milestone.start_date
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        milestone.end_date
                    )
                }
            }
            if (milestone.hours != "" && milestone.hours != "0") {
                binding.tvMilestoneDate.append(
                    ", " + milestone.hours + " " + binding.ivMilestoneCheck.context.getString(
                        R.string.hours_
                    )
                )
            }
            if (milestone.photoRequired) {
                binding.tvMilestonePhotoRequired.visibility = View.VISIBLE
                binding.viewSmall.visibility = View.GONE
                binding.viewLong.visibility = View.VISIBLE
            } else {
                binding.tvMilestonePhotoRequired.visibility = View.GONE
                binding.viewSmall.visibility = View.VISIBLE
                binding.viewLong.visibility = View.GONE
            }
            if (milestone.isChecked) {
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_check)
                binding.ivMilestoneEdit.visibility = View.VISIBLE
            } else {
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
                binding.ivMilestoneEdit.visibility = View.INVISIBLE
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mItems, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mItems, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    interface OnMilestoneDataListner {
        fun onEdit(position: Int)
        fun onDelete(position: Int)
    }
}