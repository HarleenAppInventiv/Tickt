package com.example.ticktapp.model.registration

import com.google.gson.annotations.SerializedName

class Result(
    @SerializedName("_id")
    var _id: String? = null,
    @SerializedName("firstName")
    var firstName: String? = null,
    @SerializedName("mobileNumber")
    var mobileNumber: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
)