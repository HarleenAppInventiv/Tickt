package com.example.ticktapp.model.registration

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class JobSearchRequestModel(
    @SerializedName("page")
    var page: Int? = 1,
    @SerializedName("isFiltered")
    var isFiltered: Boolean? = false,
    @SerializedName("location")
    var location: Location? = null,
    @SerializedName("jobTypes")
    var jobTypes: List<String>?=null
)

data class Location(@SerializedName("coordinates") var location: List<Double>): Serializable
