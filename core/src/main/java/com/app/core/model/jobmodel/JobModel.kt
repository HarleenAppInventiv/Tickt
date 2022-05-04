package com.app.core.model.jobmodel

import com.app.core.model.tradesmodel.TradeHome
import com.app.core.model.tradie.QuoteItem
import com.example.ticktapp.model.registration.Location
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class JobResponseModel(
    @SerializedName("resultData")
    var jobModelList: List<JobModel>? = null,
)


data class NewJobRequestsModel(
    @SerializedName("result")
    var newJobRequestList: ArrayList<JobRecModel>? = null
)

data class HomeResponseModel(
    @SerializedName("unreadCount")
    var unreadCount: Int = 0,
    @SerializedName("recomended_jobs")
    var recJobList: List<JobRecModel>? = null,
    @SerializedName("saved_jobs")
    var saved_jobs: List<JobRecModel>? = null,
    @SerializedName("saved_tradespeople")
    var saved_tradespeople: List<TradeHome>? = null,
    @SerializedName("recomended_tradespeople")
    var recomended_tradespeople: List<TradeHome>? = null
)

data class JobModel(
    @SerializedName("_id")
    var _id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("trade_name")
    var trade_name: String? = null,
    @SerializedName("specializationsId")
    var specializationsId: String? = null,
    var isSelected: Boolean? = false

) : Serializable

data class JobListModel(
    @SerializedName("resultData")
    var resultData: List<JobModel>? = null,
)

data class JobMilestoneListModel(
    @SerializedName("isCancelJobRequest")
    var isCancelJobRequest: Boolean = false,
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("milestones")
    var milestones: ArrayList<MilestoneList>? = null,
    @SerializedName("postedBy")
    var postedBy: PhostedBy
)

data class MilestoneDetails(
    @SerializedName("images")
    val images: List<Photos>,
    @SerializedName("description")
    val description: String,
    @SerializedName("hoursWorked")
    val hoursWorked: String,
) : Serializable


data class TradieBankDetails(

    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("account_name")
    var account_name: String? = null,
    @SerializedName("account_number")
    var account_number: String? = null,
    @SerializedName("bsb_number")
    var bsb_number: String? = null,
    @SerializedName("stripeAccountId")
    var stripeAccountId: String? = null,
    @SerializedName("accountVerified")
    var accountVerified: Boolean? = null

) : Serializable

data class MilestoneList(
    @SerializedName("milestoneId")
    var milestoneId: String? = null,
    @SerializedName("milestoneName")
    var milestoneName: String? = null,
    @SerializedName("isPhotoevidence")
    var isPhotoevidence: Boolean = false,
    @SerializedName("fromDate")
    var fromDate: String? = null,
    @SerializedName("toDate")
    var toDate: String? = null,
    @SerializedName("pay_type")
    var pay_type: String? = null,
    @SerializedName("recommendedHours")
    var recommendedHours: String? = null,
    @SerializedName("isChangeRequest")
    var isChangeRequest: Boolean = false,
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("declinedCount")
    var declinedCount: Int = 0,
    @SerializedName("amount")
    var amount: Double = 0.0,
    @SerializedName("jobCompletedCount")
    var jobCompletedCount: Int = 0,
    @SerializedName("order")
    var order: Int = 0,
    @SerializedName("declinedReason")
    var declinedReason: DeclinedReasons? = null,
    var isChecked: Boolean = false,
    var markComplete: Boolean = false


) : Serializable

data class DeclinedReasons(
    @SerializedName("reason")
    var reason: String = "",
    @SerializedName("url")
    var url: ArrayList<String>?,
) : Serializable


