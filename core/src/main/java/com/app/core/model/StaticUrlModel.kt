package com.app.core.model

import com.google.gson.annotations.SerializedName

class StaticUrlModel(
    @SerializedName("privacyPolicy_url")
    var privacyPolicy_url: String? = null,
    @SerializedName("tnc_url")
    var tnc_url: String? = null
)