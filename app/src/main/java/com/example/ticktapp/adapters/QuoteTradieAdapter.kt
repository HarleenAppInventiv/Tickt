package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradie.QuoteTradie
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemQuoteTradieListBinding

class QuoteTradieAdapter(
    var builderHome: ArrayList<QuoteTradie>,
    val onclickListner: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_quote_tradie_list, parent, false
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
                holder.binding.cvMainRecordList.setOnClickListener {
                    onclickListner.onTradieClick(position)
                }
                holder.binding.ivGo.setOnClickListener {
                    onclickListner.onQuoteClick(position)
                }
                holder.binding.tvQuoteAmount.setOnClickListener {
                    onclickListner.onQuoteClick(position)
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemQuoteTradieListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tradeHome: QuoteTradie) {
            binding.tvTitle.text = tradeHome.builderName
            binding.tvJobUserDetails.text =
                tradeHome.ratings.toString() + ", " + tradeHome.reviews
            binding.tvQuoteAmount.text =
                binding.root.context.getString(R.string.quote_) + " $" + tradeHome.totalQuoteAmount.toString()
            if (tradeHome.reviews == 0 || tradeHome.reviews == 1) {
                binding.tvJobUserDetails.append(
                    " " + binding.tvTitle.context.getString(
                        R.string.review
                    )
                )
            } else {
                binding.tvJobUserDetails.append(
                    " " + binding.tvTitle.context.getString(
                        R.string.reviews
                    )
                )
            }
            if (tradeHome.builderImage != null) {
                Glide.with(binding.root.context).load(tradeHome.builderImage)
                    .placeholder(R.drawable.placeholder_profile)
                    .into(binding.ivUserProfile)
            }
            binding.tvQuoteAmountStatus.text = tradeHome.status
        }
    }

    fun setData(builderHome: ArrayList<QuoteTradie>) {
        this.builderHome.clear()
        this.builderHome.addAll(builderHome)
        notifyDataSetChanged()
    }

    fun addData(builderHome: ArrayList<QuoteTradie>) {
        this.builderHome.addAll(builderHome)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onTradieClick(pos: Int)
        fun onQuoteClick(pos: Int)
    }
}