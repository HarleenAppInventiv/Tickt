package com.app.core.model.chat

import java.io.Serializable

/**
 * Created by appinventiv-pc on 6/3/18.
 */
class UserBean : Serializable {
    var userId: String? = null
    var name: String? = null
    var email: String? = null
    var image:String? = ""
    var deviceToken: String? = null
    var deviceId: String? = null
    var userType: Int? = null
    var deviceType: Any? = null
    var onlineStatus: Any? = null
}