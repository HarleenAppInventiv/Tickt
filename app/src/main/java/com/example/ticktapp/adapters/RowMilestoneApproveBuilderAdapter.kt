package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestoneListSelectionBuilderBinding
import com.app.core.model.jobmodel.JobMilestone
import com.example.ticktapp.util.DateUtils

class RowMilestoneApproveBuilderAdapter(
    val mItems: List<JobMilestone>?,
    var listerner: OnMilestoneClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_milestone_list_selection_builder, parent, false
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
                holder.binding.ivMilestoneCheck.setOnClickListener {
                    if (mItems != null) {
                        if ((mItems.get(position).status == 1 || mItems.get(position).status == 5) && (position == 0 || (mItems.get(
                                position - 1
                            ).status == 2 || mItems.get(position - 1).status == 3))
                        ) {
                            mItems.get(position).checked = mItems.get(position).checked != true
                            notifyItemChanged(position)
                        } else if (mItems.get(position).status == 2 || mItems.get(position).status == 3) {
                            if (holder.binding.tvCheckDetails.visibility == View.GONE) {
                                holder.binding.tvCheckDetails.visibility = View.VISIBLE
                            } else {
                                holder.binding.tvCheckDetails.visibility = View.GONE
                            }
                        }
                    }
                }
                holder.binding.tvCheckApprove.setOnClickListener {
                    listerner.onCheckApprove(position,true)
                }
                holder.binding.tvCheckDetails.setOnClickListener {
                    listerner.onCheckApprove(position,false)
                }
            }
        }
    }


    inner class ViewHolderAnswer(val binding: RowMilestoneListSelectionBuilderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(milestone: JobMilestone) {
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
            } else if (!milestone.toDate.isNullOrEmpty() && !milestone.fromDate.isNullOrEmpty())
                {
                    var toText=""
                    var fromText=""
                    if (milestone.toDate!!.contains("-") && milestone.toDate!!.split("-").size>0)
                    {
                      toText=  milestone.toDate!!.split("-")[0]
                    }
                    if (milestone.fromDate!!.contains("-") && milestone.fromDate!!.split("-").size>0)
                    {
                      fromText=  milestone.fromDate!!.split("-")[0]
                    }

                    if (toText.equals(fromText)) {
                        binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            milestone.fromDate
                        ) + " - " + DateUtils.changeDateFormat(
                            DateUtils.DATE_FORMATE_8,
                            DateUtils.DATE_FORMATE_14,
                            milestone.toDate
                        )
                    }else {
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
                }else if ((milestone.toDate.isNullOrEmpty() || milestone.toDate.equals("null") ) && !milestone.fromDate.isNullOrEmpty()
            ) {
                binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    milestone.fromDate
                )
                milestone.toDate = ""
            }
            else {
                binding.tvMilestoneDate.text=""
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
            if (milestone.status == 0) {
                binding.ivMilestoneCheck.alpha = 0.7f
                binding.rlLine.alpha = 0.7f
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
                binding.llRowMilestone.alpha = 0.5f
                binding.tvCheckApprove.visibility = View.GONE
            }
            if (milestone.status == 4) {
                binding.ivMilestoneCheck.alpha = 0.7f
                binding.rlLine.alpha = 0.7f
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
                binding.llRowMilestone.alpha = 0.5f
                binding.tvCheckApprove.visibility = View.GONE
            } else if (milestone.status == 3) {
                binding.ivMilestoneCheck.alpha = 0.7f
                binding.rlLine.alpha = 0.7f
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
                binding.llRowMilestone.alpha = 0.5f
                binding.tvCheckApprove.visibility = View.GONE
            } else if (milestone.status == 2) {
                binding.llRowMilestone.alpha = 0.5f
                binding.ivMilestoneCheck.alpha = 1f
                binding.rlLine.alpha = 1f
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_check)
                binding.tvCheckApprove.visibility = View.GONE
            }
            if (milestone.status == 5) {
                binding.ivMilestoneCheck.alpha = 0.7f
                binding.rlLine.alpha = 0.7f
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
                binding.llRowMilestone.alpha = 0.5f
                binding.tvCheckApprove.visibility = View.GONE
            } else {
                if (milestone.status == 1) {
                    if (milestone.checked == true) {
                        binding.ivMilestoneCheck.setImageResource(R.drawable.radio_check)
                        binding.tvCheckApprove.visibility = View.VISIBLE
                    } else {
                        binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
                        binding.tvCheckApprove.visibility = View.GONE
                    }
                }
            }

        }
    }

    public interface OnMilestoneClickListener {
        fun onCheckApprove(pos: Int,isApproved:Boolean)
    }

}