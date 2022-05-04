package com.example.ticktapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradie.PortFolio
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowPortfolioBinding
import com.example.ticktapp.mvvm.view.builder.PortFolioActivity

class PortfolioAdapter(var photos: ArrayList<PortFolio>) :
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
                    context.startActivity(
                        Intent(
                            context,
                            PortFolioActivity::class.java
                        ).putExtra("data", photos[position])
                    )
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowPortfolioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photos: PortFolio) {
            if (photos.portfolioImage?.size!! > 0) {
                binding.ivPhotos.setImageResource(R.drawable.bg_drawable_rect_dfe5ef)
                binding.ivEdit.visibility = View.GONE
                binding.ivAddImage.visibility = View.GONE
                Glide.with(binding.root.context).load(photos.portfolioImage?.get(0))
                    .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                    .into(binding.ivPhotos)
            }

        }
    }
}