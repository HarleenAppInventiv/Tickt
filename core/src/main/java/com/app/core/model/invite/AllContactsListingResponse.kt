package com.app.core.model.invite

import com.app.core.model.ContactInfo

data class AllContactsListingResponse(val allInvitedUser: ContactData?)
data class ContactData(val total: Int?, val pageNo: Int?, val data: ArrayList<ContactInfo>?)