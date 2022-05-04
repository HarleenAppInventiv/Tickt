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
import com.example.ticktapp.databinding.RowPortfolioBinding
import com.example.ticktapp.mvvm.view.DialogImageViewPostActivity
import com.example.ticktapp.mvvm.view.FileOpenActivity

class DeclineImageAdapter(var photos: ArrayList<String>) :
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
                holder.binding.ivPhotos.setOnClickListener {
                    holder.binding.ivPhotos.context.startActivity(
                        Intent(
                            holder.binding.ivPhotos.context,
                            DialogImageViewPostActivity::class.java
                        ).putExtra("photos", photos)
                            .putExtra("pos",position)
                    )
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowPhotosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photos: String) {
            Glide.with(binding.root.context).load(photos)
                .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                .into(binding.ivPhotos)
        }
    }
}