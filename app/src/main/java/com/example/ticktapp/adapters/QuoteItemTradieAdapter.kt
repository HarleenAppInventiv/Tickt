package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradie.QuoteItem
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemQuoteItemTradieListBinding

class QuoteItemTradieAdapter(
    var builderHome: ArrayList<QuoteItem>,
    val isEdit: Boolean = false,
    val onclickListner: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_quote_item_tradie_list, parent, false
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
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemQuoteItemTradieListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tradeHome: QuoteItem) {
            if (isEdit) {
                binding.ivPencil.visibility = View.VISIBLE
            } else {
                binding.ivPencil.visibility = View.GONE
            }
            binding.tvQuoteItem.text = tradeHome.item_number.toString()
            binding.tvQuoteDesc.text = tradeHome.description
            binding.tvQuotePrice.text = "$" + tradeHome.totalAmount.toString()
            binding.ivPencil.tag = absoluteAdapterPosition
            binding.ivPencil.setOnClickListener(onclickListner)
        }
    }

    fun setData(builderHome: ArrayList<QuoteItem>) {
        this.builderHome.clear()
        this.builderHome.addAll(builderHome)
        notifyDataSetChanged()
    }

    fun addData(builderHome: ArrayList<QuoteItem>) {
        this.builderHome.addAll(builderHome)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<QuoteItem> {
        return builderHome
    }

    fun addData(builderHome: QuoteItem) {
        var pos = -1
        this.builderHome.forEachIndexed { index, data ->
            if (data._id != null && data._id?.length!! > 0) {
                if (data._id == builderHome._id) {
                    pos = index
                    this.builderHome.set(index, builderHome)
                }
            }
        }
        if (pos == -1)
            this.builderHome.add(builderHome)
        notifyDataSetChanged()
    }

    fun lastDataUpdate(builderHome: QuoteItem) {
        this.builderHome.set(this.builderHome.size - 1, builderHome)
        notifyDataSetChanged()
    }

    fun getLastCount(): Int? {
        try {
            return (this.builderHome.get(this.builderHome.size - 1).item_number?.plus(1))
        } catch (e: Exception) {
            return 1
        }
    }

    fun clear() {
        this.builderHome.clear()
        notifyDataSetChanged()
    }

    fun removeItem(_ids: String) {
        var pos = -1
        this.builderHome.forEachIndexed { index, data ->
            if (data._id != null && data._id?.length!! > 0) {
                if (data._id == _ids) {
                    pos=index
                }
            }
        }
        if (pos >=0)
            this.builderHome.removeAt(pos)
        notifyDataSetChanged()
    }
}