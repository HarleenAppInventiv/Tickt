package com.example.ticktapp.mvvm.view.tradie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.core.model.QuestionListResponseModel
import com.app.core.model.jobmodel.AnswerData
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityTradieQuestionListBinding
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.jobmodel.QuestionData
import com.app.core.model.jobmodel.QuestionsData
import com.app.core.model.questionlist.MyList
import com.app.core.model.questionlist.Result
import com.example.ticktapp.adapters.BuilderQuestionsAdapter
import com.example.ticktapp.adapters.NewTradieQuestionsAdapter
import com.example.ticktapp.mvvm.viewmodel.QuestionsViewModel
import com.example.ticktapp.mvvm.viewmodel.TradieQuestionsViewModel

@Suppress("DEPRECATION")
public class TradieQuestionListActivity : BaseActivity(), OnClickListener,
    NewTradieQuestionsAdapter.ListenerNew {

    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityTradieQuestionListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(TradieQuestionsViewModel::class.java) }
    private val mViewDeleteAnswer by lazy { ViewModelProvider(this).get(QuestionsViewModel::class.java) }
    private var isUpdate: Boolean = false
    private var result: Result? = null
    var listData: ArrayList<MyList> = ArrayList()
    var page: Int = 1
    var status: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tradie_question_list)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
        setData()
        setRecyclerList()
    }

    private fun getQuestionListData(jobId: String, page: Int = 1) {
        mViewModel.tradieQuestionList(jobId, page)
    }

    private fun getIntentData() {
        data = intent.getSerializableExtra("data") as JobRecModel

        if (intent.hasExtra("status")) {
            status = intent.extras!!.getString("status", "")
        } else {
            data.status?.let {
                status = it
            }
        }
        /*if (intent.hasExtra("isQuestionAsked")) {
            if (!intent.getBooleanExtra("isQuestionAsked", true))
                mBinding.tvAskQuestion.visibility = View.GONE
        }*/

        if (status != null && (status == "EXPIRED" || status == "COMPLETED" ||
                    status == "CANCELLED")
        ) {
            mBinding.tvAskQuestion.visibility = View.GONE
        }
    }

    private fun setData() {
        mBinding.tvJobTitle.text = data.jobName
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.WHITE)
        }
    }

    fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }


    private fun listener() {
        mBinding.questionListBack.setOnClickListener { onBackPressed() }

        mBinding.tvAskQuestion.setOnClickListener {
            startActivityForResult(
                Intent(this, TradieAskQuestionActivity::class.java)
                    .putExtra("jobId", data.jobId)
                    .putExtra("builderId", data.postedBy.builderId)
                    .putExtra("tradeId", data.tradeId)
                    .putExtra("specializationId", data.specializationId)
                    .putExtra("isUpdate", false), 1410
            )
        }
    }

    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
        setBaseViewModel(mViewDeleteAnswer)
        mViewDeleteAnswer.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.TRADIE_DELETE_QUESTION -> {
                showToastShort(exception.message)
            }
            ApiCodes.DELETE_ANSWER -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.DELETE_ANSWER -> {
                page = 1
                getQuestionListData(this.data.jobId!!, page)
            }
            ApiCodes.TRADIE_DELETE_QUESTION -> {
                isUpdate = true

                if (deletePos != -1) {
                    mViewModel.result.list.removeAt(deletePos)
                    newTradieQuestionsAdapter?.notifyItemRemoved(deletePos)
                }

                page = 1
                getQuestionListData(this.data.jobId!!, page)
            }

            ApiCodes.QUESTIONS_LIST -> {
                mViewModel.result.let {
                    mViewModel.result.let {
                        result = mViewModel.result
                        //                        setList(result!!)

                        if (it.list.size == 0 && page == 1) {
                            mBinding.rvQuestionList.visibility = View.GONE
                            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
                        } else {
                            mBinding.rvQuestionList.visibility = View.VISIBLE
                            mBinding.tvResultTitleNoData.visibility = View.GONE
                        }
                        if (page == 1) {
                            listData.clear()
                        }
                        isLoading = false

                        if (page == 1) {
                            listData.addAll(it.list)
                            newTradieQuestionsAdapter!!.notifyDataSetChanged()
                        } else {
                            listData.addAll(it.list)
                            newTradieQuestionsAdapter!!.notifyDataSetChanged()
                        }

                        if (isNumber(it.count.toString())) {
                            mBinding.tvQuestionCount.text =
                                "${it.count.toString().toDouble().toInt().toString()}  ${getString(R.string.questions_)}"
                        }
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }

    fun isNumber(s: String): Boolean {
        return try {
            s.toInt()
            true
        } catch (ex: NumberFormatException) {
            false
        }
    }

    var newTradieQuestionsAdapter: NewTradieQuestionsAdapter? = null
    var layoutManager: LinearLayoutManager? = null
    var isLoading: Boolean = false

    private fun setRecyclerList() {
        layoutManager = LinearLayoutManager(this)
        mBinding.rvQuestionList.layoutManager = layoutManager
        newTradieQuestionsAdapter = NewTradieQuestionsAdapter(
            this, data.jobId!!, status, listData,
            this
        )
        mBinding.rvQuestionList.adapter = newTradieQuestionsAdapter
        mBinding.rvQuestionList.addOnScrollListener(scrollListener)

        getQuestionListData(data.jobId!!, page)
    }

    /*  private fun setList(questionListResponseModel: Result) {
          if (result!!.list != null && result!!.list.size > 0) {
              mBinding.rvQuestionList.visibility = View.VISIBLE
              mBinding.tvResultTitleNoData.visibility = View.GONE

              mBinding.rvQuestionList.layoutManager = LinearLayoutManager(this)
              newTradieQuestionsAdapter = NewTradieQuestionsAdapter(
                  this, data.jobId!!, status, questionListResponseModel.list,
                  this
              )

              mBinding.rvQuestionList.adapter = newTradieQuestionsAdapter
              mBinding.rvQuestionList.addOnScrollListener(scrollListener)

          } else {
              mBinding.rvQuestionList.visibility = View.GONE
              mBinding.tvResultTitleNoData.visibility = View.VISIBLE
          }
      }*/

    var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            var lastVisibleItem = layoutManager!!.findLastVisibleItemPosition()
            Log.i("dataaaaaaaa", "onScrollStateChanged: $lastVisibleItem")
            if (listData.size % 10 == 0) {
                if (!isLoading && (lastVisibleItem == listData.size - 1)) {
                    isLoading = true
                    page++
                    getQuestionListData(data.jobId!!, page)
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("data", data)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    var deletePos: Int = -1
    override fun onClick(p0: View?) {
        showAppPopupDialog(
            getString(R.string.are_you_want_to_delete_question),
            getString(R.string.yes),
            getString(R.string.no),
            getString(R.string.delete),
            {
                val pos = p0?.tag as Int
                deletePos = pos
                val reviewData = HashMap<String, Any>()
                if (data.jobId != null)
                    reviewData.put("jobId", data.jobId!!)
                data.questionsData!![pos].questionData?.questionId?.let { it2 ->
                    reviewData.put(
                        "questionId",
                        it2
                    )
                }
                data.questionsData!![pos].questionData?.questionId?.let { it2 ->
                    mViewModel.deleteQuestion(
                        reviewData
                    )
                }
            },
            {
            },
            true
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1410 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                try {
                    page = 1
                    getQuestionListData(this.data.jobId!!, page)
                } catch (ex: Exception) {

                    ex.printStackTrace()
                }
            }
        }
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                try {
                    isUpdate = true
                    page = 1
                    getQuestionListData(this.data.jobId!!, page)

                } catch (ex: Exception) {

                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onQuestiondelete(quesId: String, jobId: String) {
        showAppPopupDialog(
            getString(R.string.are_you_want_to_delete_question),
            getString(R.string.yes),
            getString(R.string.no),
            getString(R.string.delete),
            {
                val reviewData = HashMap<String, Any>()
                reviewData.put("questionId", quesId)
                reviewData.put("jobId", jobId)
                mViewModel.deleteQuestion(reviewData)
            },
            {
            },
            true
        )
    }

    override fun onTradieAnswerdelete(quesId: String, answerId: String, jobId: String) {
        showAppPopupDialog(
            getString(R.string.are_you_want_to_delete_answer),
            getString(R.string.yes),
            getString(R.string.no),
            getString(R.string.delete),
            {
                mViewDeleteAnswer.deleteAnswer(
                    quesId,
                    answerId, 0
                )
            },
            {
            },
            true
        )
    }
}