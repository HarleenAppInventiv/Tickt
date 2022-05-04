package com.example.ticktapp.model.registration

import com.google.gson.annotations.SerializedName

data class RegistrationModel (@SerializedName("message")
                               var message: String? = null,
                               @SerializedName("status")
                               var status: Boolean? = null,
                               @SerializedName("status_code")
                               var status_code: Int? = null)

