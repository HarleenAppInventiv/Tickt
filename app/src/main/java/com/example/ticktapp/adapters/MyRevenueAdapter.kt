package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.myrevenue.RevenueList
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowPastJobsBinding
import com.example.ticktapp.util.DateUtils

class MyRevenueAdapter(var jobList: ArrayList<RevenueList>, var isCompleteList: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_past_jobs, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        if (isCompleteList) {
            return jobList.size
        } else {
            if (jobList.size > 2)
                return 2
            return jobList.size
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                jobList[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: RowPastJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: RevenueList) {
            binding.tvDateHeading.text = DateUtils.getDateWithUpdatedFormat(
                jobs.from_date,
                DateUtils.DATE_FORMAT_18,
                DateUtils.DATE_FORMATE_9
            )
            if (absoluteAdapterPosition > 0) {
                val date1 = DateUtils.getDateWithUpdatedFormat(
                    jobList?.get(absoluteAdapterPosition)?.from_date,
                    DateUtils.DATE_FORMAT_18,
                    DateUtils.DATE_FORMATE_9
                )

                val date2 = DateUtils.getDateWithUpdatedFormat(
                    jobList?.get(absoluteAdapterPosition - 1)?.from_date,
                    DateUtils.DATE_FORMAT_18,
                    DateUtils.DATE_FORMATE_9
                )
                if (date1 == date2) {
                    binding.tvDateHeading.visibility = View.GONE
                } else {
                    binding.tvDateHeading.visibility = View.VISIBLE
                }
            } else {
                binding.tvDateHeading.visibility = View.VISIBLE
            }


            // make color from string and set in drawable
            binding.tvTitle.text = jobs.jobName
            binding.tvPrice.text = jobs.earning
            binding.tvDescription.text = DateUtils.getDateWithUpdatedFormat(
                jobs.from_date,
                DateUtils.DATE_FORMAT_18,
                DateUtils.DATE_FORMATE_9
            )
            /*if (jobs.media?.size ?: 0 > 0) {
                ViewUtils.loadImage(
                    binding.ivImage,
                    binding.pbImage,
                    R.drawable.placeholder_rounded_square_with_image,
                    item.media?.get(0)?.url ?: "",
                    false
                )
            }*/
        }
    }

    fun setData(jobList: ArrayList<RevenueList>) {
        this.jobList.clear()
        this.jobList = jobList
        notifyDataSetChanged()
    }

    fun addData(jobList: ArrayList<RevenueList>) {
        this.jobList.addAll(jobList)
        notifyDataSetChanged()
    }
}
