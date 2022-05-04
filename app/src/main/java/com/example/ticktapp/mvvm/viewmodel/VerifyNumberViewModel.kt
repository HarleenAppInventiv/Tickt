package com.example.ticktapp.mvvm.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.app.core.coroutines.CoroutinesBase
import com.app.core.model.VerifyEmailModel
import com.app.core.model.requestsmodel.OTPRequest
import com.app.core.model.usermodel.UserDataModel
import com.app.core.util.ValidationsConstants
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.base.API_VIEWMODEL_DATA
import com.example.ticktapp.base.BaseViewModel
import com.example.ticktapp.base.LoadingState
import com.example.ticktapp.mvvm.repo.OTPRepo
import com.example.ticktapp.validation.DataValidation
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit

class VerifyNumberViewModel : BaseViewModel() {
    private val timerTextLiveData = MutableLiveData<String>()
    private var countDownTimer: CountDownTimer? = null
    private var verifyPhoneModel: VerifyEmailModel? = null

    private val mRepo by lazy { OTPRepo() }

    internal fun getTimerTextLiveData(): MutableLiveData<String> {
        return timerTextLiveData
    }


    // show counter of 1 min for entering received otp
    fun startTimer() {
        if (countDownTimer != null) {
            (countDownTimer as CountDownTimer).cancel()
        }
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val ms = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
                timerTextLiveData.value = ms
            }

            override fun onFinish() {
                timerTextLiveData.value = "00:00"
            }
        }.start()
    }


    fun hitVerifyEmailOtp(otp: String, email: String) {
        if (otp.isEmpty()) {
            mValidationLiveData.value =
                DataValidation(
                    ApplicationClass.applicationContext().getString(R.string.please_enter_otp),
                    ValidationsConstants.OTP_EMPTY
                )
        } else if (otp.length != 5) {
            mValidationLiveData.value =
                DataValidation(
                    ApplicationClass.applicationContext()
                        .getString(R.string.please_enter_complete_OTP),
                    ValidationsConstants.OTP_INVALID
                )
        } else {
            val otpRequest = OTPRequest(otp, email)
            CoroutinesBase.main {
                setLoadingState(LoadingState.LOADING())
                val resp = mRepo.hitRegisterOTPVerify(otpRequest)
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

    fun hitVerifyOtp(otp: String, phone: String) {
        if (otp.isEmpty()) {
            mValidationLiveData.value =
                DataValidation(
                    ApplicationClass.applicationContext().getString(R.string.please_enter_otp),
                    ValidationsConstants.OTP_EMPTY
                )
        } else if (otp.length != 5) {
            mValidationLiveData.value =
                DataValidation(
                    ApplicationClass.applicationContext()
                        .getString(R.string.please_enter_complete_OTP),
                    ValidationsConstants.OTP_INVALID
                )
        } else {
            val otpRequest = OTPRequest(otp, mobileNumber = phone)
            CoroutinesBase.main {
                setLoadingState(LoadingState.LOADING())
                val resp = mRepo.verifiyMobileOtp(otpRequest)
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

    /**
     * Api call to check mobile number
     */
    fun checkMobileNumber(mobileNo: String) {
        CoroutinesBase.main {
            setLoadingState(LoadingState.LOADING())
            val resp = mRepo.hitCheckPhoneNumber(mobileNo)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response =
                            Gson().fromJson(Gson().toJson(it.data), VerifyEmailModel::class.java)
                        this.verifyPhoneModel = response
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    /**
     * Api call to check email
     */
    fun checkEmailId(mobileNo: String, progress: Boolean) {
        CoroutinesBase.main {
            if (progress)
                setLoadingState(LoadingState.LOADING())
            val resp = mRepo.checkEmailId(mobileNo)
            updateView(resp) {
                when (it) {
                    is API_VIEWMODEL_DATA.API_SUCCEED -> {
                        val response =
                            Gson().fromJson(Gson().toJson(it.data), VerifyEmailModel::class.java)
                        this.verifyPhoneModel = response
                    }
                }
            }
            setLoadingState(LoadingState.LOADED())
        }
    }

    fun getVerifyPhoneData() = verifyPhoneModel

}





