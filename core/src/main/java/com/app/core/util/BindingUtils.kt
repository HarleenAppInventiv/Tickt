package com.secretmenu.chat

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

object BindingUtils {

    private fun getActivity(v: View): Activity? {
        var context = v.context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = (context).baseContext
        }
        return null
    }

    @BindingAdapter("backOnClick")
    @JvmStatic
    fun onBackClick(ivBack: ImageView,costant:String) {
       ivBack.setOnClickListener{
           getActivity(ivBack)?.finish()
       }
    }


}



