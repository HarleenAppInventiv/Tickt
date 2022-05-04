package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.TradeData
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemMixJobsBinding

class TradieMixAdapter(var tradData: ArrayList<TradeData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_mix_jobs, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return tradData.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                tradData[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemMixJobsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tradeData: TradeData) {
            binding.tvTitle.text = tradeData.tradeName
        }
    }

}