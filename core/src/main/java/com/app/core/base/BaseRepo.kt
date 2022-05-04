package com.app.core.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.core.R
import com.app.core.basehandler.GenericApiRequest
import com.app.core.model.usermodel.UserDataModel
import com.app.core.preferences.PreferenceManager
import com.app.core.retrofit.RetrofitManager
import com.app.core.util.ApiCodes
import com.app.core.util.CoreContextWrapper

/**
 * Base class for all repos using in App
 *
 */
open class BaseRepo : GenericApiRequest<Any>() {
    protected val unauthenticatedApiClient = RetrofitManager.getRetroInstance().getService()
    protected val socialLoginClient = RetrofitManager.getRetroInstance().getService()


    fun updateProfileStep(stepCount: Int) =
        PreferenceManager.putInt(PreferenceManager.PROFILE_STEP, stepCount)

    fun getProfileStep() = PreferenceManager.getInt(PreferenceManager.PROFILE_STEP)
    fun getName() = "${PreferenceManager.getString(PreferenceManager.FIRST_NAME)} ${
        PreferenceManager.getString(PreferenceManager.LAST_NAME)
    }"

    fun getFirstName() = "${PreferenceManager.getString(PreferenceManager.FIRST_NAME)}"
    fun getLastName() = "${PreferenceManager.getString(PreferenceManager.LAST_NAME)}"
    fun updateConfirmationOfPIC(isConfirmed: Boolean) =
        PreferenceManager.putBoolean(PreferenceManager.IS_CONFIRMED_BY_PIC, isConfirmed)

    fun isConfirmedByPIC() = PreferenceManager.getBoolean(PreferenceManager.IS_CONFIRMED_BY_PIC)

    fun clearAllPreference() = PreferenceManager.clearAllPrefs()





    suspend fun saveUserData(userDataModel: UserDataModel?) {
        userDataModel?.let {
            it.firstName?.let {
                PreferenceManager.putString(PreferenceManager.NAME, it)
            }
            it.lastName?.let {
                PreferenceManager.putString(PreferenceManager.LAST_NAME, it)
            }
            it.firstName?.let {
                PreferenceManager.putString(PreferenceManager.FIRST_NAME, it)
            }
            it.accessToken?.let {
                PreferenceManager.putString(PreferenceManager.AUTH_TOKEN, it)
            }
            it.email?.let {
                PreferenceManager.putString(PreferenceManager.EMAIL, it)
            }
            it.type?.let {
                PreferenceManager.putString(PreferenceManager.USER_TYPE, it)
            }
            it.image?.let {
                PreferenceManager.putString(PreferenceManager.PROFILE_IMAGE, it)
            }
            it.personInCareImage?.let {
                PreferenceManager.putString(PreferenceManager.PROFILE_IMAGE_PERSON_IN_CARE, it)
            }
            it.phoneNo?.let {
                PreferenceManager.putString(PreferenceManager.PHONE_NUMBER, it)
            }
            it._id?.let {
                PreferenceManager.putString(PreferenceManager.USER_ID, it)
            }

            it.familyRelationWithPic?.let {
                PreferenceManager.putString(PreferenceManager.RELATION, it)
            }
            it.picRelationWithFamily?.let {
                PreferenceManager.putString(PreferenceManager.MY_PIC_RELATION, it)
            }

//            it.picFirstName?.let {
            PreferenceManager.putString(
                PreferenceManager.PERSON_IN_CARE_NAME,
                "${it.picFirstName ?: ""}${if (it.picLastName.isNullOrBlank()) "" else " "}${it.picLastName ?: ""}"
            )
            it.picFirstName?.let {
                PreferenceManager.putString(PreferenceManager.PIC_FIRST_NAME, it)
            }
            it.picLastName?.let {
                PreferenceManager.putString(PreferenceManager.PIC_LAST_NAME, it)
            }

//            }

        }

    }



    fun getUserId() = PreferenceManager.getString(PreferenceManager.USER_ID)
    fun getFamilyProfileImage() = PreferenceManager.getString(PreferenceManager.PROFILE_IMAGE)
    fun getRelation() = PreferenceManager.getString(PreferenceManager.RELATION)
    fun getMyPICRelation() = PreferenceManager.getString(PreferenceManager.MY_PIC_RELATION)
    fun getPICFirstName() = PreferenceManager.getString(PreferenceManager.PIC_FIRST_NAME)
    fun getPICLastName() = PreferenceManager.getString(PreferenceManager.PIC_LAST_NAME)
    fun getPICProfileImage() =
        PreferenceManager.getString(PreferenceManager.PROFILE_IMAGE_PERSON_IN_CARE)

    /*fun getDeviceOwnerType() = PreferenceManager.getString(PreferenceManager.DEVICE_BELONGS_TO)
        ?: UserType.FAMILY_MEMBER_PROFILE*/

    fun updateDeviceOwnerType(userType: String) =
        PreferenceManager.putString(PreferenceManager.DEVICE_BELONGS_TO, userType)

    fun getPersonInCareName() =
        PreferenceManager.getString(PreferenceManager.PERSON_IN_CARE_NAME)

    /*fun getUserType() =
        if (PreferenceManager.getString(PreferenceManager.USER_TYPE).isNullOrBlank())
            UserType.FAMILY_MEMBER_PROFILE
        else
            PreferenceManager.getString(PreferenceManager.USER_TYPE)*/


    suspend fun doLogout() =
        apiRequest(ApiCodes.LOGOUT) {
            unauthenticatedApiClient.doLogout()
        }






    fun setPicEmail(email: String) = PreferenceManager.putString(PreferenceManager.PIC_EMAIL, email)
    fun setPicPassword(password: String) =
        PreferenceManager.putString(PreferenceManager.PIC_PASSWORD, password)

    fun getPicEmail() = PreferenceManager.getString(PreferenceManager.PIC_EMAIL) ?: ""
    fun getPicPassword() = PreferenceManager.getString(PreferenceManager.PIC_PASSWORD) ?: ""


}
