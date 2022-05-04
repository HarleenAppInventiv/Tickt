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
import com.example.ticktapp.databinding.RowTradieQuestionListBinding
import com.app.core.model.jobmodel.QuestionData
import com.app.core.model.jobmodel.QuestionsData
import com.example.ticktapp.mvvm.view.tradie.TradieAskQuestionActivity


class TradieQuestionAdapter(
    val context: Context,
    val jobId: String,
    var questionList: List<QuestionsData>, val listener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.row_tradie_question_list, parent, false
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

    inner class ViewHolderAnswer(val binding: RowTradieQuestionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(questionData: QuestionData) {
            binding.tvUserName.text = questionData.userName
            binding.tvDate.text = questionData.date
            binding.tvQuestion.text = questionData.question
            Glide.with(binding.root.context).load(questionData.userImage)
                .placeholder(R.drawable.bg_circle_grey)
                .into(binding.ivUserProfile)

            if (questionData.answerData != null && questionData.answerData!!.answerId != null &&
                questionData.answerData!!.answerId?.length!! > 0
            ) {
                binding.llFooter.visibility = View.VISIBLE
                binding.tvOtherUserName.text = questionData.answerData!!.userName
                binding.tvOtherDate.text = questionData.answerData!!.date
                binding.tvOtherQuestion.text = questionData.answerData!!.answer
                Glide.with(binding.root.context).load(questionData.answerData!!.userImage)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivOtherUserProfile)
            } else {
                binding.llFooter.visibility = View.GONE
            }

            binding.tvUserAnswer.setOnClickListener {

            }

            binding.tvUserEdit.setOnClickListener {
                val context = binding.tvUserName.context
                if (context is AppCompatActivity) {
                    context.startActivityForResult(
                        Intent(context, TradieAskQuestionActivity::class.java)
                            .putExtra("jobId", jobId)
                            .putExtra(
                                "questinID",
                                questionList.get(absoluteAdapterPosition).questionData?.questionId
                            ).putExtra("isUpdate", true).putExtra(
                                "question",
                                questionList.get(absoluteAdapterPosition).questionData?.question
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
