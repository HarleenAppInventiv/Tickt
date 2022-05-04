package com.app.core.model.profile

import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradie.AreaOfSpecialisation
import com.app.core.model.tradie.PortFolio
import com.app.core.model.tradie.ReviewData
import com.app.core.model.tradie.VouchesData
import com.app.core.model.jobmodel.JobRecModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InitalProfileModel(
    @SerializedName("trade")
    var trade: ArrayList<String>? = null,
    @SerializedName("specialization")
    var specialization: ArrayList<String>? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("builderName")
    var builderName: String? = null,
    @SerializedName("position")
    var position: String? = null,
    @SerializedName("aboutCompany")
    var aboutCompany: String? = null,
    @SerializedName("companyName")
    var companyName: String? = null,
    @SerializedName("builderImage")
    var builderImage: String? = null,
    @SerializedName("fullName")
    var fullName: String? = null,
    @SerializedName("mobileNumber")
    var mobileNumber: String? = null,
    @SerializedName("abn")
    var abn: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("userImage")
    var userImage: String? = null,
    @SerializedName("profileCompleted")
    var profileCompleted: String = "0%",
    @SerializedName("userType")
    var userType: Int = 0,
    @SerializedName("reviews")
    var reviews: Int = 0,
    @SerializedName("reviewsCount")
    var reviewsCount: Int = 0,
    @SerializedName("ratings")
    var ratings: Double = 0.0,
    @SerializedName("jobCompletedCount")
    var jobCompletedCount: Int = 0,
    @SerializedName("areasOfSpecialization")
    var areasOfSpecialization: AreaOfSpecialisation? = null,
    @SerializedName("specializationData")
    var specializationData: ArrayList<SpecialisationData>? = null,
    @SerializedName("portfolio")
    var portfolio: ArrayList<PortFolio>? = null,
    @SerializedName("reviewData")
    var reviewData: ArrayList<ReviewData>? = null,
    @SerializedName("vouchesData")
    var vouchesData: ArrayList<VouchesData>? = null,
    @SerializedName("jobPostedData")
    var jobPostedData: ArrayList<JobRecModel>? = null,
    @SerializedName("totalJobPostedCount")
    var totalJobPostedCount: Int=0,
) : Serializable
