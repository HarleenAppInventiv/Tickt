package com.example.ticktapp.mvvm.viewmodel

import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.tradie.ReviewData
import com.app.core.model.tradie.ReviewList
import com.app.core.model.tradie.ReviewLists
import com.app.core.repo.ReviewRepo
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.google.gson.Gson

class ReviewListViewModel : BaseViewModel() {

    private val mRepo by lazy { ReviewRepo() }
    lateinit var reviewData: ReviewData
    var reviewDataList = ArrayList<ReviewList>()

    var pos: Int = 0

    fun addReviewReply(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.addReviewReply(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), ReviewData::class.java)
                        this.reviewData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun updateReviewReply(data: HashMap<String, Any>) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.updateReviewReply(data)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), ReviewData::class.java)
                        this.reviewData = responseModel
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun deleteReviewReply(qId: String, aId: String, pos: Int) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.deleteReviewReply(qId, aId)
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

    fun getBuilderReviewList(tradieID: String, page: Int) {
        CoroutinesBase.main {
            val resp = mRepo.getBuilderReviewList(tradieID, page)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val responseModel =
                            Gson().fromJson(Gson().toJson(it.data), ReviewLists::class.java)
                        this.reviewDataList = responseModel.list as ArrayList<ReviewList>
                    }
                }
            }
        }
    }
}


