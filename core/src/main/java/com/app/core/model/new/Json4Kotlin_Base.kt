import com.google.gson.annotations.SerializedName
import kotlin.collections.List

/*
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Json4Kotlin_Base(
    @SerializedName("result") val result: Result,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Boolean,
    @SerializedName("status_code") val status_code: Int
) {
    data class Result(
        @SerializedName("list") val list: ArrayList<List>,
        @SerializedName("count") val count: Int
    ) {
        data class List(
            @SerializedName("_id") val _id: String,
            @SerializedName("updatedAt") val updatedAt: String,
            @SerializedName("createdAt") val createdAt: String,
            @SerializedName("question") val question: String,
            @SerializedName("tradieId") val tradieId: String,
            @SerializedName("builderId") val builderId: String,
            @SerializedName("answers") val answers: ArrayList<Answers>,
            @SerializedName("answerSize") val answerSize: Int,
            @SerializedName("jobId") val jobId: String,
            @SerializedName("tradieData") val tradieData: ArrayList<TradieData>,
            @SerializedName("builderData") val builderData: ArrayList<String>
        ) {
            data class Answers(
                @SerializedName("tradieId") val tradieId: String,
                @SerializedName("sender_user_type") val sender_user_type: Int,
                @SerializedName("updatedAt") val updatedAt: String,
                @SerializedName("createdAt") val createdAt: String,
                @SerializedName("_id") val _id: String,
                @SerializedName("answer") val answer: String,
                @SerializedName("builderId") val builderId: String,
                @SerializedName("tradie") val tradie: ArrayList<String>,
                @SerializedName("builder") val builder: ArrayList<Builder>
            ) {
                data class Builder(
                    @SerializedName("_id") val _id: String,
                    @SerializedName("user_image") val user_image: String,
                    @SerializedName("firstName") val firstName: String,
                    @SerializedName("email") val email: String
                )
            }

            data class TradieData(
                @SerializedName("_id") val _id: String,
                @SerializedName("location") val location: Location,
                @SerializedName("country_code") val country_code: Int,
                @SerializedName("isMobileVerified") val isMobileVerified: Boolean,
                @SerializedName("accountType") val accountType: String,
                @SerializedName("forgotPasswordCode") val forgotPasswordCode: String,
                @SerializedName("isEmailVerified") val isEmailVerified: Boolean,
                @SerializedName("trade") val trade: ArrayList<String>,
                @SerializedName("specialization") val specialization: ArrayList<String>,
                @SerializedName("company_name") val company_name: String,
                @SerializedName("position") val position: String,
                @SerializedName("about") val about: String,
                @SerializedName("about_company") val about_company: String,
                @SerializedName("abn") val abn: String,
                @SerializedName("user_image") val user_image: String,
                @SerializedName("customerId") val customerId: String,
                @SerializedName("accountId") val accountId: String,
                @SerializedName("status") val status: Int,
                @SerializedName("firstName") val firstName: String,
                @SerializedName("mobileNumber") val mobileNumber: Int,
                @SerializedName("email") val email: String,
                @SerializedName("password") val password: String,
                @SerializedName("deviceToken") val deviceToken: String,
                @SerializedName("qualification") val qualification: ArrayList<Qualification>,
                @SerializedName("user_type") val user_type: Int,
                @SerializedName("settings") val settings: Settings,
                @SerializedName("portfolio") val portfolio: ArrayList<Portfolio>,
                @SerializedName("updatedAt") val updatedAt: String,
                @SerializedName("createdAt") val createdAt: String,
                @SerializedName("otp") val otp: Int,
                @SerializedName("businessName") val businessName: String
            ) {
                data class Location(
                    @SerializedName("type") val type: String,
                    @SerializedName("coordinates") val coordinates: ArrayList<Double>
                )

                data class Qualification(
                    @SerializedName("status") val status: Int,
                    @SerializedName("_id") val _id: String,
                    @SerializedName("qualification_id") val qualification_id: String,
                    @SerializedName("url") val url: String
                )

                data class Settings(
                    @SerializedName("messages") val messages: Messages,
                    @SerializedName("reminders") val reminders: Reminders,
                    @SerializedName("pushNotificationCategory") val pushNotificationCategory: ArrayList<Int>
                ) {
                    data class Messages(
                        @SerializedName("email") val email: Boolean,
                        @SerializedName("pushNotification") val pushNotification: Boolean,
                        @SerializedName("smsMessages") val smsMessages: Boolean
                    )

                    data class Reminders(
                        @SerializedName("email") val email: Boolean,
                        @SerializedName("pushNotification") val pushNotification: Boolean,
                        @SerializedName("smsMessages") val smsMessages: Boolean
                    )
                }

                data class Portfolio(
                    @SerializedName("url") val url: ArrayList<String>,
                    @SerializedName("status") val status: Int,
                    @SerializedName("_id") val _id: String,
                    @SerializedName("jobName") val jobName: String,
                    @SerializedName("jobDescription") val jobDescription: String
                )
            }
        }
    }
}