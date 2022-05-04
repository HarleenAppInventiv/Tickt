package com.app.core.util

object APIHeaders {
    const val Authorization = "authorization"
    const val Authorization_HEADER = "Authorization"
    const val API_USERNAME = "tickt_app"
    const val API_PASSWORD = "tickt_app_123sadefss"
    const val API_KEY = "1234"
}


object DeepLinkTypes {
    const val REGISTER = "register"
    const val RESET_PASSWORD = "reset-password"
    const val UNBLOCK = "unblocked"
    const val DEEPLINK_TOKEN = "resettoken"
}

object MDM {
    const val GET_ALL_DEVICES = "https://care2talk.hexnodemdm.com/api/v1/devices/"
    const val GET_DEVICE_DETAILS = "https://care2talk.hexnodemdm.com/api/v1/devices/"
    const val API_KEY = "TMa5jAjE4TJOPiBPkKZL"
}

object ListingTypes {
    const val REGISTERED_USERS = "REGISTERED"
    const val NOT_REGISTERED_USERS = "NOT_REGISTERED"
    const val ALL_USERS = "ALL"
}

object JobStatus {
    const val APPROVED = "APPROVED"
    const val NEED_APPROVAL = "NEED APPROVAL"
}


object StripeConstants {
    const val PUBLISHABLE_KEY =
        "pk_test_51IdTOqKjjnW7jXnVURB0mRIVVZ997twxcbwTmAyc9EDhI60iB05YtmCNOC8ExoEMNO3t7ZBSc8WhqHFZMlzZyDen00cSy6hX4e"
}

object MilestoneStatus {
    const val PENDING = 0
    const val COMPLETED = 1
    const val APPROVED = 2
    const val DECLINED = 3
    const val CHANGE_REQUEST = 4
    const val CHANGE_REQUEST_ACCEPTED = 5
}

object MoEngageConstants {
    const val USER_TYPE: String = "user_type"
    const val SUCCESS_STATUS: String = "success_status"
    const val MOENGAGE_APP_ID = "9MGBIF1KHFC5GXE7YHMXRPEE"

