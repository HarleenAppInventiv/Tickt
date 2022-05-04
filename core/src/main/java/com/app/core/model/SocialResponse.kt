package com.airhireme.data.model.onboarding.social


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SocialResponse(
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("profileUrl")
    var profileUrl: String? = null,
    @SerializedName("socialType")
    var socialType: String? = null,
    var isEmailVerified: Boolean = false,
    var accountType: String? = null,

) : Parcelable