package com.app.core.retrofit

import com.app.core.api.RestAPI
import com.app.core.preferences.PreferenceManager
import com.app.core.util.APIHeaders
import com.app.core.util.DeviceConstants
import com.app.core.util.IntentConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class RetrofitManager {
    private var service: RestAPI? = null


    val retrofit: Retrofit
        get() {
            val builder = Retrofit.Builder().baseUrl(RestAPI.APP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder.client(OkHttpClient.Builder().build()).build()
            return retrofit
        }


    fun getService(baseUrl: String = RestAPI.APP_BASE_URL): RestAPI {
        return setService(baseUrl)
    }

    /*"Bearer ${PreferenceManager.getString(PreferenceManager.AUTH_TOKEN)}"*/
    private fun setService(baseURl: String): RestAPI {
        val gson = GsonBuilder().setLenient().create()
        val httppClient = OkHttpClient.Builder().connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
        //        val appVersion = BuildConfig.VERSION_NAME
        httppClient.addInterceptor { chain ->
            val original = chain.request()
            val request: Request.Builder
            request = original.newBuilder()
            val auth = original.header(APIHeaders.Authorization)
            val authHeader = PreferenceManager.getString(IntentConstants.TOKEN)
            if (auth.isNullOrEmpty()) {
                request.header(
                    APIHeaders.Authorization,
                    Credentials.basic(APIHeaders.API_USERNAME, APIHeaders.API_PASSWORD)
                )
            }
            if (!(original.url.encodedPath.contains("privacyPolicy") || original.url.encodedPath.contains(
                    "tnc"
                ) || original.url.encodedPath.contains("/fcm/send"))
            ) {
                if (authHeader != null && !authHeader.equals("")) {
                    request.header(
                        APIHeaders.Authorization_HEADER,
                        authHeader
                    )
                }
            }
            /*request.header(
                DeviceConstants.DEVICE_TOKEN,
                PreferenceManager.getString(PreferenceManager.DEVICE_TOKEN) ?: ""
            )
            request.header(
                DeviceConstants.DEVICE_ID,
                CoreUtils.getDeviceId(CoreContextWrapper.getContext())
            )
            request.header(DeviceConstants.PLATFORM, "1")*/

            //  request.header(DeviceConstants.OFFSET, CoreUtils.getTimeZoneoffset())
            request.header(DeviceConstants.TIMEZONE, TimeZone.getDefault().id)
            chain.proceed(request.build())
        }

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httppClient.addInterceptor(logging)
        val client = httppClient.build()
        service = getRetrofit(client, gson, baseURl).create(RestAPI::class.java)
        return service!!
    }


    private fun getRetrofit(client: OkHttpClient?, gson: Gson?, baseURl: String): Retrofit {
        var okHttpClient = client
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder().build()
        }
        val builder = Retrofit.Builder().baseUrl(baseURl)
        if (gson == null) {
            builder.addConverterFactory(GsonConverterFactory.create())
        } else {
            builder.addConverterFactory(GsonConverterFactory.create(gson))
        }
        val retrofit = builder.client(okHttpClient).build()
        return retrofit
    }


    companion object {

        var instance: RetrofitManager? = null

        fun getRetroInstance(): RetrofitManager {
            if (instance == null) {
                instance =
                    RetrofitManager()
            }
            return instance as RetrofitManager
        }

    }
}