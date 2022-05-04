package com.example.ticktapp.mvvm.viewmodel

import CoreUtils
import androidx.databinding.ObservableField
import com.app.core.base.RegisterUserRepo
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.usermodel.UserDataModel
import com.app.core.util.ValidationsConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.validation.DataValidation
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RegisterUserViewModel : BaseViewModel() {

    var mEmailEditText = ObservableField<String>()
    var mPasswordEditText = ObservableField<String>()
    var mConfirmPasswordEditText = ObservableField<String>()
    var mOTPEditText = ObservableField<String>()

    private val mRepo by lazy { RegisterUserRepo() }
    private var mUserModel: UserDataModel? = null

    fun hitRegisterUserApi(deviceToken: String?, token: String?) {
        if (mEmailEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_email_address
                ), ValidationsConstants.EMAIL_EMPTY
            )
        } else if (!CoreUtils.isEmailValid(mEmailEditText.get() ?: "")) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.email_is_not_valid
                ), ValidationsConstants.EMAIL_INVALID
            )
        }
        else if (mPasswordEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_password
                ),
                ValidationsConstants.PASSWORD_EMPTY
            )
        } else if (mConfirmPasswordEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_confirm_password
                ),
                ValidationsConstants.CONFIRM_PASSWORD_EMPTY
            )
        } else if (mPasswordEditText.get().isNullOrBlank() || !mPasswordEditText.get()
                .equals(mConfirmPasswordEditText.get().toString())
        ) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.password_mismatch
                ),
                ValidationsConstants.CONFIRM_PASSWORD_INVALID
            )
        } else if (!CoreUtils.isPasswordValid(mPasswordEditText.get() ?: "")) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.password_error
                ),
                ValidationsConstants.PASSWORD_INVALID
            )
        } else {

            CoroutinesBase.main {
                setLoadingState(LoadingState.LOADING())
                val resp = mRepo.hitRegisterUserApi(
                    mEmailEditText.get(),
                    mPasswordEditText.get(),
                    mConfirmPasswordEditText.get(),
                    token,
                    deviceToken
                )

                updateView(
                    resp
                ) {
                    when (it) {
                        is API_VIEWMODEL_DATA.API_SUCCEED -> {
                            val response =
                                Gson().fromJson(Gson().toJson(it.data), UserDataModel::class.java)
                            this.mUserModel = response
                        }

                    }
                }
                setLoadingState(LoadingState.LOADED())
            }

        }
    }

    fun getUserModel() = mUserModel


}