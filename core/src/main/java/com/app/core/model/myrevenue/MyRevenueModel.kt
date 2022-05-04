package com.app.core.model.myrevenue

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MyRevenueModel(
    @SerializedName("totalEarnings")
    var totalEarnings: Double? = null,
    @SerializedName("totalJobs")
    var totalJobs: Double? = null,
    @SerializedName("revenue")
    var revenue: Revenue? = null
) : Serializable

data class Revenue(
    @SerializedName("revenueList")
    var revenueList: ArrayList<RevenueList>? = null,
    @SerializedName("count")
    var count: Double? = null,
) : Serializable

class RevenueList(
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("tradieId")
    var tradieId: String? = null,
    @SerializedName("from_date")
    var from_date: String? = null,
    @SerializedName("to_date")
    var to_date: String? = null,
    @SerializedName("builderName")
    var builderName: String? = null,
    @SerializedName("builderImage")
    var builderImage: String? = null,
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("job_description")
    var job_description: String? = null,
    @SerializedName("earning")
    var earning: String? = null,
    @SerializedName("totalEarning")
    var totalEarning: String? = null,
    @SerializedName("milestones")
    var milestones: List<MilestoneList>? = null

) : Serializable
data class MilestoneList(
    @SerializedName("_id")
    var milestoneId: String? = null,
    @SerializedName("milestone_name")
    var milestoneName: String? = null,
    @SerializedName("isPhotoevidence")
    var isPhotoevidence: Boolean = true,
    @SerializedName("from_date")
    var fromDate: String? = null,
    @SerializedName("to_date")
    var toDate: String? = null,
    @SerializedName("pay_type")
    var pay_type: String? = null,
    @SerializedName("actual_hours")
    var recommendedHours: String? = null,
    @SerializedName("milestoneEarning")
    var milestoneEarning: String? = null,
    @SerializedName("isChangeRequest")
    var isChangeRequest: Boolean = false,
    @SerializedName("status")
    var status: String = "",
    @SerializedName("declinedCount")
    var declinedCount: Int = 0,
    @SerializedName("amount")
    var amount: Double = 0.0,
    @SerializedName("jobCompletedCount")
    var jobCompletedCount: Int = 0,
    @SerializedName("order")
    var order: Int = 0,
    @SerializedName("payAmount")
    var payAmount: Double = 0.0,
    @SerializedName("declinedReason")
    var isChecked: Boolean = false,
    var markComplete: Boolean = false
) : Serializable