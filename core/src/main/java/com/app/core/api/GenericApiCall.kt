package com.app.core.api

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.app.core.api.interfaces.NetworkListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Call to handling all request and response for the API
 */
object GenericApiCall {

    fun hitService(
        call: Call<ResponseBody>?,
        apiCode: Int,
        networkListener: NetworkListener
    ) {
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    if (response.body() != null) {
                        val apiResponse = response.body()!!.string()
                        Log.e("****SUCCESS*****", apiResponse)
                        networkListener.onSuccess(apiResponse, apiCode)


                    } else if (response.errorBody() != null) {
                        val i = response.errorBody()!!.string()
                        networkListener.onError(
                            response.errorBody()!!.string(),
                            apiCode
                        )
                    }
                } catch (e: Exception) {
                    if (e is JsonSyntaxException) {
                    }
                    e.printStackTrace()
                    networkListener.onFailure(null, apiCode)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                networkListener.onFailure(t, apiCode)
            }

        })

    }

}