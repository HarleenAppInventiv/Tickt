package com.example.ticktapp.mvvm.viewmodel

import CoreUtils
import androidx.databinding.ObservableField
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.registrationmodel.OnBoardingData
import com.app.core.preferences.PreferenceManager
import com.app.core.repo.ABNRepo
import com.app.core.util.IntentConstants
import com.app.core.util.ValidationsConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.model.registration.TokenModel
import com.example.ticktapp.validation.DataValidation
import com.google.gson.Gson

class ABNViewModel : BaseViewModel() {
    var mCompanyEditText = ObservableField<String>()
    var mPositionEditText = ObservableField<String>()
    var mABNEditText = ObservableField<String>()
    public var registrationModel: TokenModel? = null


    private lateinit var mResetToken: String
    private val mRepo by lazy { ABNRepo() }

    /**
     * Api call for login activity
     */
    fun registerUser(onBoardingData: OnBoardingData) {
        if (mCompanyEditText.get()
                .isNullOrBlank() && PreferenceManager.getString(PreferenceManager.USER_TYPE)
                ?.toInt() == 2
        ) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_company_name
                ),
                ValidationsConstants.COMPANY_EMPTY
            )
        } else if (mPositionEditText.get().isNullOrBlank() && PreferenceManager.getString(
                PreferenceManager.USER_TYPE
            )?.toInt() == 2
        ) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_position
                ),
                ValidationsConstants.POSITION_EMPTY
            )
        } else if (mABNEditText.get().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_abn
                ),
                ValidationsConstants.ABN_EMPTY
            )
        } else if (mABNEditText.get().toString().replace(" ", "").length != 11) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.abn_invalid_11
                ),
                ValidationsConstants.ABN_INVALID
            )
        } else if (!CoreUtils.validABN(mABNEditText.get() ?: " ") && mABNEditText.get().toString()
                .replace(" ", "").length != 11
        ) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.abn_invalid
                ),
                ValidationsConstants.ABN_INVALID
            )
        } else {
            CoroutinesBase.main {
                setLoadingState(LoadingState.LOADING())
                val resp = mRepo.hitSignUp(onBoardingData)
                updateView(
                    resp
                ) {
                    when (it) {
                        is API_VIEWMODEL_DATA.API_SUCCEED -> {
                            val response =
                                Gson().fromJson(
                                    Gson().toJson(it.data),
                                    TokenModel::class.java
                                )
                            this.registrationModel = response
                            if (response.user_type != null) {
                                PreferenceManager.putString(
                                    PreferenceManager.USER_TYPE.toString(),
                                    response.user_type.toString()
                                )
                            }
                            if (this.registrationModel!!.deviceToken != null) {
                                PreferenceManager.putString(
                                    IntentConstants.TOKEN,
                                    this.registrationModel!!.deviceToken.toString()
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
                setLoadingState(LoadingState.LOADED())
            }

        }
    }

    fun getToken() = mResetToken


}