package com.example.ticktapp.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.core.base.BaseRepo
import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.amazons3.S3MediaBean
import com.app.core.model.invite.AllContactsListingResponse
import com.app.core.util.ApiCodes
import com.app.core.util.ApiError
import com.example.ticktapp.validation.DataValidation

/**
 * All the ViewModel classes will be extending this class
 *
 */
open class BaseViewModel : ViewModel() {

    lateinit var mListingResponseModel: AllContactsListingResponse
    private val _response = MutableLiveData<ApiResponseData>()

    private val mUploadMediaLiveData by lazy { MutableLiveData<S3MediaBean>() }
    private val mBaseRepo by lazy { BaseRepo() }

    fun getUploadMediaLiveData() = mUploadMediaLiveData

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val response: LiveData<ApiResponseData>
        get() = _response

    val mValidationLiveData by lazy { MutableLiveData<DataValidation>() }

    fun getValidationLiveData() = mValidationLiveData


    /* */
    /**
     * This function is used to edit user data
     *
     * @param updateUserRequestModel model containing updated info for user
     *//*
    fun updateUserDetails(updateUserRequestModel: UpdateUserRequestModel) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            updateView(mBaseRepo.updateUserDetails(updateUserRequestModel)) {

            }
            setLoadingState(LoadingState.LOADED())
        }

    }*/


    private fun updateResponseObserver(response: ApiResponseData) {
        _response.value = response
    }

    fun getResponseObserver() = response

    fun setLoadingState(state: LoadingState) {
        _loadingState.value = state
    }

    fun getUserId() = mBaseRepo.getUserId()
    fun getFamilyProfileImage() = mBaseRepo.getFamilyProfileImage()
    fun getPICProfileImage() = mBaseRepo.getPICProfileImage()
    fun getPersonInCareName() = mBaseRepo.getPersonInCareName()

    /*
        fun getUserType() = mBaseRepo.getUserType()
    */
    fun getName() = mBaseRepo.getName()
    fun getFirstName() = mBaseRepo.getFirstName()
    fun getLastName() = mBaseRepo.getLastName()
    fun getRelation() = mBaseRepo.getRelation()
    fun getMyPICRelation() = mBaseRepo.getMyPICRelation()
    fun getPICFirstName() = mBaseRepo.getPICFirstName()
    fun getPICLastName() = mBaseRepo.getPICLastName()

    fun updateConfirmationOfPIC(isConfirmed: Boolean) =
        mBaseRepo.updateConfirmationOfPIC(isConfirmed)

    fun isConfirmedByPIC() = mBaseRepo.isConfirmedByPIC()

    fun updateProfileStep(stepCount: Int) = mBaseRepo.updateProfileStep(stepCount)
    fun getProfileStep() = mBaseRepo.getProfileStep()
    fun updateDeviceOwnerType(userType: String) = mBaseRepo.updateDeviceOwnerType(userType)

    //  fun getDeviceOwnerType() = mBaseRepo.getDeviceOwnerType()
    fun clearAllPreference() = mBaseRepo.clearAllPreference()

    fun doLogout() {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            updateView(mBaseRepo.doLogout()) {}
            setLoadingState(LoadingState.LOADED())
        }
    }

    /**
     * Getting list of all invited contacts
     *
     */


    /**
     *
     *
     * @param baseData response coming from backend
     * @param callBack lambda callback update view model
     */
    fun updateView(baseData: BaseResponse<Any>?, callBack: (API_VIEWMODEL_DATA) -> Unit) {
        if (baseData != null) {

            if (baseData.isInternetOn) {
                if (baseData.status_code != null && (baseData.status_code == 200 ||
                            baseData.status_code == 202 || baseData.status_code == 201)
                ) {
                    callBack(API_VIEWMODEL_DATA.API_SUCCEED(baseData.result, baseData.apiCode))
                    updateResponseObserver(
                        ApiResponseData.API_SUCCEED(
                            baseData.status_code!!,
                            baseData.apiCode,
                            baseData.message
                        )
                    )
                    return
                } else if (baseData.status_code != null && baseData.status_code == 401) {
                    callBack(API_VIEWMODEL_DATA.API_EXPIRED_SESSION(baseData.apiCode))
                    updateResponseObserver(
                        ApiResponseData.API_EXPIRED_SESSION(
                            ApiError(),
                            baseData.apiCode
                        )
                    )

                } else {

                    callBack(API_VIEWMODEL_DATA.API_EXCEPTION(baseData.apiCode))
                    updateResponseObserver(
                        ApiResponseData.API_EXCEPTION(
                            baseData.apiError!!,
                            baseData.apiCode
                        )
                    )


                    return
                }

            } else {
                callBack(API_VIEWMODEL_DATA.NO_INTERNET(baseData.apiCode))
                updateResponseObserver(
                    ApiResponseData.NO_INTERNET(
                        baseData.apiCode,
                        baseData.apiError!!.message
                    )
                )
                return
            }
        }
        val error = ApiError()
        error.message = "Something went wrong"
        callBack(API_VIEWMODEL_DATA.API_EXCEPTION(ApiCodes.EMPTY_RESPONSE))
        updateResponseObserver(ApiResponseData.API_EXCEPTION(error, ApiCodes.EMPTY_RESPONSE))
        return
    }

    fun setPicEmail(email: String) = mBaseRepo.setPicEmail(email)
    fun setPicPassword(password: String) = mBaseRepo.setPicPassword(password)
    fun getPicEmail() = mBaseRepo.getPicEmail()
    fun getPicPassword() = mBaseRepo.getPicPassword()


}