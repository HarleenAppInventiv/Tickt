package com.app.core.model.BankPaymentStripe

import java.io.Serializable

data class ClientSecretResponse(
    val clientSecret: String
) : Serializable

data class Result(
    val clientSecret: String
)
