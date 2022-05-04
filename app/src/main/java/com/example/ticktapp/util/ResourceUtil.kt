package com.example.ticktapp.util

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.model.Country
import com.example.ticktapp.util.StringUtils.readEncodedJsonString
import org.json.JSONArray

object ResourceUtil {

    fun getLocaleCode(code: String): Country {
        val country = Country()
        try {
            val allCountriesCode: String = readEncodedJsonString()
            val countryArray = JSONArray(allCountriesCode)
            for (i in 0 until countryArray.length()) {
                val jsonObject = countryArray.getJSONObject(i)
                country.flag = jsonObject.getString("flag")
                country.countryCode = jsonObject.getString("code")
                country.name = jsonObject.getString("name")
                country.dialCode = jsonObject.getString("dial_code")
                if (country.countryCode == code) {
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return country
    }

    fun getResourceIdFromResourceName(drawableName: String): Int {
        try {
            val res = R.drawable::class.java
            val field = res.getField(drawableName)
            return field.getInt(null)
        } catch (e: Exception) {

        }
        return -1
    }

    fun getDrawable(resId: Int): Drawable? {
        return ContextCompat.getDrawable(ApplicationClass.applicationContext(), resId)
    }

    fun getColor(colorResId: Int): Int {
        return ContextCompat.getColor(ApplicationClass.applicationContext(), colorResId)
    }

    fun getString(resId: Int): String {
        return ApplicationClass.applicationContext().getString(resId)
    }

    fun getString(context: Context, resId: Int): String {
        return context.getString(resId)
    }

    fun getStringArray(resId: Int): ArrayList<String> {
        val stringArray: Array<String> =
            ApplicationClass.applicationContext().resources.getStringArray(resId)
        return stringArray.toCollection(ArrayList())
    }

    fun getFont(font: Int): Typeface? {
        return ResourcesCompat.getFont(ApplicationClass.applicationContext(), font)
    }
}