    const val MOENGAGE_EVENT_SIGN_UP = "Sign Up"
    const val MOENGAGE_EVENT_APP_OPEN = "App Open"
    const val MOENGAGE_EVENT_LOG_OUT = "Log Out"
    const val MOENGAGE_EVENT_VIEW_QUOTE = "View Quote"
    const val MOENGAGE_EVENT_ACCEPT_QUOTE = "Accept quote"
    const val MOENGAGE_EVENT_CANCEL_QUOTE_JOB = "Cancel quoted job"
    const val MOENGAGE_EVENT_MILESTONE_CHECK_APPROVED = "Milestone Checked and approved"
    const val MOENGAGE_EVENT_MADE_PAYMENT = "Made Payment"
    const val MOENGAGE_EVENT_MILESTONE_DECLINED = "Milestone declined"
    const val MOENGAGE_EVENT_SAVED_TRADIE = "Saved tradie"
    const val MOENGAGE_EVENT_VIEWED_REVIEWS = "Viewed reviews"
    const val MOENGAGE_EVENT_LEFT_VOUCHER = "Left voucher"
    const val MOENGAGE_EVENT_PAYMENT_SUCCESS = "Payment success"
    const val MOENGAGE_EVENT_PAYMENT_FAILURE = "Payment failure"
    const val MOENGAGE_EVENT_EDIT_MILESTONES = "Edit milestones"
    const val MOENGAGE_EVENT_REPUBLISHED_JOB = "Republished job"
    const val MOENGAGE_EVENT_ADDED_INFO_ABOUT_COMPANY = "Added info about company"
    const val MOENGAGE_EVENT_ADDED_PORTFOLIO = "Added portfolio"
    const val MOENGAGE_EVENT_ADDED_PAYMENT_DETAILS = "Added payment details"
    const val MOENGAGE_EVENT_APPLIED_FOR_JOB = "Applied for a job"
    const val MOENGAGE_EVENT_VIEWED_TRADIE_PROFILE = "Viewed tradie profile"
    const val MOENGAGE_EVENT_TRADIE_ADDED_BANK_DETAILS = "Added payment details"
    const val MOENGAGE_EVENT_TRADIE_ACCEPT_CANCELLATION = "Accept cancellation"
    const val MOENGAGE_EVENT_TRADIE_REJECT_CANCELLATION = "Reject cancellation"
    const val MOENGAGE_EVENT_TRADIE_ASKED_QUESTION = "Asked a question"
    const val MOENGAGE_EVENT_TRADIE_ADDED_REVIEW = "Added review"
    const val MOENGAGE_EVENT_TRADIE_SEARCHED_FOR_JOBS = "Searched for jobs"
    const val MOENGAGE_EVENT_VIEWED_APPROVED_MILESTONE = "Viewed approved milestone"
    const val MOENGAGE_EVENT_VIEWED_BUILDER_PROFILE = "Viewed builder profile"
    const val MOENGAGE_EVENT_SEARCHED_FOR_TRADIE = "Searched for tradies"
    const val MOENGAGE_EVENT_POSTED_JOB = "Posted a job"
    const val MOENGAGE_EVENT_QUOTED_A_JOB = "Quoted a job"
    const val MOENGAGE_EVENT_MILESTONE_COMPLETED = "Milestone completed"
    const val MOENGAGE_EVENT_CHAT = "Chat"
    const val NAME = "name"
    const val SIGN_UP_SOURCE = "sign up source"
    const val EMAIL = "email"
    const val PLATFORM = "Platform"
    const val APP_OPEN = "app_open"
    const val CURRENT_PAGE = "current_page"
    const val TIME_STAMP = "timestamp"
    const val CATEGORY = "category"
    const val NUMBER_OF_MILESTONES = "Number of milestones"
    const val AMOUNT = "Amount"
    const val START_DATE = "start date"
    const val END_DATE = "end date"
    const val LOCATION = "location"
    const val MILESTONE_NUMBER = "Milestone number"
    const val MAX_BUDGET = "Max budget"
    const val LENGTH_OF_HIRE = "length of hire"

}


object Constants {
    const val DELETE = 1
    const val EDIT = 2
    const val REGISTER = 3
    const val CALL = 4
    const val HOME = 5
    const val CONFIGURATION = 6
    const val RESEND = 7
    const val INVITE = 8
    const val LARGE = 9
    const val SMALL = 10
    const val OTHER = "Other"
    const val OTHERS = "Others"
    const val PICKFILE_REQUEST_CODE = 11
    const val DROPBOX_REQUEST_CODE = 1111
    const val FORGOT_PASSWORD = 12
    const val TRADIE_JOB_CANCEL = 300
    const val LOGIN = 13
    const val TERMS = 14
    const val PRIVACY = 15
    const val GOOGLE = 16
    const val LINKEDIN = 17
    const val REQUEST_LOCATION = 20
    const val CAMERA_REQUEST = 1100
    const val GALLERY_REQUEST = 1200
    const val LINKEDINRESULT = 2000
    const val BANK = 21
}

object ApiCodes {

