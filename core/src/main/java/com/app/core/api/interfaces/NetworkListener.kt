package com.app.core.api.interfaces

interface NetworkListener {

    /**
     * This method is called when response ir received with code 200
     *
     * @param response     - response in String
     *
     */
    fun onSuccess(response: String?,apiCode:Int?)


    /**
     * This method is called when response ir received in the error body
     *
     * @param errorResponse    - response in th error body
     */
    fun onError(errorResponse: Any?,apiCode:Int?)


    /**
     * This method is called when we receive a call in failure
     *
     * @param throwable
     */
    fun onFailure(throwable: Throwable?,apiCode:Int?)
}