package com.example.ticktapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradie.PortFolio
import com.bumptech.glide.Glide
import com.exampl.AddEditPortfolioActivity
import com.example.ticktapp.R
import com.example.ticktapp.constants.PermissionConstants
import com.example.ticktapp.databinding.RowPortfolioBinding
import com.example.ticktapp.mvvm.view.builder.PortFolioActivity

class PortfolioEditableAdapter(
    val activity: Context,
    val isBuilder: Boolean,
    var photos: ArrayList<PortFolio>
) :
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
                holder.binding.ivPhotos.setOnClickListener {
                    val context = holder.binding.rowLlPhotos.context
                    if (activity is AppCompatActivity) {
                        activity.startActivityForResult(
                            Intent(
                                activity,
                                PortFolioActivity::class.java
                            ).putExtra("data", photos[position]).putExtra("isBuilder", isBuilder),
                            PermissionConstants.EDIT_PORTFOLIO
                        )
                    }
                }
                holder.binding.ivAddImage.setOnClickListener {
                    if (activity is AppCompatActivity) {
                        activity.startActivityForResult(
                            Intent(
                                activity,
                                AddEditPortfolioActivity::class.java
                            ).putExtra("isBuilder", isBuilder), PermissionConstants.ADD_PORTFOLIO
                        )
                    }
                }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowPortfolioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photos: PortFolio) {
            if (absoluteAdapterPosition == itemCount - 1 && photos.portfolioId.isNullOrEmpty()) {
                binding.ivAddImage.visibility = View.VISIBLE
                binding.ivPhotos.visibility = View.GONE
            } else {
                binding.ivAddImage.visibility = View.GONE
                binding.ivPhotos.visibility = View.VISIBLE
            }

            Glide.with(binding.root.context).load(photos.portfolioImage?.get(0))
                .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                .into(binding.ivPhotos)
            if (photos.isEditable)
                binding.ivEdit.visibility = View.VISIBLE
            else
                binding.ivEdit.visibility = View.GONE

        }
    }
}