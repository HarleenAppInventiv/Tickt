package com.app.core.model.questionlist



data class Answer(
    val _id: String,
    val answer: String,
    val builder: List<Builder>,
    val builderId: String,
    val createdAt: String,
    val sender_user_type: Int,
    val tradie: List<Tradie>,
    val tradieId: String,
    val updatedAt: String
)