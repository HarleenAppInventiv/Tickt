package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.QuestionListResponseModel
import com.app.core.repo.QuestionsRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.AnswerData
import com.app.core.model.questionlist.QuestionModelBuilder
import com.app.core.model.questionlist.Result
import com.google.gson.Gson

class QuestionsViewModel : BaseViewModel() {

    private val mRepo by lazy { QuestionsRepo() }
    lateinit var answerData: AnswerData
    lateinit var questionListData: QuestionModelBuilder
    lateinit var result: Result
    var pos: Int = 0

    fun builderQuestionList(jobId: String, page: Int = 1) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.builderQuestionList(jobId, page)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        Log.i("questionListResponse", "raw json data: ${it.data.toString()}")
                        val responseModel = Gson().fromJson(
                            Gson().toJson(it.data),
                            Result::class.java
                        )
                        Log.i("questionListResponse", "Model data: ${responseModel.list.size}")
                        Log.i(
                            "questionListResponse",
                            "Model data: ${responseModel.count.toString()}"
                        )
                        this.result = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun addAnswer(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addAnswer(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), AnswerData::class.java)
                        this.answerData = responseModel

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun addTradieAnswer(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addTradieAnswer(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), AnswerData::class.java)
                        this.answerData = responseModel

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun updateAnswer(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.updateAnswer(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), AnswerData::class.java)
                        this.answerData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun updateTradieAnswer(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.updateTradieAnswer(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), AnswerData::class.java)
                        this.answerData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun deleteAnswer(qId: String, aId: String, pos: Int) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.deleteAnswer(qId, aId)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        this.pos = pos
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }
}