    const val QUESTIONS_LIST: Int = 512
    const val SUCCESS = 200
    const val ACCOUNT_LOCKED = 403
    const val ACCOUNT_EXPIRED = 401
    const val COMMON_ERRORS = 400
    const val EMPTY_RESPONSE = -1
    const val NO_API = 0
    const val SOCIAL_LOGIN = 2
    const val USER_PROFILE = 3
    const val REGISTER_USER = 4
    const val FORGOT_PASSWORD = 5
    const val RESET_PASSWORD = 6
    const val LOGOUT = 7
    const val LOGIN = 8
    const val VERIFY_OTP = 9
    const val FORGOT_PASSWORD_OTP = 10
    const val ACCOUNT_UNBLOCK_OTP = 11
    const val UPDATE_PROFILE = 12
    const val GET_TRADE_LIST = 14
    const val GET_ALL_CONTACTS = 15
    const val VERIFY_EMAIL = 16
    const val HIT_RESEND_INVITE = 17
    const val DELETE_CONTACT = 18
    const val GET_ALL_DEVICES = 19
    const val GET_DEVICE_DETAILS = 29
    const val CHECK_PHONE_NUMBER = 30
    const val CREATE_PASSWORD = 13
    const val PRIVACY = 20
    const val TNC = 21
    const val SOCIAL_ID = 22
    const val UPLOAD_FILE = 23
    const val JOB_TYPE_LIST = 24
    const val HOME = 25
    const val UPLOAD = 26
    const val SEARCH = 27
    const val SEARCH_DATA = 28
    const val RECENT_SEARCH = 29
    const val JOB_DETAILS = 30
    const val SAVE_JOBS = 31
    const val APPLY = 32
    const val CREATE_TEMPLATE = 33
    const val TEMPLATE_LIST = 34
    const val TEMPLATE_MILESTONE_LIST = 35
    const val CREATE_POST = 36
    const val ACTIVE_JOBS = 300
    const val APPLIED_JOBS = 301
    const val NEW_JOB_REQUEST = 302
    const val APPROVED_MILESTONE_REQUEST = 303
    const val JOB_MILESTONE_LIST = 304
    const val GET_BANK_DETAILS = 305
    const val ADD_BANK_DETAILS = 306
    const val REMOVE_BANK_DETAILS = 307
    const val MARK_JOB_COMPLETE = 308
    const val TRADIE_JOB_CANCEL = 309
    const val BUILDER_JOB_CANCEL = 310
    const val TRADIE_BASIC_PROFILE = 310
    const val TRADIE_PROFILE_IMG = 311
    const val TRADIE_PROFILE_PUBLIC = 312
    const val TRADIE_EDIT_PROFILE = 313
    const val ADD_PORTFOLIO = 314
    const val DELETE_PORTFOLIO = 315
    const val EDIT_PORTFOLIO = 316
    const val CHANGE_PASSWORD = 317
    const val CHANGE_EMAIL = 318
    const val CANCELLATION_REPLY = 319
    const val CHANGEREQUEST = 222
    const val ACTIVE_TRADIE_JOBS = 37
    const val NEW_APPLICANT = 38
    const val NEW_APPLICANT_LIST = 39
    const val TRADIE_PROFILE = 40
    const val ACCEPT_DECLINE_REQUEST = 41
    const val REVIEW_TRADIE = 42
    const val ADD_ANSWER = 43
    const val UPDATE_ANSWER = 44
    const val DELETE_ANSWER = 45
    const val MILESTONE_LIST = 46
    const val SAVE_TRADIE = 47
    const val CANCEL_INVITE = 48
    const val PAGE_LIST = 49
    const val JOB_LIST = 50
    const val INVITE_FOR_JOB = 51
    const val CHANGE_MILESTONE_REQUEST = 52
    const val JOB_MILESTONE_DETAILS = 53
    const val LODGE_DISPUTE = 54
    const val DECLINE_MILESTONE = 55
    const val GET_LAST_USED_CARD = 56
    const val CARD_LIST = 57
    const val ADD_CARD = 58
    const val CREATE_CLIENT_SECRET = 96
    const val SAVE_STRIPE_BANK_TRANSACTION = 99
    const val UPDATE_CARD = 59
    const val DELETE_CARD = 60
    const val PAY = 61
    const val INIT_BASIC_PROFILE = 62
    const val BASIC_PROFILE = 63
    const val BASIC_PROFILE_IMG = 64
    const val PAST_JOBS = 304
    const val BUILDER_REVIEW = 305
    const val BUILDER_PROFILE = 306
    const val BUILDER_EDIT_PROFILE = 66
    const val NEED_APPROVAL = 67
    const val GET_SAVED_TRADE_LIST = 68

