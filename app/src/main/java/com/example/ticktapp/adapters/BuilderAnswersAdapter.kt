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
import com.example.ticktapp.databinding.RowQuestionAnswersListBinding
import com.app.core.model.questionlist.Answer
import com.example.ticktapp.mvvm.view.builder.AskQuestionReplyActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BuilderAnswersAdapter(
    val context: Context,
    val jobId: String,
    val isFullAnswersListShown: Boolean = false,
    val lastAnswerOfBuilder: Int=-1,
    val quesId: String,
    var questionList: ArrayList<Answer>,
    val status: String,
    val listener: AnswersListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderAnswer(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.row_question_answers_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderAnswer -> {
                questionList.let {
                    holder.bind(it[position])
                }
            }
        }
    }

    private fun lastAnswerOfBuilder(arrayList: ArrayList<Answer>): Int {
        var senderUserTypeList: ArrayList<Int> = ArrayList()
        for (index in arrayList.indices) {
            senderUserTypeList.add(arrayList[index].sender_user_type)
        }
        return senderUserTypeList.lastIndexOf(2)
    }

    inner class ViewHolderAnswer(val binding: RowQuestionAnswersListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(questionData: Answer) {
            if (questionData.sender_user_type == 2) {
                if (!questionData.builder.isNullOrEmpty()) {
                    binding.tvOtherUserName.text = questionData.builder[0].firstName
                    Glide.with(binding.root.context).load(questionData.builder[0].user_image)
                        .placeholder(R.drawable.bg_circle_grey)
                        .into(binding.ivOtherUserProfile)
                }
            } else {
                binding.tvOtherUserName.text = questionData.tradie[0].firstName
                Glide.with(binding.root.context).load(questionData.tradie[0].user_image)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivOtherUserProfile)
            }
            binding.tvOtherDate.text = formattedDate(questionData.createdAt)
            binding.tvOtherQuestion.text = questionData.answer

            if (questionData.sender_user_type == 2 && absoluteAdapterPosition == lastAnswerOfBuilder && isFullAnswersListShown) {
                binding.llEditDelete.visibility = View.VISIBLE
            } else {
                binding.llEditDelete.visibility = View.GONE
            }

            if (questionData.sender_user_type == 2) {
                if (!isFullAnswersListShown) {
                    if (lastAnswerOfBuilder > 3) {
                        binding.llEditDelete.visibility = View.GONE
                    } else if (lastAnswerOfBuilder <= 3) {
                        if (absoluteAdapterPosition == lastAnswerOfBuilder) {
                            binding.llEditDelete.visibility = View.VISIBLE
                        }
                    }
                }
            }

            binding.tvUserEdit.setOnClickListener {
                val context = binding.tvOtherUserName.context
                if (context is AppCompatActivity) {
                    context.startActivityForResult(
                        Intent(context, AskQuestionReplyActivity::class.java)
                            .putExtra("jobId", jobId)
                            .putExtra("answerEdit", questionData.answer)
                            .putExtra(
                                "questinID", quesId
                            ).putExtra("isUpdate", true).putExtra(
                                "question", questionData.answer
                            ).putExtra(
                                "answerID", questionData._id
                            ), 1310
                    )
                }

                if (status != null && (status.equals("EXPIRED") || status.equals("COMPLETED") ||
                            status.equals("CANCELLED"))
                ) {
                    binding.llEditDelete.visibility=View.GONE
                }
                /*  if (status != null && (status.equals("EXPIRED") || status.equals("COMPLETED") ||
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
              }*/
            }

            binding.tvUserDelete.setOnClickListener {
                it.tag = absoluteAdapterPosition
                listener.onDeleteClickec(quesId, questionData._id, jobId)
            }


            /*if (status != null && (status.equals("EXPIRED") || status.equals("COMPLETED") ||
                        status.equals("CANCELLED"))
            ) {
                binding.llEditDelete.visibility = View.GONE
            } else {
                binding.llEditDelete.visibility = View.VISIBLE
            }*/

            /*if (questionList.size <= 3) {
                if (absoluteAdapterPosition == 2) {
                    binding.tvShowAll.visibility = View.VISIBLE
                    binding.tvShowAll.text = "Show All Answers"
                }
                if (questionList.size < 3) {
                    binding.tvShowAll.visibility = View.GONE
                }
            } else if (questionList.size > 3) {
                if (absoluteAdapterPosition == itemCount) {
                    binding.tvShowAll.visibility = View.VISIBLE
                    binding.tvShowAll.text = "Hide Answer"
                }
            }
            binding.tvShowAll.setOnClickListener {
                if (questionList.size == 3) {
                    listener.onShowAllClicked(true)
                } else {
                    listener.onShowAllClicked(false)
                }*/

            /*if (questionData != null && questionData.answerId != null &&
                questionData.answerId?.length!! > 0
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
                                questionList.get(absoluteAdapterPosition).
                            ), 1310
                    )
                }
            }*/
        }
        /* binding.tvUserEdit.setOnClickListener {
             val context = binding.tvOtherUserName.context
             if (context is AppCompatActivity) {
                 context.startActivityForResult(
                     Intent(context, AskQuestionReplyActivity::class.java)
                         .putExtra("jobId", jobId)
                         .putExtra(
                             "questinID",
                             quesId
                         ).putExtra("isUpdate", true).putExtra(
                             "question",
                             questionList.get(absoluteAdapterPosition).answer
                         )
                         .putExtra(
                             "answerID", questionList[adapterPosition].answerId
                         ), 1310
                 )
             }
         }
         binding.tvUserDelete.setOnClickListener {
             it.tag = absoluteAdapterPosition
             listener.onDeleteClickec(quesId,questionList[adapterPosition].answerId!!,jobId)
         }*/
//        }
    }

    interface AnswersListener {
        fun onDeleteClickec(quesId: String, answerId: String, jobId: String)
    }

    private fun formattedDate(createdAt: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val output = SimpleDateFormat("yyyy-MM-dd HH:mm")

        val parsedDate: Date = input.parse(createdAt)
        val formattedDate: String = output.format(parsedDate)
        return formattedDate
    }


}