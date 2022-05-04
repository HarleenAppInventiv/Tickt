package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowPhotosBinding
import com.example.ticktapp.mvvm.view.DialogImageViewPostActivity
import com.example.ticktapp.mvvm.view.FileOpenActivity
import com.example.ticktapp.mvvm.view.VideoOpenActivity

class MediaPostAdapter(var photos: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_photos, parent, false
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
            }
        }

    }

    inner class ViewHolderAnswer(val binding: RowPhotosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photos: String) {
            if (photos.lowercase().endsWith(".png") || photos.lowercase()
                    .endsWith(".jpeg") || photos.lowercase().endsWith("jpg")
            ) {
                binding.rowPhotosIvPlay.visibility = View.GONE
                binding.rowPhotosIvOther.visibility = View.GONE
                binding.ivPhotos.visibility = View.VISIBLE
                Glide.with(binding.root.context).load(photos)
                    .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                    .into(binding.ivPhotos)
            } else if (photos.lowercase().endsWith(".mp4")) {
                binding.ivPhotos.visibility = View.VISIBLE
                binding.rowPhotosIvPlay.visibility = View.VISIBLE
                binding.rowPhotosIvOther.visibility = View.GONE
                Glide.with(binding.root.context).load(photos)
                    .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                    .into(binding.ivPhotos)

            } else if (photos.lowercase().endsWith(".doc") || photos.lowercase()
                    .endsWith(".docx")
            ) {
                binding.ivPhotos.visibility = View.GONE
                binding.rowPhotosIvPlay.visibility = View.GONE
                binding.rowPhotosIvOther.visibility = View.VISIBLE
                binding.rowPhotosIvOther.setImageResource(R.drawable.ic_doc_svg)
            } else if (photos.lowercase().endsWith(".pdf")) {
                binding.ivPhotos.visibility = View.GONE
                binding.rowPhotosIvPlay.visibility = View.GONE
                binding.rowPhotosIvOther.visibility = View.VISIBLE
                binding.rowPhotosIvOther.setImageResource(R.drawable.ic_pdf_svg)
            } else {
                binding.ivPhotos.visibility = View.VISIBLE
                binding.rowPhotosIvPlay.visibility = View.VISIBLE
                binding.rowPhotosIvOther.visibility = View.GONE
                Glide.with(binding.root.context).load(photos)
                    .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                    .into(binding.ivPhotos)
            }
            binding.rowLlPhotos.setOnClickListener {

                if (photos.lowercase().endsWith(".png") || photos.lowercase()
                        .endsWith(".jpeg") || photos.lowercase().endsWith("jpg")
                ) {
                    val images = ArrayList<String>()
                    images.add(photos)
                    it.context.startActivity(
                        Intent(it.context, DialogImageViewPostActivity::class.java)
                            .putExtra("photos", images)
                            .putExtra("pos", position)
                    )
                } else if (photos.lowercase().endsWith(".doc") || photos.lowercase()
                        .endsWith(".docx") || photos.lowercase()
                        .endsWith(".pdf")
                ) {
                    it.context.startActivity(
                        Intent(it.context, FileOpenActivity::class.java)
                            .putExtra("data", photos)
                    )
                } else if (photos.lowercase().endsWith(".mp4")) {
                    it.context.startActivity(
                        Intent(it.context, VideoOpenActivity::class.java)
                            .putExtra("data", photos)
                    )
                } else {
                    it.context.startActivity(
                        Intent(it.context, VideoOpenActivity::class.java)
                            .putExtra("data", photos)
                    )
                }
            }
        }
    }
}