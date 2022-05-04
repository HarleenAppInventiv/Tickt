package com.app.core.model.loginmodel

data class LoginRequest(
    var deviceToken: String? = null,
    var email: String? = null,
    var password: String? = null,
    var user_type: String? = null,
    var deviceType: String = "2"
)
