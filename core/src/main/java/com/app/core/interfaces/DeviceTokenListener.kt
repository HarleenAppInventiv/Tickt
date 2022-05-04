package com.app.core.interfaces

interface DeviceTokenListener {
    fun onTokenReceive(token: String)
    fun onFailure(e: Exception)
}