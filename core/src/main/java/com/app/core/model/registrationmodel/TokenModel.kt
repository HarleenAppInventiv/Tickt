package com.example.ticktapp.model.registration

import com.google.gson.annotations.SerializedName

data class TokenModel(

    @SerializedName("_id")
    var _id: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("token")
    var deviceToken: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("userName")
    var userName: String? = null,

    @SerializedName("firstName")
    var firstName: String? = null,

    @SerializedName("status")
    var status: Boolean? = null,

    @SerializedName("status_code")
    var status_code: Int? = null,

    @SerializedName("user_type")
    var user_type: Int? = null,

    @SerializedName("specialization")
    var specialization: List<String>? = null,

    @SerializedName("trade")
    var trade: List<String>? = null,
)

