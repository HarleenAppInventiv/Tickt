package com.app.core.model.questionlist

data class TradieData(
    val _id: String,
    val abn: String,
    val about: String,
    val about_company: String,
    val accountId: String,
    val accountType: String,
    val businessName: String,
    val company_name: String,
    val country_code: String,
    val createdAt: String,
    val customerId: String,
    val deviceToken: String,
    val email: String,
    val firstName: String,
    val forgotPasswordCode: String,
    val isEmailVerified: Boolean,
    val isMobileVerified: Boolean,
    val location: LocationX,
    val mobileNumber: String,
    val otp: String,
    val password: String,
    val portfolio: List<PortfolioX>,
    val position: String,
    val qualification: List<Qualification>,
    val settings: SettingsX,
    val specialization: List<String>,
    val status: Int,
    val trade: List<String>,
    val updatedAt: String,
    val user_image: String,
    val user_type: Int
)