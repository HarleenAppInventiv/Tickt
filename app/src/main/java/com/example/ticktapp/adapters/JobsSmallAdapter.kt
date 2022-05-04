package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemSmallJobsBinding
import com.app.core.model.jobmodel.JobModel

class JobsSmallAdapter(private val listener: JobAdapterListener, var jobList: ArrayList<JobModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_small_jobs, parent, false
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
                    listener.onJobClick(position = position)
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemSmallJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobModel) {
            binding.tvTitle.text = jobs.name
        }
    }

    interface JobAdapterListener {
        fun onJobClick(position: Int)
    }


}