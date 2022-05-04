package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.SpecialisationData
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemMixSpecializationBinding

class SpecializationMixAdapter(
    private val list: ArrayList<SpecialisationData>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_mix_specialization, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                holder.bind(list[position])
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemMixSpecializationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SpecialisationData) {
            binding.tvSpec.text = data.specializationName
        }
    }

}
