package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowPortfolioBinding
import com.app.core.model.jobmodel.Photos
import com.example.ticktapp.mvvm.view.DialogImageViewPostActivity

class ImageAdapter(var photos: ArrayList<Photos>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_portfolio, parent, false
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
                holder.binding.rowLlPhotos.setOnClickListener {
                    val context = holder.binding.rowLlPhotos.context
                    val fotos = ArrayList<String>()
                    photos.forEach {
                        it.link?.let { it1 -> fotos.add(it1) }
                    }
                    context.startActivity(
                        Intent(context, DialogImageViewPostActivity::class.java)
                            .putExtra("photos", fotos)
                            .putExtra("pos", position)
                    )
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowPortfolioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photos: Photos) {
            if (photos?.link?.length!! > 0) {
                binding.ivPhotos.setImageResource(R.drawable.bg_drawable_rect_dfe5ef)
                binding.ivEdit.visibility = View.GONE
                binding.ivAddImage.visibility = View.GONE
                Glide.with(binding.root.context).load(photos.link)
                    .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                    .error(R.drawable.bg_drawable_rect_dfe5ef)
                    .into(binding.ivPhotos)

            }

        }
    }
}