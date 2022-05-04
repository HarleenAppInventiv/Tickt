package com.app.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class QuestionListResponseModel(
    @SerializedName("result")
    @Expose
    val result: Result,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("status")
    @Expose
    val status: Boolean,
    @SerializedName("status_code")
    @Expose
    val status_code: Int
) : Serializable {
    data class Result(
        @SerializedName("list")
        @Expose
        val list: ArrayList<List>,
        @SerializedName("count")
        @Expose
        val count: Int
    ) {
        data class List(
            @SerializedName("_id")
            @Expose
            val _id: String,
            @SerializedName("updatedAt")
            @Expose
            val updatedAt: String,
            @SerializedName("createdAt")
            @Expose
            val createdAt: String,
            @SerializedName("question")
            @Expose
            val question: String,
            @SerializedName("tradieId")
            @Expose
            val tradieId: String,
            @SerializedName("builderId")
            @Expose
            val builderId: String,
            @SerializedName("answers")
            @Expose
            val answers: ArrayList<Answers>,
            @SerializedName("answerSize")
            @Expose
            val answerSize: Int,
            @SerializedName("jobId")
            @Expose
            val jobId: String,
            @SerializedName("tradieData")
            @Expose
            val tradieData: ArrayList<TradieData>,
            @SerializedName("builderData")
            @Expose
            val builderData: ArrayList<String>
        ) {
            data class Answers(
                @SerializedName("tradieId")
                @Expose
                val tradieId: String,
                @SerializedName("sender_user_type")
                @Expose
                val sender_user_type: Int,
                @SerializedName("updatedAt")
                @Expose
                val updatedAt: String,
                @SerializedName("createdAt")
                @Expose
                val createdAt: String,
                @SerializedName("_id")
                @Expose
                val _id: String,
                @SerializedName("answer")
                @Expose
                val answer: String,
                @SerializedName("builderId")
                @Expose
                val builderId: String,
                @SerializedName("tradie")
                @Expose
                val tradie: ArrayList<String>,
                @SerializedName("builder")
                @Expose
                val builder: ArrayList<Builder>
            ) {
                data class Builder(
                    @SerializedName("_id")
                    @Expose
                    val _id: String,
                    @SerializedName("user_image")
                    @Expose
                    val user_image: String,
                    @SerializedName("firstName")
                    @Expose
                    val firstName: String,
                    @SerializedName("email")
                    @Expose
                    val email: String
                )
            }

            data class TradieData(
                @SerializedName("_id")
                @Expose
                val _id: String,
                @SerializedName("location")
                @Expose
                val location: Location,
                @SerializedName("country_code")
                @Expose
                val country_code: Int,
                @SerializedName("isMobileVerified")
                @Expose
                val isMobileVerified: Boolean,
                @SerializedName("accountType")
                @Expose
                val accountType: String,
                @SerializedName("forgotPasswordCode")
                @Expose
                val forgotPasswordCode: String,
                @SerializedName("isEmailVerified")
                @Expose
                val isEmailVerified: Boolean,
                @SerializedName("trade")
                @Expose
                val trade: ArrayList<String>,
                @SerializedName("specialization")
                @Expose
                val specialization: ArrayList<String>,
                @SerializedName("company_name")
                @Expose
                val company_name: String,
                @SerializedName("position")
                @Expose
                val position: String,
                @SerializedName("about")
                @Expose
                val about: String,
                @SerializedName("about_company")
                @Expose
                val about_company: String,
                @SerializedName("abn")
                @Expose
                val abn: String,
                @SerializedName("user_image")
                @Expose
                val user_image: String,
                @SerializedName("customerId")
                @Expose
                val customerId: String,
                @SerializedName("accountId")
                @Expose
                val accountId: String,
                @SerializedName("status")
                @Expose
                val status: Int,
                @SerializedName("firstName")
                @Expose
                val firstName: String,
                @SerializedName("mobileNumber")
                @Expose
                val mobileNumber: Int,
                @SerializedName("email")
                @Expose
                val email: String,
                @SerializedName("password")
                @Expose
                val password: String,
                @SerializedName("deviceToken")
                @Expose
                val deviceToken: String,
                @SerializedName("qualification")
                @Expose
                val qualification: ArrayList<Qualification>,
                @SerializedName("user_type")
                @Expose
                val user_type: Int,
                @SerializedName("settings")
                @Expose
                val settings: Settings,
                @SerializedName("portfolio")
                @Expose
                val portfolio: ArrayList<Portfolio>,
                @SerializedName("updatedAt")
                @Expose
                val updatedAt: String,
                @SerializedName("createdAt")
                @Expose
                val createdAt: String,
                @SerializedName("otp")
                @Expose
                val otp: Int,
                @SerializedName("businessName")
                @Expose
                val businessName: String
            ) {
                data class Location(
                    @SerializedName("type")
                    @Expose
                    val type: String,
                    @SerializedName("coordinates")
                    @Expose
                    val coordinates: ArrayList<Double>
                )

                data class Qualification(
                    @SerializedName("status")
                    @Expose
                    val status: Int,
                    @SerializedName("_id")
                    @Expose
                    val _id: String,
                    @SerializedName("qualification_id")
                    @Expose
                    val qualification_id: String,
                    @SerializedName("url")
                    @Expose
                    val url: String
                )

                data class Settings(
                    @SerializedName("messages")
                    @Expose
                    val messages: Messages,
                    @SerializedName("reminders")
                    @Expose
                    val reminders: Reminders,
                    @SerializedName("pushNotificationCategory")
                    @Expose
                    val pushNotificationCategory: ArrayList<Int>
                ) {
                    data class Messages(
                        @SerializedName("email")
                        @Expose
                        val email: Boolean,
                        @SerializedName("pushNotification")
                        @Expose
                        val pushNotification: Boolean,
                        @SerializedName("smsMessages")
                        @Expose
                        val smsMessages: Boolean
                    )

                    data class Reminders(
                        @SerializedName("email")
                        @Expose
                        val email: Boolean,
                        @SerializedName("pushNotification")
                        @Expose
                        val pushNotification: Boolean,
                        @SerializedName("smsMessages")
                        @Expose
                        val smsMessages: Boolean
                    )
                }

                data class Portfolio(
                    @SerializedName("url")
                    @Expose
                    val url: ArrayList<String>,
                    @SerializedName("status")
                    @Expose
                    val status: Int,
                    @SerializedName("_id")
                    @Expose
                    val _id: String,
                    @SerializedName("jobName")
                    @Expose
                    val jobName: String,
                    @SerializedName("jobDescription")
                    @Expose
                    val jobDescription: String
                )
            }
        }
    }
}