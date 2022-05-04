package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowMilestoneTemplateBinding
import com.example.ticktapp.model.TemplateData
import java.util.*

class TemplatesAdapter(
    val mItems: ArrayList<TemplateData>,
    val listener:View.OnClickListener,
    val deleteListener:View.OnClickListener
) :
    RecyclerView.Adapter<TemplatesAdapter.ViewHolderAnswer>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolderAnswer{
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_milestone_template, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return mItems.size
    }


    override fun onBindViewHolder(holder: ViewHolderAnswer, position: Int) {
        holder.bind(mItems.get(position))
        holder.binding.llJob.setOnClickListener {
            it.setTag(position)
            listener.onClick(it)
        }

    }


    inner class ViewHolderAnswer(val binding: RowMilestoneTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(templateData: TemplateData) {
            binding.tvTitle.text = templateData.templateName
            binding.tvCount.text = templateData.milestoneCount.toDouble().toInt().toString()
            if (templateData.milestoneCount.toDouble().toInt() == 1) {
                binding.tvCountText.text = binding.root.context.getString(R.string.milestone_)
            } else {
                binding.tvCountText.text = binding.root.context.getString(R.string.milestones)
            }
            binding.llDelete.setOnClickListener {
                it.tag=absoluteAdapterPosition
                deleteListener.onClick(it)
            }
        }
    }


}