package com.example.ticktapp.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.lang.Exception

object ContentResolverUtil {

    fun getContentResolver(context: Context): ContentResolver {
        return context.contentResolver
    }

    fun getSize(contentResolver: ContentResolver, uri: Uri?): Long {
        var size: Long = 0
        try {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    fun getName(contentResolver: ContentResolver, uri: Uri?): String {
        var size = ""
        try {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                size = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

}