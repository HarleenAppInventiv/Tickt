package com.example.ticktapp.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradie.ReviewData
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowReviewsBinding

class ReviewAdapter(var reviewData: ArrayList<ReviewData>, var isCompleteList: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_reviews, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        if (!isCompleteList) {
            if (reviewData.size > 3)
                return 3
            return reviewData.size
        } else
            return reviewData.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                reviewData[position]?.let { holder.bind(it) }

            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowReviewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reviewData: ReviewData) {
            binding.tvUserName.text = reviewData.reviewSenderName
            try {
                binding.tvDate.text =
                    reviewData.date?.split(" ")?.get(1) + " " + (reviewData.date?.split(" ")
                        ?.get(2))
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvDate.text = reviewData.date
            }
            if (reviewData.review == null || reviewData.review?.length!! == 0) {
                binding.tvReview.text = binding.root.context.getString(R.string.no_comment)
                binding.tvReview.setTypeface(binding.tvReview.typeface, Typeface.ITALIC);
            } else {
                binding.tvReview.text = reviewData.review
                binding.tvReview.setTypeface(binding.tvReview.typeface, Typeface.NORMAL);
            }
            binding.ratingBar.rating = reviewData.ratings?.toFloat()!!
            Glide.with(binding.root.context).load(reviewData.reviewSenderImage)
                .placeholder(R.drawable.placeholder_profile)
                .into(binding.ivUserProfile)

        }
    }

}