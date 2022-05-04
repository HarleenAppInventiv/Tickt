package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradie.BuilderModel
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemRecommnededTradieMixBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class ApplicantTradieAdapter(
    var builderHome: ArrayList<BuilderModel>,
    val onclickListner: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_recommneded_tradie_mix, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return builderHome.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                builderHome[position]?.let { holder.bind(it) }
                holder.binding.ivGo.tag = position
                holder.binding.ivGo.setOnClickListener(onclickListner)
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemRecommnededTradieMixBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tradeHome: BuilderModel) {
            binding.tvTitle.text = tradeHome.builderName
            binding.tvJobUserDetails.text =
                tradeHome.ratings.toString() + ", " + tradeHome.reviews
            if (tradeHome.reviews == 0 || tradeHome.reviews == 1) {
                binding.tvJobUserDetails.append(
                    " " + binding.ivGo.context.getString(
                        R.string.review
                    )
                )
            } else {
                binding.tvJobUserDetails.append(
                    " " + binding.ivGo.context.getString(
                        R.string.reviews
                    )
                )
            }
            binding.tvStatus.text = tradeHome.status
            if (tradeHome.builderImage != null) {
                Glide.with(binding.root.context).load(tradeHome.builderImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)
            }
            val mHomeAdapter = tradeHome.tradeData?.let { TradieMixAdapter(it) }
            val jobLayoutManager = FlexboxLayoutManager(binding.root.context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                alignItems = AlignItems.STRETCH
            }
            binding.rvTradieCategory.layoutManager = jobLayoutManager
            var specialisationData: ArrayList<SpecialisationData>? = null
            if (tradeHome.specializationData != null && tradeHome.specializationData!!.size > 3) {
                specialisationData = ArrayList(tradeHome.specializationData!!.subList(0, 3))
                specialisationData.add(
                    SpecialisationData(
                        specializationName = binding.root.context.getString(
                            R.string.more_
                        )
                    )
                )
            } else {
                specialisationData = tradeHome.specializationData!!
            }
            val mSpecAdapter = specialisationData.let { SpecializationMixAdapter(it) }
            binding.rvTradieCategory.adapter = ConcatAdapter(mHomeAdapter, mSpecAdapter)
            binding.cvMainRecordList.setOnClickListener {

            }
            ViewCompat.setNestedScrollingEnabled(binding.rvTradieCategory, false)
        }
    }

    fun setData(builderHome: ArrayList<BuilderModel>) {
        this.builderHome.clear()
        this.builderHome.addAll(builderHome)
        notifyDataSetChanged()
    }

    fun addData(builderHome: ArrayList<BuilderModel>) {
        this.builderHome.addAll(builderHome)
        notifyDataSetChanged()
    }
}