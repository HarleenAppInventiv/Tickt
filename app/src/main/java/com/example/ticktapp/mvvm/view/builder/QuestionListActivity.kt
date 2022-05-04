package com.example.ticktapp.mvvm.view.builder

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
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.R
import com.example.ticktapp.base.BaseActivity
import com.example.ticktapp.databinding.ActivityQuestionListBinding
import com.app.core.model.jobmodel.AnswerData
import com.app.core.model.jobmodel.JobRecModel
import com.app.core.model.questionlist.MyList
import com.app.core.model.questionlist.QuestionModelBuilder
import com.app.core.model.questionlist.Result
import com.example.ticktapp.adapters.BuilderQuestionsAdapter
import com.example.ticktapp.adapters.NewTradieQuestionsAdapter
import com.example.ticktapp.mvvm.viewmodel.QuestionsViewModel


@Suppress("DEPRECATION")
public class QuestionListActivity : BaseActivity(), OnClickListener,
    BuilderQuestionsAdapter.ListenerNew {

    private var status: String? = ""
    private lateinit var data: JobRecModel
    private lateinit var mBinding: ActivityQuestionListBinding
    private val mViewModel by lazy { ViewModelProvider(this).get(QuestionsViewModel::class.java) }
    private var isUpdate: Boolean = false
    private var questionListData: QuestionModelBuilder? = null
    private var result: Result? = null
    var listData: ArrayList<MyList> = ArrayList()
    var page: Int = 1
    var builderQuestionAdapter: BuilderQuestionsAdapter? = null
    var layoutManager: LinearLayoutManager? = null
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_question_list)
        setStatusBarColor()
        setLightStatusBar(mBinding.root)
        getIntentData()
        listener()
        setObservers()
        setData()
        setRecyclerList()
    }

    private fun setRecyclerList() {
        layoutManager = LinearLayoutManager(this)
        mBinding.rvQuestionList.layoutManager = layoutManager
        builderQuestionAdapter = BuilderQuestionsAdapter(
            this,
            data.jobId!!,
            listData,
            status!!,
            this
        )
        mBinding.rvQuestionList.adapter = builderQuestionAdapter
        mBinding.rvQuestionList.addOnScrollListener(scrollListener)

        getQuestionListData(data.jobId!!, page)
    }


    var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            var lastVisibleItem = layoutManager!!.findLastVisibleItemPosition()

            Log.i("dataaaaaaaa", "onScrollStateChanged: $lastVisibleItem")
            if (listData.size % 10 == 0) {
                if (!isLoading && (lastVisibleItem == listData.size - 1)) {
                    Log.i("dataaaaaaaa", "onScrollStateChanged page: $page")
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

    private fun getQuestionListData(jobId: String, page: Int = 1) {
        mViewModel.builderQuestionList(jobId, page)
    }

  /*  private fun setList(questionListResponseModel: Result) {
        if (!questionListResponseModel.list.isNullOrEmpty()) {
            mBinding.rvQuestionList.visibility = View.VISIBLE
            mBinding.tvResultTitleNoData.visibility = View.GONE

            mBinding.rvQuestionList.layoutManager = LinearLayoutManager(this)
            mBinding.rvQuestionList.adapter = BuilderQuestionsAdapter(
                this,
                data.jobId!!,
                questionListResponseModel.list,
                "",
                this
            )
        } else {
            mBinding.rvQuestionList.visibility = View.GONE
            mBinding.tvResultTitleNoData.visibility = View.VISIBLE
        }
    }*/

    private fun getIntentData() {
        data = intent.getSerializableExtra("data") as JobRecModel
        status = intent.extras!!.getString("status", "")
        Log.i("statussss", status.toString())
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
    }


    private fun setObservers() {
        setBaseViewModel(mViewModel)
        mViewModel.getResponseObserver().observe(this, this)
    }

    override fun onException(exception: ApiError, apiCode: Int) {
        when (apiCode) {
            ApiCodes.DELETE_ANSWER -> {
                showToastShort(exception.message)
            }
        }
    }

    override fun onResponseSuccess(statusCode: Int, apiCode: Int, msg: String?) {
        when (apiCode) {
            ApiCodes.DELETE_ANSWER -> {
                page = 1
                getQuestionListData(data.jobId!!, page)
            }
            ApiCodes.QUESTIONS_LIST -> {
                mViewModel.result?.let {
                    result = it
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
                        builderQuestionAdapter!!.notifyDataSetChanged()
                    } else {
                        listData.addAll(listData.size - 1, it.list)
                        builderQuestionAdapter!!.notifyDataSetChanged()
                    }

                    if (isNumber(it.count.toString())) {
                        mBinding.tvQuestionCount.text =
                            it.count.toString().toDouble().toInt().toString() + " " + getString(
                                R.string.questions_
                            )
                    }
                }
            }
        }
        super.onResponseSuccess(statusCode, apiCode, msg)
    }


    private fun isNumber(s: String): Boolean {
        return try {
            s.toInt()
            true
        } catch (ex: NumberFormatException) {
            false
        }
    }

    override fun onBackPressed() {
        if (isUpdate) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1310 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("data")) {
                try {
                    page = 1
                    getQuestionListData(this.data.jobId!!, page)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onClick(v: View?) {
    }

    override fun onQuesDelete(quesId: String, answerId: String, jobId: String) {
        showAppPopupDialog(
            getString(R.string.are_you_want_to_delete_answer),
            getString(R.string.yes),
            getString(R.string.no),
            getString(R.string.delete), {
                mViewModel.deleteAnswer(quesId, answerId, 0)
            }, {}, true
        )
    }
}