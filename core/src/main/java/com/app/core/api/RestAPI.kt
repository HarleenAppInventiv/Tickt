package com.app.core.api

import com.app.core.basehandler.BaseResponse
import com.app.core.model.builderreview.BuilderReviewModel
import com.app.core.model.loginmodel.LoginRequest
import com.app.core.model.profile.FirebaseModel
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.model.requestsmodel.CreatePasswordRequest
import com.app.core.model.requestsmodel.DeviceToken
import com.app.core.model.requestsmodel.OTPRequest
import com.app.core.util.ApiEndPoints
import com.app.core.util.ApiParams
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


/**
 * This class is used to define list of all APIs using in App
 *
 */
interface RestAPI {
    companion object {
        //const val APP_BASE_URL = "https://ticktdevapi.appskeeper.in/v1/"    // Developer Server
//        const val APP_BASE_URL = "https://ticktqaapi.appskeeper.in/v1/"       // QA  Server
        const val APP_BASE_URL = "https://ticktstgapi.appskeeper.in/v1/"      // STAGING  SAServer


    }

    @GET("{url}")
    suspend fun get(
        @Path("url", encoded = true) url: String,
        @QueryMap queryMap: HashMap<String, Any> = HashMap()
    ): Response<BaseResponse<Any>>

    @GET("{url}")
    fun get(
        @HeaderMap headers: Map<String, String>,
        @Path("url", encoded = true) url: String,
        @QueryMap queryMap: HashMap<String, Any> = HashMap()
    ): Call<ResponseBody>

    @POST("{url}")
    suspend fun post(
        @Path("url", encoded = true) url: String,
        @Body body: HashMap<String, Any>
    ): Response<BaseResponse<Any>>


    @PUT("{url}")
    suspend fun put(
        @Path("url", encoded = true) url: String,
        @Body body: HashMap<String, Any>
    ): Response<BaseResponse<Any>>

    @PATCH("{url}")
    suspend fun patch(
        @Path("url", encoded = true) url: String,
        @QueryMap queryParams: HashMap<String, Any> = HashMap(),
        @Body body: HashMap<String, Any>
    ): Response<BaseResponse<Any>>

    @DELETE("{url}")
    suspend fun delete(
        @Path("url", encoded = true) url: String,
        @QueryMap body: HashMap<String, Any>
    ): Response<BaseResponse<Any>>

    @HTTP(method = "DELETE", path = "{url}", hasBody = true)
    suspend fun deleteWithBody(
        @Path("url", encoded = true) url: String,
        @Body body: HashMap<String, Any>
    ): Response<BaseResponse<Any>>


    @FormUrlEncoded
    @POST(ApiEndPoints.REGISTER_USER)
    suspend fun hitRegisterUserApi(
        @Field(ApiParams.EMAIL) email: String?,
        @Field(ApiParams.PASSWORD) password: String?,
        @Field(ApiParams.CONFIRM_PASSWORD) confirmPassword: String?,
        @Field(ApiParams.TOKEN) token: String?,
        @Field(ApiParams.DEVICE_TOKEN) deviceToken: String?,
        @Field(ApiParams.DEVICE_TYPE) deviceType: String? = ApiParams.ANDROID
    ): Response<BaseResponse<Any>>


