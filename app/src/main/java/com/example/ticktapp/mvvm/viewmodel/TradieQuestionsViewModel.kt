package com.example.ticktapp.mvvm.viewmodel

import android.util.Log
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.QuestionListResponseModel
import com.app.core.repo.TradieQuestionsRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.QuestionData
import com.app.core.model.questionlist.Result
import com.google.gson.Gson

class TradieQuestionsViewModel : BaseViewModel() {

    private val mRepo by lazy { TradieQuestionsRepo() }
    lateinit var answerData: QuestionData
    lateinit var questionListData: QuestionListResponseModel
    lateinit var result: Result
    var pos: Int = 0

    fun tradieQuestionList(jobId: String, page: Int = 1) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.tradieQuestionList(jobId, page)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        Log.i("tradieQuestionResponse", "tradieQuestionList: ${it.data}")
                        val responseModel =
                            Gson().fromJson(
                                Gson().toJson(it.data),
                                Result::class.java
                            )
                        this.result = responseModel
//                        this.questionListData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun addQuestion(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addQuestion(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), QuestionData::class.java)
                        this.answerData = responseModel

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun updateQuestion(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.updateQuestion(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), QuestionData::class.java)
                        this.answerData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun deleteQuestion(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.deleteQuestion(data)
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

    fun deleteTradieAnswer(qId: String, aId: String, pos: Int) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.deleteTradieAnswer(qId, aId)
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


