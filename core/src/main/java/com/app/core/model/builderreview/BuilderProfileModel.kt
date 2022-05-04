package com.app.core.model.builderreview

import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradie.PortFolio
import com.app.core.model.tradie.ReviewData
import com.app.core.model.jobmodel.JobRecModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BuilderProfileModel(
    @SerializedName("builderName")
    var builderName: String? = null,
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("position")
    var position: String? = null,
    @SerializedName("companyName")
    var companyName: String? = null,
    @SerializedName("builderImage")
    var builderImage: String? = null,
    @SerializedName("ratings")
    var ratings: Double? = null,
    @SerializedName("reviewsCount")
    var reviewsCount: Double? = null,
    @SerializedName("jobCompletedCount")
    var jobCompletedCount: Double? = null,
    @SerializedName("areasOfjobs")
    var areasOfjobs: ArrayList<SpecialisationData>? = null,
    @SerializedName("aboutCompany")
    var about: String? = null,
    @SerializedName("jobCount")
    var jobCount: Double? = null,
    @SerializedName("portfolio")
    var portfolio: ArrayList<PortFolio>? = null,
    @SerializedName("reviewData")
    var reviewData: ArrayList<ReviewData>? = null,
    @SerializedName("totalJobPostedCount")
    var totalJobPostedCount: Double? = null,
    @SerializedName("jobPostedData")
    var jobPostedData: ArrayList<JobRecModel>? = null,
) : Serializable
