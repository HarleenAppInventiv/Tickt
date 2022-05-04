package com.example.ticktapp.model

import java.io.Serializable

data class MilestoneData(
    var name: String = "",
    var photoRequired: Boolean = false,
    var isChecked: Boolean = false,
    var start_date: String = "",
    var end_date: String = "",
    var hours: String = ""
) : Serializable

data class MilestoneRequestData(
    var milestone_name: String = "",
    var isPhotoevidence: Boolean = false,
    var from_date: String = "",
    var to_date: String = "",
    var order: Int = 0,
    var recommended_hours: String = ""
) : Serializable

data class MilestoneEditRequestData(
    var milestoneId: String = "",
    var milestone_name: String = "",
    var isPhotoevidence: Boolean = false,
    var from_date: String = "",
    var to_date: String = "",
    var order: Int = 0,
    var isDeleteRequest: Boolean = false,
    var recommended_hours: String = ""
) : Serializable
data class MilestoneEditOnlyRequestData(
    var milestoneId: String = "",
    var milestone_name: String = "",
    var isPhotoevidence: Boolean = false,
    var from_date: String = "",
    var to_date: String = "",
    var order: Int = 0,
    var recommended_hours: String = ""
) : Serializable
data class MilestoneResponesData(
    var milestoneName: String = "",
    var isPhotoevidence: Boolean = false,
    var fromDate: String = "",
    var toDate: String = "",
    var recommendedHours: String = ""
) : Serializable

data class MilestoneResponesIdData(
    var milestoneId:String="",
    var milestoneName: String = "",
    var isPhotoevidence: Boolean = false,
    var fromDate: String = "",
    var toDate: String = "",
    var recommendedHours: String = ""
) : Serializable

data class TemplateData(
    var milestoneCount: String = "",
    var templateId: String = "",
    var templateName: String = "",
) : Serializable

data class TemplateMilestoneData(
    var milestones: ArrayList<MilestoneResponesIdData>,
    var tempId: String = "",
    var templateId:String="",
    var templateName: String = "",
) : Serializable