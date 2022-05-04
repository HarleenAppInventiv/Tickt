package com.app.amazons3library.interfaces

import com.app.amazons3library.model.MediaBean

interface AmazonCallback {
    fun uploadSuccess(bean: MediaBean)
    fun uploadFailed(bean: MediaBean)
    fun uploadProgress(bean: MediaBean)
    fun uploadError(e: Exception, imageBean: MediaBean)
}