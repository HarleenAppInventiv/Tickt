package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestoneListBinding
import com.app.core.model.jobmodel.MilestoneList
import com.example.ticktapp.mvvm.view.builder.milestone.MilestoneEditProfileListingActivity
import com.example.ticktapp.util.DateUtils
import java.util.*

class RowEditMilestoneProfileAdapter(
    val mItems: ArrayList<MilestoneList>,
    val listener: OnMilestoneDataListner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    MilestoneEditProfileListingActivity.ItemTouchHelperAdapter {

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
        fun enableDisableViewGroup(viewGroup: ViewGroup, enabled: Boolean) {
            val childCount = viewGroup.childCount
            for (i in 0 until childCount) {
                val view = viewGroup.getChildAt(i)
                view.isEnabled = enabled
                if (view is ViewGroup) {
                    enableDisableViewGroup(view, enabled)
                }
            }
        }

        fun bind(milestone: MilestoneList) {
            if (milestone.status == 1 || milestone.status == 2 || milestone.status == 3 || milestone.status == 5) {
                binding.frameMain.alpha = 0.4f
                binding.srLayout.setLockDrag(true)
                enableDisableViewGroup(binding.frameMain, false)
            } else {
                binding.srLayout.setLockDrag(false)
                binding.frameMain.alpha = 1f
                enableDisableViewGroup(binding.frameMain, true)
            }
            binding.tvMilestoneName.text = milestone.milestoneName
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
                if (milestone.toDate!!.split("-")[0] == milestone.fromDate?.split("-")?.get(0)) {
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
            binding.tvMilestoneDate.append(
                ", " + milestone.recommendedHours + " " + binding.ivMilestoneCheck.context.getString(
                    R.string.hours_
                )
            )
            if (milestone.isPhotoevidence) {
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
        if (listener != null)
            listener.positionChanged()
        return true
    }

    interface OnMilestoneDataListner {
        fun onEdit(position: Int)
        fun onDelete(position: Int)
        fun positionChanged()
    }
}