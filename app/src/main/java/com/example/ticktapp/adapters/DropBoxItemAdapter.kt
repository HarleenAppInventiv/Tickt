package com.example.ticktapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.DropBoxData
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowPhotosVideoBinding
import com.app.core.model.jobmodel.Photos
import com.app.core.util.DropBoxConstants
import com.app.core.util.MediaType
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v1.DbxPathV1
import com.dropbox.core.v1.DbxThumbnailFormat
import com.dropbox.core.v2.DbxPathV2
import com.dropbox.core.v2.DbxRawClientV2
import com.dropbox.core.v2.fileproperties.DbxUserFilePropertiesRequests
import com.dropbox.core.v2.files.DbxAppFilesRequests
import com.example.ticktapp.databinding.AdapterDropboxItemBinding
import java.io.File

class DropBoxItemAdapter(var list: ArrayList<DropBoxData>, val listener: IDropBoxItemListener) :
    RecyclerView.Adapter<DropBoxItemAdapter.DropBoxViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): DropBoxViewHolder {
        return DropBoxViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_dropbox_item, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }


    inner class DropBoxViewHolder(val binding: AdapterDropboxItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DropBoxData) {
            binding.ivPlay.visibility = View.GONE
            when (data.fileType) {
                MediaType.IMAGE -> {
                    binding.fileIcon.setImageURI(Uri.fromFile(File(data.path)))
                }
                MediaType.VIDEO -> {
                    binding.ivPlay.visibility = View.VISIBLE
                    Glide.with(binding.root.context)
                        .asBitmap()
                        .load(Uri.fromFile(File(data.path)))
                        .into(binding.fileIcon);
                }
                MediaType.PDF -> {
                    binding.fileIcon.setImageResource(R.drawable.ic_pdf_svg)
                }
                else -> {
                    binding.fileIcon.setImageResource(R.drawable.ic_doc_svg)

                }
            }
            binding.fileName.text = data.name
            binding.filePath.text = data.fileName


        }
    }

    override fun onBindViewHolder(holder: DropBoxViewHolder, position: Int) {
        list[position]?.let { holder.bind(it) }
        holder.binding.parentCL.setOnClickListener {
            listener.onItemClick(holder.adapterPosition, list[position])
        }
    }

    interface IDropBoxItemListener {
        fun onItemClick(position: Int, data: DropBoxData)
    }
}