package com.app.core.preferences

import android.content.SharedPreferences
import com.app.core.util.CoreContextWrapper

object PreferenceManager {


    val MY_PIC_RELATION: String? = "my_pic_relation"
    val PIC_PASSWORD: String? = "pic_password"
    val PIC_EMAIL: String? = "pic_email"
    val IS_CONFIRMED_BY_PIC: String? = "is_confirmed_by_pic"
    public var sharedPref: SharedPreferences
    val AUTH_TOKEN: String? = "auth_token"
    val EMAIL: String? = "email"
    val APP_VERSION_CODE: String? = "app_version_code"
    val USER_TYPE: String? = "user_type"
    val DROP_BOX_TOKEN: String? = "drop_box_token"
    val SOCIAL_TYPE: String? = "social_type"
    val SOCIAL_ID: String? = "social_id"
    val LOCATION: String? = "location"
    val PREFERENCE: String? = "preference"
    val GENDER: String? = "gender"
    val NOTIFICATION_ENABLED: String? = "notification"
    val SOCIAL_LOGIN: String? = "social_login"
    val WALKTHROUGH_SCREEN: String? = "walkthrough_screen"
    val PROFILE_INFO_SETUP: String? = "profile_info_setup"
    val PROFILE_PICTURE_SETUP: String? = "profile_picture_setup"
    val CURRENT_STEP: String? = "current_step"
    val DEVICE_TOKEN: String? = "device_token"
    val NAME: String? = "name"
    val BIRTHDAY: String? = "birthday"
    val USER_ID: String? = "user_id"
    val PROFILE_IMAGE: String? = "profile_pic"
    val PROFILE_IMAGE_PERSON_IN_CARE: String? = "profile_pic_person_in_care"
    val PROFILE_STEP = "profile_step"
    val TRADE_ID: String? = "trade_id"

    val FIRST_NAME: String? = "first_name"
    val LAST_NAME: String? = "last_name"
    val PHONE_NUMBER: String? = "phone_number"
    val PERSON_IN_CARE_NAME = "person_in_care_name"
    val RELATION: String? = "relation"
    val PIC_FIRST_NAME: String? = "pic_first_name"
    val PIC_LAST_NAME: String? = "pic_last_name"
    val DEVICE_BELONGS_TO = "device_belongs_to"
    val IS_LOGIN = "is_login"
    val LAT = "lat"
    val LAN = "LAN"
    val IS_APP_GUIDE = "IS_APP_GUIDE"

    init {

        sharedPref =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(CoreContextWrapper.getContext())
    }

    /* companion object {
         @Volatile
         var INSTANCE: PreferenceManager? = null

         fun getInstance(context: Context): PreferenceManager = INSTANCE ?: synchronized(this) {
             INSTANCE ?: PreferenceManager(context)
         }
     }*/

    fun getInt(key: String?, defValue: Int = 0): Int {
        return sharedPref.getInt(key, defValue)
    }

    fun putInt(key: String?, value: Int) {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putFloat(key: String?, value: Float) {
        val editor = sharedPref.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(key: String?): Float {
        return sharedPref.getFloat(key, 0F)
    }

    fun putLong(key: String?, value: Long) {
        val editor = sharedPref.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String?): Long {
        return sharedPref.getLong(key, 0L)
    }

    fun putString(key: String?, value: String?) {
        val editor = sharedPref.edit()
//        if (value != null) {
            editor.putString(key, value) // Commit the edits!
            editor.apply()
//        }
    }

    fun getString(key: String?) = sharedPref.getString(key, "")

    fun putBoolean(key: String?, value: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String?) = sharedPref.getBoolean(key, false)


    fun isSessionAlive(): Boolean {
        val data = sharedPref.getString(AUTH_TOKEN, null)
        return !data.isNullOrEmpty()
    }

    fun isWalkThroughDone() = sharedPref.getBoolean(WALKTHROUGH_SCREEN, false)
    fun isProfileInfoSetupDone() = sharedPref.getBoolean(PROFILE_INFO_SETUP, false)
    fun isProfilePictureSetupDone() = sharedPref.getBoolean(PROFILE_PICTURE_SETUP, false)


    fun clearAllPrefs() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun clearNewJobPrefs() {
        putString(NEW_JOB_PREF.JOB_NAME, null)
        putString(NEW_JOB_PREF.CATEGORIES, null)
        putString(NEW_JOB_PREF.JOB_TYPE, null)
        putString(NEW_JOB_PREF.JOB_SPECS, null)
        putString(NEW_JOB_PREF.JOB_DATA, null)
        putString(NEW_JOB_PREF.JOB_LAT, null)
        putString(NEW_JOB_PREF.JOB_LONG, null)
        putString(NEW_JOB_PREF.JOB_LOCATION, null)
        putString(NEW_JOB_PREF.JOB_DESCRIPTION, null)
        putInt(NEW_JOB_PREF.JOB_PAY_TYPE, -1)
        putInt(NEW_JOB_PREF.JOB_BUDGET_TYPE, -1)
        putString(NEW_JOB_PREF.JOB_AMOUNT, null)
        putString(NEW_JOB_PREF.JOB_START_DATE, null)
        putString(NEW_JOB_PREF.JOB_END_DATE, null)
        putString(NEW_JOB_PREF.JOB_MILESTONE, null)
        putString(NEW_JOB_PREF.JOB_ID, null)
        putString(NEW_JOB_PREF.JOB_IMAGES, null)
        putString(NEW_JOB_PREF.JOB_IMAGES_THUMB, null)
        putString(NEW_JOB_PREF.JOB_IMAGES_UTHUMB, null)
    }

    object NEW_JOB_PREF {
        const val JOB_NAME = "job_name"
        const val CATEGORIES = "categories"
        const val JOB_TYPE = "job_type"
        const val JOB_SPECS = "job_specs"
        const val JOB_DATA = "job_data"
        const val JOB_LAT = "job_lat"
        const val JOB_LONG = "job_long"
        const val JOB_LOCATION = "job_location"
        const val JOB_DESCRIPTION = "job_description"
        const val JOB_PAY_TYPE = "job_pay_type"
        const val JOB_BUDGET_TYPE = "job_budget_type"
        const val JOB_AMOUNT = "job_amount"
        const val JOB_START_DATE = "job_start_date"
        const val JOB_END_DATE = "job_end_date"
        const val JOB_MILESTONE = "job_milestone"
        const val JOB_ID = "job_id"
        const val JOB_IMAGES = "job_images"
        const val JOB_IMAGES_THUMB = "job_images_thumb"
        const val JOB_IMAGES_UTHUMB = "job_images_Uthumb"

    }
}