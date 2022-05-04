package com.example.ticktapp.mvvm.viewmodel

import androidx.databinding.ObservableField
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.VerifyEmailModel
import com.app.core.model.loginmodel.LoginRequest
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.preferences.PreferenceManager
import com.app.core.util.IntentConstants
import com.app.core.util.ValidationsConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.model.registration.TokenModel
import com.example.ticktapp.mvvm.repo.LoginRepo
import com.example.ticktapp.validation.DataValidation
import com.google.gson.Gson

class LoginViewModel : BaseViewModel() {
    var mEmailEditText = ObservableField<String>()
    var mPasswordEditText = ObservableField<String>()
    public var registrationModel: TokenModel? = null
    private var verifyEmailModel: VerifyEmailModel? = null
    private lateinit var mResetToken: String
    private val mRepo by lazy { LoginRepo() }

    /**
     * Api call for login activity
     *
     * @param deviceToken FCM token
     */
    fun hitLoginApi(token: String, user_type: String) {
        if (mEmailEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_email_address
                ),
                ValidationsConstants.EMAIL_EMPTY
            )
        }
//        else if (!CoreUtils.isEmailValid(mEmailEditText.get() ?: "")) {
//            mValidationLiveData.value = DataValidation(
//                ApplicationClass.applicationContext().getString(
//                    R.string.email_is_not_valid
//                ),
//                ValidationsConstants.EMAIL_INVALID
//            )
//        }
        else if (mPasswordEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_password
                ),
                ValidationsConstants.PASSWORD_EMPTY
            )
        } /*else if (!CoreUtils.isPasswordValid(mPasswordEditText.get() ?: "")) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.password_invalid
                ),
                ValidationsConstants.PASSWORD_INVALID
            )
        }*/ else {
            CoroutinesBase.main {
                setLoadingState(LoadingState.LOADING())
                val onBoardingData = LoginRequest(
                    deviceToken = PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN),
                    email = mEmailEditText.get().toString(),
                    password = mPasswordEditText.get().toString(),
                    user_type = user_type
                )
                val resp = mRepo.hitLogin(onBoardingData)
                updateView(
                    resp
                ) {
                    when (it) {
                        is API_VIEWMODEL_DATA.API_SUCCEED -> {
                            val response =
                                Gson().fromJson(Gson().toJson(it.data), TokenModel::class.java)
                            this.registrationModel = response

                            if (this.registrationModel != null &&
                                this.registrationModel!!.deviceToken != null
                            ) {
                                if (response.user_type != null) {
                                    PreferenceManager.putString(
                                        PreferenceManager.USER_TYPE.toString(),
                                        response.user_type.toString()
                                    )
                                }
                                if (this.registrationModel!!.deviceToken != null) {
                                    PreferenceManager.putString(
                                        IntentConstants.TOKEN,
                                        this.registrationModel!!.deviceToken
                                    )
                                }
                                if (this.registrationModel!!.trade != null && this.registrationModel!!.trade?.size!! > 0) {
                                    PreferenceManager.putString(
                                        PreferenceManager.TRADE_ID,
                                        this.registrationModel!!.trade?.get(0).toString()
                                    )
                                }
                                if (this.registrationModel!!._id != null) {
                                    PreferenceManager.putString(
                                        PreferenceManager.USER_ID,
                                        this.registrationModel!!._id
                                    )
                                }
                                if (this.registrationModel!!.email != null) {
                                    PreferenceManager.putString(
                                        PreferenceManager.EMAIL,
                                        this.registrationModel!!.email
                                    )
                                }

                            }
                        }
                    }
                }
                setLoadingState(LoadingState.LOADED())
            }
        }
    }


    fun checkSocialId(socialId: String, email: String, userType: String) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.hitCheckSocialId(socialId, email, userType)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response =
                            Gson().fromJson(Gson().toJson(it.data), VerifyEmailModel::class.java)
                        this.verifyEmailModel = response
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }


    fun socialAuth(onBoardingData: OnBoardingData) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.hitSignUp(onBoardingData)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response =
                            Gson().fromJson(Gson().toJson(it.data), TokenModel::class.java)
                        this.registrationModel = response
                        if (this.registrationModel != null &&
                            this.registrationModel!!.deviceToken != null
                        ) {
                            if (response.user_type != null) {
                                PreferenceManager.putString(
                                    PreferenceManager.USER_TYPE.toString(),
                                    response.user_type.toString()
                                )
                            }
                            if (this.registrationModel!!.deviceToken != null) {
                                PreferenceManager.putString(
                                    IntentConstants.TOKEN,
                                    this.registrationModel!!.deviceToken
                                )
                            }
                            if (this.registrationModel!!.trade != null && this.registrationModel!!.trade?.size!! > 0) {
                                PreferenceManager.putString(
                                    PreferenceManager.TRADE_ID,
                                    this.registrationModel!!.trade?.get(0).toString()
                                )
                            }
                            if (this.registrationModel!!._id != null) {
                                PreferenceManager.putString(
                                    PreferenceManager.USER_ID,
                                    this.registrationModel!!._id
                                )
                            }
                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }


    fun getVerifyEmailData() = verifyEmailModel


    fun getToken() = mResetToken


}