    const val BUILDER_EDIT_BASIC_PROFILE = 65
    const val TRADIE_ADD_QUESTION = 67
    const val TRADIE_UPDATE_QUESTION = 68
    const val TRADIE_DELETE_QUESTION = 69
    const val ADD_VOUCH = 70
    const val ADD_REVIEW_REPLY = 71
    const val UPDATE_REVIEW_REPLY = 72
    const val DELETE_REVIEW_REPLY = 73
    const val CREATE_REPUBLISH_POST = 74
    const val REVIEW_LIST = 75
    const val JOB_REPULISH_DETAILS = 76
    const val JOB_REPUBLISH_EDIT_JOB = 157
    const val REVIEW_UPDATE_TRADIE = 77
    const val REVIEW_REMOVE_TRADIE = 78
    const val TEMPLATE_DELETE_MILESTONE_LIST = 79
    const val TERM_N_CONDITON = 80
    const val PRIVACY_POLICY = 81
    const val EDIT_MILESTONE_REQUEST = 82
    const val PUT_SETTING = 83
    const val GET_SETTING = 84
    const val MY_REVENUE_REQUEST_DETAILS = 85

    const val SAVE_JOB_REQUEST = 101
    const val NOTIFICATIONS = 86
    const val GET_QUOTE = 87
    const val OPEN_JOB_DETAILS = 88
    const val REMOVE_JOB_DETAILS = 89
    const val CLOSE_OPEN_JOB = 90
    const val CLOSE_QUOTE_JOB = 91
    const val MY_REVENUE_REQUEST = 102
    const val TRADIE_PUT_SETTING = 103
    const val TRADIE_GET_SETTING = 104
    const val ADD_ITEM = 105
    const val DEVICE_TOKEN = 106
    const val FIREBASE_MESSAGE = 107
    const val READ_NOTIFICATIONS = 108
    const val UPDATE_ITEM = 109
    const val ADD_QUOTE = 110
    const val DELETE_ITEM = 111

}

object ValidationsConstants {
    const val EMAIL_EMPTY = 1
    const val EMAIL_INVALID = 2
    const val PASSWORD_EMPTY = 3
    const val PASSWORD_INVALID = 4
    const val OTP_EMPTY = 5
    const val OTP_INVALID = 6
    const val CONFIRM_PASSWORD_EMPTY = 7
    const val CONFIRM_PASSWORD_INVALID = 8
    const val CONTACT_EMPTY = 9
    const val CONTACT_INVALID = 10
    const val GET_TRADE = 11
    const val FIRST_NAME_EMPTY = 12
    const val LAST_NAME_EMPTY = 13
    const val EMPTY_RELATIONSHIP = 14
    const val FULL_NAME_EMPTY = 15
    const val VALIDATE_SUCCESS = 16
    const val CHECK_TERMS_CONDITION = 17
    const val COMPANY_EMPTY = 18
    const val POSITION_EMPTY = 19
    const val ABN_EMPTY = 20
    const val ABN_INVALID = 21
    const val FILE_NOT_EXIST = 22
    const val ACCOUNT_NAME_EMPTY = 300
    const val ACCOUNT_NUMBER_EMPTY = 301
    const val ACCOUNT_NUMBER_LENGTH = 302
    const val BSB_NUMBER_LENGTH = 303
    const val BSB_EMPTY = 304
}

object IntentConstants {
    const val KEY_ONBOARDING_POSITION = "key_onboarding_position"
    const val TOKEN = "token"
    const val FROM = "from"
    const val FORGOT_PASSWORD = 1
    const val REGISTER_USER = 2
    const val ACCOUNT_BLOCKED = 3
    const val PROFILE_PIC_FIRST_STEP = 4
    const val PROFILE_PIC_SECOND_STEP = 5
    const val DATA = "data"
    const val TRADE_LIST = "trade_list"
    const val SPEC_LIST = "spec_list"
    const val QUAL_LIST = "qual_list"
    const val TYPE = "type"
    const val OTP = "OTP"
    const val DEEPLINK = "DEEPLINK"
    const val IS_EDIT = "is_edit"
    const val BUNDLE = "bundle"
    const val MOBILE_NUMBER = "mobileNumber"
    const val EMAIL = "email"
    const val FIRST_NAME = "firstName"
    const val PASSWORD = "password"
    const val LINKEDINCLICK = "onclickbutton"

