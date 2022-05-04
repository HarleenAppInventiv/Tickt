package com.app.core.model.builderreview

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BuilderReviewModel (
    @SerializedName("jobId") val jobId : String,
    @SerializedName("builderId") val builderId : String,
    @SerializedName("rating") val rating : Float,
    @SerializedName("review") var review : String
):Serializable
