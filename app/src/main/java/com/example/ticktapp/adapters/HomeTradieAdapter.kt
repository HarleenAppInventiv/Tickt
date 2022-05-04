package com.example.ticktapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.TradeHome
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemRecommnededTradieVerticalBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class HomeTradieAdapter(var tradeHome: ArrayList<TradeHome>, val listener: View.OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_recommneded_tradie_vertical, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return tradeHome.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                tradeHome[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemRecommnededTradieVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tradeHome: TradeHome) {
            binding.tvTitle.text = tradeHome.tradieName
            if (!tradeHome.businessName.isNullOrEmpty()) {
                binding.tvJobUserDetails.text = tradeHome.businessName
            }

            Log.i("tradeHome.businessName", "bind: ${tradeHome.businessName}")
            binding.tvCategoryTop.text = tradeHome.tradeData!!.get(0).tradeName
            binding.tvReviewRating.text =
                tradeHome.ratings.toString() + " | " + tradeHome.reviews
            if (tradeHome.reviews == 0 || tradeHome.reviews == 1) {
                binding.tvReviewRating.append(
                    " " + binding.ivGo.context.getString(
                        R.string.review
                    )
                )
            } else {
                binding.tvReviewRating.append(
                    " " + binding.ivGo.context.getString(
                        R.string.reviews
                    )
                )
            }
            if (tradeHome.tradieImage != null) {
                Glide.with(binding.root.context).load(tradeHome.tradieImage)
                    .placeholder(R.drawable.placeholder_profile)
                    .error(R.drawable.placeholder_profile)
                    .into(binding.ivUserProfile)
            }
            val mHomeAdapter = tradeHome.tradeData?.let { TradieSmallAdapter(it) }
            val jobLayoutManager = FlexboxLayoutManager(binding.root.context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                alignItems = AlignItems.STRETCH
            }
            binding.rvTradieCategory.layoutManager = jobLayoutManager
            binding.rvTradieCategory.adapter = mHomeAdapter

            val flexboxLayoutManager = FlexboxLayoutManager(binding.root.context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                alignItems = AlignItems.STRETCH
            }
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
            val mSpecAdapter = specialisationData.let { SpecializationSmallAdapter(it) }
            binding.rvTradieSpecilization.layoutManager = flexboxLayoutManager
            binding.rvTradieSpecilization.adapter = mSpecAdapter
            binding.ivGo.tag = absoluteAdapterPosition
            binding.ivGo.setOnClickListener(listener)
            ViewCompat.setNestedScrollingEnabled(binding.rvTradieCategory, false)
            ViewCompat.setNestedScrollingEnabled(binding.rvTradieSpecilization, false)
        }
    }
}