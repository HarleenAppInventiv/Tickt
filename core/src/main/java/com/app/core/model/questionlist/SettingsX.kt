package com.app.core.model.questionlist

data class SettingsX(
    val messages: MessagesX,
    val pushNotificationCategory: List<Int>,
    val reminders: RemindersX
)