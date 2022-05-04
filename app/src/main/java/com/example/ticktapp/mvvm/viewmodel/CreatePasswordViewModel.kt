package com.example.ticktapp.mvvm.viewmodel

import CoreUtils
import androidx.databinding.ObservableField
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.requestsmodel.CreatePasswordRequest
import com.app.core.model.usermodel.UserDataModel
import com.app.core.util.ValidationsConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.mvvm.repo.CreatePasswordRepo
import com.example.ticktapp.validation.DataValidation
import com.google.gson.Gson

class CreatePasswordViewModel : BaseViewModel() {
    var mPasswordEditText = ObservableField<String>()
    private val mRepo by lazy { CreatePasswordRepo() }

    fun hitCreatePasswordApi() {
        if (mPasswordEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_password
                ),
                ValidationsConstants.PASSWORD_EMPTY
            )
        } else if (!CoreUtils.isPasswordValid(mPasswordEditText.get() ?: "")) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.password_invalid
                ),
                ValidationsConstants.PASSWORD_INVALID
            )
        } else {
            mValidationLiveData.value = DataValidation(
                "",
                ValidationsConstants.VALIDATE_SUCCESS
            )
        }
        /*else {
                 val createPassword = CreatePasswordRequest(
                     mobileNumber =mobileNumber,
                     password = mPasswordEditText.get().toString())
                 CoroutinesBase.main {
                     setLoadingState(LoadingState.LOADING())
                     val resp = mRepo.hitCreatePassword(createPassword)
                     updateView(
                         resp
                     ) {
                         when (it) {
                             is API_VIEWMODEL_DATA.API_SUCCEED -> {
                                 val response = Gson().fromJson(Gson().toJson(it.data), UserDataModel::class.java)
                             }
                         }
                     }
                     setLoadingState(LoadingState.LOADED())
                 }

            }*/
    }

    fun checkMobileNumber(mobileNumber: String) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.hitCheckPhone(mobileNumber)
            updateView(resp)
            {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response =
                            Gson().fromJson(Gson().toJson(it.data), UserDataModel::class.java)
                        if (response.phoneNo == mobileNumber) {

                        }
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }


    fun hitResetPassword(email: String) {
        val createPassword = CreatePasswordRequest(
            email = email,
            password = mPasswordEditText.get().toString()
        )
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.hitCreatePassword(createPassword)
            updateView(
                resp
            ) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response =
                            Gson().fromJson(Gson().toJson(it.data), UserDataModel::class.java)

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }


}