package com.app.core.model.requestsmodel

data class OTPRequest (var otp: String? =null,var email:String?=null,var mobileNumber:String?=null)

data class CreatePasswordRequest(var mobileNumber: String? =null,
                                 var user_type:Int?=null,
                                 var password: String? =null,
                                 var email: String? =null)

data class CheckPhoneRequest(var mobileNumber: String? = null)

data class DeviceToken(var deviceId: String? = null,var deviceToken: String? = null,var deviceType: String? = null)