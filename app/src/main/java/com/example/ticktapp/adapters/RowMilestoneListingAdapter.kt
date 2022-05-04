package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.core.util.MilestoneStatus
import com.example.ticktapp.R
import com.app.core.model.jobmodel.MilestoneList
import com.example.ticktapp.util.DateUtils
import kotlin.collections.ArrayList

class RowMilestoneListingAdapter(
    var mItems: ArrayList<MilestoneList>,
    val listener: OnMilestoneDataListner
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_complete_milestone, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return mItems.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                holder.bind(mItems.get(position))
                holder.binding.ivMilestoneCheck.setOnClickListener {
                    mItems.get(position).isChecked = !mItems.get(position).isChecked
                    notifyItemChanged(position)
                }

                holder.binding.btnMarkComplete.setOnClickListener {
                    listener.onMarkComplete(position)
                }

            }
        }
    }

    fun setdata(milestones: ArrayList<MilestoneList>) {
        this.mItems = milestones
        notifyDataSetChanged()
    }


    inner class ViewHolderAnswer(val binding: com.example.ticktapp.databinding.RowCompleteMilestoneBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(milestone: MilestoneList) {
            if (milestone.status == MilestoneStatus.DECLINED) {
                binding.btnMarkComplete.setText(itemView.context.getString(R.string.remark_complete))
            } else {
                binding.btnMarkComplete.setText(itemView.context.getString(R.string.mark_complete))
            }

            if (milestone.status == MilestoneStatus.COMPLETED || milestone.status == MilestoneStatus.APPROVED) {

                binding.ivMilestoneCheck.isEnabled = false
                binding.tvMilestoneName.setTextColor(itemView.context.resources.getColor(R.color.color_748092))
                binding.tvMilestoneDate.setTextColor(itemView.context.resources.getColor(R.color.color_748092))
                binding.tvMilestonePhotoRequired.setTextColor(
                    itemView.context.resources.getColor(
                        R.color.color_748092
                    )
                )
                binding.btnMarkComplete.visibility = View.GONE
                binding.viewLong.visibility = View.VISIBLE
                if (milestone.status == MilestoneStatus.APPROVED)
                    binding.ivMilestoneCheckDone.setImageResource(R.drawable.ic_check)
                else if (milestone.status == MilestoneStatus.COMPLETED)
                    binding.ivMilestoneCheckDone.setImageResource(R.drawable.radio_check)
                binding.ivMilestoneCheck.visibility = View.GONE
                binding.ivMilestoneCheckDone.visibility = View.VISIBLE
                DrawableCompat.setTint(
                    binding.viewLong.drawable,
                    ContextCompat.getColor(itemView.context, R.color.color_0b41a8)
                );
            } else if (milestone.status == MilestoneStatus.PENDING || milestone.status == MilestoneStatus.DECLINED) {
                DrawableCompat.setTint(
                    binding.viewLong.drawable,
                    ContextCompat.getColor(itemView.context, R.color.transparent)
                );
                binding.ivMilestoneCheck.visibility = View.VISIBLE
                binding.ivMilestoneCheckDone.visibility = View.GONE
                if (milestone.markComplete) {
                    binding.ivMilestoneCheck.isEnabled = true
                    binding.btnMarkComplete.visibility = View.VISIBLE
                    binding.viewLong.visibility = View.INVISIBLE
                    binding.tvMilestoneName.setTextColor(itemView.context.resources.getColor(R.color.color_161d4a))
                    binding.tvMilestoneDate.setTextColor(itemView.context.resources.getColor(R.color.color_313d48))
                    binding.tvMilestonePhotoRequired.setTextColor(
                        itemView.context.resources.getColor(
                            R.color.color_313d48
                        )
                    )
                } else {
                    DrawableCompat.setTint(
                        binding.viewLong.drawable,
                        ContextCompat.getColor(itemView.context, R.color.color_dfe5ef)
                    );
                    binding.ivMilestoneCheck.isEnabled = false
                    binding.tvMilestoneName.setTextColor(itemView.context.resources.getColor(R.color.color_748092))
                    binding.tvMilestoneDate.setTextColor(itemView.context.resources.getColor(R.color.color_748092))
                    binding.tvMilestonePhotoRequired.setTextColor(
                        itemView.context.resources.getColor(
                            R.color.color_748092
                        )
                    )
                    binding.btnMarkComplete.visibility = View.GONE
                    binding.viewLong.visibility = View.VISIBLE
                }
            } else {
                binding.ivMilestoneCheck.visibility = View.VISIBLE
                binding.ivMilestoneCheckDone.visibility = View.GONE
                binding.ivMilestoneCheck.isEnabled = false
                binding.tvMilestoneName.setTextColor(itemView.context.resources.getColor(R.color.color_748092))
                binding.tvMilestoneDate.setTextColor(itemView.context.resources.getColor(R.color.color_748092))
                binding.tvMilestonePhotoRequired.setTextColor(
                    itemView.context.resources.getColor(
                        R.color.color_748092
                    )
                )
                binding.btnMarkComplete.visibility = View.GONE
                binding.viewLong.visibility = View.VISIBLE
            }

            if (adapterPosition == itemCount - 1 || milestone.markComplete)
                binding.viewLong.visibility = View.INVISIBLE
            else
                binding.viewLong.visibility = View.VISIBLE

            binding.tvMilestoneName.text = milestone.milestoneName


            if (!milestone.toDate.isNullOrEmpty() && !milestone.fromDate.isNullOrEmpty()) {
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
                        milestone.fromDate
                    ) && !DateUtils.checkForCurrentYear(
                        DateUtils.DATE_FORMATE_8,
                        milestone.toDate
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

            }
            else if (milestone.fromDate == milestone.toDate) {
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
            }
            else {
                if (!milestone.fromDate.isNullOrEmpty()){
                var outputFormat = DateUtils.DATE_FORMATE_15
                if (DateUtils.checkForCurrentYear(DateUtils.DATE_FORMATE_8, milestone.fromDate!!)) {
                    outputFormat = DateUtils.DATE_FORMATE_16
                }
                binding.tvMilestoneDate.setText(
                    DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        outputFormat,
                        milestone.fromDate
                    )
                )}else{
                    binding.tvMilestoneDate.setText(
                       ""
                        )
                }
            }
            if (milestone.isPhotoevidence) {
                binding.tvMilestonePhotoRequired.visibility = View.VISIBLE

            } else {
                binding.tvMilestonePhotoRequired.visibility = View.GONE

            }

            binding.btnMarkComplete.alpha = 1f
            binding.btnMarkComplete.isEnabled = true
            binding.ivMilestoneCheck.setImageResource(R.drawable.radio_check)
            binding.btnMarkComplete.setBackgroundResource(R.drawable.bg_btn_yellow)
            binding.btnMarkComplete.setTextColor(itemView.context.resources.getColor(R.color.color_161d4a))

            /*if (milestone.isChecked) {
                binding.btnMarkComplete.alpha = 1f
                binding.btnMarkComplete.isEnabled = true
                binding.ivMilestoneCheck.setImageResource(R.drawable.radio_check)
                binding.btnMarkComplete.setBackgroundResource(R.drawable.bg_btn_yellow)
                binding.btnMarkComplete.setTextColor(itemView.context.resources.getColor(R.color.color_161d4a))
            } else {
                binding.btnMarkComplete.alpha = 0.4f
                binding.btnMarkComplete.isEnabled = false
                binding.btnMarkComplete.setBackgroundResource(R.drawable.bg_btn_yellow)
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_unselect_grey)
                binding.btnMarkComplete.setTextColor(itemView.context.resources.getColor(R.color.color_161d4a))

            }*/

            if (milestone.declinedReason?.reason != null) {
                binding.llDecline.visibility = View.VISIBLE
                binding.ivMilestoneCheck.visibility = View.VISIBLE
                binding.ivMilestoneCheckDone.visibility = View.GONE
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_decline_small)
                binding.tvDeclineReason.text = milestone.declinedReason?.reason
                val imageAdapter: DeclineImageAdapter
                val layountManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, true)
                if (milestone.declinedReason?.url != null) {
                    imageAdapter = milestone.declinedReason?.url?.let {
                        it?.let { it1 ->
                            DeclineImageAdapter(
                                it1
                            )
                        }
                    }!!
                    binding.rvPhotos.layoutManager = layountManager
                    binding.rvPhotos.adapter = imageAdapter
                }
            }else {
                binding.llDecline.visibility = View.GONE
            }

            if(milestone.declinedCount > 5){
                binding.btnMarkComplete.visibility = View.GONE
            }
        }
    }


    interface OnMilestoneDataListner {
        fun onMarkComplete(position: Int)

    }
}