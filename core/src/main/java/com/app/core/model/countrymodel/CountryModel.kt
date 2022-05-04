package com.app.core.model.countrymodel


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CountryModel(
    @SerializedName("capital")
    var capital: String,
    @SerializedName("country_code")
    var countryCode: String,
    @SerializedName("latlng")
    var latlng: List<Double>,
    @SerializedName("name")
    var name: String,
    @SerializedName("timezones")
    var timezones: List<String>
) : Parcelable


@Parcelize
data class CountryDialingCodeModel(
    @SerializedName("phone")
    var dialingCode: String,
    @SerializedName("name")
    var regionName: String,
    @SerializedName("flag")
    var flag: String,
    @SerializedName("code")
    var countryCode: String,
) : Parcelable