    const val JOB_ID = "job_id"
    const val MILESTONE_ID = "milestone_id"
    const val JOB_NAME = "job_name"
    const val IS_JOB_COMPLETED = "is_job_completed"
    const val JOB_COUNT = "job_count"
    const val CATEGORY = "category"
    const val MILESTONE_NUMBER = "milestone_number"
    const val TRADIE_JOB_MILESTONES = "tradie_job_milestones"
    const val AMOUNT = "amount"
    const val PAY_TYPE = "pay_type"
    const val DESCRIPTION = "desc"
    const val MILESTONE_COUNT = "milestone_count"
    const val IMAGES = "images"
    const val Per_hour = "Per hour"
    const val Fixed_price = "Fixed price"
    const val PAST_JOBS = "past_jobs"
    const val IS_LAST_JOB = "is_last_job"

}

object UserType {
    const val TRAIDIE = 1
    const val BUILDER = 2
}

object SocialType {
    const val GOOGLE = 1
    const val LINKEDIN = 2
    const val NORMAL = 3
}


object ProfileSteps {
    const val STEP_DASHBOARD = 1
    const val PIC_WELCOME_SCREEN = 2
    const val CREATE_ACCOUNT = 3
    const val PHONE_NUMBER = 4
    const val CREATE_PASSWORD = 5
    const val SAVE_TRADE = 6
    const val SAVE_SPECIALIZATION = 7
    const val ADD_QUALIFICATION = 8
    const val ADD_DOC = 9
    const val ADD_ABN = 10
}

object ApiParams {
    const val EMAIL = "email"
    const val NAME = "name"
    const val SOCIAL_ID = "socialId"
    const val ACCOUNT_TYPE = "accountType"
    const val LOGIN_ID = "loginId"
    const val LOGIN_TYPE = "loginType"
    const val PASSWORD = "password"
    const val CONFIRM_PASSWORD = "confirmPassword"
    const val OTP = "otp"
    const val DEVICE_TOKEN = "deviceToken"
    const val DEVICE_TYPE = "deviceType"
    const val ANDROID = "ANDROID"
    const val TOKEN = "token"
    const val PASSWORD_RESET_TOKEN = "passwordResetToken"
    const val USER_ID = "userId"
    const val ORDER_BY = "order_by"
    const val PAGE = "page"
    const val MARK_READ = "markRead"
    const val DEVICE_ID = "deviceId"
    const val LIMIT = "limit"
    const val TYPE = "type"
    const val MOBILE_NUMBER = "mobileNumber"
    const val USER_TYPE = "user_type"
    const val LAT = "lat";
    const val LNG = "long"
    const val SEARCH_TEXT = "search_text"
    const val JOB_ID = "jobId"
    const val IS_RESP_JOB = "isRespJob"
    const val TRADE_ID = "tradeId"
    const val SPECIALIZATION_ID = "specializationId"
    const val BUILDER_ID = "builderId"
    const val IS_SAVE = "isSave"
    const val TEMPL_ID = "tempId"
    const val TRADIE_ID = "tradieId"
    const val PORTFOLIO_ID = "portfolioId"
    const val STATUS = "status"
    const val QUESTION_ID = "questionId"
    const val ANSWER_ID = "answerId"
    const val INVITATION_Id = "invitationId"
    const val MILESTONE_ID = "milestoneId"
    const val REVIEW_ID = "reviewId"
    const val REPLY_ID = "replyId"
    const val SORT = "sort"
}

object DeviceConstants {
    val TIMEZONE = "timezone"
    val TYPE = "type"
    val DEVICE_DETAILS = "deviceDetails"
    var DEVICE_TYPE = "type"
    var DEVICE_TOKEN = "devicetoken"
    var DEVICE_ID = "deviceid"
    var PLATFORM = "platform"
    var OFFSET = "offset"
}

