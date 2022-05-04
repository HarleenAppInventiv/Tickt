package com.app.core.model.questionlist

data class MyList(
    val _id: String,
    val answerSize: Int,
    val answers: ArrayList<Answer>,
    val builderData: List<BuilderData>,
    val builderId: String,
    val createdAt: String,
    val isModifiable: Boolean,
    val jobId: String,
    val question: String,
    val tradieData: List<TradieData>,
    val tradieId: String,
    val updatedAt: String
)
