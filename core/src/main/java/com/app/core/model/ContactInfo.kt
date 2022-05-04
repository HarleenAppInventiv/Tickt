package com.app.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactInfo(
    var _id: String? = null,
    var isLogin: Boolean? = null,
    var userId: String? = null,
    @Transient
    var name: String? = null,
    var firstName: String? = null,
    var image: String? = null,
    var lastName: String? = null,
    @Transient
    var contactId: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var familyRelationWithPic: String? = null,

    @Transient
    var isSelected: Boolean = false,
) : Parcelable