package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.CancelReason
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowCancelReasonBinding

class CancelAdapter(
    var cancellationReason: ArrayList<CancelReason>,
    val listAdapter: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_cancel_reason, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return cancellationReason.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                cancellationReason[position]?.let { holder.bind(it) }
                holder.binding.llMain.setOnClickListener {
                    cancellationReason.forEach { it.checked = false }
                    cancellationReason.get(position).checked = true
                    listAdapter.onItemClick(position)
                    notifyDataSetChanged()
                }


            }
        }
    }

    fun getSelectedId(): Int {
        cancellationReason.forEach {
            if (it.checked) {
                return it.id
            }
        }
        return 0;

    }

    inner class ViewHolderAnswer(val binding: RowCancelReasonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cancellation: CancelReason) {
            binding.tvReason1.text = cancellation.reason

            if (cancellation.checked) {
                binding.ivUncheck1.setImageResource(R.drawable.ic_check)
            } else {
                binding.ivUncheck1.setImageResource(R.drawable.ic_checkbox_unselect_grey)
            }
        }
    }

    interface OnItemClickListener {
        public fun onItemClick(pos: Int);
    }
}