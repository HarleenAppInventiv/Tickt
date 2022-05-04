package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.dialog.OptionsBottomSheet
import com.example.ticktapp.interfaces.RecyclerCallback
import kotlinx.android.synthetic.main.row_option_item.view.*

/**
 * This is the generic adapter to show selection view in [OptionsBottomSheet]
 *
 * @property mOptionsList list of all the options containing option with their type
 * @property mRecyclerCallback callback to observe the changes
 *
 */
class OptionsAdapter(
    var mOptionsList: ArrayList<OptionsBottomSheet.OptionsModel>,
    var mRecyclerCallback: RecyclerCallback
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OptionsViewHolder(
        LayoutInflater.from(
            parent.context
        ).inflate(R.layout.row_option_item, parent, false)
    )

    override fun getItemCount() = mOptionsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as OptionsViewHolder).bindData(mOptionsList.get(holder.adapterPosition))
    }

    inner class OptionsViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        fun bindData(optionsModel: OptionsBottomSheet.OptionsModel?) {
            itemView.tv_item_name.text = optionsModel?.message
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mRecyclerCallback.onClick(adapterPosition, v)
        }

    }

}