package com.app.core.model.questionlist

data class QuestionModelBuilder(
    val message: String,
    val result: Result,
    val status: Boolean,
    val status_code: Int
)