package com.example.ticktapp.mvvm.viewmodel

import com.app.core.basehandler.BaseResponse
import com.app.core.coroutines.CoroutinesBase
import com.app.core.repo.TradieBankDetailsRepo
import com.app.core.util.ValidationsConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.app.core.model.jobmodel.TradieBankDetails
import com.example.ticktapp.validation.DataValidation

class BankDetailsViewModel : BaseViewModel() {
    var mAccountNameEditText:String=""
    var mAccountNumberEditText:String=""
    var mBsbNumberEditText:String=""

    private val mRepo by lazy { TradieBankDetailsRepo() }
    var mTradieBankModel: TradieBankDetails? = null


    /**
     * Api call to get Tradiee Bank Details.
     */
    fun getBankDetails() =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.getBankDetails()
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        if ((it.data as Map<String, Object>).size > 0) {
                            val model = TradieBankDetails()
                            model.userId = (it.data).get("userId").toString()
                            model.account_name =
                                (it.data).get("account_name").toString()
                            model.account_number =
                                (it.data).get("account_number").toString()
                            model.bsb_number = (it.data).get("bsb_number").toString()
                            model.stripeAccountId = (it.data).get("stripeAccountId").toString()
                            model.accountVerified = (it.data).get("accountVerified") as Boolean
                            mTradieBankModel = model
                        }

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to get TradieeAdd  Bank Details.
     */
    fun addBankDetails(mObject: com.google.gson.JsonObject,isEdit:Boolean) =
        if (mAccountNameEditText.trim().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_accnt_name
                ),
                ValidationsConstants.ACCOUNT_NAME_EMPTY
            )
        } else if (mAccountNumberEditText.trim().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_accnt_number
                ),
                ValidationsConstants.ACCOUNT_NUMBER_EMPTY
            )
        } else if (mAccountNumberEditText.replace(" ","").length < 6) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.accnt_number_length
                ),
                ValidationsConstants.ACCOUNT_NUMBER_LENGTH
            )
        } else if (mBsbNumberEditText.trim().isNullOrBlank()) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.please_enter_BSB_number
                ),
                ValidationsConstants.BSB_EMPTY
            )
        } else if (mBsbNumberEditText.length < 6) {
            mValidationLiveData.value = DataValidation(
                ApplicationClass.applicationContext().getString(
                    R.string.bsb_number_length
                ),
                ValidationsConstants.BSB_NUMBER_LENGTH
            )
        } else {

            CoroutinesBase.main {
                setLoadingState(LoadingState.LOADING())
               var resp: BaseResponse<Any>
                if(isEdit)
                 resp = mRepo.UpdateBankDetails(mObject)
                else
                    resp = mRepo.addBankDetails(mObject)

                updateView(resp) {
                    when (it) {
                        is API_VIEWMODEL_DATA.API_SUCCEED -> {
                            if ((it.data as Map<String, String>).size > 0) {
                                val model = TradieBankDetails()
                                model.userId = (it.data as Map<String, String>).get("userId").toString()
                                model.account_name =
                                    (it.data).get("account_name").toString()
                                model.account_number =
                                    (it.data).get("account_number").toString()
                                model.bsb_number = (it.data).get("bsb_number").toString()
                                mTradieBankModel = model
                            }

                        }
                    }
                }
                setLoadingState(LoadingState.LOADED())
            }

        }

    /**
     * Api call to remove Tradiee Bank Details.
     */
    fun getRemoveBankDetails() =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.removeBankDetails()
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

    /**
     * Api call to mark job/milestone complete.
     */
    fun markJobComplete(mObject: HashMap<String, Any>) =
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.markJobComplete(mObject )
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {

                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }

}


