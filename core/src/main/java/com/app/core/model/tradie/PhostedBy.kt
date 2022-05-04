package com.app.core.model.tradie

import com.app.core.model.tradesmodel.SpecialisationData
import com.app.core.model.tradesmodel.TradeData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BuilderModel(
    @SerializedName("tradieName")
    var builderName: String? = null,
    @SerializedName("businessName")
    var businessName: String? = null,
    @SerializedName("tradieId")
    var builderId: String? = null,
    @SerializedName("position")
    var position: String? = null,
    @SerializedName("tradieImage")
    var builderImage: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("about")
    var about: String? = null,
    @SerializedName("reviews")
    var reviews: Int? = null,
    @SerializedName("ratings")
    var ratings: Double? = null,
    @SerializedName("reviewsCount")
    var reviewsCount: Double? = null,
    @SerializedName("voucherCount")
    var voucherCount: Int = 0,
    @SerializedName("jobCompletedCount")
    var jobCompletedCount: Double? = null,
    @SerializedName("isRequested")
    var isRequested: Boolean = false,
    @SerializedName("isInvited")
    var isInvited: Boolean = false,
    @SerializedName("isSaved")
    var isSaved: Boolean = false,
    @SerializedName("invitationId")
    var invitationId: String? = null,
    @SerializedName("tradeData")
    var tradeData: ArrayList<TradeData>? = null,
    @SerializedName("specializationData")
    var specializationData: ArrayList<SpecialisationData>? = null,
    @SerializedName("portfolio")
    var portfolio: ArrayList<PortFolio>? = null,
    @SerializedName("reviewData")
    var reviewData: ArrayList<ReviewData>? = null,
    @SerializedName("vouchesData")
    var vouchesData: ArrayList<VouchesData>? = null,
    @SerializedName("areasOfSpecialization")
    var areasOfSpecialization: AreaOfSpecialisation? = null
) : Serializable

data class QuoteTradie(
    @SerializedName("_id")
    var _id: String? = null,
    @SerializedName("tradieName")
    var builderName: String? = null,
    @SerializedName("tradieId")
    var builderId: String? = null,
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("position")
    var position: String? = null,
    @SerializedName("tradieImage")
    var builderImage: String? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("job_status")
    var job_status: String? = "",
    @SerializedName("reviewCount")
    var reviews: Int? = null,
    @SerializedName("rating")
    var ratings: Double? = null,
    @SerializedName("reviewsCount")
    var reviewsCount: Double? = null,
    @SerializedName("totalQuoteAmount")
    var totalQuoteAmount: Int? = null,
    @SerializedName("quote_item")
    var quote_item: List<QuoteItem>? = null
) : Serializable

data class QuoteItem(
    @SerializedName("_id")
    var _id: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("price")
    var price: Double? = null,
    @SerializedName("quantity")
    var quantity: Int? = null,
    @SerializedName("totalAmount")
    var totalAmount: Double? = null,
    @SerializedName("item_number")
    var item_number: Int? = null,
) : Serializable

data class QuoteList(
    @SerializedName("resultData")
    var resultData: ArrayList<QuoteTradie>? = null
)
data class QuoteData(
    @SerializedName("resultData")
    var resultData: QuoteItem? = null
)
data class AreaOfSpecialisation(
    @SerializedName("tradeData")
    var tradeData: ArrayList<TradeData>? = null,
    @SerializedName("specializationData")
    var specializationData: ArrayList<SpecialisationData>? = null
) : Serializable

data class PortFolio(
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("jobDescription")
    var jobDescription: String? = null,
    @SerializedName("portfolioId")
    var portfolioId: String? = null,
    @SerializedName("portfolioImage")
    var portfolioImage: List<String>? = null,
    var isEditable: Boolean = false
) : Serializable

data class ReviewData(
    @SerializedName("reviewId")
    var reviewId: String? = null,
    @SerializedName("reviewSenderImage")
    var reviewSenderImage: String? = null,
    @SerializedName("reviewSenderName")
    var reviewSenderName: String? = null,
    @SerializedName("reviewSenderId")
    var reviewSenderId: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("userImage")
    var userImage: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("ratings")
    var ratings: Double? = 0.0,
    @SerializedName("rating")
    var rating: Double? = 0.0,
    @SerializedName("review")
    var review: String? = null,
    @SerializedName("reply")
    var reply: String? = null,
    @SerializedName("replyId")
    var replyId: String? = null,
    @SerializedName("isModifiable")
    var isModifiable: Boolean? = false,
    @SerializedName("replyData")
    var replyData: ReviewData? = null,
) : Serializable

data class ReviewList(
    @SerializedName("reviewData")
    var reviewData: ReviewData? = null,
) : Serializable

data class ReviewLists(
    @SerializedName("list")
    var list: List<ReviewList>? = null,
) : Serializable

data class VouchesData(
    @SerializedName("builderId")
    var builderId: String? = null,
    @SerializedName("builderName")
    var builderName: String? = null,
    @SerializedName("builderImage")
    var builderImage: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("voucherId")
    var voucherId: String? = null,
    @SerializedName("jobId")
    var jobId: String? = null,
    @SerializedName("jobName")
    var jobName: String? = null,
    @SerializedName("tradieId")
    var tradieId: String? = null,
    @SerializedName("tradieName")
    var tradieName: String? = null,
    @SerializedName("vouchDescription")
    var vouchDescription: String? = null,
    @SerializedName("recommendation")
    var recommendation: String? = null,
    @SerializedName("photos")
    var photos: List<String>? = null
) : Serializable