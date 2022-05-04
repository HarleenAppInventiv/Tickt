package com.example.ticktapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowitemUploadedDocsBinding
import com.app.core.model.jobmodel.QualifiedDoc

class ShowUploadedDocumentsAdapter(
    private val qualList: ArrayList<QualifiedDoc>,
    private val listener: DocListAdapterListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rowitem_uploaded_docs, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return qualList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                holder.bind(qualList[position])
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowitemUploadedDocsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(qualification: QualifiedDoc) {
            binding.tvTrade.text = qualification.docName
            binding.cbQualification.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.ic_check)
            binding.ivDel.setOnClickListener{
                listener.onDocDelte(adapterPosition)
            }
            binding.tvTrade.setOnClickListener{
                listener.onDocClick(adapterPosition)
            }

        }
    }


    interface DocListAdapterListener {
        fun onDocDelte(position: Int)
        fun onDocClick(position: Int)

    }


}
