package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.cards.CreditCard
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowCardViewBinding

class CardAdapter(var cardsList: ArrayList<CreditCard>, val listerner: OnCardItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_card_view, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return cardsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                cardsList[position]?.let { holder.bind(it) }
                holder.binding.llCreditCard.setOnClickListener {
                    cardsList.forEach { it.checked = false }
                    cardsList.get(position).checked = true
                    notifyDataSetChanged()
                }

                holder.binding.llDelete.setOnClickListener {
                    listerner.onCardItemClick(position, 1)
                    holder.binding.srLayout.close(true)
                }
                holder.binding.llEdit.setOnClickListener {
                    listerner.onCardItemClick(position, 2)
                    holder.binding.srLayout.close(true)
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowCardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cards: CreditCard) {
            binding.tvCardNumber.text = "XXXX " + cards.last4
            if (cards.funding.equals("credit")) {
                binding.tvCardType.text = binding.root.context.getString(R.string.credit_card)
            } else if (cards.funding.equals("debit")) {
                binding.tvCardType.text = binding.root.context.getString(R.string.debit_card)
            } else if (cards.funding.equals("prepaid")) {
                binding.tvCardType.text = binding.root.context.getString(R.string.prepaid_card)
            } else {
                binding.root.context.getString(R.string.unknown_card)
            }
            if (cards.brand.toLowerCase().contains("visa")) {
                Glide.with(binding.root.context).load(R.drawable.ic_visa)
                    .placeholder(R.drawable.ic_visa).into(binding.imgCardType)
            } else if (cards.brand.toLowerCase().contains("master")) {
                Glide.with(binding.root.context).load(R.drawable.ic_mastercard)
                    .placeholder(R.drawable.master_card).into(binding.imgCardType)
            }else if (cards.brand.toLowerCase().contains("discover")) {
                Glide.with(binding.root.context).load(R.drawable.discover)
                    .placeholder(R.drawable.discover).into(binding.imgCardType)
            }else if (cards.brand.toLowerCase().contains("amercican")) {
                Glide.with(binding.root.context).load(R.drawable.american_express)
                    .placeholder(R.drawable.american_express).into(binding.imgCardType)
            }else if (cards.brand.toLowerCase().contains("union")) {
                Glide.with(binding.root.context).load(R.drawable.union_pay)
                    .placeholder(R.drawable.union_pay).into(binding.imgCardType)
            }else{
                Glide.with(binding.root.context).load(R.drawable.credit_card)
                    .placeholder(R.drawable.credit_card).into(binding.imgCardType)
            }

            if (cards.checked) {
                binding.ivCardCheck.visibility = View.VISIBLE
            } else {
                binding.ivCardCheck.visibility = View.GONE

            }
        }
    }

    interface OnCardItemClickListener {
        public fun onCardItemClick(position: Int, type: Int)
    }

}