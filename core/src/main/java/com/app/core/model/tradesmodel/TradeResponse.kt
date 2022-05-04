package com.app.core.model.tradesmodel

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class TradeResult(
    @SerializedName("trade") @Expose
     var trade: ArrayList<Trade?>? = null
)

data class Trade(
    var isSelected:Boolean?=false,
    @SerializedName("_id") @Expose
     var id: String? = null,
    @SerializedName("status")
    @Expose
     val status: Int? = null,

    @SerializedName("selected_url")
    @Expose
    val selectedUrl: String? = null,

    @SerializedName("trade_name")
    @Expose
     val tradeName: String? = null,

    @SerializedName("qualifications")
    @Expose
     val qualifications: ArrayList<Qualification>? = null,

    @SerializedName("updatedAt")
    @Expose
     val updatedAt: String? = null,

    @SerializedName("createdAt")
    @Expose
     val createdAt: String? = null,

    @SerializedName("specialisations")
    @Expose
     val specialisations: ArrayList<Specialisation>? = null,

    @SerializedName("trade_img")
    @Expose
     val tradeImg: String? = null,

    @SerializedName("sort_by")
    @Expose
     val sortBy: Int? = null,

    @SerializedName("tradeId")
    @Expose
     val tradeId: String? = null,

    @SerializedName("name")
    @Expose
     val name: String? = null
)
data class Trades(
    var isSelected:Boolean?=false,
    @SerializedName("_id") @Expose
    var id: String? = null,
    @SerializedName("status")
    @Expose
    val status: Int? = null,

    @SerializedName("selected_url")
    @Expose
    val selectedUrl: String? = null,

    @SerializedName("trade_name")
    @Expose
    val tradeName: String? = null,

    @SerializedName("updatedAt")
    @Expose
    val updatedAt: String? = null,

    @SerializedName("createdAt")
    @Expose
    val createdAt: String? = null,

    @SerializedName("trade_img")
    @Expose
    val tradeImg: String? = null,

    @SerializedName("sort_by")
    @Expose
    val sortBy: Int? = null,

    @SerializedName("tradeId")
    @Expose
    val tradeId: String? = null,

    @SerializedName("name")
    @Expose
    val name: String? = null
):Serializable

@Parcelize
data class Qualification(
    var isSelected: Boolean?=null,
    var isUploaded: Boolean?=null,
    var serverUrl: String?=null,
    var fileExt: String?=null,
    @SerializedName("_id")
    @Expose
     var id: String? = null,
    @SerializedName("status")
    @Expose
     val status: Int? = null,

    @SerializedName("tradeId")
    @Expose
     val tradeId: String? = null,

    @SerializedName("updatedAt")
    @Expose
     val updatedAt: String? = null,

    @SerializedName("createdAt")
    @Expose
     val createdAt: String? = null,

    @SerializedName("name")
    @Expose
     val name: String? = null,

    @SerializedName("sort_by")
    @Expose
     val sortBy: Int? = null
):Parcelable

@Parcelize
data class Specialisation(
    var isSelected: Boolean?=null,

    @SerializedName("_id")
    @Expose
     var id: String? = null,

    @SerializedName("status")
    @Expose
     val status: Int? = null,

    @SerializedName("tradeId")
    @Expose
     val tradeId: String? = null,

    @SerializedName("updatedAt")
    @Expose
     val updatedAt: String? = null,

    @SerializedName("createdAt")
    @Expose
     val createdAt: String? = null,

    @SerializedName("name")
    @Expose
     val name: String? = null,

    @SerializedName("sort_by")
    @Expose
     val sortBy: Int? = null
):Parcelable


