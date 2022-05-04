package com.example.ticktapp.base

import com.app.core.util.ApiError

/**
 * Sealed class to observe changes on [android.app.Activity]
 *
 */
sealed class ApiResponseData {
    /**
     * contains data which comes on success call
     *
     * @property statusCode code to identify weather api succeed or fail
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     * @property msg any error or success message
     */
    class API_SUCCEED(val statusCode: Int, val apiCode: Int, val msg: String?) : ApiResponseData()

    /**
     * called when there is any API error
     * @property exception observe any error in api
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    class API_EXCEPTION(val exception: ApiError, val apiCode: Int) : ApiResponseData()

    /**
     *called when there issession expired error
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    class API_EXPIRED_SESSION(val exception: ApiError, val apiCode: Int): ApiResponseData()

    /**
     *called when there is no internet connection
     *
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     * @property msg any error or success messag
     */
    class NO_INTERNET(val apiCode: Int, val msg: String?) : ApiResponseData()

    /**
     *called when there is user not authorize
     *
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     * @property msg any error or success messag
     */
    class AUTORIZE_FAILED(val apiCode: Int, val msg: String?) : ApiResponseData()
}

/**
 * Sealed class to observe changes on [BaseViewModel]
 *
 */
sealed class API_VIEWMODEL_DATA {
    /**
     * contains data which comes on success call
     *
     * @property data any contained data on success callback
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    class API_SUCCEED(val data: Any?, val apiCode: Int) : API_VIEWMODEL_DATA()

    /**
     *called when there is any API error
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    class API_EXCEPTION(val apiCode: Int) : API_VIEWMODEL_DATA()

    /**
     *called when there issession expired error
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    class API_EXPIRED_SESSION(val apiCode: Int) : API_VIEWMODEL_DATA()

    /**
     * called when there is no internet connection
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    class NO_INTERNET(val apiCode: Int) : API_VIEWMODEL_DATA()

    /**
     * called when there user not authorize
     * @property apiCode code to identify the api which is called based on [com.app.core.util.ApiCodes]
     */
    class AUTORIZE_FAILED(val apiCode: Int) : API_VIEWMODEL_DATA()
}