data class JobRecModel(
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("userImage")
    var userImage: String? = null,
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("builderName")
    var builderName: String? = null,
    @SerializedName("builderImage")
    var builderImage: String? = null,
    @SerializedName("tradeSelectedUrl")
    var tradeSelectedUrl: String? = null,
    @SerializedName("tradeId")
    var tradeId: String? = null,
    @SerializedName("tradeName")
    var tradeName: String? = null,
    @SerializedName("tradieId")
    var tradieId: String? = null,
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("specializationId")
    var specializationId: String? = null,
    @SerializedName("specializationName")
    var specializationName: String? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("amount")
    var amount: String? = null,
    @SerializedName("distance")
    var distance: String? = null,
    @SerializedName("locationName")
    var locationName: String? = null,
    @SerializedName("location_name")
    var location_name: String? = null,
    @SerializedName("durations")
    var durations: String? = null,
    @SerializedName("duration")
    var duration: String? = null,
    @SerializedName("jobDescription")
    var jobDescription: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("details")
    var details: String? = null,
    @SerializedName("viewersCount")
    var viewersCount: String? = null,
    @SerializedName("questionsCount")
    var questionsCount: String? = null,
    @SerializedName("appliedStatus")
    var appliedStatus: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("jobStatus")
    var jobStatus: String? = null,
    @SerializedName("milestoneNumber")
    var milestoneNumber: Int = 0,
    @SerializedName("totalMilestones")
    var totalMilestones: Int = 0,
    @SerializedName("isSaved")
    var isSaved: Boolean? = null,
    @SerializedName("location")
    var location: Location,
    @SerializedName("postedBy")
    var postedBy: PhostedBy,
    @SerializedName("builderData")
    var builderData: PhostedBy,
    @SerializedName("photos")
    var photos: List<Photos>,
    @SerializedName("total")
    var total: String? = null,
    @SerializedName("isCancelJobRequest")
    var isCancelJobRequest: Boolean = false,
    @SerializedName("reasonForCancelJobRequest")
    var reasonForCancelJobRequest: Int,
    @SerializedName("reasonNoteForCancelJobRequest")
    var reasonNoteForCancelJobRequest: String,
    @SerializedName("rejectReasonNoteForCancelJobRequest")
    var rejectReasonNoteForCancelJobRequest: String,
    @SerializedName("order")
    var order: String? = null,
    @SerializedName("quoteJob")
    var quoteJob: Boolean,
    @SerializedName("applyButtonDisplay")
    var applyButtonDisplay: Boolean = true,
    @SerializedName("quoteCount")
    var quoteCount: Int,
    @SerializedName("isApplied")
    var isApplied: Boolean = false,
    @SerializedName("jobMilestonesData")
    var jobMilestonesData: List<JobMilestone>,
    @SerializedName("specializationData")
    var specializationData: List<Specialisation>,
    @SerializedName("fromDate")
    var fromDate: String? = null,
    @SerializedName("toDate")
    var toDate: String? = null,
    @SerializedName("timeLeft")
    var timeLeft: String? = null,
    @SerializedName("changeRequestDeclineReason")
    var changeRequestDeclineReason: String? = null,
    @SerializedName("jobType")
    var jobType: JobType,
    @SerializedName("checked")
    var checked: Boolean = false,
    @SerializedName("questionsData")
    var questionsData: MutableList<QuestionsData> = ArrayList(),
    @SerializedName("isRated")
    var isRated: Boolean = false,
    @SerializedName("isChangeRequest")
    var isChangeRequest: Boolean? = null,
    @SerializedName("changeRequestData")
    var changeRequestData: List<ChangeRequestData>,
    @SerializedName("reasonForChangeRequest")
    var reasonForChangeRequest: ArrayList<String>,
) : Serializable

data class ChangeRequestData(
    @SerializedName("isDeleteRequest")
    var isDeleteRequest: Boolean? = null,
    @SerializedName("isPhotoevidence")
    var isPhotoevidence: Boolean? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("from_date")
    var from_date: String? = null
) : Serializable

data class JobEditData(
    @SerializedName("resultData")
    var data: JobRecModelRepublish? = null
)

data class JobRecModelRepublish(
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("categories")
    var categories: List<Trades>? = null,
    @SerializedName("location_name")
    var location_name: String? = null,
    @SerializedName("job_description")
    var jobDescription: String? = null,
    @SerializedName("amount")
    var amount: String? = null,
    @SerializedName("pay_type")
    var pay_type: String? = null,
    @SerializedName("location")
    var location: Location,
    @SerializedName("quoteJob")
    var quoteJob: Boolean,
    @SerializedName("quoteCount")
    var quoteCount: Int,
    @SerializedName("specialization")
    var specializationData: ArrayList<Specialisation>,
    @SerializedName("milestones")
    var milestones: ArrayList<MilestoneListRepublish>,
    @SerializedName("from_date")
    var fromDate: String? = null,
    @SerializedName("to_date")
    var toDate: String? = null,
    @SerializedName("timeLeft")
    var timeLeft: String? = null,
    @SerializedName("job_type")
    var jobType: List<JobType>,
    @SerializedName("urls")
    var urls: List<PhotosThumbs>,
    var isEdit: Boolean = false
) : Serializable

data class PhotosThumbs(
    @SerializedName("mediaType")
    var mediaType: Int? = null,
    @SerializedName("link")
    var link: String? = null,
    @SerializedName("thumbnail")
    var thumbnail: String? = null
) : Serializable

data class MilestoneListRepublish(
    @SerializedName("_id")
    var milestoneId: String? = null,
    @SerializedName("milestone_name")
    var milestoneName: String? = null,
    @SerializedName("isPhotoevidence")
    var isPhotoevidence: Boolean = false,
    @SerializedName("from_date")
    var fromDate: String? = null,
    @SerializedName("to_date")
    var toDate: String? = null,
    @SerializedName("pay_type")
    var pay_type: String? = null,
    @SerializedName("recommended_hours")
    var recommendedHours: String? = null,
    @SerializedName("isChangeRequest")
    var isChangeRequest: Boolean = false,
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("declinedCount")
    var declinedCount: Int = 0,
    @SerializedName("amount")
    var amount: Double = 0.0,
    @SerializedName("jobCompletedCount")
    var isChecked: Boolean = false,
    var markComplete: Boolean = false
) : Serializable

