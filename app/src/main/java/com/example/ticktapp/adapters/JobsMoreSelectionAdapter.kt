package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.jobmodel.JobModel
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemSelectionMoreJobsBinding

class JobsMoreSelectionAdapter(
    private val listener: JobAdapterListener,
    var jobList: ArrayList<JobModel>,
    var showSelected: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_selection_more_jobs, parent, false
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
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemSelectionMoreJobsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(jobs: JobModel) {
            binding.tvTitle.text = jobs.name
            var jobSelected: Boolean = showSelected && jobs.isSelected ?: false
            setImage(binding, jobs, jobSelected)
            if (showSelected && jobs.isSelected == true) {
                binding.llJob.setBackgroundResource(R.drawable.bg_selected_rect_new)
                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )
            } else {


                binding.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_161d4a
                    )
                )
                binding.llJob.setBackgroundResource(R.drawable.bg_blue_rect_new)
            }
            binding.llJob.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            jobList.forEachIndexed { _id, index ->
                if (_id != adapterPosition)
                    jobList.get(_id).isSelected = false
            }
            jobList.get(adapterPosition).isSelected =
                jobList.get(adapterPosition).isSelected != true
            notifyDataSetChanged()
        }
    }

    private fun setImage(
        binding: RowitemSelectionMoreJobsBinding,
        jobs: JobModel,
        isJobSelected: Boolean
    ) {
        Glide.with(binding.imgJobType.context).load(jobs.image).into(binding.imgJobType)
        if (isJobSelected)

            binding.imgJobType.setColorFilter(
                ContextCompat.getColor(
                    binding.imgJobType.context,
                    R.color.color_fee600
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
        else
            binding.imgJobType.setColorFilter(
                ContextCompat.getColor(
                    binding.imgJobType.context,
                    R.color.color_123f95
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
    }

    interface JobAdapterListener {
        fun onJobClick(position: Int)
    }


}