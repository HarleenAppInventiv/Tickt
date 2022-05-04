package com.example.ticktapp.mvvm.viewmodel

import android.provider.Settings
import androidx.databinding.ObservableField
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.VerifyEmailModel
import com.app.core.model.requestsmodel.CreatePasswordRequest
import com.app.core.model.requestsmodel.DeviceToken
import com.app.core.preferences.PreferenceManager
import com.app.core.util.Constants
import com.app.core.util.CoreContextWrapper.getContext
import com.app.core.util.ValidationsConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.model.registration.RegistrationModel
import com.example.ticktapp.mvvm.repo.PhoneNumberRepo
import com.example.ticktapp.validation.DataValidation
import com.google.gson.Gson

class PhoneNumberViewModel : BaseViewModel() {
    var mPhoneEditText = ObservableField<String>()
    var countryCode = ObservableField<String>()
    var fromWhere: Int = 0
    private var verifyPhoneModel: VerifyEmailModel? = null
    private var commonResponse: RegistrationModel? = null
    private lateinit var mResetToken: String

    private val mRepo by lazy {
        PhoneNumberRepo()
    }


    /**
     * Api call to check mobile number
     */
    fun checkMobileNumber() {
        if (mPhoneEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_phone_num
                ),
                ValidationsConstants.CONTACT_EMPTY
            )
        } else if ((mPhoneEditText.get() as String).length != 11) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.message_phone_number_should_have_min_six_digits
                ),
                ValidationsConstants.CONTACT_INVALID
            )
        } else {
            if (fromWhere == Constants.FORGOT_PASSWORD) {
                val forgotPassword = CreatePasswordRequest(
                    user_type = PreferenceManager.getString(PreferenceManager.USER_TYPE)?.toInt(),
                    mobileNumber = mPhoneEditText.get().toString().replace(" ", "")
                )
                CoroutinesBase.main {
                    setLoadingState(LoadingState.LOADING())
                    val resp = mRepo.hitForgotPAssword(forgotPassword)
                    updateView(resp) {
                        when (it) {
                            is API_VIEWMODEL_DATA.API_SUCCEED -> {
                                val response = Gson().fromJson(
                                    Gson().toJson(it.data),
                                    RegistrationModel::class.java
                                )
                                this.commonResponse = response
                            }
                        }
                    }
                    setLoadingState(LoadingState.LOADED())
                }
            } else if (fromWhere == Constants.REGISTER) {
                CoroutinesBase.main {
                    setLoadingState(LoadingState.LOADING())
                    val resp =
                        mRepo.hitCheckPhoneNumber(mPhoneEditText.get().toString().replace(" ", ""))
                    updateView(resp) {
                        when (it) {
                            is API_VIEWMODEL_DATA.API_SUCCEED -> {
                                val response = Gson().fromJson(
                                    Gson().toJson(it.data),
                                    VerifyEmailModel::class.java
                                )
                                this.verifyPhoneModel = response
                            }
                        }
                    }
                    setLoadingState(LoadingState.LOADED())
                }
            }

        }
    }


    fun addDeviceToken() {
        val deviceToken = DeviceToken(
            Settings.Secure.getString(
                getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
            ),
            PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN), "3"
        )
        CoroutinesBase.main {
            val resp = mRepo.addDeviceToken(deviceToken)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                    }
                }
            }
        }
    }

    fun getVerifyPhoneData() = verifyPhoneModel
    fun getForgotPassword() = commonResponse


    fun getFrom(from: Int) {
        fromWhere = from
    }

    fun getToken() = mResetToken
}