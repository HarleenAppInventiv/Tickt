package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.Trade
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemTradesBinding

class TradeAdapter(private val listener: TradeAdapterListener, var tradeList: ArrayList<Trade?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_trades, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return tradeList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                tradeList[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemTradesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trade: Trade) {
            binding.tvTrade.text = trade.tradeName
            if (trade.isSelected == true) {
                binding.flBackground.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.bg_selected_rect_trade_new
                )
                binding.ivTick.visibility = View.VISIBLE
                Glide.with(binding.root.context).load(trade.selectedUrl).circleCrop()
                    .placeholder(R.drawable.bg_selected_rect_trade_new)
                    .into(binding.ivTrade)
                binding.ivTrade.setColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_fee600
                    )
                );
            } else {
                binding.ivTick.visibility = View.GONE
                binding.flBackground.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.bg_selected_rect_trade_new
                )
                Glide.with(
                    binding.root.context
                ).load(trade.selectedUrl).circleCrop()
                    .placeholder(R.drawable.bg_selected_rect_trade_new)
                    .into(binding.ivTrade)
                binding.ivTrade.setColorFilter(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                );
            }

            binding.rlTrade.setOnClickListener {
                listener.onTradeClick(bindingAdapterPosition)
            }
        }
    }

    interface TradeAdapterListener {
        fun onTradeClick(position: Int)
    }


}