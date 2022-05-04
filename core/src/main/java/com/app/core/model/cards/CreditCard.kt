package com.app.core.model.cards

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreditCard(
    @SerializedName("id") val id: String,
    @SerializedName("cardId") val cardId: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("funding") val funding: String,
    @SerializedName("last4") val last4: String,
    @SerializedName("exp_month") val exp_month: String,
    @SerializedName("exp_year") val exp_year: String,
    @SerializedName("name") val name: String,
    var checked: Boolean
) : Serializable