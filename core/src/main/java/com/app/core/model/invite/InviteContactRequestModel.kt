package com.app.core.model.invite

import com.app.core.model.ContactInfo

data class InviteContactRequestModel(val userId: String, val invite: ArrayList<ContactInfo>?)