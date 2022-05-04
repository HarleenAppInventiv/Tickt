package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowSearchCircleSuggestionBinding
import com.app.core.model.jobmodel.JobModel

class SearchCircleDataAdapter(var onClickListener: View.OnClickListener, var jobList: ArrayList<JobModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_search_circle_suggestion, parent, false
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

    inner class ViewHolderAnswer(val binding: RowSearchCircleSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobs: JobModel) {
            binding.tvTitle.text = jobs.name
            binding.tvDetails.text = jobs.trade_name
            Glide.with(binding.root.context).load(jobs.image)
                .into(binding.ivUserProfile)
            binding.llRowSearchSuggestion.setOnClickListener {
                it.tag = adapterPosition
                onClickListener.onClick(it)
            }
        }
    }
}