object ApiEndPoints {
    const val POST_BUILDER_REVIEW = "job/tradie/reviewBuilder"
    const val CHECK_MOBILE_NUMBER = "auth/checkMobileNumber"
    const val RESENDEMAILOTP = "auth/resendEmailOtp"
    const val VERIFY_OTP = "auth/verifyOTP"
    const val VERIFY_MOBILE_OTP = "auth/verifyMobileOtp"
    const val CREATE_PASSWORD = "auth/createPassword"
    const val TRADES_LISTING = "auth/tradeList"
    const val JOB_TYPE_LISTING = "auth/jobTypeList"
    const val CHECK_EMAIL = "auth/checkEmailId"
    const val REGISTER_USER = "auth/signup"
    const val LOGIN = "auth/login"
    const val PRIVACY = "auth/privacyPolicy"
    const val TERMS_CONDITION = "auth/tnc"
    const val CHECK_SOCIAL_ID = "auth/checkSocialId"
    const val CHECK_SOCIAL_AUTH = "auth/socialAuth"
    const val FORGOT_PASSWORD = "auth/forgot_password"
    const val ADD_DEVICE_TOKEN = "auth/addDeviceToken"
    const val LOGOUT = "auth/logout"
    const val HOME = "home"
    const val UPLOAD = "upload"
    const val SEARCH = "home/search"
    const val UNREAD_NOTIFICATIONS = "home/unReadNotification"
    const val JOB_DETAILS = "home/jobDetails"
    const val SEARCH_DATA = "admin/getSearchData"
    const val RECENT_SEARCH_DATA = "admin/getRecentSearch"
    const val SAVE_JOB = "home/saveJob"
    const val APPLY = "home/apply"
    const val CREATE_TEMPLATE = "job/createTemplate"
    const val LIST_TEMPLATE = "profile/builder/templatesList"
    const val TEMP_MILESTONE_LIST = "job/tempMilestoneList"
    const val DELETE_TEMPLATE = "profile/builder/deleteTemplate"
    const val CREATE_POST = "job/create"
    const val CREATE_REPUBLISH_POST = "job/builder/publishJobAgain"
    const val CREATE_UPDATE_JOB = "job/update"
    const val ACTIVE_JOB_LIST = "job/tradie/activeJobList"
    const val APPLIED_JOB_LIST = "job/tradie/appliedJobList"
    const val NEW_JOB_LIST = "job/tradie/newJobList"
    const val PAST_JOB_LIST = "job/tradie/pastJobList"
    const val APPROVED_MILESTONE_LIST = "job/tradie/approveMilestoneList"
    const val TRADIE_JOB_DETAILS = "job/tradie/jobDetails"
    const val TRADIE_JOB_MILESTONES = "job/tradie/milestoneList"
    const val BUILDER_JOB_MILESTONES = "job/builder/milestoneList"
    const val MILESTONE_CHANGE_REQUEST = "job/builder/changeRequest"
    const val MILESTONE_DECLINE_REQUEST = "job/builder/milestoneApproveDecline"
    const val MILESTONE_EDIT_REQUEST = "job/editTemplate"

    const val TRADIE_GET_BANK_DETAILS = "profile/tradie/getBankDetails"
    const val TRADIE_ADD_BANK_DETAILS = "profile/tradie/addBankDetails"
    const val TRADIE_UPDATE_BANK_DETAILS = "profile/tradie/updateBankDetails"
    const val TRADIE_REMOVE_BANK_DETAILS = "profile/tradie/removeBankDetails"
    const val TRADIE_MARK_COMPLETE = "job/tradie/markComplete"
    const val TRADIE_CANCEL_JOB = "job/tradie/cancelJob"
    const val TRADIE_INITIAL_PROFILE_INFO = "profile/tradie/"
    const val TRADIE_PROFILE_IMAGE = "profile/tradie/userImage"
    const val TRADIE_PUBLIC_PROFILE = "profile/tradie/view"
    const val TRADIE_EDIT_PROFILE = "profile/tradie/editProfile"
    const val TRADIE_EDIT_BASIC_PROFILE = "profile/tradie/editBasicDetails"
    const val TRADIE_ADD_PORTFOLIO = "profile/tradie/addPortfolio"
    const val TRADIE_DELETE_PORTFOLIO = "profile/tradie/deletePortfolio"
    const val TRADIE_EDIT_PORTFOLIO = "profile/tradie/editPortfolio"
    const val TRADIE_GET_BASIC_DETAILS = "profile/tradie/getBasicDetails"
    const val TRADIE_CHANGE_PASSWORD = "profile/tradie/changePassword"
    const val TRADIE_CHANGE_EMAIL = "profile/tradie/changeEmail"
    const val TRADIE_VERIFY_EMAIL = "profile/tradie/verifyEmail"


