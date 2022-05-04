package com.example.ticktapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradesmodel.Specialisation
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemSpecializationBinding
import com.example.ticktapp.mvvm.view.builder.postjob.PostNewJobActivity

class SpecializationAdapter(
    private val list: ArrayList<Specialisation>,
    private val listener: SpecListAdapterListener,
    private val isSelection: Boolean = true,
    var fromPostJob: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_specialization, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                holder.bind(list[position], position)
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemSpecializationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Specialisation, pos: Int) {
            binding.tvSpec.text = data.name
            if (data.isSelected == true) {
                binding.tvSpec.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.bg_selected_rect_new)
                binding.tvSpec.setTextColor(
                    binding.root.context.resources.getColor(R.color.white)
                )
            } else {
                binding.tvSpec.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.bg_blue_rect_new)
                binding.tvSpec.setTextColor(
                    binding.root.context.resources.getColor(R.color.colorPrimary)
                )

            }
            if (!isSelection) {
                binding.tvSpec.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.bg_light_rect_new)
            }

            binding.tvSpec.setOnClickListener {
//                if (list[pos].isSelected == true) {
//                    list[pos].isSelected=false
//                    notifyDataSetChanged()
//                }
                if (isSelection) {
                    Log.i("selections", "bind: $adapterPosition")
                    Log.i("selections", "bind: ${list[0].name}")
                    if (pos == 0) {
                        list.forEachIndexed { index, elements ->
                            list[index].isSelected = false

                        }
                        list[0].isSelected = true
                       notifyDataSetChanged()
                    } else {
                        if (list[0].name == binding.root.context.getString(
                                R.string.all
                            ) && list[0].isSelected == true
                        ) {
                            list[0].isSelected = false
                            notifyItemChanged(0)
                        }

                        list[pos].isSelected =  !(list[pos].isSelected?:false)

                        if (list.none { it.isSelected == true })
                        {
                            list[0].isSelected=true
                            notifyItemChanged(0)
                        }
                        notifyItemChanged(pos)
                    }
                }

            }
        }
    }

    interface SpecListAdapterListener {
        fun onSpecCLick(position: Int)
    }

}
