package com.app.core.model.questionlist

data class Reminders(
    val email: Boolean,
    val pushNotification: Boolean,
    val smsMessages: Boolean
)