data class Trades(
    var isSelected: Boolean? = false,
    @SerializedName("categoryId")
    @Expose
    var id: String? = null,
    @SerializedName("categorySelectedUrl")
    @Expose
    val selectedUrl: String? = null,
    @SerializedName("categoryName")
    @Expose
    val tradeName: String? = null,
) : Serializable

data class JobDashboardModel(
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("tradieImage")
    var tradieImage: String? = null,
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("tradeSelectedUrl")
    var tradeSelectedUrl: String? = null,
    @SerializedName("tradeId")
    var tradeId: String? = null,
    @SerializedName("tradieId")
    var tradieId: String? = null,
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("builderImage")
    var builderImage: String? = null,
    @SerializedName("tradeName")
    var tradeName: String? = null,
    @SerializedName("specializationId")
    var specializationId: String? = null,
    @SerializedName("specializationName")
    var specializationName: String? = null,
    @SerializedName("quoteJob")
    var quoteJob: Boolean,
    @SerializedName("quoteCount")
    var quoteCount: Int,
    @SerializedName("fromDate")
    var fromDate: String? = null,
    @SerializedName("toDate")
    var toDate: String? = null,
    @SerializedName("amount")
    var amount: String? = null,
    @SerializedName("distance")
    var distance: String? = null,
    @SerializedName("durations")
    var durations: String? = null,
    @SerializedName("timeLeft")
    var timeLeft: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("questionsCount")
    var questionsCount: String? = null,
    @SerializedName("jobDescription")
    var jobDescription: String? = null,
    @SerializedName("details")
    var details: String? = null,
    @SerializedName("milestoneNumber")
    var milestoneNumber: Int = 0,
    @SerializedName("totalMilestones")
    var totalMilestones: Int = 0,
    @SerializedName("isApplied")
    var isApplied: Boolean = false,
    @SerializedName("location")
    var location: String,
    @SerializedName("locationName")
    var locationName: String,
    @SerializedName("location_name")
    var location_name: String,
    @SerializedName("total")
    var total: String,
    @SerializedName("jobData")
    var jobData: JobData,
    @SerializedName("tradieData")
    var tradieData: Tradie,
    @SerializedName("isRated")
    var isRated: Boolean = false,
    @SerializedName("needApproval")
    var needApproval: Boolean = false,
    @SerializedName("quote")
    var quote: List<QuoteItem>,


    ) : Serializable

data class JobData(
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("tradeSelectedUrl")
    var tradeSelectedUrl: String? = null,
    @SerializedName("tradeName")
    var tradeName: String? = null,
    @SerializedName("fromDate")
    var fromDate: String? = null,
    @SerializedName("toDate")
    var toDate: String? = null,
) : Serializable

data class JobAppliedModel(
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("appliedId")
    var appliedId: String? = null,
    @SerializedName("tradeId")
    var tradeId: String? = null,
    @SerializedName("specializationId")
    var specializationId: String? = null,
    @SerializedName("appliedStatus")
    var appliedStatus: String? = null,
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("appliedAt")
    var appliedAt: String? = null
) : Serializable

data class JobType(
    @SerializedName("jobTypeId")
    var jobTypeId: String? = null,
    @SerializedName("jobTypeName")
    var jobTypeName: String? = null,
    @SerializedName("jobTypeImage")
    var jobTypeImage: String? = null
) : Serializable

data class Specialisation(
    @SerializedName("specializationId")
    var specializationId: String? = null,
    @SerializedName("specializationName")
    var specializationName: String? = null
) : Serializable

data class JobMilestStonRespnse(
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("dispute")
    var dispute: Boolean? = null,
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("tradieId")
    var tradieId: String? = null,
    @SerializedName("tradie")
    var tradie: TradeHome? = null,
    @SerializedName("categories")
    var categories: List<CategoryData>? = null,
    @SerializedName("milestones")
    var milestones: List<JobMilestone>? = null,
) : Serializable

data class CategoryData(
    @SerializedName("_id")
    var _id: String? = null,
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("sort_by")
    var sort_by: Int? = 0,
    @SerializedName("updatedAt")
    var updatedAt:String? = null,
    @SerializedName("createdAt")
    var createdAt:String? = null,
    @SerializedName("trade_name")
    var trade_name:String? = null,
    @SerializedName("selected_url")
    var selected_url: String? = null,
    @SerializedName("unselected_url")
    var unselected_url:String? = null,
    @SerializedName("description")
    var description:String? = null,
):Serializable

