package com.example.ticktapp.adapters

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowQuestionListBinding
import com.app.core.model.jobmodel.QuestionData
import com.app.core.model.jobmodel.QuestionsData
import com.example.ticktapp.mvvm.view.builder.AskQuestionReplyActivity


class QuestionAdapter(
    val context: Context,
    val jobId: String,
    var questionList: List<QuestionsData>, val status: String, val listener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.row_question_list, parent, false
            )
        )
    }


    override fun getItemCount(): Int {
        return questionList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                questionList[position].questionData?.let { holder.bind(it) }

            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowQuestionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(questionData: QuestionData) {
            binding.tvUserName.text = questionData.userName
            binding.tvDate.text = questionData.date
            binding.tvQuestion.text = questionData.question
            Glide.with(binding.root.context).load(questionData.userImage)
                .placeholder(R.drawable.bg_circle_grey)
                .into(binding.ivUserProfile)

            if (status != null && (status.equals("EXPIRED") || status.equals("COMPLETED") ||
                        status.equals("CANCELLED"))
            ) {
                if (questionData.answerData != null && questionData.answerData!!.answerId != null &&
                    questionData.answerData!!.answerId?.length!! > 0
                ) {
                    binding.tvUserAnswer.visibility = View.VISIBLE
                } else {
                    binding.tvUserAnswer.visibility = View.GONE
                }
                binding.llEditDelete.visibility = View.GONE
            } else {
                binding.tvUserAnswer.visibility = View.VISIBLE
                binding.llEditDelete.visibility = View.VISIBLE
            }
            if (questionData.answerData != null && questionData.answerData!!.answerId != null &&
                questionData.answerData!!.answerId?.length!! > 0
            ) {
                (binding.tvUserAnswer.layoutParams as LinearLayout.LayoutParams).gravity =
                    Gravity.CENTER
                binding.tvUserAnswer.text =
                    binding.tvUserAnswer.context.getString(R.string.show_answer)
                binding.llFooter.visibility = View.GONE
                binding.tvOtherUserName.text = questionData.answerData!!.userName
                binding.tvOtherDate.text = questionData.answerData!!.date
                binding.tvOtherQuestion.text = questionData.answerData!!.answer
                Glide.with(binding.root.context).load(questionData.answerData!!.userImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivOtherUserProfile)
            } else {
                (binding.tvUserAnswer.layoutParams as LinearLayout.LayoutParams).gravity =
                    Gravity.START
                binding.tvUserAnswer.text = binding.tvUserAnswer.context.getString(R.string.answer)
                binding.llFooter.visibility = View.GONE
            }
            binding.tvUserAnswer.setOnClickListener {
                if (questionData.answerData != null && questionData.answerData!!.answerId != null &&
                    questionData.answerData!!.answerId?.length!! > 0
                ) {
                    if (binding.llFooter.visibility == View.GONE) {
                        binding.llFooter.visibility = View.VISIBLE
                        binding.tvUserAnswer.text =
                            binding.tvUserAnswer.context.getString(R.string.hide_answer)
                    } else {
                        binding.llFooter.visibility = View.GONE
                        binding.tvUserAnswer.text =
                            binding.tvUserAnswer.context.getString(R.string.show_answer)
                    }
                } else {
                    val context = binding.tvUserName.context
                    if (context is AppCompatActivity) {
                        context.startActivityForResult(
                            Intent(context, AskQuestionReplyActivity::class.java)
                                .putExtra("jobId", jobId)
                                .putExtra(
                                    "questinID",
                                    questionList.get(absoluteAdapterPosition).questionData?.questionId
                                ), 1310
                        )
                    }
                }
            }
            binding.tvUserEdit.setOnClickListener {
                val context = binding.tvUserName.context
                if (context is AppCompatActivity) {
                    context.startActivityForResult(
                        Intent(context, AskQuestionReplyActivity::class.java)
                            .putExtra("jobId", jobId)
                            .putExtra(
                                "questinID",
                                questionList.get(absoluteAdapterPosition).questionData?.questionId
                            ).putExtra("isUpdate", true).putExtra(
                                "question",
                                questionList.get(absoluteAdapterPosition).questionData?.answerData?.answer
                            )
                            .putExtra(
                                "answerID",
                                questionList.get(absoluteAdapterPosition).questionData?.answerData?.answerId
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
