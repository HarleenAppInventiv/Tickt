package com.app.core.model

import com.google.gson.annotations.SerializedName

class CommonResponse (@SerializedName("message")
                      var message: String? = null,
                      @SerializedName("status")
                      var status: Boolean? = null,
                      @SerializedName("status_code")
                      var status_code: Int? = null)