    const val TRADIE_QUESTION_LIST = "job/tradie/questionList"
    const val BUILDER_QUESTION_LIST = "job/builder/questionList"
    const val BUILDER_CANCEL_JOB = "job/builder/cancelJob"
    const val TRADIE_BUILDER_PROFILE = "job/tradie/builderProfile"
    const val TRADIE_LODGE_DISPUTE = "job/tradie/lodgeDispute"
    const val EDIT_BASIC_BUILDER_PROFILE = "profile/builder/editBasicDetails"
    const val EDIT_BUILDER_PROFILE = "profile/builder/editProfile"
    const val TRADIE_ASK_QUESTION = "job/tradie/askQuestion"
    const val TRADIE_UPDATE_QUESTION = "job/tradie/updateQuestion"
    const val TRADIE_DELETE_QUESTION = "job/tradie/deleteQuestion"

    const val SAVE_TRADIE = "job/builder/saveTradie"

    const val CANCEL_INVITE = "job/builder/cancelInviteForJob"

    const val ACTIVE_BUILDER_JOB_LIST = "job/builder/activeJobList"
    const val OPEN_BUILDER_JOB_LIST = "job/builder/openJobList"
    const val JOB_DETAILS_BUILDER = "job/builder/jobDetails"
    const val NEW_APPLICANT = "job/builder/newApplicants"
    const val NEW_APPLICANT_LIST = "job/builder/newJobApplicationList"
    const val TRADIE_PROFILE = "job/builder/tradieProfile"
    const val TRADIE_PROFILE_HOME = "home/tradieProfile"
    const val ACCEPT_DECLINE_REQUEST = "job/builder/acceptDeclineRequest"
    const val PAST_BUILDER_JOB_LIST = "job/builder/pastJobList"
    const val REVIEW_TRADIE = "job/builder/reviewTradie"
    const val REVIEW_UPDATE_TRADIE = "job/builder/updateReviewTradie"
    const val REVIEW_DELETE_TRADIE = "job/builder/removeReviewTradie"

