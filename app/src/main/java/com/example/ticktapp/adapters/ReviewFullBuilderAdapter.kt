package com.example.ticktapp.adapters

import android.content.Intent
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.tradie.ReviewData
import com.app.core.model.tradie.ReviewList
import com.app.core.preferences.PreferenceManager
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowReviewsBuilderBinding
import com.example.ticktapp.mvvm.view.builder.ReplyReviewActivity
import com.example.ticktapp.mvvm.view.builder.TradieEditReviewActivity

class ReviewFullBuilderAdapter(
    var reviewData: ArrayList<ReviewList>,
    var isTradie:Boolean=false,
    val listener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_reviews_builder, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return reviewData.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                reviewData[position]?.let { it.reviewData?.let { it1 -> holder.bind(it1) } }
            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowReviewsBuilderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reviewData: ReviewData) {
            if (isTradie) {
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

                if (PreferenceManager.getString(PreferenceManager.USER_ID) == reviewData.reviewSenderId) {
                    binding.llRowReviewEdit.visibility = View.VISIBLE
                } else {
                    binding.llRowReviewEdit.visibility = View.GONE
                }
                binding.tvUserEdit.setOnClickListener {
                    val context = binding.tvUserName.context
                    if (context is AppCompatActivity) {
                        context.startActivityForResult(
                            Intent(context, TradieEditReviewActivity::class.java)
                                .putExtra("reviewId", reviewData.reviewId)
                                .putExtra("reviews", reviewData.review)
                                .putExtra("rating", reviewData.ratings), 1310
                        )
                    }
                }
                binding.tvUserDelete.setOnClickListener {
                    it.tag = absoluteAdapterPosition
                    listener.onClick(it)
                }
            } else {
                binding.tvUserName.text = reviewData.name
                try {
                    binding.tvDate.text =
                        reviewData.date?.split(" ")?.get(1) + " " + (reviewData.date?.split(" ")
                            ?.get(2))
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.tvDate.text = reviewData.date
                }
                if (reviewData.review?.length!! == 0) {
                    binding.tvReview.text = binding.root.context.getString(R.string.no_comment)
                    binding.tvReview.setTypeface(binding.tvReview.typeface, Typeface.ITALIC);
                } else {
                    binding.tvReview.text = reviewData.review
                    binding.tvReview.setTypeface(binding.tvReview.typeface, Typeface.NORMAL);
                }
                binding.ratingBar.rating = reviewData.rating?.toFloat()!!
                Glide.with(binding.root.context).load(reviewData.userImage)
                    .placeholder(R.drawable.placeholder_profile)
                    .into(binding.ivUserProfile)

                if (reviewData.replyData != null && reviewData.replyData!!.reviewId != null &&
                    reviewData.replyData?.reviewId?.length!! > 0
                ) {
                    (binding.tvUserAnswer.layoutParams as LinearLayout.LayoutParams).gravity =
                        Gravity.CENTER
                    binding.tvUserAnswer.text =
                        binding.tvUserAnswer.context.getString(R.string.show_reply)
                    binding.llFooter.visibility = View.GONE
                    binding.tvOtherUserName.text = reviewData.replyData!!.name
                    binding.tvOtherDate.text = reviewData.replyData!!.date
                    binding.tvOtherQuestion.text = reviewData.replyData!!.reply
                    Glide.with(binding.root.context).load(reviewData.replyData!!.userImage)
                        .placeholder(R.drawable.bg_circle_grey)
                        .into(binding.ivOtherUserProfile)
                } else {
                    (binding.tvUserAnswer.layoutParams as LinearLayout.LayoutParams).gravity =
                        Gravity.START
                    binding.tvUserAnswer.text =
                        binding.tvUserAnswer.context.getString(R.string.reply)
                    binding.llFooter.visibility = View.GONE
                }
                binding.tvUserAnswer.setOnClickListener {
                    if (reviewData.replyData != null && reviewData.replyData!!.reviewId != null &&
                        reviewData.replyData!!.reviewId?.length!! > 0
                    ) {
                        if (binding.llFooter.visibility == View.GONE) {
                            binding.llFooter.visibility = View.VISIBLE
                            binding.tvUserAnswer.text =
                                binding.tvUserAnswer.context.getString(R.string.hide_reply)
                        } else {
                            binding.llFooter.visibility = View.GONE
                            binding.tvUserAnswer.text =
                                binding.tvUserAnswer.context.getString(R.string.show_reply)
                        }
                    } else {
                        val context = binding.tvUserName.context
                        if (context is AppCompatActivity) {
                            context.startActivityForResult(
                                Intent(context, ReplyReviewActivity::class.java)
                                    .putExtra("reviewId", reviewData.reviewId), 1310
                            )
                        }
                    }
                }
                binding.tvUserEdit.setOnClickListener {
                    val context = binding.tvUserName.context
                    if (context is AppCompatActivity) {
                        context.startActivityForResult(
                            Intent(context, ReplyReviewActivity::class.java)
                                .putExtra("reviewId", reviewData.reviewId)
                                .putExtra("isUpdate", true).putExtra(
                                    "reviews",
                                    reviewData.replyData?.reply
                                )
                                .putExtra(
                                    "replyID",
                                    reviewData.replyData?.replyId
                                ), 1310
                        )
                    }
                }
                binding.tvUserDelete.setOnClickListener {
                    it.tag = absoluteAdapterPosition
                    listener.onClick(it)
                }
            }
        }
    }

}