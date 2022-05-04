package com.app.core.model.restpassword

data class ResetPasswordRequest(
    val token: String,
    val type: String?,
    val resetPassword: String?,
    val confirmPassword: String?,
)