    //    const val ANSWER_QUESTION = "job/builder/answerQuestion"
    const val ANSWER_QUESTION = "job/builder/answer"
    const val TRADIE_ANSWER_QUESTION = "job/tradie/answer"
    const val UPDATE_QUESTION = "job/builder/updateAnswer"
    const val TRADIE_UPDATE_ANSWER = "job/tradie/updateAnswer"
    const val DELETE_QUESTION = "job/builder/deleteAnswer"
    const val DELETE_ANSWER_TRADIE = "job/tradie/deleteAnswer"
    const val MILSTONE_LIST = "job/builder/milestoneList"
    const val JOB_LIST = "job/builder/chooseJob"
    const val VOUCH_JOB_LIST = "job/builder/vouchesJob"
    const val INVITE_FOR_JOB = "job/builder/inviteForJob"
    const val MILESTONE_DETAILS = "job/builder/milestoneDetails"
    const val TRADIE_LODGE_DISPUTE_BUILDER = "job/builder/lodgeDispute"
    const val CREATE_CLIENT_SECRET_KEY = "payment/createClientSecretKey"
    const val SAVE_STRIPE_BANK_TRANSACTION = "payment/saveTransaction"
    const val LAST_USED_CARD = "payment/builder/lastUsedCard"
    const val CARD_LIST = "payment/builder/cardList"
    const val ADD_CARD = "payment/builder/addNewCard"
    const val EDIT_CARD = "payment/builder/updateCard"
    const val DELETE_CARD = "payment/builder/deleteCard"
    const val PAY = "job/builder/milestoneApproveDecline"
    const val INITIAL_PROFILE_INFO = "profile/builder"
    const val GET_BASIC_DETAILS = "profile/builder/view"
    const val PROFILE_IMAGE = "profile/builder/userImage"
    const val GET_BASIC_BUILDER_DETAILS = "profile/builder/getBasicDetails"
    const val NEED_APPROVAL = "job/builder/needApproval"
    const val JOB_DETAILS_REPUBLISH_BUILDER = "job/republishJob"
    const val JOB_DETAILS_OPEN_JOB = "job/getOpenJobDetails"
    const val REMOVE_JOB_DETAILS = "job/remove"
    const val CLOSE_QUOTE_JOB = "job/closeQuoteJob"
    const val CLOSE_OPEN_JOB = "job/cancelOpenJob"
    const val TRADIE_REPLY_CHANGE_REQUEST = "job/tradie/replyChangeRequest"
    const val TRADIE_REPLY_CANCELLATION_REQUEST = "job/tradie/replyCancellation"
    const val GET_SAVED_TRADIE = "profile/builder/getSavedTradies"
    const val BUILDER_REPLY_CANCELLATION_REQUEST = "job/builder/replyCancellation"
    const val ADD_VOUCHER = "job/builder/addVoucher"
    const val ANSWER_REVIEW_REPLY = "job/builder/reviewReply"
    const val UPDATE_REVIEW_REPLY = "job/builder/updateReviewReply"
    const val DELETE_REVIEW_REPLY = "job/builder/removeReviewReply"
    const val REVIEW_LIST = "job/builder/reviewList"
    const val SETTING = "profile/builder/settings"
    const val GET_SETTING = "profile/builder/getSettingsData"
    const val SAVED_JOB_LIST = "profile/tradie/getSavedJobs"
    const val TRADIE_SETTING = "profile/tradie/settings"
    const val TRADIE_GET_SETTING = "profile/tradie/getSettingsData"
    const val MY_REVENUE_LIST = "profile/tradie/myRevenue"
    const val BUILDEE_REVENUE_LIST = "profile/builder/myRevenue"
    const val BUILDEE_REVENUE_DETAILS = "profile/builder/revenueDetail"
    const val NOTIFICATIONS = "home/notification"
    const val QUOTE_BY_JOB = "quote/quoteByJobId"
    const val ADD_QUOTE = "quote/addQuote"
    const val ADD_ITEM = "quote/addItem"
    const val EDIT_ITEM = "quote/updateItem"
    const val DELETE_ITEM = "quote/deleteItem"
    const val SEND_PUSH_NOTIFICATION = "https://fcm.googleapis.com/fcm/send"
}

object DropBoxConstants{
    const val ACCESS_TOKEN_PERSONAL: String =
        "sl.BC85ImvfxCGx_Kv4SPpP_WTKhSIHlNHDtyGkYsy3ly66B-Q4IPXa1H8pFyInYvQKKHS7B76prBf0dWZM48sDjH74fww7oF83lS7VkD2f-Q6HWR0A8m0BV0RR0O3tN6LeI5ZwOp0"



    const val DROPBOX_APP_KEY_PERSONAL = "klrulw5bqvnusb0"
    const val DROPBOX_APP_SECRET_PERSONAL = "tf750yq7zg6lf95"

   /* const val DROPBOX_APP_KEY = "373j3wfbv1cs1q8"
    const val DROPBOX_APP_SECRET = "2cnreteqyodz392"*/

//    val ACCESS_TYPE: Accesstype = AccessType.DROPBOX
}


object FireStore {
    object FireStoreConstPassword {
        const val DEFAULT_PASS = "12345678"
    }

    object NODE {
        const val USERS = "users"
        const val USER_LISTING = "userListing"
    }

    object FIELD {
        const val EVENTS = "events"
    }

    object REQUEST_IDS

}


object MediaType{
    const val IMAGE=1
    const val VIDEO=2
    const val DOC=3
    const val PDF=4
}
