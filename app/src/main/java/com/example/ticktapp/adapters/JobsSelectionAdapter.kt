package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemSelectionJobsBinding
import com.app.core.model.jobmodel.JobModel

class JobsSelectionAdapter(
    private val listener: JobAdapterListener,
    var jobList: ArrayList<JobModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_selection_jobs, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return jobList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                jobList[position]?.let { holder.bind(it) }
                holder.binding.llJob.setOnClickListener {
                    jobList.get(position).isSelected =
                        jobList.get(position).isSelected != true
                    notifyItemChanged(position)
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemSelectionJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobModel) {
            binding.tvTitle.text = jobs.name
             if (jobs.isSelected == true) {
                binding.llJob.setBackgroundResource(R.drawable.bg_selected_rect_new)
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )
            } else {
                binding.llJob.setBackgroundResource(R.drawable.bg_blue_rect_new)
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_161d4a
                    )
                )
            }
        }
    }

    interface JobAdapterListener {
        fun onJobClick(position: Int)
    }
}