package com.example.ticktapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.questionlist.Answer
import com.app.core.model.questionlist.MyList
import com.bumptech.glide.Glide
import com.example.ticktapp.R
import com.example.ticktapp.databinding.RowTradieQuestionListBinding
import com.example.ticktapp.mvvm.view.builder.AskQuestionReplyActivity
import com.example.ticktapp.mvvm.view.tradie.TradieAskQuestionActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewTradieQuestionsAdapter(
    val context: Context,
    val jobId: String,
    val status: String,
    var questionList: ArrayList<MyList>, val listener: ListenerNew
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
                questionList[position]?.let { holder.bind(it) }

            }
        }
    }

    inner class ViewHolderAnswer(val binding: RowTradieQuestionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var showAll = false
        fun bind(questionData: MyList) {
            try {
                binding.tvUserName.text = questionData.tradieData[0].firstName
                binding.tvDate.text = formattedDate(questionData.createdAt)
                binding.tvQuestion.text = questionData.question
                Glide.with(binding.root.context).load(questionData.tradieData[0].user_image)
                    .placeholder(R.drawable.bg_circle_grey)
                    .into(binding.ivUserProfile)

                if (!questionData.answers.isNullOrEmpty()) {
                    var lastAnswer = lastAnswerOfTradie(questionData.answers)
                    if (questionData.answers.size > 3) {  //show more textview managed
                        binding.tvShowMoreAnswersTradie.visibility = View.VISIBLE
                    } else {
                        binding.tvShowMoreAnswersTradie.visibility = View.GONE
                    }
                    var answersList: ArrayList<Answer> = ArrayList()
                    var isFullAnswersListShown: Boolean = false
                    if (questionData.answers.size > 3) {   //when answers size is < 3
                        for (index in questionData.answers.indices) {
                            if (showAll) {   //true when list size is < 3 and show all is clicked
                                answersList.clear()
                                answersList =
                                    questionData.answers   // adding all items in list to show all items
                                isFullAnswersListShown = true
                                break
                            } else {
                                if (index < 3) {        // by default 3 items in list will be shown if size < 3
                                    isFullAnswersListShown = false
                                    answersList.add(questionData.answers[index])  //adding first 3 indices in list to show 3 items
                                }
                            }
                        }
                    } else {
                        isFullAnswersListShown = true    //size is 3 or > 3
                        answersList = questionData.answers
                    }

                    var tradieAnswersAdapter: TradieAnswersAdapter =
                        TradieAnswersAdapter(
                            context,
                            jobId,
                            status,
                            isFullAnswersListShown,
                            lastAnswer,
                            questionData._id,
                            answersList,
                            object : TradieAnswersAdapter.Listener {
                                override fun onAnswerTradieDeleteClickec(
                                    quesId: String,
                                    answerId: String,
                                    jobId: String
                                ) {
                                    listener.onTradieAnswerdelete(quesId, answerId, jobId)
                                }
                            })

                    binding.rvQuestionAnswers.adapter = tradieAnswersAdapter

                    if (questionData.answers.size <= 3) {
                        binding.tvUserReply.visibility = View.VISIBLE
                    } else if (questionData.answers.size > 3 && showAll) {
                        binding.tvUserReply.visibility = View.VISIBLE
                    } else {
                        binding.tvUserReply.visibility = View.GONE
                    }

                    if (showAll)   //it is true when show all is clicked
                        binding.tvShowMoreAnswersTradie.text = "Show Less"
                    else           //when show less is clicked or by default
                        binding.tvShowMoreAnswersTradie.text = "Show All"
                } else {
                    Log.i("datacheckkkk", "bind: empty - $position")
                    binding.tvShowMoreAnswersTradie.visibility = View.GONE
                }

                binding.tvShowMoreAnswersTradie.setOnClickListener {
                    var showAllText: Boolean =
                        binding.tvShowMoreAnswersTradie.text.toString().toLowerCase()
                            .contains("all")
                    showAll = showAllText
                    notifyDataSetChanged()
                }
                binding.tvUserReply.setOnClickListener {
                    val context = binding.tvUserName.context
                    if (context is AppCompatActivity) {
                        context.startActivityForResult(
                            Intent(context, AskQuestionReplyActivity::class.java)
                                .putExtra("builderId", questionData.builderId)
                                .putExtra("jobId", jobId)
                                .putExtra("isTradie", true)
                                .putExtra("tradieId", questionData.tradieId)
                                .putExtra("answer", "")
                                .putExtra(
                                    "questinID",
                                    questionData._id
                                ), 1310
                        )
                    }
                }
                binding.tvUserEdit.setOnClickListener {
                    val context = binding.tvUserName.context
                    if (context is AppCompatActivity) {
                        context.startActivityForResult(
                            Intent(context, TradieAskQuestionActivity::class.java)
                                .putExtra("jobId", jobId)
                                .putExtra(
                                    "questinID",
                                    questionList.get(absoluteAdapterPosition)._id
                                ).putExtra("isUpdate", true).putExtra(
                                    "question",
                                    questionList.get(absoluteAdapterPosition).question
                                ), 1310
                        )
                    }
                }
                binding.tvUserDelete.setOnClickListener {
                    it.tag = absoluteAdapterPosition
                    listener.onQuestiondelete(questionData._id, jobId)
                }

                if (status != null && (status == "EXPIRED" || status == "COMPLETED" ||
                            status == "CANCELLED")
                ) {
                    binding.llTradieEditDeleteQues.visibility=View.GONE
                    binding.tvUserReply.visibility=View.GONE
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun lastAnswerOfTradie(arrayList: ArrayList<Answer>): Int {
        var senderUserTypeList: ArrayList<Int> = ArrayList()
        for (index in arrayList.indices) {
            senderUserTypeList.add(arrayList[index].sender_user_type)
        }
        return senderUserTypeList.lastIndexOf(1)
    }


    private fun formattedDate(createdAt: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val output = SimpleDateFormat("yyyy-MM-dd HH:mm")

        val parsedDate: Date = input.parse(createdAt)
        val formattedDate: String = output.format(parsedDate)
        return formattedDate
    }

    interface ListenerNew {
        fun onQuestiondelete(quesId: String, jobId: String)
        fun onTradieAnswerdelete(quesId: String, answerId: String, jobId: String)
    }

    fun setData(questionList: ArrayList<MyList>) {
        this.questionList.clear()
        this.questionList.addAll(questionList)
        notifyDataSetChanged()
    }

    fun addData(questionList: ArrayList<MyList>) {
        this.questionList.addAll(questionList)
        notifyDataSetChanged()
    }
}