    @PUT(ApiEndPoints.FORGOT_PASSWORD)
    suspend fun hitForgotPasswordApi(@Body createPasswordRequest: CreatePasswordRequest): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.ADD_DEVICE_TOKEN)
    suspend fun addDeviceToken(@Body deviceToken: DeviceToken): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.LOGOUT)
    suspend fun doLogout(): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.CHECK_MOBILE_NUMBER)
    suspend fun checkMobileNumber(@Query(ApiParams.MOBILE_NUMBER) mobileNumber: String?): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.RESENDEMAILOTP)
    suspend fun checkEmailId(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.VERIFY_OTP)
    suspend fun verifiyOtp(@Body OTPRequest: OTPRequest): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.VERIFY_MOBILE_OTP)
    suspend fun verifiyMobileOtp(@Body OTPRequest: OTPRequest): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.CREATE_PASSWORD)
    suspend fun createPassword(@Body createPasswordRequest: CreatePasswordRequest): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADES_LISTING)
    suspend fun tradeListing(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.GET_SAVED_TRADIE)
    suspend fun tradeSaveListing(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.REVIEW_LIST)
    suspend fun getBuilderReviewList(
        @Query(ApiParams.TRADIE_ID) traide: String,
        @Query(ApiParams.PAGE) page: Int
    ): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.SETTING)
    suspend fun putSetting(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.GET_SETTING)
    suspend fun getSetting(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.JOB_TYPE_LISTING)
    suspend fun jobTypeListing(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.ACTIVE_JOB_LIST)
    suspend fun activeJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.APPLIED_JOB_LIST)
    suspend fun appliedJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.PAST_JOB_LIST)
    suspend fun pastJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.NEW_JOB_LIST)
    suspend fun newJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.APPROVED_MILESTONE_LIST)
    suspend fun approvedMilestoneList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_JOB_MILESTONES)
    suspend fun jobMilestonesList(@Query(ApiParams.JOB_ID) jobId: String): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.BUILDER_JOB_MILESTONES)
    suspend fun jobBuilderMilestonesList(@Query(ApiParams.JOB_ID) jobId: String): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.MILESTONE_CHANGE_REQUEST)
    suspend fun changeMilestoneRequest(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.MILESTONE_EDIT_REQUEST)
    suspend fun editMilestoneRequest(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.MILESTONE_DECLINE_REQUEST)
    suspend fun declineMilestoneRequest(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_GET_BANK_DETAILS)
    suspend fun getBankDetails(): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.TRADIE_ADD_BANK_DETAILS)
    suspend fun addBankDetails(@Body mObject: JsonObject): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_UPDATE_BANK_DETAILS)
    suspend fun updateBankDetails(@Body mObject: JsonObject): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.TRADIE_REMOVE_BANK_DETAILS)
    suspend fun removeBankDetails(): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.TRADIE_MARK_COMPLETE)
    suspend fun markJobComplete(@Body mObject: HashMap<String, Any>): Response<BaseResponse<Any>>


    @PUT(ApiEndPoints.BUILDER_CANCEL_JOB)
    suspend fun builderCancelJob(@Body mObject: JsonObject): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_CANCEL_JOB)
    suspend fun tradieCancelJob(@Body mObject: JsonObject): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.UPLOAD)
    suspend fun uploadImage(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.HOME)
    suspend fun getHome(
        @Query(ApiParams.LAT) lat: String,
        @Query(ApiParams.LNG) long: String
    ): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.TRADIE_INITIAL_PROFILE_INFO)
    suspend fun tradieIntialProfileData(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_GET_BASIC_DETAILS)
    suspend fun getTradieBasicProfileDetails(): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_PROFILE_IMAGE)
    suspend fun uploadTradieProfileImg(@Body mObjcet: JsonObject): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_CHANGE_PASSWORD)
    suspend fun changePassword(@Body mObjcet: JsonObject): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_CHANGE_EMAIL)
    suspend fun changeEmail(@Body mObjcet: JsonObject): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_VERIFY_EMAIL)
    suspend fun verifyEmail(@Body mObjcet: JsonObject): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_PUBLIC_PROFILE)
    suspend fun getTradiePublicProfile(): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.SEARCH)
    suspend fun search(@Body jobSearchRequestModel: Any): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_EDIT_PROFILE)
    suspend fun tradieEditProfile(@Body mObjcet: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_EDIT_BASIC_PROFILE)
    suspend fun tradieEditBasicProfile(@Body mObjcet: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.UNREAD_NOTIFICATIONS)
    suspend fun readNotificaitons(@Body mObjcet: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.SEARCH)
    suspend fun searchTradie(@Body searchTradie: Any): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.CHECK_EMAIL)
    suspend fun checkEmail(@Query(ApiParams.EMAIL) email: String?): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.JOB_DETAILS)
    suspend fun jobDetails(
        @Query(ApiParams.JOB_ID) jobId: String?,
        @Query(ApiParams.TRADE_ID) tradeId: String?,
        @Query(ApiParams.SPECIALIZATION_ID) specializationId: String?
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.POST_BUILDER_REVIEW)
    suspend fun postBuilderReview(
        @Body rating: BuilderReviewModel
    ): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.TRADIE_JOB_DETAILS)
    suspend fun tradieJobDetails(
        @Query(ApiParams.JOB_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.JOB_DETAILS_BUILDER)
    suspend fun jobDetailsFromBuilder(
        @Query(ApiParams.JOB_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.JOB_DETAILS_REPUBLISH_BUILDER)
    suspend fun jobDetailsFromBuilderRepublish(
        @Query(ApiParams.JOB_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.JOB_DETAILS_REPUBLISH_BUILDER)
    suspend fun jobDetailsForBuilderEditJob(
        @Query(ApiParams.JOB_ID) jobId: String?,
        @Query(ApiParams.IS_RESP_JOB) isEdit: Boolean,
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.JOB_DETAILS_OPEN_JOB)
    suspend fun jobDetailsFromBuilderOpen(
        @Query(ApiParams.JOB_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.REMOVE_JOB_DETAILS)
    suspend fun jobDetailsRemove(
        @Query(ApiParams.JOB_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.CLOSE_QUOTE_JOB)
    suspend fun closeQuoteJob(
        @Body data: HashMap<String, Any>
    ): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.CLOSE_OPEN_JOB)
    suspend fun closeOpenJob(
        @Body data: HashMap<String, Any>
    ): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.SAVE_JOB)
    suspend fun saveJob(
        @Query(ApiParams.JOB_ID) jobId: String?,
        @Query(ApiParams.TRADE_ID) tradeId: String?,
        @Query(ApiParams.SPECIALIZATION_ID) specializationId: String?,
        @Query(ApiParams.IS_SAVE) isSave: Boolean?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.SAVE_TRADIE)
    suspend fun saveTradie(
        @Query(ApiParams.TRADIE_ID) tradeId: String?,
        @Query(ApiParams.IS_SAVE) isSave: Boolean?
    ): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.CANCEL_INVITE)
    suspend fun cancelInvite(
        @Query(ApiParams.INVITATION_Id) ivId: String?,
        @Query(ApiParams.TRADIE_ID) tradeId: String?,
        @Query(ApiParams.JOB_ID) jobID: String?
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.APPLY)
    suspend fun applyJob(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.ACTIVE_BUILDER_JOB_LIST)
    suspend fun activeBuilderJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.OPEN_BUILDER_JOB_LIST)
    suspend fun openBuilderJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.PAST_BUILDER_JOB_LIST)
    suspend fun pastBuilderJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.CREATE_TEMPLATE)
    suspend fun createTemplate(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.REVIEW_TRADIE)
    suspend fun reviewTradie(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.REVIEW_UPDATE_TRADIE)
    suspend fun reviewUpdateTradie(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.REVIEW_DELETE_TRADIE)
    suspend fun reviewRemoveTradie(@Query(ApiParams.REVIEW_ID) reviewID: String?): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.BUILDER_QUESTION_LIST)
    suspend fun builderQuestionList(
        @Query(ApiParams.JOB_ID) jobId: String,
        @Query(ApiParams.PAGE) page: Int
    ): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.TRADIE_QUESTION_LIST)
    suspend fun tradieQuestionList(
        @Query(ApiParams.JOB_ID) jobId: String,
        @Query(ApiParams.PAGE) page: Int
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.ANSWER_QUESTION)
    suspend fun addAnswer(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.TRADIE_ANSWER_QUESTION)
    suspend fun addTradieAnswer(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.ANSWER_REVIEW_REPLY)
    suspend fun addReviewReply(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.UPDATE_QUESTION)
    suspend fun updateAnswer(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_UPDATE_ANSWER)
    suspend fun updateTradieAnswer(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.UPDATE_REVIEW_REPLY)
    suspend fun updateReviewReply(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.MILSTONE_LIST)
    suspend fun milestoneLists(@Query(ApiParams.JOB_ID) jobId: String?): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.JOB_LIST)
    suspend fun jobsList(@Query(ApiParams.PAGE) page: Int?): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.VOUCH_JOB_LIST)
    suspend fun vouchJobsList(
        @Query(ApiParams.PAGE) page: Int?,
        @Query(ApiParams.TRADIE_ID) tradeId: String?
    ): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.INVITE_FOR_JOB)
    suspend fun inviteForJob(
        @Query(ApiParams.TRADIE_ID) tradieId: String?,
        @Query(ApiParams.JOB_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.MILESTONE_DETAILS)
    suspend fun getMilestoneDetails(
        @Query(ApiParams.JOB_ID) jobId: String?,
        @Query(ApiParams.MILESTONE_ID) milestoneId: String?
    ): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.DELETE_QUESTION)
    suspend fun deleteAnswer(
        @Query(ApiParams.QUESTION_ID) qID: String?,
        @Query(ApiParams.ANSWER_ID) aID: String?
    ): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.DELETE_ANSWER_TRADIE)
    suspend fun deleteTradieAnswer(
        @Query(ApiParams.QUESTION_ID) qID: String?,
        @Query(ApiParams.ANSWER_ID) aID: String?
    ): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.DELETE_REVIEW_REPLY)
    suspend fun deleteReviewReply(
        @Query(ApiParams.REVIEW_ID) qID: String?,
        @Query(ApiParams.REPLY_ID) aID: String?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.SEARCH_DATA)
    suspend fun getSearchData(@Query(ApiParams.SEARCH_TEXT) text: String?): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.LIST_TEMPLATE)
    suspend fun getTemplateList(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TEMP_MILESTONE_LIST)
    suspend fun getTemplateMilestoneList(@Query(ApiParams.TEMPL_ID) text: String?): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.DELETE_TEMPLATE)
    suspend fun deleteTemplateMilestoneList(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.RECENT_SEARCH_DATA)
    suspend fun getRecentSearchData(): Response<BaseResponse<Any>>

    @Headers(
        "Content-Type:application/json",
        "Authorization: key=AAAAuTeicwc:APA91bEzBmgYwilKP3WkpZVp31g91YbLojHTftJbk_0Sc80gWZTRMRPaBZXMQZR-dcBJ5IGijVDFX_jFr2lk1fVoXBxsEwQ_h4olbSbyUy_Yg-psRJ51Wn-TnTxfoXg3wJvumbOkAwdH"
    )
    @POST(ApiEndPoints.SEND_PUSH_NOTIFICATION)
    suspend fun sendPushNotifications(@Body firebaseMessaging: FirebaseModel): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.REGISTER_USER)
    suspend fun registerUser(@Body onBoardingData: OnBoardingData): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.LOGIN)
    suspend fun login(@Body loginRequest: LoginRequest): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.PRIVACY)
    suspend fun getPrivacyData(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TERMS_CONDITION)
    suspend fun getTermsData(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.CHECK_SOCIAL_ID)
    suspend fun checkSocialID(
        @Query(ApiParams.SOCIAL_ID) socialId: String?,
        @Query(ApiParams.EMAIL) email: String?,
        @Query(ApiParams.USER_TYPE) accountType: String?
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.CHECK_SOCIAL_AUTH)
    suspend fun socialAuth(@Body onBoardingData: OnBoardingData): Response<BaseResponse<Any>>

    @Multipart
    @POST(ApiEndPoints.UPLOAD)
    suspend fun uploadFile(@Part file: MultipartBody.Part?): Response<BaseResponse<Any>>

    @Multipart
    @POST(ApiEndPoints.UPLOAD)
    suspend fun uploadFile(@Part file: Array<MultipartBody.Part?>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.CREATE_POST)
    suspend fun createPost(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.CREATE_REPUBLISH_POST)
    suspend fun createRepublishPost(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.CREATE_UPDATE_JOB)
    suspend fun createUpdatedPost(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.NEW_APPLICANT)
    suspend fun newApplicantList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.NOTIFICATIONS)
    suspend fun notifications(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.NOTIFICATIONS)
    suspend fun markAllNotificationsRead(
        @Query(ApiParams.PAGE) page: Int,
        @Query(ApiParams.MARK_READ) markRead: Int
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.NEED_APPROVAL)
    suspend fun needApproval(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.QUOTE_BY_JOB)
    suspend fun getQuoteList(
        @Query(ApiParams.JOB_ID) jobID: String,
        @Query(ApiParams.SORT) page: Int
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.QUOTE_BY_JOB)
    suspend fun getQuoteListWithTraide(
        @Query(ApiParams.JOB_ID) jobID: String,
        @Query(ApiParams.TRADIE_ID) tradieID: String,
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.NEW_APPLICANT_LIST)
    suspend fun newApplicantLists(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_PROFILE)
    suspend fun getTradieProfile(
        @Query(ApiParams.TRADIE_ID) tradieId: String?, @Query(ApiParams.JOB_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_PROFILE_HOME)
    suspend fun getTradieProfile(
        @Query(ApiParams.TRADIE_ID) tradieId: String?
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.CREATE_CLIENT_SECRET_KEY)
    suspend fun createClientSecretKey(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.SAVE_STRIPE_BANK_TRANSACTION)
    suspend fun saveBankTransactionStripe(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.LAST_USED_CARD)
    suspend fun getLastUSEDCARD(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.CARD_LIST)
    suspend fun getAllCards(): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.ADD_CARD)
    suspend fun addCards(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.PAY)
    suspend fun pay(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.EDIT_CARD)
    suspend fun updateCards(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.DELETE_CARD)
    suspend fun deleteCards(@QueryMap params: HashMap<String, Any>): Response<BaseResponse<Any>>


    @POST(ApiEndPoints.TRADIE_ADD_PORTFOLIO)
    suspend fun addPortfolio(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_EDIT_PORTFOLIO)
    suspend fun editPortfolio(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>


    @DELETE(ApiEndPoints.TRADIE_DELETE_PORTFOLIO)
    suspend fun deletePortfolio(
        @Query(ApiParams.PORTFOLIO_ID) portfolioId: String
    ): Response<BaseResponse<Any>>


    @PUT(ApiEndPoints.ACCEPT_DECLINE_REQUEST)
    @FormUrlEncoded
    suspend fun acceptDeclineRequest(
        @Field(ApiParams.JOB_ID) jobId: String?,
        @Field(ApiParams.TRADIE_ID) tradeId: String?,
        @Field(ApiParams.STATUS) status: Int?
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_BUILDER_PROFILE)
    suspend fun builderProfile(
        @Query(ApiParams.BUILDER_ID) jobId: String?
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.TRADIE_LODGE_DISPUTE_BUILDER)
    suspend fun lodgeDisputeBuidler(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>


    @POST(ApiEndPoints.TRADIE_REPLY_CHANGE_REQUEST)
    suspend fun replyChangeRequest(@Body params: JsonObject): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.TRADIE_REPLY_CANCELLATION_REQUEST)
    suspend fun replyCancellationRequest(@Body params: JsonObject): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.BUILDER_REPLY_CANCELLATION_REQUEST)
    suspend fun replyCancellationRequestBuilder(@Body params: JsonObject): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.ADD_VOUCHER)
    suspend fun addVoucher(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.TRADIE_LODGE_DISPUTE)
    suspend fun lodgeDispute(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.INITIAL_PROFILE_INFO)
    suspend fun intialProfileData(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.GET_BASIC_DETAILS)
    suspend fun getBasicProfileDetails(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.GET_BASIC_BUILDER_DETAILS)
    suspend fun getBasicBuilerProfileDetails(): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.PROFILE_IMAGE)
    suspend fun uploadProfileImg(@Body mObjcet: JsonObject): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.EDIT_BASIC_BUILDER_PROFILE)
    suspend fun builderEditBasicProfile(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.EDIT_BUILDER_PROFILE)
    suspend fun builderEditProfile(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.TRADIE_ASK_QUESTION)
    suspend fun tradieAddQuestion(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_UPDATE_QUESTION)
    suspend fun tradieUpdateQuestion(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.MY_REVENUE_LIST)
    suspend fun myRevenueList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.BUILDEE_REVENUE_LIST)
    suspend fun myBuilderRevenueList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.BUILDEE_REVENUE_DETAILS)
    suspend fun myBuilderRevenueDetails(@Query(ApiParams.JOB_ID) jobId: String): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.MY_REVENUE_LIST)
    suspend fun getSearchRevenue(
        @Query("search") text: String?,
        @Query(ApiParams.PAGE) page: Int
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.BUILDEE_REVENUE_LIST)
    suspend fun getMyBuilderRevenue(
        @Query("search") text: String?,
        @Query(ApiParams.PAGE) page: Int
    ): Response<BaseResponse<Any>>

    @HTTP(method = "DELETE", path = ApiEndPoints.TRADIE_DELETE_QUESTION, hasBody = true)
    suspend fun tradieDeleteQuestion(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.SAVED_JOB_LIST)
    suspend fun savedJobList(@Query(ApiParams.PAGE) page: Int): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.TRADIE_SETTING)
    suspend fun putTradieSetting(@Body data: HashMap<String, Any>): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.TRADIE_GET_SETTING)
    suspend fun getTradieSetting(): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.ADD_ITEM)
    suspend fun addItem(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.ADD_QUOTE)
    suspend fun addQuote(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.EDIT_ITEM)
    suspend fun editItem(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

    @PUT(ApiEndPoints.DELETE_ITEM)
    suspend fun deleteItem(@Body params: HashMap<String, Any>): Response<BaseResponse<Any>>

}