package com.example.ticktapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.app.core.model.questionlist.Answer
import com.example.ticktapp.databinding.RowTradieAnswersListBinding
import com.example.ticktapp.mvvm.view.builder.AskQuestionReplyActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TradieAnswersAdapter(
    val context: Context,
    val jobId: String,
    val status: String,
    val isFullAnswersListShown: Boolean = false,
    val lastAnswerOfTradie: Int = -1,
    val quesIs: String,
    var questionList: ArrayList<Answer>, val listener: Listener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.row_tradie_answers_list, parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                questionList.let {
                    holder.bind(it.get(position))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    inner class ViewHolderAnswer(val binding: RowTradieAnswersListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(questionData: Answer) {
            if (questionData.sender_user_type == 1) {
                binding.tvOtherUserName.text = questionData.tradie[0].firstName
                Glide.with(binding.root.context).load(questionData.tradie[0].user_image)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivOtherUserProfile)
            } else {
                if (!questionData.builder.isNullOrEmpty()) {
                    binding.tvOtherUserName.text = questionData.builder[0].firstName
                    Glide.with(binding.root.context).load(questionData.builder[0].user_image)
                        .placeholder(R.drawable.bg_circle_grey)
                        .into(binding.ivOtherUserProfile)
                }
            }
            binding.tvOtherDate.text = formattedDate(questionData.createdAt)
            binding.tvOtherQuestion.text = questionData.answer

            if (questionData.sender_user_type == 1 && absoluteAdapterPosition == lastAnswerOfTradie && isFullAnswersListShown) {
                binding.llEditDelete.visibility = View.VISIBLE
            } else {
                binding.llEditDelete.visibility = View.GONE
            }

            if (questionData.sender_user_type == 1) {
                if (!isFullAnswersListShown) {
                    if (lastAnswerOfTradie > 3) {
                        binding.llEditDelete.visibility = View.GONE
                    } else if (lastAnswerOfTradie <= 3) {
                        if (absoluteAdapterPosition == lastAnswerOfTradie) {
                            binding.llEditDelete.visibility = View.VISIBLE
                        }
                    }
                }
            }

            if (status != null && (status == "EXPIRED" || status == "COMPLETED" ||
                        status == "CANCELLED")
            ) {
                binding.llEditDelete.visibility = View.GONE
            }

            binding.tvUserEdit.setOnClickListener {
                val context = binding.tvOtherUserName.context
                if (context is AppCompatActivity) {
                    context.startActivityForResult(
                        Intent(context, AskQuestionReplyActivity::class.java)
                            .putExtra("jobId", jobId)
                            .putExtra("answerEdit", questionData.answer)
                            .putExtra(
                                "questinID",
                                quesIs
                            ).putExtra("isUpdate", true)
                            .putExtra(
                                "answerID", questionData._id
                            ), 1310
                    )
                }
            }
            binding.tvUserDelete.setOnClickListener {
                it.tag = absoluteAdapterPosition
                listener.onAnswerTradieDeleteClickec(quesIs, questionData._id, jobId)
            }
        }
    }

    interface Listener {
        fun onAnswerTradieDeleteClickec(quesId: String, answerId: String, jobId: String)
    }

    private fun formattedDate(createdAt: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val output = SimpleDateFormat("yyyy-MM-dd HH:mm")

        val parsedDate: Date = input.parse(createdAt)
        val formattedDate: String = output.format(parsedDate)
        return formattedDate
    }
}