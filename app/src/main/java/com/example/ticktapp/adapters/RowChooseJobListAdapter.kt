package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowChooseJobListBinding
import com.app.core.model.jobmodel.JobRecModel
import com.example.ticktapp.mvvm.view.builder.postjob.AllMilestoneActivity
import com.example.ticktapp.util.DateUtils
import java.util.*

class RowChooseJobListAdapter(
    val mItems: ArrayList<JobRecModel>,
    val listener: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AllMilestoneActivity.ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_choose_job_list, parent, false
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
                    mItems.forEach { it.checked = false }
                    mItems.get(position).checked = true
                    notifyDataSetChanged()
                }


            }
        }
    }


    inner class ViewHolderAnswer(val binding: RowChooseJobListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobRecModel: JobRecModel) {
            binding.tvMilestoneName.text = jobRecModel.tradeName

            if (jobRecModel.fromDate == jobRecModel.toDate) {
                binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    jobRecModel.fromDate
                ) + " - " + DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    jobRecModel?.fromDate
                )
            } else if (jobRecModel.toDate == null || jobRecModel.toDate.equals("null") || jobRecModel.toDate.equals(
                    ""
                )
            ) {
                binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                    DateUtils.DATE_FORMATE_8,
                    DateUtils.DATE_FORMATE_14,
                    jobRecModel.fromDate
                )
                jobRecModel.toDate = ""
            } else {
                if (jobRecModel.toDate!!.split("-")[0] == jobRecModel.fromDate?.split("-")
                        ?.get(0)
                ) {
                    binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobRecModel.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_14,
                        jobRecModel.toDate
                    )
                } else {
                    binding.tvMilestoneDate.text = DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        jobRecModel.fromDate
                    ) + " - " + DateUtils.changeDateFormat(
                        DateUtils.DATE_FORMATE_8,
                        DateUtils.DATE_FORMATE_15,
                        jobRecModel.toDate
                    )
                }
            }

            binding.tvMilestonePhotoRequired.text = jobRecModel.jobName
            if (jobRecModel.description != null && jobRecModel.description!!.length > 0)
                binding.tvMilestoneDesc.text = jobRecModel.description
            else if (jobRecModel.details != null && jobRecModel.details!!.length > 0)
                binding.tvMilestoneDesc.text = jobRecModel.details
            if (jobRecModel.jobDescription != null && jobRecModel.jobDescription!!.length > 0)
                binding.tvMilestoneDesc.text = jobRecModel.jobDescription
            else
                binding.tvMilestoneDesc.text = ""

            if (jobRecModel.checked == true) {
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_check)
            } else {
                binding.ivMilestoneCheck.setImageResource(R.drawable.ic_checkbox_un_active)
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

    interface OnItemClikListner {
        fun OnItemClick(position: Int)
    }
}