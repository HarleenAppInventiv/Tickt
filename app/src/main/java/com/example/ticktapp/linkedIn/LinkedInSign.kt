package com.example.ticktapp.linkedIn


import android.app.Activity
import android.util.Log
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.io.Serializable
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class LinkedInSign : Serializable {

    private lateinit var listener: View.OnClickListener
    var linkedinId: String? = null
    var linkedinFirstName: String? = null
    var linkedinLastName: String? = null
    var linkedinEmail: String? = null
    var mActivity: Activity? = null

    constructor()

    constructor(fisrtName: String?, lastName: String?, id: String?, email: String?) {
        linkedinFirstName = fisrtName
        linkedinLastName = lastName
        linkedinEmail = email
        linkedinId = id
    }


    fun linkedInRequestForAccessToken(listener: View.OnClickListener) {
        this.listener = listener;
        GlobalScope.launch(Dispatchers.Default) {
            val grantType = "authorization_code"
            val postParams =
                "grant_type=" + grantType + "&code=" + LinkedInConstants.CODE + "&redirect_uri=" + LinkedInConstants.REDIRECT_URI + "&client_id=" + LinkedInConstants.CLIENT_ID + "&client_secret=" + LinkedInConstants.CLIENT_SECRET
            val url = URL(LinkedInConstants.TOKENURL)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            withContext(Dispatchers.IO) {
                outputStreamWriter.write(postParams)
                outputStreamWriter.flush()
            }
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            val jsonObject = JSONTokener(response).nextValue() as JSONObject

            val accessToken = jsonObject.getString("access_token") //The access token
            Log.d("accessToken is: ", accessToken)

            val expiresIn = jsonObject.getInt("expires_in") //When the access token expires
            Log.d("expires in: ", expiresIn.toString())


            withContext(Dispatchers.Main) {
                // Get user's id, first name, last name, profile pic url
                fetchlinkedInUserProfile(accessToken)
            }
        }
    }

    fun fetchlinkedInUserProfile(token: String) {
        GlobalScope.launch(Dispatchers.Default) {
            val tokenURLFull =
                "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))&oauth2_access_token=$token"
            val url = URL(tokenURLFull)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8

            val json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }

            val linkedInProfileModel = json.decodeFromString<LinkedInProfileModel>(response)

            withContext(Dispatchers.Main) {
                Log.d("LinkedIn Access Token: ", token)

                // LinkedIn Id
                linkedinId = linkedInProfileModel.id
                Log.d("LinkedIn Id: ", linkedinId!!)

                // LinkedIn First Name
                linkedinFirstName = linkedInProfileModel.firstName.localized.enUS
                Log.d("LinkedIn First Name: ", linkedinFirstName!!)

                // LinkedIn Last Name
                linkedinLastName = linkedInProfileModel.lastName.localized.enUS
                Log.d("LinkedIn Last Name: ", linkedinLastName!!)

                // LinkedIn Profile Picture URL
                /*
                     Change row of the 'elements' array to get diffrent size of the profile pic
                     elements[0] = 100x100
                     elements[1] = 200x200
                     elements[2] = 400x400
                     elements[3] = 800x800
                */

                /*       val linkedinProfilePic =
                           linkedInProfileModel.profilePicture.displayImage.elements.get(2)
                               .identifiers.get(0).identifier
                       Log.d("LinkedIn Profile URL: ", linkedinProfilePic)

                 */

                // Get user's email address
                fetchLinkedInEmailAddress(token)
            }
        }
    }

    fun fetchLinkedInEmailAddress(token: String) {
        val tokenURLFull =
            "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))&oauth2_access_token=$token"

        GlobalScope.launch(Dispatchers.Default) {
            val url = URL(tokenURLFull)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() } // defaults to UTF-8

            val linkedInEmailModel = Json.decodeFromString<LinkedInEmailModel>(response)
            withContext(Dispatchers.Main) {
                // LinkedIn Email
                linkedinEmail = linkedInEmailModel.elements.get(0).elementHandle.emailAddress
                Log.d("LinkedIn Email: ", linkedinEmail!!)
            }

            if (listener != null) {
                listener.onClick(null)
            }
//            val context = mActivity?.applicationContext
//            mActivity?.startActivity(Intent(context, LoginActivity::class.java).apply {
//                putExtra(IntentConstants.LINKEDINCLICK, Constants.LINKEDINRESULT)
//            })

        }

    }


}