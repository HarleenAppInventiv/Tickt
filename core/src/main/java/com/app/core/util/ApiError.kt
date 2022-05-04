package com.app.core.util

open class ApiError() {
    var status: Boolean = false
    var message: String? = null
    var result: Any? = null
    var status_code:Int?=null
}