package com.app.core.model.questionlist

data class Settings(
    val messages: Messages,
    val pushNotificationCategory: List<Int>,
    val reminders: Reminders
)