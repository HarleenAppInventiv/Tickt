package com.app.core.model.registrationmodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class OnBoardingData(
    var firstName: String? =null,
    var email: String? = null,
    var mobileNumber: String? = null,
    var password: String? = null,
    val deviceToken: String?=null,
    var trade: ArrayList<String> ? = null,
    var specialization: ArrayList<String> ?=null,
     var qualification: ArrayList<Qualification>?=null,
    var abn:String?=null,
    var user_type:Int?=null,
    var company_name: String?=null,
    var position: String?=null,
    var socialId: String?=null,
    var accountType: String?=null,
    var authType: String?=null,
    var location: LocationFinder? = null
): Parcelable

@Parcelize
public data class Qualification(
    var qualification_id: String? =null,
    var url: String? =null):Parcelable

@Parcelize
data class Coordinates(
    var latitude : Double? = null,
    var longitude : Double? = null
) : Parcelable

@Parcelize
data class LocationFinder(
    var type: String? = null,
    var coordinates: ArrayList<Double>? = null
): Parcelable


