package com.example.ticktapp.util

import android.content.ContentResolver
import android.net.Uri
import android.os.Handler
import android.provider.OpenableColumns
import android.view.View
import com.example.ticktapp.model.MilestoneData
import com.google.android.material.snackbar.Snackbar
import com.dropbox.core.v2.DbxClientV2

import com.dropbox.core.DbxRequestConfig
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONArray


fun View.snackbar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

fun preventTwoClick(view: View) {
    view.isEnabled = false
    Handler().postDelayed(object : Runnable {
        override fun run() {
            view.isEnabled = true
        }
    }, 100)

}

object PostJobData {

    var postJobData = PostJobDataSave()

    fun postjobDataNullCheck(): Boolean {
        postJobData.let {
            return it.mileStoneList != null
        }
    }

    fun setPostNewJobActivityData(
        mileStoneList: ArrayList<MilestoneData>,
        jobTypePayment: Int = 1,
        price: Int = 0,
        startDate: String = "",
        endDate: String = ""
    ) {
        postJobData.mileStoneList = mileStoneList
        postJobData.price = price
        postJobData.jobTypePayment = jobTypePayment
        postJobData.startDate = startDate
        postJobData.endDate = endDate
    }

    fun clearMilestones() {
        postJobData.let {
            it.mileStoneList.let { it2 ->
                it2!!.clear()
                it.jobTypePayment = 1
                it.startDate = ""
                it.endDate = ""
            }
        }
    }

    fun getMileStoneList(): ArrayList<MilestoneData> {
        postJobData.let {
            return it.mileStoneList!!
        }
    }
}

data class PostJobDataSave(
    var mileStoneList: ArrayList<MilestoneData>? = null,
    var jobTypePayment: Int = 1,   // 1 for per hour ,2 for fixed budget, 3 for i need a quote
    var price: Int = 1,   // 1 for per hour , 2 for fixed price
    var startDate: String = "",
    var endDate: String = ""
)


fun <T> T.toJsonString(): String? {
    return Gson().toJson(this).toString()
}
//fun <T> ArrayList<T>.toJsonString(): String? {
//    return Gson().toJson(this,object :TypeToken<ArrayList<T>>(){}.type).toString()
//}


fun <T> String?.getPojoData(type: Class<T>): T? {
    if (this.isNullOrEmpty()) {
        return null
    } else
        return Gson().fromJson<T>(this, type)
}

inline fun <reified E> String?.getList(): ArrayList<E>? {
    if (this.isNullOrEmpty()) {
        return null
    } else {
        val array = JSONArray(this)

        val list = ArrayList<E>()
        for (i in 0 until array.length()) {
            array[i].toString().getPojoData<E>(E::class.java)?.let { list.add(it) }
        }
        return list
    }
}
 fun <E> String?.getDataList(): ArrayList<E>? {
    if (this.isNullOrEmpty()) {
        return null
    } else {
        val array = JSONArray(this)

        val list = ArrayList<E>()
        for (i in 0 until array.length()) {
           list.add( array[i] as E)
        }
        return list
    }
}