data class JobMilestone(
    @SerializedName("milestoneId")
    var milestoneId: String? = null,
    @SerializedName("milestoneName")
    var milestoneName: String? = null,
    @SerializedName("fromDate")
    var fromDate: String? = null,
    @SerializedName("toDate")
    var toDate: String? = null,
    @SerializedName("isPhotoevidence")
    var isPhotoevidence: Boolean? = null,
    @SerializedName("recommendedHours")
    var recommendedHours: String? = null,
    @SerializedName("hoursWorked")
    var hoursWorked: String? = null,
    @SerializedName("declinedCount")
    var declinedCount: String? = null,
    @SerializedName("hourlyRate")
    var hourlyRate: String? = null,
    @SerializedName("milestoneAmount")
    var milestoneAmount: String? = null,
    @SerializedName("taxes")
    var taxes: String? = null,
    @SerializedName("platformFees")
    var platformFees: String? = null,
    @SerializedName("total")
    var total: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("checked")
    var checked: Boolean? = null,
    @SerializedName("order")
    var order: String? = null,
    @SerializedName("isChangeRequest")
    var isChangeRequest: Boolean? = null
) : Serializable

data class Photos(
    @SerializedName("mediaType")
    var mediaType: Int? = null,
    @SerializedName("link")
    var link: String? = null
) : Serializable

data class PhotosThumb(
    @SerializedName("mediaType")
    var mediaType: Int? = null,
    @SerializedName("thumbnail")
    var thumbnail: String? = null,
    @SerializedName("link")
    var link: String? = null
) : Serializable

data class TradieJobDashboardModel(
    @SerializedName("active")
    var activeJobList: ArrayList<JobDashboardModel>? = null,
    @SerializedName("open")
    var openJobList: ArrayList<JobDashboardModel>? = null,
    @SerializedName("past")
    var pastJobList: ArrayList<JobDashboardModel>? = null,
    @SerializedName("newApplicantsCount")
    var newApplicantsCount: Int = 0,
    @SerializedName("needApprovalCount")
    var needApprovalCount: Int = 0
)

data class TradieJobsDashboardModel(
    @SerializedName("active")
    var activeJobsList: ArrayList<JobRecModel>? = null,
    @SerializedName("applied")
    var appliedJobList: ArrayList<JobRecModel>? = null,
    @SerializedName("completed")
    var pastJobList: ArrayList<JobRecModel>? = null,
    @SerializedName("newJobsCount")
    var newJobsCount: Int = 0,
    @SerializedName("milestonesCount")
    var milestonesCount: Int = 0

)

data class TradieInitalProfileModel(
    @SerializedName("trade")
    var trade: ArrayList<String>? = null,
    @SerializedName("businessName")
    var businessName: String? = null,
    @SerializedName("specialization")
    var specialization: ArrayList<String>? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
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
    @SerializedName("ratings")
    var ratings: Double = 0.0,
    @SerializedName("jobCompletedCount")
    var jobCompletedCount: Int = 0,
    @SerializedName("qualificationDoc")
    var qualificationDoc: ArrayList<QualifiedDoc>? = null


)

data class QualifiedDoc(
    @SerializedName("qualification_id")
    var qualification_id: String? = null,
    @SerializedName("docName")
    var docName: String? = null,
    @SerializedName("url")
    var url: String? = null
) : Serializable

data class PhostedBy(
    @SerializedName("builderName")
    var builderName: String? = null,
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("builderImage")
    var builderImage: String? = null,
    @SerializedName("reviews")
    var reviews: Int? = null,
    @SerializedName("ratings")
    var ratings: Double? = null
) : Serializable


data class Tradie(
    @SerializedName("tradieName")
    var tradieName: String? = null,
    @SerializedName("tradieId")
    var tradieId: String? = null,
    @SerializedName("tradieImage")
    var tradieImage: String? = null,
    @SerializedName("reviews")
    var reviews: Int? = null,
    @SerializedName("ratings")
    var ratings: Double? = null
) : Serializable

data class QuestionsData(
    @SerializedName("questionData")
    var questionData: QuestionData? = null
) : Serializable

data class QuestionData(
    @SerializedName("questionId")
    var questionId: String? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("userImage")
    var userImage: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("question")
    var question: String? = null,
    @SerializedName("isModifiable")
    var isModifiable: Boolean? = null,
    @SerializedName("answerData")
    var answerData:  AnswerData? = null
) : Serializable

data class AnswerData(
    @SerializedName("answerId")
    var answerId: String? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("userImage")
    var userImage: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("answer")
    var answer: String? = null,
    @SerializedName("isModifiable")
    var isModifiable: Boolean? = null
) : Serializable