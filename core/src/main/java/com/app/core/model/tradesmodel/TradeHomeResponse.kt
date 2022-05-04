package com.app.core.model.tradesmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class TradeHome(
    var isSelected: Boolean? = false,
    @SerializedName("tradieId") @Expose
    var tradieId: String? = null,
    @SerializedName("tradieImage")
    @Expose
    val tradieImage: String? = null,
    @SerializedName("tradieName")
    @Expose
    val tradieName: String? = null,
    @SerializedName("reviews")
    @Expose
    val reviews: Int? = 0,
    @SerializedName("ratings")
    @Expose
    val ratings: Double? = 0.0,
    @SerializedName("specializationData")
    @Expose
    val specializationData: ArrayList<SpecialisationData>? = null,
    @SerializedName("tradeData")
    @Expose
    val tradeData: ArrayList<TradeData>? = null,
    @SerializedName("businessName")
    @Expose
    val businessName: String? = null,

) : Serializable

data class TradieSearchResponse(
    @SerializedName("data")
    @Expose
    var data: List<TradeHome>
)
data class SpecialisationData(
    @SerializedName("specializationId")
    @Expose
    var specializationId: String? = null,
    @SerializedName("specializationName")
    @Expose
    var specializationName: String? = null,
):Serializable

data class TradeData(
    @SerializedName("tradeId")
    @Expose
    var tradeId: String? = null,
    @SerializedName("tradeName")
    @Expose
    var tradeName: String? = null,
    @SerializedName("tradeSelectedUrl")
    @Expose
    var tradeSelectedUrl: String? = null,
):Serializable



