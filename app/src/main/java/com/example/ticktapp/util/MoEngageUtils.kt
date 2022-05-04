package com.example.ticktapp.util

import android.content.Context
import android.util.Log
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.Properties

class MoEngageUtils {

    companion object {
        fun sendEvent(context: Context, eventName: String, properties: Properties) {
            Log.i("moEngage_event_tracker", "sendEvent: $eventName")
            Log.i("moEngage_event_tracker", "event properties: ${properties != null}")
            MoEHelper.getInstance(context).trackEvent(eventName, properties)
            Log.i("moEngage_event_tracker", "init_check: ${MoEHelper.getInstance(context) != null}")
        }
    }
}