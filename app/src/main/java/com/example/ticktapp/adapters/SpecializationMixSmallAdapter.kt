package com.example.ticktapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.SpecialisationData
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemMixSmallSpecializationBinding

class SpecializationMixSmallAdapter(
    private val list: ArrayList<SpecialisationData>,
    var isShowMore: Boolean = false,
    var onspecializationClicked: OnSpecializationClicked? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_mix_small_specialization, parent, false
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
                holder.itemView.setOnClickListener {
                    Log.i("listSize", "isShowMore $position")
                    if (isShowMore) {
                        onspecializationClicked!!.onExpandClick(position, list[position])
                    }
                }
            }
        }
    }

    fun showMoreLess(list: ArrayList<SpecialisationData>, showMore: Boolean = true) {
        if (showMore) {
            for (index in list.indices) {
                this.list.addAll(list)
            }
            this.list.add(SpecialisationData("", "Show Less"))
            notifyDataSetChanged()
        } else {
            this.list.clear()
            for (index in list.indices) {
                if (index <= 4) {
                    this.list.add(list[index])
                } else if (index == 5) {
                    this.list.add(SpecialisationData("", "Show More"))
                    break
                }
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemMixSmallSpecializationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SpecialisationData) {
            binding.tvSpec.text = data.specializationName
        }
    }

    interface OnSpecializationClicked {
        fun onExpandClick(position: Int, specialisationData: SpecialisationData)
    }
}
