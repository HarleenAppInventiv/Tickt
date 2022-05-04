package com.app.core.model.questionlist

data class Portfolio(
    val _id: String,
    val jobDescription: String,
    val jobName: String,
    val status: Int,
    val url: List<String>
)