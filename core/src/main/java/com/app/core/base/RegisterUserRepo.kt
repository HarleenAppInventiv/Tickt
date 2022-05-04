package com.app.core.base

import android.util.Log
import com.app.core.basehandler.BaseResponse
import com.app.core.model.usermodel.UserDataModel
import com.app.core.util.ApiCodes
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import java.lang.Exception

class RegisterUserRepo : BaseRepo() {
    suspend fun hitRegisterUserApi(
        email: String?,
        password: String?,
        confirmPassword: String?,
        token: String?,
        deviceToken: String?
    ): BaseResponse<Any> {
        val response= apiRequest(ApiCodes.REGISTER_USER) {
            unauthenticatedApiClient.hitRegisterUserApi(
                email,
                password,
                confirmPassword,
                token,
                deviceToken
            )
        }
        saveUserData(Gson().fromJson(Gson().toJson(response.result), UserDataModel::class.java))
        return response


    }

}