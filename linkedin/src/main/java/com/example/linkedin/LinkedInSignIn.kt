package com.example.linkedin

import android.app.Activity
import android.content.Context
import com.ssw.linkedinmanager.dto.LinkedInAccessToken
import com.ssw.linkedinmanager.dto.LinkedInEmailAddress
import com.ssw.linkedinmanager.dto.LinkedInUserProfile
import com.ssw.linkedinmanager.events.LinkedInManagerResponse
import com.ssw.linkedinmanager.ui.LinkedInRequestManager


class LinkedInSignIn : LinkedInManagerResponse  {
    private var mbase : Activity? = null
    private lateinit var  mLinkedInManagerResponse : LinkedInManagerResponse
     lateinit var mLinkedInRequestManager: LinkedInRequestManager

        var linkedInRequestManager = LinkedInRequestManager(
        mbase,
        mLinkedInManagerResponse,
        LinkedInConstants.CLIENT_ID,
        LinkedInConstants.CLIENT_SECRET,
        LinkedInConstants.REDIRECT_URL,
        false
    )

    val email_only =  LinkedInRequestManager.MODE_EMAIL_ADDRESS_ONLY  //will return only the email address
    val profie_only = LinkedInRequestManager.MODE_LITE_PROFILE_ONLY  //will return only the profile data
    val both_mode = LinkedInRequestManager.MODE_BOTH_OPTIONS       // will return both email address and profile data



    override fun onGetAccessTokenFailed() {
    }

    override fun onGetAccessTokenSuccess(linkedInAccessToken: LinkedInAccessToken?) {
    }

    override fun onGetCodeFailed() {
    }

    override fun onGetCodeSuccess(code: String?) {
    }

    override fun onGetProfileDataFailed() {
    }

    override fun onGetProfileDataSuccess(linkedInUserProfile: LinkedInUserProfile?) {

    }

    override fun onGetEmailAddressFailed() {
    }

    override fun onGetEmailAddressSuccess(linkedInEmailAddress: LinkedInEmailAddress?) {
    }
}