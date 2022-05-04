package com.example.ticktapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowPhotosVideoBinding
import com.app.core.model.jobmodel.Photos
import java.io.File

class ViewMediaAdapter(var photos: ArrayList<Photos>, val listener: View.OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_photos_video, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return photos.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                photos[position]?.let { holder.bind(it) }
                holder.binding.ivCancelImage.setOnClickListener {
                    try {
                        photos.removeAt(position)
                        notifyDataSetChanged()
                        if (photos[photos.size - 1].mediaType != 0) {
                            val photo = Photos(0, "")
                            photos.add(photos.size, photo)
                            notifyDataSetChanged()
                        }
                        listener.onClick(it)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowPhotosVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photos) {
            if (adapterPosition == photos.size - 1 && photo.mediaType == 0) {
                binding.ivAddImage.visibility = View.VISIBLE
                binding.ivPhotos.visibility = View.GONE
                binding.ivCancelImage.visibility = View.GONE
                binding.rowPhotosIvOther.visibility = View.GONE

            } else {
                binding.ivAddImage.visibility = View.GONE
                binding.ivPhotos.visibility = View.VISIBLE
                binding.ivCancelImage.visibility = View.VISIBLE
                if (photo.mediaType == 1) {
                    binding.rowPhotosIvPlay.visibility = View.GONE
                    binding.rowPhotosIvOther.visibility = View.GONE
                    binding.ivPhotos.visibility = View.VISIBLE
                    if (photo.link?.contains("http") == true) {
                        Glide.with(binding.root.context).load(photo.link)
                            .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                            .into(binding.ivPhotos)
                    } else {
                        binding.ivPhotos.setImageURI(Uri.fromFile(File(photo.link)))
                    }
                } else if (photo.mediaType == 2) {
                    binding.ivPhotos.visibility = View.VISIBLE
                    binding.rowPhotosIvPlay.visibility = View.VISIBLE
                    binding.rowPhotosIvOther.visibility = View.GONE
                    if (photo.link?.contains("http") == true) {
                        Glide.with(binding.root.context).load(photo.link)
                            .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                            .into(binding.ivPhotos)
                    } else {
                        Glide.with(binding.root.context)
                            .asBitmap()
                            .load(Uri.fromFile(File(photo.link)))
                            .into(binding.ivPhotos);
                    }
                } else if (photo.mediaType == 3) {
                    binding.ivPhotos.visibility = View.GONE
                    binding.rowPhotosIvPlay.visibility = View.GONE
                    binding.rowPhotosIvOther.visibility = View.VISIBLE
                    binding.rowPhotosIvOther.setImageResource(R.drawable.ic_doc_svg)
                } else if (photo.mediaType == 4) {
                    binding.ivPhotos.visibility = View.GONE
                    binding.rowPhotosIvPlay.visibility = View.GONE
                    binding.rowPhotosIvOther.visibility = View.VISIBLE
                    binding.rowPhotosIvOther.setImageResource(R.drawable.ic_pdf_svg)
                }
            }
        }
    }
}